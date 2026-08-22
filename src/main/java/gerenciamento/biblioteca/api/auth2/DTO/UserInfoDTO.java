package gerenciamento.biblioteca.api.auth2.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import gerenciamento.biblioteca.api.auth2.Entities.Roles;

import java.util.List;

public record UserInfoDTO(String sub, String name, String email, Boolean emailVerified, List<Roles> roles) {
    @JsonCreator
    public UserInfoDTO(@JsonProperty("sub") String sub,
                       @JsonProperty("name") String name,
                       @JsonProperty("email") String email,
                       @JsonProperty("emailVerified") Boolean emailVerified,
                       @JsonProperty("roles") List<Roles> roles) {
        this.sub = sub;
        this.name = name;
        this.email = email;
        this.emailVerified = emailVerified;
        this.roles = roles;
    }
}
