package gerenciamento.biblioteca.api.auth2.DTO.External;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record ErrorResponse(
        Integer status,
        String message,
        LocalDateTime hour) {
    @JsonCreator
    public ErrorResponse(@JsonProperty("status") Integer status,
                         @JsonProperty("message") String message,
                         @JsonProperty("hour") LocalDateTime hour) {
        this.status = status;
        this.message = message;
        this.hour = hour;
    }
}
