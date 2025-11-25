package br.com.catedral.visitacao.dto.response;

import java.time.LocalDateTime;

public class SessaoResponse {

    private Long id;
    private EventoResponse evento;
    private LocalDateTime dataHora;
    private Double preco;
    private Integer capacidade;
    private UsuarioResponse guia;
    private boolean ativo;
    private Integer vagasDisponiveis;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EventoResponse getEvento() { return evento; }
    public void setEvento(EventoResponse evento) { this.evento = evento; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }

    public Integer getCapacidade() { return capacidade; }
    public void setCapacidade(Integer capacidade) { this.capacidade = capacidade; }

    public UsuarioResponse getGuia() { return guia; }
    public void setGuia(UsuarioResponse guia) { this.guia = guia; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public Integer getVagasDisponiveis() { return vagasDisponiveis; }
    public void setVagasDisponiveis(Integer vagasDisponiveis) { this.vagasDisponiveis = vagasDisponiveis; }
}
