package gerenciamento.biblioteca.api.auth2;

import gerenciamento.biblioteca.api.auth2.DTO.CreationTokenDTO;
import gerenciamento.biblioteca.api.auth2.DTO.TokenCreatedDTO;
import gerenciamento.biblioteca.api.auth2.Entities.Situacao;
import gerenciamento.biblioteca.api.auth2.Entities.Token;
import gerenciamento.biblioteca.api.auth2.Entities.Usuario;
import gerenciamento.biblioteca.api.auth2.Repositories.TokenRepository;
import gerenciamento.biblioteca.api.auth2.Repositories.UsuarioRepository;
import gerenciamento.biblioteca.api.auth2.Services.ManagementJWT;
import gerenciamento.biblioteca.api.auth2.Services.TokenService;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ValidacaoTokenTest {
    /*
     * O cliente enviará o accessToken para verificar se ele não foi adulterado.
     * Para verificar a adulteração, será obtido os dados do token e será pesquisado no banco de dados, se o mesmo existir.
     * Se não foi adulterado, é retornado verdadeiro, caso contrário, retorna falso.
     */

    @Spy
    private ManagementJWT managementJWT = new ManagementJWT();

    @InjectMocks
    private TokenService tokenService = new TokenService(managementJWT);

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private final static List<Usuario> listaDeUsuarios = new ArrayList<>();
    private final static List<Token> listaDeTokens = new ArrayList<>();

    @BeforeAll
    public static void start() {
        ObjectMapper mapper = new ObjectMapper();

        for (var item : geradorDeTokenDTO()) {
            String midSplit = item.accessToken().split("\\.")[1];
            String payload = new String(Base64.getUrlDecoder().decode(midSplit));

            JsonNode payloadJSON = mapper.readTree(payload);
            int id = payloadJSON.has("sub") ? payloadJSON.get("sub").asInt() : 0;
            String nome = payloadJSON.has("name") ? payloadJSON.get("name").asString() : "";
            String email = payloadJSON.has("email") ? payloadJSON.get("email").asString() : "";

            Usuario usuario = new Usuario(id, nome, email, "senha","telefone");

            listaDeUsuarios.add(usuario);
            listaDeTokens.add(new Token(id, usuario, item.refreshToken()));
        }
    }

    @ParameterizedTest
    @MethodSource("geradorDeTokenDTO")
    public void tokenAdulterado(TokenCreatedDTO tokenDTO) {

    }

    private static TokenCreatedDTO[] geradorDeTokenDTO() {
        ManagementJWT managementJWT_Gerador = new ManagementJWT();
        return new TokenCreatedDTO[] {
                managementJWT_Gerador.criarTokens(new CreationTokenDTO(1, "nome", "email", new String[] {"ROLE_USER"})),
                managementJWT_Gerador.criarTokens(new CreationTokenDTO(2, "nome", "email", new String[] {"ROLE_ADMIN"}))
        };
    }
}
