package br.com.catedral.visitacao.controller;

import br.com.catedral.visitacao.dto.response.UsuarioResponse;
import br.com.catedral.visitacao.model.Usuario;
import br.com.catedral.visitacao.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")

public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/perfil")
    public ResponseEntity<UsuarioResponse> meuPerfil(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(toResponse(usuario));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioResponse>> listarTodos() {
        List<Usuario> usuarios = usuarioService.listarTodos();
        List<UsuarioResponse> responses = usuarios.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(toResponse(usuario));
    }

    @PutMapping("/{id}/perfis")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> atualizarPerfis(@PathVariable Long id, @RequestBody List<String> perfis) {
        Usuario usuario = usuarioService.atualizarPerfis(id, perfis);
        return ResponseEntity.ok(toResponse(usuario));
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        UsuarioResponse response = new UsuarioResponse();
        response.setId(usuario.getId());
        response.setNome(usuario.getNome());
        response.setEmail(usuario.getEmail());
        response.setCpf(usuario.getCpf());
        response.setCelular(usuario.getCelular());
        response.setAtivo(usuario.isAtivo());
        response.setDataCriacao(usuario.getDataCriacao());
        response.setPerfis(usuario.getPerfis());
        return response;
    }
}
