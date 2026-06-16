package gerenciamento.biblioteca.api.auth2.Services;

import gerenciamento.biblioteca.api.auth2.DTO.CreationTokenDTO;
import gerenciamento.biblioteca.api.auth2.DTO.TokenCreatedDTO;
import gerenciamento.biblioteca.api.auth2.Entities.Situacao;
import gerenciamento.biblioteca.api.auth2.Entities.Token;
import gerenciamento.biblioteca.api.auth2.Entities.Usuario;
import gerenciamento.biblioteca.api.auth2.Expections.*;
import gerenciamento.biblioteca.api.auth2.Repositories.TokenRepository;
import gerenciamento.biblioteca.api.auth2.Repositories.UsuarioRepository;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;

import java.time.LocalDateTime;
import java.util.*;

import static gerenciamento.biblioteca.api.auth2.Application.SECRET_KEY;

@Service
public class TokenService {
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

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
        Token token = new Token(tokenDTO.id(), tokenCreatedDTO.refreshToken());
        tokenRepository.save(token);

        return tokenCreatedDTO;
    }

    public void validarToken(String accessToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();

            int id = Integer.parseInt(claims.getSubject());

            if (id <= 0) throw new RegistroInconsistenteException("ID encontra-se incorreto (Menor ou igual a zero).");
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

        if (token.getSituacao() == Situacao.INATIVO || token.getDataExpiracao().isBefore(LocalDateTime.now())) throw new TokenExpiradoException("Token expirado.");
        if (token.getSituacao() == Situacao.BLOQUEADO) throw new UsuarioBloqueadoException("Token bloqueado.");

        String midSplit = tokenCreatedDTO.accessToken().split("\\.")[1];
        JsonNode payload = mapper.readTree(new String(Base64.getUrlDecoder().decode(midSplit)));

        ArrayNode rolesNode = payload.get("roles").asArray();
        ArrayList<String> roles = new ArrayList<>();

        for (JsonNode roleNode : rolesNode) roles.add(roleNode.asString());
        CreationTokenDTO tokenDTO = new CreationTokenDTO(
                token.getUsuario().getId(),
                token.getUsuario().getNome(),
                token.getUsuario().getEmail(),
                roles.toArray(new String[0]));

        return managementJWT.criarAccessToken(tokenDTO);
    }

    public void atualizarSituacao(int idRequerinte, int idAtualizador) {

    }
}
