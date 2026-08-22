package gerenciamento.biblioteca.api.auth2.Externals;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gerenciamento.biblioteca.api.auth2.Entities.Roles;
import gerenciamento.biblioteca.api.auth2.Entities.Usuario;
import gerenciamento.biblioteca.api.auth2.Expections.RegistroInexistenteException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Component
public class BibliotecaAPI {
    @Value("${EXTERNAL_USER_URL}")
    private String url;
    @Value("${EXTERNAL_USER_API_KEY}")
    private String apiKey;

    private final ObjectMapper mapper = new ObjectMapper();

    public List<Roles> getRolesByUserId(Integer userId) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String requestUrl = this.url + "/api/users/roles/" + userId;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .header("x-api-key", apiKey)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // TypeReference evita erros de casting de coleções/arrays no Jackson
                return mapper.readValue(response.body(), new TypeReference<List<Roles>>() {});
            }

            return List.of();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao comunicar com Biblioteca API (roles): " + e.getMessage(), e);
        }
    }

    public Usuario getUsuarioByEmail(String email) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String requestUrl = this.url + "/api/users/email/" + email;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .header("x-api-key", apiKey)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), Usuario.class);
            }

            throw new RegistroInexistenteException("Não foi possível encontrar o usuário com o email: " + email);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao comunicar com Biblioteca API (usuario): " + e.getMessage(), e);
        }
    }
}