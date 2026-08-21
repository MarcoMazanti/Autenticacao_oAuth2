package gerenciamento.biblioteca.api.auth2.Configurations;

import gerenciamento.biblioteca.api.auth2.DTO.External.ErrorResponse;
import gerenciamento.biblioteca.api.auth2.Expections.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GloblaExpectionHandler {
    // Adicionar os demais possíveis erros aqui

    // Erro 400 - Bad Request
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(TokenExpiradoException.class)
    public ResponseEntity<Object> handleTokenExpiradoException(TokenExpiradoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(DadosInvalidosException.class)
    public ResponseEntity<Object> handleDadosInvalidosException(DadosInvalidosException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(RegistroJaExisteException.class)
    public ResponseEntity<Object> handleRegistroJaExisteException(RegistroJaExisteException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), LocalDateTime.now()));
    }

    // Erro 401 - Unauthorized
    @ExceptionHandler(UsuarioSemTokenException.class)
    public ResponseEntity<Object> handleUsuarioSemTokenException(UsuarioSemTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), LocalDateTime.now()));
    }

    // Erro 403 - Forbidden
    @ExceptionHandler(UsuarioBloqueadoException.class)
    public ResponseEntity<Object> handleUsuarioBloqueadoException(UsuarioBloqueadoException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Object> handleJwtException(JwtException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage(), LocalDateTime.now()));
    }

    // Erro 404 - Not Found
    @ExceptionHandler(RegistroInexistenteException.class)
    public ResponseEntity<Object> handleRegistroInexistenteException(RegistroInexistenteException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(), LocalDateTime.now()));
    }

    // Erro 406 - Not Acceptable
    @ExceptionHandler(AlteracaoNegadaException.class)
    public ResponseEntity<Object> handleAlteracaoNegadaException(AlteracaoNegadaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body(new ErrorResponse(HttpStatus.NOT_ACCEPTABLE.value(), ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(RegistroInconsistenteException.class)
    public ResponseEntity<Object> handleRegistroInconsistenteException(RegistroInconsistenteException ex) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body(new ErrorResponse(HttpStatus.NOT_ACCEPTABLE.value(), ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Object> handleExpiredJwtException(ExpiredJwtException ex) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body(new ErrorResponse(HttpStatus.NOT_ACCEPTABLE.value(), ex.getMessage(), LocalDateTime.now()));
    }

    // Erro 500 - Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), LocalDateTime.now()));
    }
}
