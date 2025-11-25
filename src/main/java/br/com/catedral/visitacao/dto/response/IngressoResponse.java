package br.com.catedral.visitacao.dto.response;

import br.com.catedral.visitacao.enums.StatusIngresso;

import java.time.LocalDateTime;

public class IngressoResponse {

    private Long id;
    private SessaoResponse sessao;
    private Integer numeroIngresso;
    private UsuarioResponse comprador;
    private UsuarioResponse vendedor;
    private String qrCode;
    private StatusIngresso status;
    private LocalDateTime dataCheckin;
    private String descricao;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public SessaoResponse getSessao() { return sessao; }
    public void setSessao(SessaoResponse sessao) { this.sessao = sessao; }

    public Integer getNumeroIngresso() { return numeroIngresso; }
    public void setNumeroIngresso(Integer numeroIngresso) { this.numeroIngresso = numeroIngresso; }

    public UsuarioResponse getComprador() { return comprador; }
    public void setComprador(UsuarioResponse comprador) { this.comprador = comprador; }

    public UsuarioResponse getVendedor() { return vendedor; }
    public void setVendedor(UsuarioResponse vendedor) { this.vendedor = vendedor; }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }

    public StatusIngresso getStatus() { return status; }
    public void setStatus(StatusIngresso status) { this.status = status; }

    public LocalDateTime getDataCheckin() { return dataCheckin; }
    public void setDataCheckin(LocalDateTime dataCheckin) { this.dataCheckin = dataCheckin; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
