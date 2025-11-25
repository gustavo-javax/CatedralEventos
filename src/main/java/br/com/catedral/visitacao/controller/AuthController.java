package br.com.catedral.visitacao.controller;

import br.com.catedral.visitacao.dto.request.CadastroRequest;
import br.com.catedral.visitacao.dto.request.LoginRequest;
import br.com.catedral.visitacao.dto.response.LoginResponse;
import br.com.catedral.visitacao.dto.response.MensagemResponse;
import br.com.catedral.visitacao.dto.response.UsuarioResponse;
import br.com.catedral.visitacao.model.Usuario;
import br.com.catedral.visitacao.service.AuthService;
import br.com.catedral.visitacao.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    public AuthController(AuthService authService, UsuarioService usuarioService) {
        this.authService = authService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request.getEmail(), request.getSenha());
        Usuario usuario = (Usuario) usuarioService.loadUserByUsername(request.getEmail());

        LoginResponse response = new LoginResponse(
                token,
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfis()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody CadastroRequest request) {
        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setSenha(request.getSenha());
        usuario.setCpf(request.getCpf());
        usuario.setCelular(request.getCelular());

        Usuario usuarioSalvo = authService.registrar(usuario);

        UsuarioResponse response = new UsuarioResponse();
        response.setId(usuarioSalvo.getId());
        response.setNome(usuarioSalvo.getNome());
        response.setEmail(usuarioSalvo.getEmail());
        response.setCpf(usuarioSalvo.getCpf());
        response.setCelular(usuarioSalvo.getCelular());
        response.setAtivo(usuarioSalvo.isAtivo());
        response.setDataCriacao(usuarioSalvo.getDataCriacao());
        response.setPerfis(usuarioSalvo.getPerfis());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/verificar-email")
    public ResponseEntity<MensagemResponse> verificarEmail(@RequestParam String email) {
        boolean existe = usuarioService.existePorEmail(email);
        if (existe) {
            return ResponseEntity.badRequest().body(new MensagemResponse("Email já cadastrado"));
        }
        return ResponseEntity.ok(new MensagemResponse("Email disponível"));
    }
}
