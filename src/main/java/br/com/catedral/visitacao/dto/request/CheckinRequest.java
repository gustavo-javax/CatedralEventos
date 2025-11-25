package br.com.catedral.visitacao.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CheckinRequest {

    @NotBlank(message = "QR Code é obrigatório")
    private String qrCode;

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
}
