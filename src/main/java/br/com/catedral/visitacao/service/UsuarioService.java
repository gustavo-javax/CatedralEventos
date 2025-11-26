package br.com.catedral.visitacao.service;

import br.com.catedral.visitacao.enums.Perfil;
import br.com.catedral.visitacao.model.Usuario;
import br.com.catedral.visitacao.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
    }

    public Usuario salvar(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public Usuario atualizarPerfis(Long usuarioId, List<String> perfis) {
        Usuario usuario = buscarPorId(usuarioId);
        usuario.getPerfis().clear();

        for (String perfilStr : perfis) {
            try {
                Perfil perfil = Perfil.valueOf(perfilStr.toUpperCase());
                usuario.getPerfis().add(perfil);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Perfil inválido: " + perfilStr);
            }
        }

        return usuarioRepository.save(usuario);
    }

    public boolean existePorEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}
