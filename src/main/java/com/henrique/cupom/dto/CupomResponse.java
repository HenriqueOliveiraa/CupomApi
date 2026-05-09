package com.henrique.cupom.dto;

import com.henrique.cupom.domain.Cupom;
import com.henrique.cupom.domain.CupomStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Dados do cupom")
public record CupomResponse(

        @Schema(description = "Identificador único do cupom")
        UUID id,

        @Schema(description = "Código sanitizado do cupom (6 caracteres alfanuméricos)")
        String code,

        @Schema(description = "Descrição do cupom")
        String description,

        @Schema(description = "Valor do desconto")
        BigDecimal discountValue,

        @Schema(description = "Data de expiração")
        Instant expirationDate,

        @Schema(description = "Indica se o cupom está publicado")
        boolean published,

        @Schema(description = "Indica se o cupom foi resgatado")
        boolean redeemed,

        @Schema(description = "Status do cupom")
        CupomStatus status
) {
    public static CupomResponse from(Cupom cupom) {
        return new CupomResponse(
                cupom.getId(),
                cupom.getCodigo(),
                cupom.getDescricao(),
                cupom.getValorDesconto(),
                cupom.getDataExpiracao(),
                cupom.isPublicado(),
                cupom.isResgatado(),
                cupom.getStatus()
        );
    }
}
