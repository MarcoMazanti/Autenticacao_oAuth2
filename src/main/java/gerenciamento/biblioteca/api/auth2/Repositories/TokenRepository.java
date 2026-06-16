package gerenciamento.biblioteca.api.auth2.Repositories;

import gerenciamento.biblioteca.api.auth2.Entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
    boolean existsById(int id);
    Optional<Token> findByRefreshToken(String refreshToken);
    Optional<Token> findByUsuarioId(int id);
}
