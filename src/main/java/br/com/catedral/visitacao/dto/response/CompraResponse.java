package br.com.catedral.visitacao.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class CompraResponse {

    private Long pagamentoId;
    private String codigoTransacao;
    private BigDecimal valorTotal;
    private Integer quantidadeIngressos;
    private String status;
    private List<IngressoResponse> ingressos;
    private String mensagem;

    public Long getPagamentoId() { return pagamentoId; }
    public void setPagamentoId(Long pagamentoId) { this.pagamentoId = pagamentoId; }

    public String getCodigoTransacao() { return codigoTransacao; }
    public void setCodigoTransacao(String codigoTransacao) { this.codigoTransacao = codigoTransacao; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }

    public Integer getQuantidadeIngressos() { return quantidadeIngressos; }
    public void setQuantidadeIngressos(Integer quantidadeIngressos) { this.quantidadeIngressos = quantidadeIngressos; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<IngressoResponse> getIngressos() { return ingressos; }
    public void setIngressos(List<IngressoResponse> ingressos) { this.ingressos = ingressos; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
}
