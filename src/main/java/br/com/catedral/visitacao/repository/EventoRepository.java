package br.com.catedral.visitacao.repository;

import br.com.catedral.visitacao.enums.TipoEvento;
import br.com.catedral.visitacao.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByAtivoTrue();
    List<Evento> findByTipoEventoAndAtivoTrue(TipoEvento tipoEvento);
    List<Evento> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome);
}
