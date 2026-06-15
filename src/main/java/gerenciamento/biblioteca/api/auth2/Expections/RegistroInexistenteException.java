package gerenciamento.biblioteca.api.auth2.Expections;

public class RegistroInexistenteException extends RuntimeException {
    public RegistroInexistenteException(String message) {
        super(message);
    }
}
