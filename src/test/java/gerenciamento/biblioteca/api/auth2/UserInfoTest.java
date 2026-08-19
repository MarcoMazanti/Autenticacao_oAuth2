package gerenciamento.biblioteca.api.auth2;

import gerenciamento.biblioteca.api.auth2.DTO.CreationTokenDTO;
import gerenciamento.biblioteca.api.auth2.DTO.UserInfoDTO;
import gerenciamento.biblioteca.api.auth2.Entities.Roles;
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

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserInfoTest {
    /*
     * Esta funcionalidade apenas retornará as informações que são utilizadas para a geração do access token de forma crua,
     *  de fácil leitura. Entretanto para efetuar isso, basta estar logado, pois será utilizado o próprio token de
     *  validação para obter tais campos.
     * Será obtido o accessToken do Header para verificar o e-mail novamente e retornar o DTO para o usuário.
     */

    @Spy
    private static ManagementJWT managementJWT = new ManagementJWT();

    @InjectMocks
    private TokenService tokenService = new TokenService(managementJWT);

    @Mock
    private UsuarioRepository usuarioRepository;

    private static Stream<String> proverTokensValidos() {
        CreationTokenDTO dto = new CreationTokenDTO(
                1,
                "nome",
                "email@email.com",
                List.of(Roles.ADMIN.toString(), Roles.USER.toString()).toArray(String[]::new)
        );

        String token = managementJWT.criarAccessToken(dto);

        return Stream.of(token);
    }

    @ParameterizedTest
    @MethodSource("proverTokensValidos")
    public void UsuarioInexistente(String accessToken) {
        // given
        given(usuarioRepository.existsById(anyInt())).willReturn(false);

        UserInfoDTO userInfoDTO = tokenService.getInfo(accessToken);

        verify(usuarioRepository).existsById(anyInt());

        Assertions.assertFalse(userInfoDTO.emailVerified());
    }

    @ParameterizedTest
    @MethodSource("proverTokensValidos")
    public void UsuarioValido(String accessToken) {
        // given
        given(usuarioRepository.existsById(anyInt())).willReturn(true);

        UserInfoDTO userInfoDTO = tokenService.getInfo(accessToken);

        verify(usuarioRepository).existsById(anyInt());

        Assertions.assertTrue(userInfoDTO.emailVerified());
        Assertions.assertNotNull(userInfoDTO.name());
        Assertions.assertNotNull(userInfoDTO.email());
        Assertions.assertNotNull(userInfoDTO.roles());
    }
}
