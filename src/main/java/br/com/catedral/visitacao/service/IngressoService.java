package br.com.catedral.visitacao.service;

import br.com.catedral.visitacao.enums.StatusIngresso;
import br.com.catedral.visitacao.model.Ingresso;
import br.com.catedral.visitacao.model.Pagamento;
import br.com.catedral.visitacao.model.Sessao;
import br.com.catedral.visitacao.model.Usuario;
import br.com.catedral.visitacao.repository.IngressoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class IngressoService {

    private final IngressoRepository ingressoRepository;
    private final QrCodeService qrCodeService;
    private final EmailService emailService;

    public List<Ingresso> listarCheckinsPorSessao(Long sessaoId) {
        return ingressoRepository.findBySessaoIdAndStatus(sessaoId, StatusIngresso.UTILIZADO);
    }

    public Long contarCheckinsPorSessao(Long sessaoId) {
        return ingressoRepository.countBySessaoIdAndStatusIn(sessaoId, List.of(StatusIngresso.UTILIZADO));
    }

    public IngressoService(IngressoRepository ingressoRepository, QrCodeService qrCodeService, EmailService emailService) {
        this.ingressoRepository = ingressoRepository;
        this.qrCodeService = qrCodeService;
        this.emailService = emailService;
    }

    public List<Ingresso> criarIngressos(Sessao sessao, Integer quantidade, Usuario comprador, Usuario vendedor, Pagamento pagamento) {
        List<Ingresso> ingressos = new ArrayList<>();

        for (int i = 1; i <= quantidade; i++) {
            Ingresso ingresso = new Ingresso();
            ingresso.setSessao(sessao);
            ingresso.setNumeroIngresso(i);
            ingresso.setComprador(comprador);
            ingresso.setVendedor(vendedor);
            ingresso.setPagamento(pagamento);
            ingresso.setQrCode(gerarQrCodeUnico());
            ingresso.setStatus(StatusIngresso.ATIVO);

            ingressos.add(ingresso);
        }

        List<Ingresso> ingressosSalvos = ingressoRepository.saveAll(ingressos);

        emailService.enviarIngressos(comprador.getEmail(), ingressosSalvos, sessao);

        return ingressosSalvos;
    }

    public Ingresso processarCheckin(String qrCode, Usuario guia) {
        Ingresso ingresso = ingressoRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new RuntimeException("Ingresso não encontrado"));

        if (!ingresso.getSessao().getGuia().getId().equals(guia.getId())) {
            throw new RuntimeException("Guia não autorizado para esta sessão");
        }

        if (ingresso.getStatus() == StatusIngresso.UTILIZADO) {
            throw new RuntimeException("Ingresso já utilizado");
        }

        if (ingresso.getSessao().getDataHora().isBefore(LocalDateTime.now().minusHours(1))) {
            throw new RuntimeException("Sessão expirada");
        }

        ingresso.setStatus(StatusIngresso.UTILIZADO);
        ingresso.setDataCheckin(LocalDateTime.now());

        return ingressoRepository.save(ingresso);
    }

    public List<Ingresso> listarPorComprador(Long compradorId) {
        return ingressoRepository.findByCompradorId(compradorId);
    }

    public List<Ingresso> listarPorSessao(Long sessaoId) {
        return ingressoRepository.findBySessaoId(sessaoId);
    }

    public Long contarIngressosVendidosPorSessao(Long sessaoId) {
        return ingressoRepository.countBySessaoIdAndStatusIn(
                sessaoId,
                List.of(StatusIngresso.ATIVO, StatusIngresso.UTILIZADO)
        );
    }

    private String gerarQrCodeUnico() {
        String qrCode;
        do {
            qrCode = "CAT_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (ingressoRepository.existsByQrCode(qrCode));

        return qrCode;
    }
    public Ingresso buscarPorId(Long id) {
        return ingressoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingresso não encontrado"));
    }

    public void cancelarIngresso(Long id) {
        Ingresso ingresso = buscarPorId(id);
        if (ingresso.getStatus() != StatusIngresso.ATIVO) {
            throw new RuntimeException("Ingresso não pode ser cancelado");
        }
        ingresso.setStatus(StatusIngresso.CANCELADO);
        ingressoRepository.save(ingresso);
    }

    public Ingresso salvar(Ingresso ingresso) {
        return ingressoRepository.save(ingresso);
    }

}
