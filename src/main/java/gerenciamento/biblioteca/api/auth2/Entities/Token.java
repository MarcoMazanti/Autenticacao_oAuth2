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
    @OneToOne(cascade = CascadeType.MERGE)
    @MapsId
    @JoinColumn(name = "id")
    private Usuario usuario;
    @NotNull
    private String refreshToken;
    @Column(name = "situacao", length = 9)
    @Enumerated(EnumType.STRING)
    private Situacao situacao = Situacao.ATIVO;
    private LocalDateTime dataExpiracao = LocalDateTime.now().plusDays(7);

    public Token(Integer id, String refreshToken) {
        this.id = id;
        this.refreshToken = refreshToken;
    }

    public Token(Integer id, Usuario usuario, String refreshToken) {
        this.id = id;
        this.usuario = usuario;
        this.refreshToken = refreshToken;
    }

    public Token(Integer id, String refreshToken, Situacao situacao) {
        this.id = id;
        this.refreshToken = refreshToken;
        this.situacao = situacao;
    }
}
