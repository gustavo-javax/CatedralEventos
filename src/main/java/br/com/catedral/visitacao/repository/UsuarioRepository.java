package br.com.catedral.visitacao.repository;
import br.com.catedral.visitacao.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);

    @Query("SELECT u FROM Usuario u JOIN u.perfis p WHERE p = :perfil")
    List<Usuario> findByPerfil(@Param("perfil") String perfil);

    List<Usuario> findByAtivoTrue();
}
