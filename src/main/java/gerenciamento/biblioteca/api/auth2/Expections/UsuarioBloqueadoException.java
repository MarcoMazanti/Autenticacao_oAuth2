package gerenciamento.biblioteca.api.auth2.Expections;

public class UsuarioBloqueadoException extends RuntimeException {
    public UsuarioBloqueadoException(String message) {
        super(message);
    }
}
