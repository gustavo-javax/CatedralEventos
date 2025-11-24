package br.com.catedral.visitacao.model;

import br.com.catedral.visitacao.enums.StatusPagamento;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pagamento")

public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Valor total é obrigatório")
    @Column(name = "valor_total", precision = 10, scale = 2, nullable = false)
    private BigDecimal valorTotal;

    @NotNull(message = "Quantidade é obrigatória")
    @Column(name = "quantidade_ingressos", nullable = false)
    private Integer quantidadeIngressos;

    @Column(name = "comissao_vendedor", precision = 10, scale = 2)
    private BigDecimal comissaoVendedor = BigDecimal.ZERO;

    @Column(name = "valor_liquido", precision = 10, scale = 2)
    private BigDecimal valorLiquido;

    @NotNull(message = "Data do pagamento é obrigatória")
    @Column(name = "data_pagamento", nullable = false)
    private LocalDateTime dataPagamento = LocalDateTime.now();

    @NotNull(message = "Status é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPagamento status = StatusPagamento.PENDENTE;

    @Column(name = "metodo_pagamento", length = 50)
    private String metodoPagamento;

    @Column(name = "codigo_transacao", length = 100)
    private String codigoTransacao;

    @OneToMany(mappedBy = "pagamento", cascade = CascadeType.ALL)
    private List<Ingresso> ingressos = new ArrayList<>();

    // Construtores
    public Pagamento() {}

    public Pagamento(BigDecimal valorTotal, Integer quantidadeIngressos, String metodoPagamento) {
        this.valorTotal = valorTotal;
        this.quantidadeIngressos = quantidadeIngressos;
        this.metodoPagamento = metodoPagamento;
        this.valorLiquido = valorTotal;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }

    public Integer getQuantidadeIngressos() { return quantidadeIngressos; }
    public void setQuantidadeIngressos(Integer quantidadeIngressos) { this.quantidadeIngressos = quantidadeIngressos; }

    public BigDecimal getComissaoVendedor() { return comissaoVendedor; }
    public void setComissaoVendedor(BigDecimal comissaoVendedor) { this.comissaoVendedor = comissaoVendedor; }

    public BigDecimal getValorLiquido() { return valorLiquido; }
    public void setValorLiquido(BigDecimal valorLiquido) { this.valorLiquido = valorLiquido; }

    public LocalDateTime getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDateTime dataPagamento) { this.dataPagamento = dataPagamento; }

    public StatusPagamento getStatus() { return status; }
    public void setStatus(StatusPagamento status) { this.status = status; }

    public String getMetodoPagamento() { return metodoPagamento; }
    public void setMetodoPagamento(String metodoPagamento) { this.metodoPagamento = metodoPagamento; }

    public String getCodigoTransacao() { return codigoTransacao; }
    public void setCodigoTransacao(String codigoTransacao) { this.codigoTransacao = codigoTransacao; }

    public List<Ingresso> getIngressos() { return ingressos; }
    public void setIngressos(List<Ingresso> ingressos) { this.ingressos = ingressos; }

    public void calcularComissao(BigDecimal percentualComissao) {
        if (percentualComissao != null && percentualComissao.compareTo(BigDecimal.ZERO) > 0) {
            this.comissaoVendedor = valorTotal.multiply(percentualComissao)
                    .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
            this.valorLiquido = valorTotal.subtract(comissaoVendedor);
        } else {
            this.comissaoVendedor = BigDecimal.ZERO;
            this.valorLiquido = valorTotal;
        }
    }

    public boolean isAprovado() {
        return status == StatusPagamento.APROVADO;
    }

    public boolean isPendente() {
        return status == StatusPagamento.PENDENTE;
    }
}
