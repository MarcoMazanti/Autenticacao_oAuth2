package gerenciamento.biblioteca.api.auth2.Services;

import gerenciamento.biblioteca.api.auth2.DTO.CreationTokenDTO;
import gerenciamento.biblioteca.api.auth2.DTO.TokenCreatedDTO;
import gerenciamento.biblioteca.api.auth2.Entities.Token;
import gerenciamento.biblioteca.api.auth2.Entities.Usuario;
import gerenciamento.biblioteca.api.auth2.Expections.RegistroInconsistenteException;
import gerenciamento.biblioteca.api.auth2.Expections.RegistroInexistenteException;
import gerenciamento.biblioteca.api.auth2.Expections.RegistroJaExisteException;
import gerenciamento.biblioteca.api.auth2.Repositories.TokenRepository;
import gerenciamento.biblioteca.api.auth2.Repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TokenService {
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    private ManagementJWT managementJWT;

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

    public boolean validarToken(TokenCreatedDTO token) {
        return false;
    }
}
