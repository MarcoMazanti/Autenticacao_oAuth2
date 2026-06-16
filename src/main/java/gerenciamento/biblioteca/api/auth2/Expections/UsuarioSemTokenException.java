package gerenciamento.biblioteca.api.auth2.Expections;

public class UsuarioSemTokenException extends RuntimeException {
    public UsuarioSemTokenException(String message) {
        super(message);
    }
}
