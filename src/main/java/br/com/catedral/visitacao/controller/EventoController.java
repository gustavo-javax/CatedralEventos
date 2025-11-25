package br.com.catedral.visitacao.controller;

import br.com.catedral.visitacao.dto.request.EventoRequest;
import br.com.catedral.visitacao.dto.response.EventoResponse;
import br.com.catedral.visitacao.dto.response.MensagemResponse;
import br.com.catedral.visitacao.model.Evento;
import br.com.catedral.visitacao.service.EventoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/eventos")
@CrossOrigin(origins = "*")

public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @GetMapping
    public ResponseEntity<List<EventoResponse>> listarTodos() {
        List<Evento> eventos = eventoService.listarTodos();
        List<EventoResponse> responses = eventos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponse> buscarPorId(@PathVariable Long id) {
        Evento evento = eventoService.buscarPorId(id);
        return ResponseEntity.ok(toResponse(evento));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventoResponse> criar(@Valid @RequestBody EventoRequest request) {
        Evento evento = new Evento();
        evento.setNome(request.getNome());
        evento.setDescricao(request.getDescricao());
        evento.setTipoEvento(request.getTipoEvento());
        evento.setDuracaoMinutos(request.getDuracaoMinutos());
        evento.setImagemUrl(request.getImagemUrl());

        Evento eventoSaved = eventoService.salvar(evento);
        return ResponseEntity.ok(toResponse(eventoSaved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventoResponse> atualizar(@PathVariable Long id, @Valid @RequestBody EventoRequest request) {
        Evento eventoAtualizado = new Evento();
        eventoAtualizado.setNome(request.getNome());
        eventoAtualizado.setDescricao(request.getDescricao());
        eventoAtualizado.setTipoEvento(request.getTipoEvento());
        eventoAtualizado.setDuracaoMinutos(request.getDuracaoMinutos());
        eventoAtualizado.setImagemUrl(request.getImagemUrl());

        Evento evento = eventoService.atualizar(id, eventoAtualizado);
        return ResponseEntity.ok(toResponse(evento));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MensagemResponse> desativar(@PathVariable Long id) {
        eventoService.desativar(id);
        return ResponseEntity.ok(new MensagemResponse("Evento desativado com sucesso"));
    }

    private EventoResponse toResponse(Evento evento) {
        EventoResponse response = new EventoResponse();
        response.setId(evento.getId());
        response.setNome(evento.getNome());
        response.setDescricao(evento.getDescricao());
        response.setTipoEvento(evento.getTipoEvento());
        response.setDuracaoMinutos(evento.getDuracaoMinutos());
        response.setImagemUrl(evento.getImagemUrl());
        response.setAtivo(evento.isAtivo());
        return response;
    }
}
