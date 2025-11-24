package br.com.catedral.visitacao.repository;

import br.com.catedral.visitacao.enums.StatusPagamento;
import br.com.catedral.visitacao.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    List<Pagamento> findByStatus(StatusPagamento status);
    List<Pagamento> findByDataPagamentoBetween(LocalDateTime inicio, LocalDateTime fim);
}
