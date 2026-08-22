package gerenciamento.biblioteca.api.auth2.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "usuario")
public class Usuario {
    @Id
    private Integer id;
    @NotNull
    private String nome;
    @NotNull
    private String email;
    @NotNull
    private String senha;
    private String tefelone;

    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public Usuario(String nome, String email, String senha, String tefelone) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tefelone = tefelone;
    }


}
