package br.com.catedral.visitacao.dto.response;

public class MensagemResponse {

    private String mensagem;

    public MensagemResponse(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMensagem() { return mensagem; }
}
