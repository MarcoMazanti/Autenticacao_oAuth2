package gerenciamento.biblioteca.api.auth2;

import gerenciamento.biblioteca.api.auth2.DTO.CreationTokenDTO;
import gerenciamento.biblioteca.api.auth2.DTO.TokenCreatedDTO;
import gerenciamento.biblioteca.api.auth2.Entities.Usuario;
import gerenciamento.biblioteca.api.auth2.Expections.RegistroInconsistenteException;
import gerenciamento.biblioteca.api.auth2.Expections.RegistroInexistenteException;
import gerenciamento.biblioteca.api.auth2.Expections.RegistroJaExisteException;
import gerenciamento.biblioteca.api.auth2.Repositories.TokenRepository;
import gerenciamento.biblioteca.api.auth2.Repositories.UsuarioRepository;
import gerenciamento.biblioteca.api.auth2.Services.ManagementJWT;
import gerenciamento.biblioteca.api.auth2.Services.TokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CriacaoTokenTest {
    /*
     * Para criação de um token, devem ser passado os valores abaixo:
     *  - id
     *  - nome
     *  - e-mail
     *  - roles
     * Ao obter estes dados, é verificado se o usuário já possui um token, caso tenha, é retornado uma exception.
     * Com o decorrer, é verificado se o id realmente existe na tabela usuário e se bate com o nome e e-mail ali cadastrado. Caso não, retorna uma exception.
     * Após a validação, é gerado os tokens de access e de refresh token.
     * Por fim, é salvo no banco de dados os seguintes dados:
     *  - id
     *  - refresh_token
     *  - situacao (criada inicialmente pelo banco como ativo)
     *  - vencimento (criada apenas pelo banco)
     * Logo após é apenas verificado se ocorreu tudo de acordo. Caso não, é retornado um exception.
     */

    @Spy
    private ManagementJWT managementJWT = new ManagementJWT();

    @InjectMocks
    private TokenService tokenService = new TokenService(managementJWT);

    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private UsuarioRepository usuarioRepository;

    @ParameterizedTest
    @MethodSource("geradorDeCreationTokenDTO")
    public void testeUsuarioComTokenExistente(CreationTokenDTO tokenDTO) {
        when(tokenRepository.existsById(anyInt())).thenReturn(true);

        Assertions.assertThrows(RegistroJaExisteException.class, () -> tokenService.criarToken(tokenDTO));
    }

    @ParameterizedTest
    @MethodSource("geradorDeCreationTokenDTO")
    public void testeUsuarioInexistente(CreationTokenDTO tokenDTO) {
        when(tokenRepository.existsById(anyInt())).thenReturn(false);
        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.empty());

        Assertions.assertThrows(RegistroInexistenteException.class, () -> tokenService.criarToken(tokenDTO));
    }

    @ParameterizedTest
    @MethodSource("geradorDeCreationTokenDTO")
    public void testeUsuarioInconsistente(CreationTokenDTO tokenDTO) {
        Usuario usuario = new Usuario(tokenDTO.name(), tokenDTO.email() + "asd", "senha");

        when(tokenRepository.existsById(anyInt())).thenReturn(false);
        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuario));

        Assertions.assertThrows(RegistroInconsistenteException.class, () -> tokenService.criarToken(tokenDTO));
    }

    @ParameterizedTest
    @MethodSource("geradorDeCreationTokenDTO")
    public void testCriacaoToken(CreationTokenDTO tokenDTO) {
        Usuario usuario = new Usuario(tokenDTO.name(), tokenDTO.email(), "senha");

        when(tokenRepository.existsById(anyInt())).thenReturn(false);
        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuario));

        TokenCreatedDTO tokenCreatedDTO = tokenService.criarToken(tokenDTO);
        Assertions.assertNotNull(tokenCreatedDTO);
    }

    private static CreationTokenDTO[] geradorDeCreationTokenDTO() {
        return new CreationTokenDTO[] {
                new CreationTokenDTO(1, "nome", "email", new String[] {"ROLE_USER"}),
                new CreationTokenDTO(2, "nome", "email", new String[] {"ROLE_ADMIN"})
        };
    }
}
