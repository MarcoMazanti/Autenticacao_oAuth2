package gerenciamento.biblioteca.api.auth2.Expections;

public class TokenExpiradoException extends RuntimeException {
    public TokenExpiradoException(String message) {
        super(message);
    }
}
