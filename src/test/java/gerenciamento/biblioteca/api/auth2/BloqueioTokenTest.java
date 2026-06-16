package gerenciamento.biblioteca.api.auth2;

import gerenciamento.biblioteca.api.auth2.Repositories.TokenRepository;
import gerenciamento.biblioteca.api.auth2.Repositories.UsuarioRepository;
import gerenciamento.biblioteca.api.auth2.Services.ManagementJWT;
import gerenciamento.biblioteca.api.auth2.Services.TokenService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private UsuarioRepository usuarioRepository;
}
