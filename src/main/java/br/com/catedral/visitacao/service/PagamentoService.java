package br.com.catedral.visitacao.service;

import br.com.catedral.visitacao.enums.StatusPagamento;
import br.com.catedral.visitacao.model.Pagamento;
import br.com.catedral.visitacao.model.Usuario;
import br.com.catedral.visitacao.repository.PagamentoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service

public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;

    public PagamentoService(PagamentoRepository pagamentoRepository) {
        this.pagamentoRepository = pagamentoRepository;
    }

    public Pagamento criarPagamento(BigDecimal valorTotal, Integer quantidade, String metodoPagamento) {
        Pagamento pagamento = new Pagamento();
        pagamento.setValorTotal(valorTotal);
        pagamento.setQuantidadeIngressos(quantidade);
        pagamento.setMetodoPagamento(metodoPagamento);
        pagamento.setStatus(StatusPagamento.PENDENTE);
        pagamento.setDataPagamento(LocalDateTime.now());
        pagamento.setValorLiquido(valorTotal);

        return pagamentoRepository.save(pagamento);
    }

    public Pagamento aprovarPagamento(Long pagamentoId, String codigoTransacao) {
        Pagamento pagamento = buscarPorId(pagamentoId);
        pagamento.setStatus(StatusPagamento.APROVADO);
        pagamento.setCodigoTransacao(codigoTransacao);

        // Se tem vendedor, calcular comissão
        if (!pagamento.getIngressos().isEmpty() &&
                pagamento.getIngressos().get(0).getVendedor() != null) {

            Usuario vendedor = pagamento.getIngressos().get(0).getVendedor();
            BigDecimal comissao = vendedor.getDadosProfissionais().getPercentualComissao();
            pagamento.calcularComissao(comissao);
        }

        return pagamentoRepository.save(pagamento);
    }

    public Pagamento recusarPagamento(Long pagamentoId) {
        Pagamento pagamento = buscarPorId(pagamentoId);
        pagamento.setStatus(StatusPagamento.RECUSADO);
        return pagamentoRepository.save(pagamento);
    }

    public Pagamento buscarPorId(Long id) {
        return pagamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));
    }

    public List<Pagamento> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return pagamentoRepository.findByDataPagamentoBetween(inicio, fim);
    }

    public BigDecimal calcularTotalVendasPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        List<Pagamento> pagamentos = listarPorPeriodo(inicio, fim);
        return pagamentos.stream()
                .filter(p -> p.getStatus() == StatusPagamento.APROVADO)
                .map(Pagamento::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
