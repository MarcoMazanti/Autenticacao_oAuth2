package gerenciamento.biblioteca.api.auth2.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "token")
public class Token {
    @Id
    @Column(name = "id")
    private Integer id;
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Usuario usuario;
    @NotNull
    private String refreshToken;
    @Column(nullable = false, columnDefinition = "VARCHAR(9) DEFAULT 'ATIVO'")
    @Enumerated(EnumType.STRING)
    private Situacao situacao;
    private LocalDateTime dataExpiracao;

    public Token(Integer id, String refreshToken) {
        this.id = id;
        this.refreshToken = refreshToken;
    }

    public Token(Integer id, String refreshToken, Situacao situacao) {
        this.id = id;
        this.refreshToken = refreshToken;
        this.situacao = situacao;
    }
}
