package br.com.catedral.visitacao.repository;

import br.com.catedral.visitacao.model.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SessaoRepository extends JpaRepository<Sessao, Long> {

    List<Sessao> findByAtivoTrue();
    List<Sessao> findByEventoIdAndAtivoTrue(Long eventoId);
    List<Sessao> findByGuiaIdAndAtivoTrue(Long guiaId);

    @Query("SELECT s FROM Sessao s WHERE s.ativo = true AND s.dataHora BETWEEN :start AND :end")
    List<Sessao> findSessoesAtivasNoPeriodo(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT s FROM Sessao s WHERE s.ativo = true AND s.dataHora > :now ORDER BY s.dataHora ASC")
    List<Sessao> findProximasSessoes(@Param("now") LocalDateTime now);

    List<Sessao> findByGuiaIdAndDataHoraAfter(Long guiaId, LocalDateTime dataHora);
}
