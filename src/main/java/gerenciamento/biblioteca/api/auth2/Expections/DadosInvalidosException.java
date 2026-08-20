package gerenciamento.biblioteca.api.auth2.Expections;

public class DadosInvalidosException extends RuntimeException {
    public DadosInvalidosException(String message) {
        super(message);
    }
}
