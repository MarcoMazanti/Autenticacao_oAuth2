package gerenciamento.biblioteca.api.auth2.Services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class GoogleAuthService {
    @Value("${google.client.id}")
    private String googleClientId;

    public GoogleIdToken.Payload validarEExtrairPayload(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken == null) {
                throw new IllegalArgumentException("Token do Google inválido, expirado ou com audience incorreta.");
            }

            return idToken.getPayload();
        } catch (Exception e) {
            throw new RuntimeException("Falha na validação do token do Google", e);
        }
    }
}
