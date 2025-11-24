package br.com.catedral.visitacao.service;

import br.com.catedral.visitacao.enums.TipoEvento;
import br.com.catedral.visitacao.model.Evento;
import br.com.catedral.visitacao.repository.EventoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;

    public EventoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    public Evento salvar(Evento evento) {
        return eventoRepository.save(evento);
    }

    public List<Evento> listarTodos() {
        return eventoRepository.findByAtivoTrue();
    }

    public List<Evento> listarPorTipo(TipoEvento tipoEvento) {
        return eventoRepository.findByTipoEventoAndAtivoTrue(tipoEvento);
    }

    public List<Evento> buscarPorNome(String nome) {
        return eventoRepository.findByNomeContainingIgnoreCaseAndAtivoTrue(nome);
    }

    public Evento buscarPorId(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado"));
    }

    public void desativar(Long id) {
        Evento evento = buscarPorId(id);
        evento.setAtivo(false);
        eventoRepository.save(evento);
    }

    public Evento atualizar(Long id, Evento eventoAtualizado) {
        Evento evento = buscarPorId(id);
        evento.setNome(eventoAtualizado.getNome());
        evento.setDescricao(eventoAtualizado.getDescricao());
        evento.setTipoEvento(eventoAtualizado.getTipoEvento());
        evento.setDuracaoMinutos(eventoAtualizado.getDuracaoMinutos());
        evento.setImagemUrl(eventoAtualizado.getImagemUrl());

        return eventoRepository.save(evento);
    }
}
