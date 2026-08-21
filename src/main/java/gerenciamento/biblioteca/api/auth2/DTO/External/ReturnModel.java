package gerenciamento.biblioteca.api.auth2.DTO.External;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReturnModel(
        Integer status,
        String path,
        boolean success,
        Integer quantity,
        List<?> itens,
        ErrorResponse errors) {
    @JsonCreator
    public ReturnModel(@JsonProperty("status") Integer status,
                       @JsonProperty("path") String path,
                       @JsonProperty("success") boolean success,
                       @JsonProperty("quantity") Integer quantity,
                       @JsonProperty("itens") List<?> itens,
                       @JsonProperty("errors") ErrorResponse errors) {
        this.status = status;
        this.path = path;
        this.success = success;
        this.quantity = quantity;
        this.itens = itens;
        this.errors = errors;
    }
}