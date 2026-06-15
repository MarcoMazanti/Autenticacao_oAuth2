package gerenciamento.biblioteca.api.auth2.Expections;

public class RegistroJaExisteException extends RuntimeException {
    public RegistroJaExisteException(String message) {
        super(message);
    }
}
