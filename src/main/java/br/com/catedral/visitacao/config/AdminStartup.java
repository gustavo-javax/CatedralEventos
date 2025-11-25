package br.com.catedral.visitacao.config;

import br.com.catedral.visitacao.enums.Perfil;
import br.com.catedral.visitacao.model.Usuario;
import br.com.catedral.visitacao.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class AdminStartup implements CommandLineRunner {
    private final UsuarioService usuarioService;

    public AdminStartup(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!usuarioService.existePorEmail("admin@teste.com")) {
            Usuario admin = new Usuario();
            admin.setNome("Admin");
            admin.setEmail("admin@teste.com");
            admin.setSenha("123456");
            admin.setAtivo(true);
            admin.setPerfis(Set.of(Perfil.ADMIN));
            usuarioService.salvar(admin);
            System.out.println("Admin criado!");
        }
    }
}
