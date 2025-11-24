package br.com.catedral.visitacao.enums;

public enum StatusIngresso {
    ATIVO("Ativo"),
    UTILIZADO("Utilizado"),
    CANCELADO("Cancelado"),
    EXPIRADO("Expirado");

    private final String descricao;

    StatusIngresso(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
