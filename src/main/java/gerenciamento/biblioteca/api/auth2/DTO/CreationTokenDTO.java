package gerenciamento.biblioteca.api.auth2.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CreationTokenDTO(int id, String name, String email, String[] roles) {
    @JsonCreator
    public CreationTokenDTO(@JsonProperty("id") int id,
                            @JsonProperty("name") String name,
                            @JsonProperty("email") String email,
                            @JsonProperty("roles") String[] roles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.roles = roles;
    }
}
