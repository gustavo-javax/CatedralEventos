package br.com.catedral.visitacao.repository;

import br.com.catedral.visitacao.enums.StatusIngresso;
import br.com.catedral.visitacao.model.Ingresso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngressoRepository extends JpaRepository<Ingresso, Long> {
    Optional<Ingresso> findByQrCode(String qrCode);
    List<Ingresso> findByCompradorId(Long compradorId);
    List<Ingresso> findBySessaoId(Long sessaoId);
    List<Ingresso> findByVendedorId(Long vendedorId);
    List<Ingresso> findByStatus(StatusIngresso status);

    @Query("SELECT i FROM Ingresso i WHERE i.sessao.id = :sessaoId AND i.status = :status")
    List<Ingresso> findBySessaoIdAndStatus(@Param("sessaoId") Long sessaoId, @Param("status") StatusIngresso status);

    @Query("SELECT COUNT(i) FROM Ingresso i WHERE i.sessao.id = :sessaoId AND i.status IN :statuses")
    Long countBySessaoIdAndStatusIn(@Param("sessaoId") Long sessaoId, @Param("statuses") List<StatusIngresso> statuses);

    boolean existsByQrCode(String qrCode);
}
