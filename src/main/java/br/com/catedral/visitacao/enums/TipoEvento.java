package br.com.catedral.visitacao.enums;

public enum TipoEvento {
    MISSA("Missa"),
    CONCERTO("Concerto"),
    VISITA_GUIADA("Visita Guiada"),
    CASAMENTO("Casamento"),
    BATIZADO("Batizado"),
    EXPOSICAO("Exposição"),
    PALESTRA("Palestra"),
    ESPECIAL("Evento Especial");

    private final String descricao;

    TipoEvento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

}
