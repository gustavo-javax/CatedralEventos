package br.com.catedral.visitacao.service;

import br.com.catedral.visitacao.model.Ingresso;
import br.com.catedral.visitacao.model.Sessao;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import org.springframework.mail.javamail.JavaMailSender;

import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Locale;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final QrCodeService qrCodeService;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine, QrCodeService qrCodeService) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.qrCodeService = qrCodeService;
    }

    public void enviarIngressos(String emailDestinatario, List<Ingresso> ingressos, Sessao sessao) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(emailDestinatario);
            helper.setSubject("Seus ingressos para " + sessao.getEvento().getNome());

            Context context = new Context(new Locale("pt", "BR"));
            context.setVariable("ingressos", ingressos);
            context.setVariable("sessao", sessao);
            context.setVariable("comprador", ingressos.get(0).getComprador());

            for (Ingresso ingresso : ingressos) {
                String qrCodeBase64 = qrCodeService.gerarQrCodeBase64(ingresso.getQrCode(), 200, 200);
                context.setVariable("qrCode_" + ingresso.getId(), qrCodeBase64);
            }

            String htmlContent = templateEngine.process("email/ingressos", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Erro ao enviar email", e);
        }
    }

    public void enviarEmailRecuperacaoSenha(String emailDestinatario, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(emailDestinatario);
            helper.setSubject("Recuperação de Senha - Catedral");

            Context context = new Context(new Locale("pt", "BR"));
            context.setVariable("token", token);

            String htmlContent = templateEngine.process("email/recuperacao-senha", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Erro ao enviar email de recuperação", e);
        }
    }
}
