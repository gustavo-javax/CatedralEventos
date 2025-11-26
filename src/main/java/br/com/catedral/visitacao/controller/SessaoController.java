package br.com.catedral.visitacao.controller;

import br.com.catedral.visitacao.dto.request.SessaoRequest;
import br.com.catedral.visitacao.dto.response.MensagemResponse;
import br.com.catedral.visitacao.dto.response.SessaoResponse;
import br.com.catedral.visitacao.dto.response.EventoResponse;
import br.com.catedral.visitacao.dto.response.UsuarioResponse;
import br.com.catedral.visitacao.model.Evento;
import br.com.catedral.visitacao.model.Sessao;
import br.com.catedral.visitacao.model.Usuario;
import br.com.catedral.visitacao.service.EventoService;
import br.com.catedral.visitacao.service.SessaoService;
import br.com.catedral.visitacao.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sessoes")
@CrossOrigin(origins = "*")
public class SessaoController {

    private final SessaoService sessaoService;
    private final EventoService eventoService;
    private final UsuarioService usuarioService;

    public SessaoController(SessaoService sessaoService, EventoService eventoService, UsuarioService usuarioService) {
        this.sessaoService = sessaoService;
        this.eventoService = eventoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<SessaoResponse>> listarAtivas() {
        List<Sessao> sessoes = sessaoService.listarAtivas();
        List<SessaoResponse> responses = sessoes.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/proximas")
    public ResponseEntity<List<SessaoResponse>> listarProximas() {
        List<Sessao> sessoes = sessaoService.listarProximas();
        List<SessaoResponse> responses = sessoes.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<SessaoResponse>> listarPorEvento(@PathVariable Long eventoId) {
        List<Sessao> sessoes = sessaoService.listarPorEvento(eventoId);
        List<SessaoResponse> responses = sessoes.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessaoResponse> buscarPorId(@PathVariable Long id) {
        Sessao sessao = sessaoService.buscarPorId(id);
        return ResponseEntity.ok(toResponse(sessao));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SessaoResponse> criar(@Valid @RequestBody SessaoRequest request) {
        Evento evento = eventoService.buscarPorId(request.getEventoId());

        Sessao sessao = new Sessao();
        sessao.setEvento(evento);
        sessao.setDataHora(request.getDataHora());
        sessao.setPreco(request.getPreco());
        sessao.setCapacidade(request.getCapacidade());

        if (request.getGuiaId() != null) {
            Usuario guia = usuarioService.buscarPorId(request.getGuiaId());
            if (!guia.isGuia()) {
                throw new RuntimeException("Usuário não é um guia");
            }

            boolean disponivel = sessaoService.verificarDisponibilidadeGuia(
                    guia.getId(), request.getDataHora(), evento.getDuracaoMinutos()
            );

            if (!disponivel) {
                throw new RuntimeException("Guia não disponível para esta data/hora");
            }

            sessao.setGuia(guia);
        }

        Sessao sessaoSalva = sessaoService.salvar(sessao);
        return ResponseEntity.ok(toResponse(sessaoSalva));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SessaoResponse> atualizar(@PathVariable Long id, @Valid @RequestBody SessaoRequest request) {
        Sessao sessao = sessaoService.buscarPorId(id);
        Evento evento = eventoService.buscarPorId(request.getEventoId());

        sessao.setEvento(evento);
        sessao.setDataHora(request.getDataHora());
        sessao.setPreco(request.getPreco());
        sessao.setCapacidade(request.getCapacidade());

        if (request.getGuiaId() != null) {
            Usuario guia = usuarioService.buscarPorId(request.getGuiaId());
            if (!guia.isGuia()) {
                throw new RuntimeException("Usuário não é um guia");
            }
            sessao.setGuia(guia);
        } else {
            sessao.setGuia(null);
        }

        Sessao sessaoAtualizada = sessaoService.salvar(sessao);
        return ResponseEntity.ok(toResponse(sessaoAtualizada));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MensagemResponse> desativar(@PathVariable Long id) {
        sessaoService.desativar(id);
        return ResponseEntity.ok(new MensagemResponse("Sessão desativada com sucesso"));
    }

    private SessaoResponse toResponse(Sessao sessao) {
        SessaoResponse response = new SessaoResponse();
        response.setId(sessao.getId());
        response.setDataHora(sessao.getDataHora());
        response.setPreco(sessao.getPreco());
        response.setCapacidade(sessao.getCapacidade());
        response.setAtivo(sessao.isAtivo());

        if (sessao.getEvento() != null) {
            EventoResponse eventoResponse = new EventoResponse();
            eventoResponse.setId(sessao.getEvento().getId());
            eventoResponse.setNome(sessao.getEvento().getNome());
            eventoResponse.setDescricao(sessao.getEvento().getDescricao());
            eventoResponse.setTipoEvento(sessao.getEvento().getTipoEvento());
            eventoResponse.setDuracaoMinutos(sessao.getEvento().getDuracaoMinutos());
            eventoResponse.setImagemUrl(sessao.getEvento().getImagemUrl());
            eventoResponse.setAtivo(sessao.getEvento().isAtivo());
            response.setEvento(eventoResponse);
        }

        if (sessao.getGuia() != null) {
            UsuarioResponse guiaResponse = new UsuarioResponse();
            guiaResponse.setId(sessao.getGuia().getId());
            guiaResponse.setNome(sessao.getGuia().getNome());
            guiaResponse.setEmail(sessao.getGuia().getEmail());
            guiaResponse.setCpf(sessao.getGuia().getCpf());
            guiaResponse.setCelular(sessao.getGuia().getCelular());
            guiaResponse.setAtivo(sessao.getGuia().isAtivo());
            guiaResponse.setDataCriacao(sessao.getGuia().getDataCriacao());
            guiaResponse.setPerfis(sessao.getGuia().getPerfis());
            response.setGuia(guiaResponse);
        }

        Long ingressosVendidos = sessaoService.contarIngressosVendidos(sessao.getId());
        response.setVagasDisponiveis(sessao.getCapacidade() - ingressosVendidos.intValue());

        return response;
    }
}