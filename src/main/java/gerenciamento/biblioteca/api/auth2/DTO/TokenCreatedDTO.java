package gerenciamento.biblioteca.api.auth2.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenCreatedDTO(String accessToken, String refreshToken) {
    @JsonCreator
    public TokenCreatedDTO(@JsonProperty("accessToken") String accessToken,
                           @JsonProperty("refreshToken") String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
