package gerenciamento.biblioteca.api.auth2.Repositories;

import gerenciamento.biblioteca.api.auth2.Entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
}
