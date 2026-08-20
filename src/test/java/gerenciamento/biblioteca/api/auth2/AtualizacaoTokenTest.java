package gerenciamento.biblioteca.api.auth2;

import gerenciamento.biblioteca.api.auth2.DTO.CreationTokenDTO;
import gerenciamento.biblioteca.api.auth2.DTO.TokenCreatedDTO;
import gerenciamento.biblioteca.api.auth2.Entities.Roles;
import gerenciamento.biblioteca.api.auth2.Entities.Situacao;
import gerenciamento.biblioteca.api.auth2.Entities.Token;
import gerenciamento.biblioteca.api.auth2.Entities.Usuario;
import gerenciamento.biblioteca.api.auth2.Expections.TokenExpiradoException;
import gerenciamento.biblioteca.api.auth2.Expections.UsuarioBloqueadoException;
import gerenciamento.biblioteca.api.auth2.Repositories.TokenRepository;
import gerenciamento.biblioteca.api.auth2.Services.ManagementJWT;
import gerenciamento.biblioteca.api.auth2.Services.TokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AtualizacaoTokenTest {
    /*
    Para atualizar o token, será necessário enviar o refreshToken válido e que não esteja vencido ou bloqueado,
    para efetuar isso, é obtido as informações relacionadas a este token presentes no banco.
    Caso o refreshToken esteja expirado, será enviado uma exceção o usuário logar novamente. Se o token encontra-se bloqueado,
    o usuário não poderá acessar mais o site por aquela conta.
    Posteriormente desta validação e com os dados relacionados em mãos, é desenvolvido um novo accessToken.
     */

    @Spy
    private ManagementJWT managementJWT = new ManagementJWT();

    @InjectMocks
    private TokenService tokenService = new TokenService(managementJWT);

    @Mock
    private TokenRepository tokenRepository;

    private final static ObjectMapper mapper = new ObjectMapper();

    @Test
    public void tokenExpirado() {
        when(tokenRepository.findByRefreshToken(anyString())).thenReturn(
                Optional.of(new Token(
                        1,
                        new Usuario(1, "nome", "email", "senha", "telefone"),
                        "refreshToken",
                        Situacao.ATIVO,
                        LocalDateTime.now().minusMinutes(1))));

        Assertions.assertThrows(TokenExpiradoException.class, () -> tokenService.atualizarToken(geradorDeTokenDTO()));
    }

    @Test
    public void tokenBloqueado() {
        when(tokenRepository.findByRefreshToken(anyString())).thenReturn(
                Optional.of(new Token(
                        1,
                        new Usuario(1, "nome", "email", "senha", "telefone"),
                        "refreshToken",
                        Situacao.BLOQUEADO,
                        LocalDateTime.now().plusDays(1))));

        Assertions.assertThrows(UsuarioBloqueadoException.class, () -> tokenService.atualizarToken(geradorDeTokenDTO()));
    }

    @Test
    public void tokenAtualizado() {
        when(tokenRepository.findByRefreshToken(anyString())).thenReturn(
                Optional.of(new Token(
                        1,
                        new Usuario(1, "nome", "email", "senha", "telefone"),
                        "refreshToken",
                        Situacao.ATIVO,
                        LocalDateTime.now().plusDays(1))));

        Assertions.assertDoesNotThrow(() -> tokenService.atualizarToken(geradorDeTokenDTO()));
    }

    private TokenCreatedDTO geradorDeTokenDTO() {
        return managementJWT.criarTokens(
                new CreationTokenDTO(
                        1,
                        "nome",
                        "email",
                        new String[] {String.valueOf(Roles.USER), String.valueOf(Roles.ADMIN)}));
    }
}
