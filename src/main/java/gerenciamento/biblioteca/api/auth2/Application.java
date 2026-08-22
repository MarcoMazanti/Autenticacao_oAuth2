package gerenciamento.biblioteca.api.auth2;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
public class Application {
	private static final Dotenv dotenv = Dotenv.load();
	public static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(dotenv.get("SECRET_KEY").getBytes(StandardCharsets.UTF_8));

	public static void main(String[] args) {
		try {
			dotenv.entries().forEach(entry ->
					System.setProperty(entry.getKey(), entry.getValue())
			);
			System.out.println("Variáveis do .env carregadas com sucesso na JVM.");
		} catch (Exception e) {
			System.out.println("Arquivo .env não encontrado. Utilizando variáveis de ambiente do sistema ou application.properties.");
		}

		SpringApplication.run(Application.class, args);
	}
}
