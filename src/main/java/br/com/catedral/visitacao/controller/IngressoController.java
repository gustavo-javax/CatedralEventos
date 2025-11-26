package br.com.catedral.visitacao.controller;

import br.com.catedral.visitacao.dto.request.CompraRequest;
import br.com.catedral.visitacao.dto.response.*;
import br.com.catedral.visitacao.enums.StatusIngresso;
import br.com.catedral.visitacao.model.Ingresso;
import br.com.catedral.visitacao.model.Pagamento;
import br.com.catedral.visitacao.model.Sessao;
import br.com.catedral.visitacao.model.Usuario;
import br.com.catedral.visitacao.service.IngressoService;
import br.com.catedral.visitacao.service.PagamentoService;
import br.com.catedral.visitacao.service.SessaoService;
import br.com.catedral.visitacao.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ingressos")
@CrossOrigin(origins = "*")

public class IngressoController {

    private final IngressoService ingressoService;
    private final SessaoService sessaoService;
    private final UsuarioService usuarioService;
    private final PagamentoService pagamentoService;

    public IngressoController(IngressoService ingressoService, SessaoService sessaoService,
                              UsuarioService usuarioService, PagamentoService pagamentoService) {
        this.ingressoService = ingressoService;
        this.sessaoService = sessaoService;
        this.usuarioService = usuarioService;
        this.pagamentoService = pagamentoService;
    }

    @PostMapping("/comprar")
    public ResponseEntity<CompraResponse> comprar(@Valid @RequestBody CompraRequest request, Authentication authentication) {
        // Buscar dados necessários
        Sessao sessao = sessaoService.buscarPorId(request.getSessaoId());
        Usuario comprador = (Usuario) authentication.getPrincipal();

        // Verificar se há vagas disponíveis
        Long ingressosVendidos = ingressoService.contarIngressosVendidosPorSessao(sessao.getId());
        if (ingressosVendidos + request.getQuantidade() > sessao.getCapacidade()) {
            throw new RuntimeException("Vagas insuficientes para esta sessão");
        }

        // Buscar vendedor se especificado
        Usuario vendedor = null;
        if (request.getVendedorId() != null) {
            vendedor = usuarioService.buscarPorId(request.getVendedorId());
            if (!vendedor.isVendedor()) {
                throw new RuntimeException("Usuário não é um vendedor");
            }
        }

        // Criar pagamento
        BigDecimal valorTotal = BigDecimal.valueOf(sessao.getPreco() * request.getQuantidade());
        Pagamento pagamento = pagamentoService.criarPagamento(valorTotal, request.getQuantidade(), request.getMetodoPagamento());

        // Criar ingressos
        List<Ingresso> ingressos = ingressoService.criarIngressos(sessao, request.getQuantidade(), comprador, vendedor, pagamento);

        // Aprovar pagamento (em produção, isso viria do gateway de pagamento)
        Pagamento pagamentoAprovado = pagamentoService.aprovarPagamento(pagamento.getId(), "PIX_" + System.currentTimeMillis());

        // Preparar resposta
        CompraResponse response = new CompraResponse();
        response.setPagamentoId(pagamentoAprovado.getId());
        response.setCodigoTransacao(pagamentoAprovado.getCodigoTransacao());
        response.setValorTotal(pagamentoAprovado.getValorTotal());
        response.setQuantidadeIngressos(pagamentoAprovado.getQuantidadeIngressos());
        response.setStatus(pagamentoAprovado.getStatus().name());
        response.setIngressos(ingressos.stream().map(this::toIngressoResponse).collect(Collectors.toList()));
        response.setMensagem("Compra realizada com sucesso! Ingressos enviados por email.");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/meus-ingressos")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<IngressoResponse>> listarMeusIngressos(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        List<Ingresso> ingressos = ingressoService.listarPorComprador(usuario.getId());
        List<IngressoResponse> responses = ingressos.stream()
                .map(this::toIngressoResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngressoResponse> buscarPorId(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Ingresso ingresso = ingressoService.buscarPorId(id);

        // Verificar se o usuário tem permissão para ver este ingresso
        if (!ingresso.getComprador().getId().equals(usuario.getId()) && !usuario.isAdmin()) {
            throw new RuntimeException("Acesso negado a este ingresso");
        }

        return ResponseEntity.ok(toIngressoResponse(ingresso));
    }

    @PutMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<MensagemResponse> cancelar(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Ingresso ingresso = ingressoService.buscarPorId(id);

        // Verificar se o usuário é o dono do ingresso
        if (!ingresso.getComprador().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acesso negado a este ingresso");
        }

        // Lógica de cancelamento (simplificada)
        ingresso.setStatus(StatusIngresso.CANCELADO);
        ingressoService.salvar(ingresso);

        return ResponseEntity.ok(new MensagemResponse("Ingresso cancelado com sucesso"));
    }

    // MÉTODO CORRIGIDO - Agora preenche TODOS os dados
    private IngressoResponse toIngressoResponse(Ingresso ingresso) {
        IngressoResponse response = new IngressoResponse();
        response.setId(ingresso.getId());
        response.setNumeroIngresso(ingresso.getNumeroIngresso());
        response.setQrCode(ingresso.getQrCode());
        response.setStatus(ingresso.getStatus());
        response.setDataCheckin(ingresso.getDataCheckin());
        response.setDescricao(ingresso.getDescricao());

        // ✅ PREENCHE COMPRADOR COMPLETO
        if (ingresso.getComprador() != null) {
            response.setComprador(toUsuarioResponse(ingresso.getComprador()));
        }

        // ✅ PREENCHE VENDEDOR (se existir)
        if (ingresso.getVendedor() != null) {
            response.setVendedor(toUsuarioResponse(ingresso.getVendedor()));
        }

        // ✅ PREENCHE SESSÃO COMPLETA (com evento e guia)
        if (ingresso.getSessao() != null) {
            response.setSessao(toSessaoResponse(ingresso.getSessao()));
        }

        return response;
    }

    private UsuarioResponse toUsuarioResponse(Usuario usuario) {
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

    private SessaoResponse toSessaoResponse(Sessao sessao) {
        SessaoResponse response = new SessaoResponse();
        response.setId(sessao.getId());
        response.setDataHora(sessao.getDataHora());
        response.setPreco(sessao.getPreco());
        response.setCapacidade(sessao.getCapacidade());
        response.setAtivo(sessao.isAtivo());

        // ✅ PREENCHE EVENTO DA SESSÃO
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

        // ✅ PREENCHE GUIA DA SESSÃO
        if (sessao.getGuia() != null) {
            response.setGuia(toUsuarioResponse(sessao.getGuia()));
        }

        // Calcular vagas disponíveis
        Long ingressosVendidos = sessaoService.contarIngressosVendidos(sessao.getId());
        response.setVagasDisponiveis(sessao.getCapacidade() - ingressosVendidos.intValue());

        return response;
    }
}