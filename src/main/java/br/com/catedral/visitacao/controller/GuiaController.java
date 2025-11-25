package br.com.catedral.visitacao.controller;

import br.com.catedral.visitacao.dto.request.CheckinRequest;
import br.com.catedral.visitacao.dto.response.IngressoResponse;
import br.com.catedral.visitacao.dto.response.MensagemResponse;
import br.com.catedral.visitacao.dto.response.SessaoResponse;
import br.com.catedral.visitacao.dto.response.UsuarioResponse;
import br.com.catedral.visitacao.model.Ingresso;
import br.com.catedral.visitacao.model.Sessao;
import br.com.catedral.visitacao.model.Usuario;
import br.com.catedral.visitacao.service.IngressoService;
import br.com.catedral.visitacao.service.SessaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/guia")
@PreAuthorize("hasRole('GUIA')")
@CrossOrigin(origins = "*")
public class GuiaController {

    private final SessaoService sessaoService;
    private final IngressoService ingressoService;

    public GuiaController(SessaoService sessaoService, IngressoService ingressoService) {
        this.sessaoService = sessaoService;
        this.ingressoService = ingressoService;
    }

    @GetMapping("/minhas-sessoes")
    public ResponseEntity<List<SessaoResponse>> listarMinhasSessoes(Authentication authentication) {
        Usuario guia = (Usuario) authentication.getPrincipal();

        List<SessaoResponse> sessoes = sessaoService.listarPorGuia(guia.getId()).stream()
                .map(this::toSessaoResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(sessoes);
    }

    @GetMapping("/sessoes/{sessaoId}/ingressos")
    public ResponseEntity<List<IngressoResponse>> listarIngressosDaSessao(
            @PathVariable Long sessaoId,
            Authentication authentication
    ) {
        Usuario guia = (Usuario) authentication.getPrincipal();
        Sessao sessao = sessaoService.buscarPorId(sessaoId);

        if (!sessao.getGuia().getId().equals(guia.getId())) {
            throw new RuntimeException("Acesso negado a esta sessão");
        }

        List<Ingresso> ingressos = ingressoService.listarPorSessao(sessaoId);
        List<IngressoResponse> responses = ingressos.stream()
                .map(this::toIngressoResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @PostMapping("/checkin")
    public ResponseEntity<MensagemResponse> processarCheckin(
            @Valid @RequestBody CheckinRequest request,
            Authentication authentication
    ) {
        Usuario guia = (Usuario) authentication.getPrincipal();

        try {
            Ingresso ingresso = ingressoService.processarCheckin(request.getQrCode(), guia);
            return ResponseEntity.ok(new MensagemResponse(
                    String.format("Check-in realizado: Ingresso %d de %s",
                            ingresso.getNumeroIngresso(),
                            ingresso.getComprador().getNome())
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MensagemResponse(e.getMessage()));
        }
    }

    @GetMapping("/checkins/{sessaoId}")
    public ResponseEntity<List<IngressoResponse>> listarCheckinsDaSessao(
            @PathVariable Long sessaoId,
            Authentication authentication
    ) {
        Usuario guia = (Usuario) authentication.getPrincipal();
        Sessao sessao = sessaoService.buscarPorId(sessaoId);

        if (!sessao.getGuia().getId().equals(guia.getId())) {
            throw new RuntimeException("Acesso negado a esta sessão");
        }

        List<Ingresso> ingressos = ingressoService.listarCheckinsPorSessao(sessaoId);
        List<IngressoResponse> responses = ingressos.stream()
                .map(this::toIngressoResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }


    private SessaoResponse toSessaoResponse(Sessao sessao) {
        SessaoResponse response = new SessaoResponse();
        response.setId(sessao.getId());
        response.setDataHora(sessao.getDataHora());
        response.setPreco(sessao.getPreco());
        response.setCapacidade(sessao.getCapacidade());
        response.setAtivo(sessao.isAtivo());

        Long totalIngressos = ingressoService.contarIngressosVendidosPorSessao(sessao.getId());
        Long checkinsRealizados = ingressoService.contarCheckinsPorSessao(sessao.getId());

        response.setVagasDisponiveis(sessao.getCapacidade() - totalIngressos.intValue());
        return response;
    }

    private IngressoResponse toIngressoResponse(Ingresso ingresso) {
        IngressoResponse response = new IngressoResponse();
        response.setId(ingresso.getId());
        response.setNumeroIngresso(ingresso.getNumeroIngresso());
        response.setQrCode(ingresso.getQrCode());
        response.setStatus(ingresso.getStatus());
        response.setDataCheckin(ingresso.getDataCheckin());
        response.setDescricao(ingresso.getDescricao());

        Usuario comprador = ingresso.getComprador();
        if (comprador != null) {
            UsuarioResponse compradorResponse = new UsuarioResponse();
            compradorResponse.setId(comprador.getId());
            compradorResponse.setNome(comprador.getNome());
            compradorResponse.setEmail(comprador.getEmail());
            compradorResponse.setCpf(comprador.getCpf());
            compradorResponse.setCelular(comprador.getCelular());
            compradorResponse.setAtivo(comprador.isAtivo());
            compradorResponse.setPerfis(comprador.getPerfis());
            compradorResponse.setDataCriacao(comprador.getDataCriacao());

            response.setComprador(compradorResponse);
        }

        return response;
    }
}
