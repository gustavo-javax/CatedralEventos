package br.com.catedral.visitacao.service;

import br.com.catedral.visitacao.model.Sessao;
import br.com.catedral.visitacao.repository.SessaoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SessaoService {

    private final SessaoRepository sessaoRepository;

    public SessaoService(SessaoRepository sessaoRepository) {
        this.sessaoRepository = sessaoRepository;
    }

    public Sessao salvar(Sessao sessao) {
        return sessaoRepository.save(sessao);
    }

    public List<Sessao> listarAtivas() {
        return sessaoRepository.findByAtivoTrue();
    }

    public List<Sessao> listarProximas() {
        return sessaoRepository.findProximasSessoes(LocalDateTime.now());
    }

    public List<Sessao> listarPorGuia(Long guiaId) {
        return sessaoRepository.findByGuiaIdAndAtivoTrue(guiaId);
    }

    public List<Sessao> listarPorEvento(Long eventoId) {
        return sessaoRepository.findByEventoIdAndAtivoTrue(eventoId);
    }

    public Sessao buscarPorId(Long id) {
        return sessaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sessão não encontrada"));
    }

    public void desativar(Long id) {
        Sessao sessao = buscarPorId(id);
        sessao.setAtivo(false);
        sessaoRepository.save(sessao);
    }

    public boolean verificarDisponibilidadeGuia(Long guiaId, LocalDateTime dataHora, Integer duracao) {
        List<Sessao> sessoes = sessaoRepository.findByGuiaIdAndDataHoraAfter(guiaId, LocalDateTime.now().minusDays(1));

        return sessoes.stream().noneMatch(sessao -> {
            LocalDateTime fimSessao = sessao.getDataHora().plusMinutes(sessao.getEvento().getDuracaoMinutos());
            LocalDateTime fimNovaSessao = dataHora.plusMinutes(duracao);

            return dataHora.isBefore(fimSessao) && fimNovaSessao.isAfter(sessao.getDataHora());
        });
    }
}
