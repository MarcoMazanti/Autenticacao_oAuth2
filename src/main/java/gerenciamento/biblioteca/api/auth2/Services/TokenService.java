package gerenciamento.biblioteca.api.auth2.Services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import gerenciamento.biblioteca.api.auth2.DTO.CreationTokenDTO;
import gerenciamento.biblioteca.api.auth2.DTO.TokenCreatedDTO;
import gerenciamento.biblioteca.api.auth2.DTO.UserInfoDTO;
import gerenciamento.biblioteca.api.auth2.Entities.*;
import gerenciamento.biblioteca.api.auth2.Expections.*;
import gerenciamento.biblioteca.api.auth2.Externals.BibliotecaAPI;
import gerenciamento.biblioteca.api.auth2.Repositories.TokenRepository;
import gerenciamento.biblioteca.api.auth2.Repositories.UsuarioRepository;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.*;

import static gerenciamento.biblioteca.api.auth2.Application.SECRET_KEY;

@Service
public class TokenService {
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private BibliotecaAPI bibliotecaAPI;
    @Autowired
    private GoogleAuthService googleAuthService;
    @Autowired
    private ManagementJWT managementJWT;

    private final static ObjectMapper mapper = new ObjectMapper();

    public TokenService() {
    }

    public TokenService(ManagementJWT managementJWT) {
        this.managementJWT = managementJWT;
    }

    public TokenCreatedDTO criarToken(CreationTokenDTO tokenDTO) {
        if (tokenRepository.existsById(tokenDTO.id())) throw new RegistroJaExisteException("Usuário já possui token criado.");

        Optional<Usuario> usuarioOptional = usuarioRepository.findById(tokenDTO.id());
        if (usuarioOptional.isEmpty()) throw new RegistroInexistenteException("Não foi encontrado o usuário com o id: " + tokenDTO.id() + " cadastrado no banco.");

        Usuario usuario = usuarioOptional.get();
        if (!usuario.getEmail().equals(tokenDTO.email()) || !usuario.getNome().equals(tokenDTO.name())) throw new RegistroInconsistenteException("O campo não bate com o que se encontra cadastrado no banco.");

        TokenCreatedDTO tokenCreatedDTO = managementJWT.criarTokens(tokenDTO);
        System.out.println(tokenCreatedDTO);
        Token token = new Token(usuario.getId(), usuario, tokenCreatedDTO.refreshToken());
        System.out.println(token);
        tokenRepository.save(token);

        return tokenCreatedDTO;
    }

    public boolean validarToken(String accessToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();

            int id = Integer.parseInt(claims.getSubject());

            if (id <= 0) throw new RegistroInconsistenteException("ID encontra-se incorreto (Menor ou igual a zero).");

            return true;
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(), "Token expirado!");
        } catch (JwtException | IllegalArgumentException e) {
            throw new RegistroInconsistenteException("Token inválido ou adulterado!");
        }
    }

    public String atualizarToken(TokenCreatedDTO tokenCreatedDTO) {
        Optional<Token> tokenOpt = tokenRepository.findByRefreshToken(tokenCreatedDTO.refreshToken());

        if (tokenOpt.isEmpty()) throw new RegistroInexistenteException("Token não encontrado no banco.");
        Token token = tokenOpt.get();

        if (token.getSituacao() == Situacao.INATIVO || token.getDataExpiracao().isBefore(LocalDateTime.now()))
            throw new TokenExpiradoException("Token expirado.");
        if (token.getSituacao() == Situacao.BLOQUEADO)
            throw new UsuarioBloqueadoException("Token bloqueado.");

        try {
            String midSplit = tokenCreatedDTO.accessToken().split("\\.")[1];
            byte[] payloadBytes = Base64.getUrlDecoder().decode(midSplit);
            JsonNode payload = mapper.readTree(payloadBytes);

            List<String> roles = new ArrayList<>();
            JsonNode rolesNode = payload.path("roles");

            if (rolesNode.isArray()) {
                for (JsonNode roleNode : rolesNode) {
                    roles.add(roleNode.asText());
                }
            }

            CreationTokenDTO tokenDTO = new CreationTokenDTO(
                    token.getUsuario().getId(),
                    token.getUsuario().getNome(),
                    token.getUsuario().getEmail(),
                    roles.toArray(new String[0]));

            return managementJWT.criarAccessToken(tokenDTO);
        } catch (Exception e) {
            throw new RegistroInconsistenteException("Falha ao ler o payload do token JWT.");
        }
    }

    public Situacao atualizarSituacao(int idRequerinte, int idAlvo) {
        if (idRequerinte <= 0 || idAlvo <= 0) throw new DadosInvalidosException("Não existe IDs menores ou iguais a zero!");
        if (idRequerinte == idAlvo) throw new AlteracaoNegadaException("O usuário não pode se auto bloquear ou desbloquear!");

        List<Roles> rolesList = bibliotecaAPI.getRolesByUserId(idRequerinte);

        if (rolesList.isEmpty()) throw new RegistroInexistenteException("Objeto recebido encontra-se vazio.");

        if (rolesList.contains(Roles.ADMIN)) {
            Optional<Token> optionalToken = tokenRepository.findByUsuarioId(idAlvo);
            if (optionalToken.isEmpty()) throw new UsuarioSemTokenException("Usuário Não possui token!");
            Token token = optionalToken.get();

            if (token.getSituacao() == Situacao.BLOQUEADO) {
                if (token.getDataExpiracao().isBefore(LocalDateTime.now())) {
                    token.setSituacao(Situacao.INATIVO);
                } else {
                    token.setSituacao(Situacao.ATIVO);
                }
            } else {
                token.setSituacao(Situacao.BLOQUEADO);
            }

            tokenRepository.save(token);
            return token.getSituacao();
        }

        throw new AlteracaoNegadaException("Não possui cargo para alterar a situação do token!");
    }

    public UserInfoDTO getInfo(String accessToken) {
        try {
            validarToken(accessToken);

            // 2. Decodifica o payload do JWT (segunda parte da String separada por ponto)
            byte[] payloadBytes = Base64.getUrlDecoder().decode(accessToken.split("\\.")[1]);
            JsonNode jsonNode = mapper.readTree(payloadBytes);

            // 3. Extrai as propriedades básicas do JSON usando o .asText()
            String sub = jsonNode.path("sub").asText();
            String name = jsonNode.path("name").asText();
            String email = jsonNode.path("email").asText();

            boolean existe = usuarioRepository.existsById(Integer.parseInt(sub));

            // 4. Mapeia o array de roles com segurança
            List<Roles> rolesList = new ArrayList<>();
            JsonNode rolesNode = jsonNode.path("roles");
            if (rolesNode.isArray()) {
                for (JsonNode roleNode : rolesNode) {
                    rolesList.add(Roles.valueOf(roleNode.asText()));
                }
            }

            return new UserInfoDTO(sub, name, email, existe, rolesList);

        } catch (ExpiredJwtException | RegistroInconsistenteException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw new RegistroInconsistenteException("Erro na conversão das roles ou dados do token inválidos.");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar o payload do token JWT", e);
        }
    }

    public TokenCreatedDTO autenticarComGoogle(String idToken) {
        // 1. Valida o ID Token do Google e extrai o Payload com segurança
        GoogleIdToken.Payload payload = googleAuthService.validarEExtrairPayload(idToken);

        String email = payload.getEmail();
        Boolean emailVerified = payload.getEmailVerified();
        String name = (String) payload.get("name");

        // Caso o e-mail não esteja verificado, retorna um erro
        if (!emailVerified) {
            throw new IllegalArgumentException("O e-mail da conta do Google precisa estar verificado.");
        }

        // Obtém o usuário com base no e-mail, se não estiver cadastrado, é gerado uma nova conta para ele
        Usuario usuario;
        try {
            usuario = bibliotecaAPI.getUsuarioByEmail(email);
        } catch (RegistroInexistenteException e) {
            usuario = new Usuario(name, email, email);
            usuarioRepository.save(usuario);
        }

        // Gera o token com base no usuário obtido anteriormente
        CreationTokenDTO creationDTO = new CreationTokenDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                bibliotecaAPI.getRolesByUserId(usuario.getId()).stream().map(Enum::toString).toArray(String[]::new)
        );

        return managementJWT.criarTokens(creationDTO);
    }
}
