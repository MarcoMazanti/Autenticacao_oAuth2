package gerenciamento.biblioteca.api.auth2;

import gerenciamento.biblioteca.api.auth2.Entities.Roles;
import gerenciamento.biblioteca.api.auth2.Entities.Situacao;
import gerenciamento.biblioteca.api.auth2.Entities.Token;
import gerenciamento.biblioteca.api.auth2.Entities.Usuario;
import gerenciamento.biblioteca.api.auth2.Expections.AlteracaoNegadaException;
import gerenciamento.biblioteca.api.auth2.Expections.DadosInvalidosException;
import gerenciamento.biblioteca.api.auth2.Expections.RegistroInexistenteException;
import gerenciamento.biblioteca.api.auth2.Expections.UsuarioSemTokenException;
import gerenciamento.biblioteca.api.auth2.Externals.BibliotecaAPI;
import gerenciamento.biblioteca.api.auth2.Repositories.TokenRepository;
import gerenciamento.biblioteca.api.auth2.Services.ManagementJWT;
import gerenciamento.biblioteca.api.auth2.Services.TokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class BloqueioTokenTest {
    /*
    Para efetuar o login, será necessário que o usuário esteja logado e que possua o cargo ADMIN, se não houver, a requisição é cancelada.
    Ao efetuar essa ação, será mudado o status do token da seguinte forma:
        No token não é vencido e desbloqueado → BLOQUEADO.
        No token não é vencido e bloqueado → ATIVO.
        No token vencido e desbloqueado → INATIVO.
        No token vencido e bloqueado → INATIVO.
     */

    @Spy
    private ManagementJWT managementJWT = new ManagementJWT();

    @InjectMocks
    private TokenService tokenService = new TokenService(managementJWT);

    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private BibliotecaAPI bibliotecaAPI;

    @ParameterizedTest
    @CsvSource(value = {"0, 0",
            "0, 1",
            "1, 0",
            "-1, -1",
            "-1, 1",
            "1, -1"})
    public void idNegativo(int idRequerinte, int idUserAlvo) {
        // WHEN AND THEN
        Assertions.assertThrows(DadosInvalidosException.class, () -> tokenService.atualizarSituacao(idRequerinte, idUserAlvo));
    }

    // IDs iguais
    @ParameterizedTest
    @CsvSource(value = {"1, 1",
            "2, 2",
            "3, 3",
            "1000, 1000",
            "10001000, 10001000",
            "1876434658, 1876434658"})
    public void idsIguais(int idRequerinte, int idUserAlvo) {
        // WHEN AND THEN
        Assertions.assertThrows(AlteracaoNegadaException.class, () -> tokenService.atualizarSituacao(idRequerinte, idUserAlvo));
    }

    // Requerinte sem Roles
    @ParameterizedTest
    @CsvSource(value = {"1, 2"})
    public void requerinteSemRoles(int idRequerinte, int idUserAlvo) {
        // GIVEN
        given(bibliotecaAPI.getRolesByUserId(anyInt())).willReturn(List.of());

        // WHEN AND THEN
        Assertions.assertThrows(RegistroInexistenteException.class, () -> tokenService.atualizarSituacao(idRequerinte, idUserAlvo));
    }

    // Requerinte com USER de Roles
    @ParameterizedTest
    @CsvSource(value = {"1, 2"})
    public void requerinteComUSERdeRoles(int idRequerinte, int idUserAlvo) {
        // GIVEN
        given(bibliotecaAPI.getRolesByUserId(anyInt())).willReturn(List.of(Roles.USER));

        // WHEN AND THEN
        Assertions.assertThrows(AlteracaoNegadaException.class, () -> tokenService.atualizarSituacao(idRequerinte, idUserAlvo));
    }

    // Usuário alvo sem token
    @ParameterizedTest
    @CsvSource(value = {"1, 2"})
    public void usuarioAlvoSemToken(int idRequerinte, int idUserAlvo) {
        // GIVEN
        given(bibliotecaAPI.getRolesByUserId(anyInt())).willReturn(List.of(Roles.ADMIN));
        given(tokenRepository.findByUsuarioId(anyInt())).willReturn(Optional.empty());

        // WHEN AND THEN
        Assertions.assertThrows(UsuarioSemTokenException.class, () -> tokenService.atualizarSituacao(idRequerinte, idUserAlvo));
    }

    // Situacao diferente de BLOQUEADO
    @ParameterizedTest
    @CsvSource(value = {"1, 2"})
    public void situacaoDiferenteDeBLOQUEADO(int idRequerinte, int idUserAlvo) {
        // GIVEN
        given(bibliotecaAPI.getRolesByUserId(anyInt())).willReturn(List.of(Roles.ADMIN));
        given(tokenRepository.findByUsuarioId(anyInt()))
                .willReturn(Optional.of(new Token(
                        1,
                        new Usuario(1, "nome", "email", "senha", "telefone"),
                        "refreshToken",
                        Situacao.ATIVO,
                        LocalDateTime.now().minusMinutes(1))))
                .willReturn(Optional.of(new Token(
                        2,
                        new Usuario(2, "nome", "email", "senha", "telefone"),
                        "refreshToken",
                        Situacao.ATIVO,
                        LocalDateTime.now().minusMinutes(1))));

        ArgumentCaptor<Token> captor = ArgumentCaptor.forClass(Token.class);

        // WHEN
        Situacao situacaoUm = tokenService.atualizarSituacao(idRequerinte, idUserAlvo);
        Situacao situacaoDois = tokenService.atualizarSituacao(idRequerinte, idUserAlvo);

        // THEN
        verify(tokenRepository, times(2)).save(captor.capture());

        Assertions.assertEquals(Situacao.BLOQUEADO, situacaoUm);
        Assertions.assertEquals(Situacao.BLOQUEADO, situacaoDois);

        verify(tokenRepository, times(2)).save(any(Token.class));

        Assertions.assertEquals(Situacao.BLOQUEADO, captor.getAllValues().get(0).getSituacao());
        Assertions.assertEquals(Situacao.BLOQUEADO, captor.getAllValues().get(1).getSituacao());
    }

    // Situacao igual a BLOQUEADO
    @Nested
    public class SituacaoBLOQUEADO {
        // Token Expirado
        @ParameterizedTest
        @CsvSource(value = {"1, 2"})
        public void tokenExpirado(int idRequerinte, int idUserAlvo) {
            // GIVEN
            given(bibliotecaAPI.getRolesByUserId(anyInt())).willReturn(List.of(Roles.ADMIN));
            given(tokenRepository.findByUsuarioId(anyInt()))
                    .willReturn(Optional.of(new Token(
                            1,
                            new Usuario(1, "nome", "email", "senha", "telefone"),
                            "refreshToken",
                            Situacao.BLOQUEADO,
                            LocalDateTime.now().minusMinutes(1))));

            ArgumentCaptor<Token> captor = ArgumentCaptor.forClass(Token.class);

            // WHEN
            Situacao situacao = tokenService.atualizarSituacao(idRequerinte, idUserAlvo);

            // THEN
            verify(tokenRepository).save(captor.capture());

            Assertions.assertEquals(Situacao.INATIVO, captor.getValue().getSituacao());
            Assertions.assertEquals(Situacao.INATIVO, situacao);
        }

        // Token Nao Expirado
        @ParameterizedTest
        @CsvSource(value = {"1, 2"})
        public void tokenNaoExpirado(int idRequerinte, int idUserAlvo) {
            // GIVEN
            given(bibliotecaAPI.getRolesByUserId(anyInt())).willReturn(List.of(Roles.ADMIN));
            given(tokenRepository.findByUsuarioId(anyInt()))
                    .willReturn(Optional.of(new Token(
                            1,
                            new Usuario(1, "nome", "email", "senha", "telefone"),
                            "refreshToken",
                            Situacao.BLOQUEADO, // CORRIGIDO: O token inicial deve ser BLOQUEADO
                            LocalDateTime.now().plusMinutes(1)))); // CORRIGIDO: Não expirado (no futuro)

            ArgumentCaptor<Token> captor = ArgumentCaptor.forClass(Token.class);

            // WHEN
            Situacao situacao = tokenService.atualizarSituacao(idRequerinte, idUserAlvo);

            // THEN
            verify(tokenRepository).save(captor.capture());

            Assertions.assertEquals(Situacao.ATIVO, captor.getValue().getSituacao());
            Assertions.assertEquals(Situacao.ATIVO, situacao);
        }
    }
}
