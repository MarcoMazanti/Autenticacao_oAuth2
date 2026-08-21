package gerenciamento.biblioteca.api.auth2.Externals;

import gerenciamento.biblioteca.api.auth2.Entities.Roles;
import gerenciamento.biblioteca.api.auth2.Entities.Usuario;
import gerenciamento.biblioteca.api.auth2.Expections.RegistroInexistenteException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

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
            String url = this.url + "/api/users/roles/" + userId;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("x-api-key", apiKey)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), mapper.getTypeFactory().constructCollectionType(List.class, Roles.class));
            }

            return List.of();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Usuario getUsuarioByEmail(String email) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String url = this.url + "/api/users/email/" + email;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("x-api-key", apiKey)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), mapper.getTypeFactory().constructType(Usuario.class));
            }

            throw new RegistroInexistenteException("Não foi possível encontrar o usuário com o email: " + email + " no banco de dados.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
