package gerenciamento.biblioteca.api.auth2;

import gerenciamento.biblioteca.api.auth2.DTO.CreationTokenDTO;
import gerenciamento.biblioteca.api.auth2.DTO.TokenCreatedDTO;
import gerenciamento.biblioteca.api.auth2.Entities.Roles;
import gerenciamento.biblioteca.api.auth2.Expections.RegistroInconsistenteException;
import gerenciamento.biblioteca.api.auth2.Services.ManagementJWT;
import gerenciamento.biblioteca.api.auth2.Services.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class ValidacaoTokenTest {
    /*
     * O cliente enviará o accessToken para verificar se ele não foi adulterado ou expirado.
     * Posteriormente é garantido que o ‘id’ é positivo e maior que zero.
     */

    @Spy
    private ManagementJWT managementJWT = new ManagementJWT();

    @InjectMocks
    private TokenService tokenService = new TokenService(managementJWT);

    private final static ObjectMapper mapper = new ObjectMapper();
    private final static TokenCreatedDTO[] tokensDTO = geradorDeTokenDTO();

    @ParameterizedTest
    @FieldSource("tokensDTO")
    public void idTokenNegativo(TokenCreatedDTO tokenDTO) {
        String midSplit = tokenDTO.accessToken().split("\\.")[1];
        String payload = new String(Base64.getUrlDecoder().decode(midSplit));

        Map<String, Object> mapa = mapper.readValue(payload, Map.class);
        mapa.put("sub", Integer. parseInt(mapa.get("sub").toString()) * (-1));
        String paylaodMapAdulterado = mapper.writeValueAsString(mapa);
        String paylaodAdulterado = Base64.getUrlEncoder().withoutPadding().encodeToString(paylaodMapAdulterado.getBytes());

        String accessTokenAdulterado = String.format("%s.%s.%s",
                tokenDTO.accessToken().split("\\.")[0],
                paylaodAdulterado,
                tokenDTO.accessToken().split("\\.")[2]);

        Assertions.assertThrows(RegistroInconsistenteException.class, () -> tokenService.validarToken(accessTokenAdulterado));
    }

    @Test
    public void TokenExpirado() throws InterruptedException {
        managementJWT = new ManagementJWT(1);

        TokenCreatedDTO tokenDTO = managementJWT.criarTokens(
                new CreationTokenDTO(1, "nome1", "email1", new String[] {String.valueOf(Roles.USER)}));
        Thread.sleep(10);

        Assertions.assertThrows(ExpiredJwtException.class, () -> tokenService.validarToken(tokenDTO.accessToken()));
    }

    @Test
    public void TokenAdulterado() {
        TokenCreatedDTO tokenDTO = managementJWT.criarTokens(
                new CreationTokenDTO(1, "nome1", "email1", new String[] {String.valueOf(Roles.USER)}));

        String midSplit = new String(Base64.getUrlDecoder().decode(tokenDTO.accessToken().split("\\.")[1]));
        midSplit.replace("USER", "ADMIN");

        String accessTokenAdulterado = tokenDTO.accessToken().split("\\.")[0] +
                Base64.getUrlEncoder().encodeToString(midSplit.getBytes()) +
                tokenDTO.accessToken().split("\\.")[2];

        Assertions.assertThrows(RegistroInconsistenteException.class, () -> tokenService.validarToken(accessTokenAdulterado));
    }

    @Test
    public void TokenValido() {
        TokenCreatedDTO tokenDTO = managementJWT.criarTokens(
                new CreationTokenDTO(1, "nome1", "email1", new String[] {String.valueOf(Roles.USER)}));

        Assertions.assertDoesNotThrow(() -> tokenService.validarToken(tokenDTO.accessToken()));
    }

    private static TokenCreatedDTO[] geradorDeTokenDTO() {
        ManagementJWT managementJWT_Gerador = new ManagementJWT();
        return new TokenCreatedDTO[] {
                managementJWT_Gerador.criarTokens(new CreationTokenDTO(1, "nome1", "email1", new String[] {String.valueOf(Roles.USER)})),
                managementJWT_Gerador.criarTokens(new CreationTokenDTO(2, "nome2", "email2", new String[] {String.valueOf(Roles.ADMIN)}))
        };
    }
}
