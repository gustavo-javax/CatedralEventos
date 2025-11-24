package br.com.catedral.visitacao.model;

import br.com.catedral.visitacao.enums.StatusIngresso;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "ingresso")
public class Ingresso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Sessão é obrigatória")
    @ManyToOne
    @JoinColumn(name = "sessao_id", nullable = false)
    private Sessao sessao;

    @NotNull(message = "Número do ingresso é obrigatório")
    @Column(name = "numero_ingresso", nullable = false)
    private Integer numeroIngresso;

    @NotNull(message = "Comprador é obrigatório")
    @ManyToOne
    @JoinColumn(name = "comprador_id", nullable = false)
    private Usuario comprador;

    @ManyToOne
    @JoinColumn(name = "vendedor_id")
    private Usuario vendedor;

    @NotNull(message = "Pagamento é obrigatório")
    @ManyToOne
    @JoinColumn(name = "pagamento_id", nullable = false)
    private Pagamento pagamento;

    @Column(name = "qr_code", unique = true, nullable = false)
    private String qrCode;

    @NotNull(message = "Status é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusIngresso status = StatusIngresso.ATIVO;

    @Column(name = "data_checkin")
    private LocalDateTime dataCheckin;

    // Construtores
    public Ingresso() {}

    public Ingresso(Sessao sessao, Integer numeroIngresso, Usuario comprador, Pagamento pagamento, String qrCode) {
        this.sessao = sessao;
        this.numeroIngresso = numeroIngresso;
        this.comprador = comprador;
        this.pagamento = pagamento;
        this.qrCode = qrCode;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Sessao getSessao() { return sessao; }
    public void setSessao(Sessao sessao) { this.sessao = sessao; }

    public Integer getNumeroIngresso() { return numeroIngresso; }
    public void setNumeroIngresso(Integer numeroIngresso) { this.numeroIngresso = numeroIngresso; }

    public Usuario getComprador() { return comprador; }
    public void setComprador(Usuario comprador) { this.comprador = comprador; }

    public Usuario getVendedor() { return vendedor; }
    public void setVendedor(Usuario vendedor) { this.vendedor = vendedor; }

    public Pagamento getPagamento() { return pagamento; }
    public void setPagamento(Pagamento pagamento) { this.pagamento = pagamento; }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }

    public StatusIngresso getStatus() { return status; }
    public void setStatus(StatusIngresso status) { this.status = status; }

    public LocalDateTime getDataCheckin() { return dataCheckin; }
    public void setDataCheckin(LocalDateTime dataCheckin) { this.dataCheckin = dataCheckin; }

    public boolean isVendaDireta() {
        return vendedor == null;
    }

    public String getDescricao() {
        return String.format("Ingresso %d/%d - %s",
                numeroIngresso,
                pagamento.getQuantidadeIngressos(),
                sessao.getEvento().getNome());
    }

    public boolean podeSerUtilizado() {
        return status == StatusIngresso.ATIVO &&
                sessao.getDataHora().isAfter(LocalDateTime.now().minusHours(1));
    }
}
