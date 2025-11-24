package br.com.catedral.visitacao.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "sessao")

public class Sessao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Evento é obrigatório")
    @ManyToOne
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @NotNull(message = "Data e hora são obrigatórias")
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @NotNull(message = "Preço é obrigatório")
    @Column(nullable = false)
    private Double preco;

    @NotNull(message = "Capacidade é obrigatória")
    @Column(nullable = false)
    private Integer capacidade;

    @ManyToOne
    @JoinColumn(name = "guia_id")
    private Usuario guia;

    @Column(nullable = false)
    private boolean ativo = true;

    // Construtores
    public Sessao() {}

    public Sessao(Evento evento, LocalDateTime dataHora, Double preco, Integer capacidade) {
        this.evento = evento;
        this.dataHora = dataHora;
        this.preco = preco;
        this.capacidade = capacidade;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Evento getEvento() { return evento; }
    public void setEvento(Evento evento) { this.evento = evento; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }

    public Integer getCapacidade() { return capacidade; }
    public void setCapacidade(Integer capacidade) { this.capacidade = capacidade; }

    public Usuario getGuia() { return guia; }
    public void setGuia(Usuario guia) { this.guia = guia; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public boolean temGuia() {
        return guia != null && guia.isGuia();
    }

    public String getDescricaoCompleta() {
        return String.format("%s - %s", evento.getNome(), dataHora.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
    }
}
