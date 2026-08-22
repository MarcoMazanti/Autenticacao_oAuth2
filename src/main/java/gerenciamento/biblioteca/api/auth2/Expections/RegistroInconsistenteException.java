package gerenciamento.biblioteca.api.auth2.Expections;

public class RegistroInconsistenteException extends RuntimeException {
    public RegistroInconsistenteException(String message) {
        super(message);
    }
}
