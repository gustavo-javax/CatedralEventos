package br.com.catedral.visitacao.dto.response;

import br.com.catedral.visitacao.enums.Perfil;

import java.util.Set;

public class LoginResponse {

    private String token;
    private String tipoToken = "Bearer";
    private Long usuarioId;
    private String nome;
    private String email;
    private Set<Perfil> perfis;

    public LoginResponse(String token, Long usuarioId, String nome, String email, Set<Perfil> perfis) {
        this.token = token;
        this.usuarioId = usuarioId;
        this.nome = nome;
        this.email = email;
        this.perfis = perfis;
    }

    public String getToken() { return token; }
    public String getTipoToken() { return tipoToken; }
    public Long getUsuarioId() { return usuarioId; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public Set<Perfil> getPerfis() { return perfis; }
}
