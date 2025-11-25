package br.com.catedral.visitacao.controller;

import br.com.catedral.visitacao.dto.request.CompraRequest;
import br.com.catedral.visitacao.dto.response.*;
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
        Sessao sessao = sessaoService.buscarPorId(request.getSessaoId());
        Usuario comprador = (Usuario) authentication.getPrincipal();

        Long ingressosVendidos = ingressoService.contarIngressosVendidosPorSessao(sessao.getId());
        if (ingressosVendidos + request.getQuantidade() > sessao.getCapacidade()) {
            throw new RuntimeException("Vagas insuficientes para esta sessão");
        }

        Usuario vendedor = null;
        if (request.getVendedorId() != null) {
            vendedor = usuarioService.buscarPorId(request.getVendedorId());
            if (!vendedor.isVendedor()) {
                throw new RuntimeException("Usuário não é um vendedor");
            }
        }

        BigDecimal valorTotal = BigDecimal.valueOf(sessao.getPreco() * request.getQuantidade());
        Pagamento pagamento = pagamentoService.criarPagamento(valorTotal, request.getQuantidade(), request.getMetodoPagamento());

        List<Ingresso> ingressos = ingressoService.criarIngressos(sessao, request.getQuantidade(), comprador, vendedor, pagamento);

        Pagamento pagamentoAprovado = pagamentoService.aprovarPagamento(pagamento.getId(), "PIX_" + System.currentTimeMillis());

        CompraResponse response = new CompraResponse();
        response.setPagamentoId(pagamentoAprovado.getId());
        response.setCodigoTransacao(pagamentoAprovado.getCodigoTransacao());
        response.setValorTotal(pagamentoAprovado.getValorTotal());
        response.setQuantidadeIngressos(pagamentoAprovado.getQuantidadeIngressos());
        response.setStatus(pagamentoAprovado.getStatus().name());
        response.setIngressos(ingressos.stream().map(this::toResponse).collect(Collectors.toList()));
        response.setMensagem("Compra realizada com sucesso! Ingressos enviados por email.");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/meus-ingressos")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<IngressoResponse>> listarMeusIngressos(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        List<Ingresso> ingressos = ingressoService.listarPorComprador(usuario.getId());
        List<IngressoResponse> responses = ingressos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngressoResponse> buscarPorId(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Ingresso ingresso = ingressoService.buscarPorId(id);

        if (!ingresso.getComprador().getId().equals(usuario.getId()) && !usuario.isAdmin()) {
            throw new RuntimeException("Acesso negado a este ingresso");
        }

        return ResponseEntity.ok(toResponse(ingresso));
    }

    @PutMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<MensagemResponse> cancelar(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Ingresso ingresso = ingressoService.buscarPorId(id);

        if (!ingresso.getComprador().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acesso negado a este ingresso");
        }

        ingressoService.cancelarIngresso(id);

        return ResponseEntity.ok(new MensagemResponse("Ingresso cancelado com sucesso"));
    }

    private IngressoResponse toResponse(Ingresso ingresso) {
        IngressoResponse response = new IngressoResponse();
        response.setId(ingresso.getId());
        response.setNumeroIngresso(ingresso.getNumeroIngresso());
        response.setQrCode(ingresso.getQrCode());
        response.setStatus(ingresso.getStatus());
        response.setDataCheckin(ingresso.getDataCheckin());
        response.setDescricao(ingresso.getDescricao());

        Sessao sessao = ingresso.getSessao();
        if (sessao != null) {
            SessaoResponse sessaoResponse = new SessaoResponse();
            sessaoResponse.setId(sessao.getId());
            sessaoResponse.setDataHora(sessao.getDataHora());
            sessaoResponse.setPreco(sessao.getPreco());
            sessaoResponse.setCapacidade(sessao.getCapacidade());
            sessaoResponse.setAtivo(sessao.isAtivo());
            response.setSessao(sessaoResponse);
        }

        Usuario comprador = ingresso.getComprador();
        if (comprador != null) {
            UsuarioResponse usuarioResponse = new UsuarioResponse();
            usuarioResponse.setId(comprador.getId());
            usuarioResponse.setNome(comprador.getNome());
            usuarioResponse.setEmail(comprador.getEmail());
            response.setComprador(usuarioResponse);
        }

        return response;
    }
}
