package gerenciamento.biblioteca.api.auth2.Repositories;

import gerenciamento.biblioteca.api.auth2.Entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
    boolean existsById(int id);
}
