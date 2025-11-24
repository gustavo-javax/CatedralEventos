package br.com.catedral.visitacao.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable

public class DadosProfissionais {
    // DADOS DE GUIA
    @Column(name = "especializacao_guia", length = 200)
    private String especializacaoGuia;

    @Column(name = "biografia_guia", columnDefinition = "TEXT")
    private String biografiaGuia;

    @Column(name = "avaliacao_guia", precision = 3, scale = 2)
    private BigDecimal avaliacaoGuia;

    @Column(name = "guia_ativo")
    private boolean guiaAtivo = false;

    // DADOS DE VENDEDOR
    @Column(name = "razao_social", length = 200)
    private String razaoSocial;

    @Column(name = "cnpj", length = 14)
    private String cnpj;

    @Column(name = "percentual_comissao", precision = 5, scale = 2)
    private BigDecimal percentualComissao = BigDecimal.ZERO;

    @Column(name = "vendedor_ativo")
    private boolean vendedorAtivo = false;

    // Getters e Setters
    public String getEspecializacaoGuia() { return especializacaoGuia; }
    public void setEspecializacaoGuia(String especializacaoGuia) { this.especializacaoGuia = especializacaoGuia; }

    public String getBiografiaGuia() { return biografiaGuia; }
    public void setBiografiaGuia(String biografiaGuia) { this.biografiaGuia = biografiaGuia; }

    public BigDecimal getAvaliacaoGuia() { return avaliacaoGuia; }
    public void setAvaliacaoGuia(BigDecimal avaliacaoGuia) { this.avaliacaoGuia = avaliacaoGuia; }

    public boolean isGuiaAtivo() { return guiaAtivo; }
    public void setGuiaAtivo(boolean guiaAtivo) { this.guiaAtivo = guiaAtivo; }

    public String getRazaoSocial() { return razaoSocial; }
    public void setRazaoSocial(String razaoSocial) { this.razaoSocial = razaoSocial; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public BigDecimal getPercentualComissao() { return percentualComissao; }
    public void setPercentualComissao(BigDecimal percentualComissao) { this.percentualComissao = percentualComissao; }

    public boolean isVendedorAtivo() { return vendedorAtivo; }
    public void setVendedorAtivo(boolean vendedorAtivo) { this.vendedorAtivo = vendedorAtivo; }
}

