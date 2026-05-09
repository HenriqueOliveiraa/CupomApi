package com.henrique.cupom.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public record CriarCupomRequest(

        @NotBlank
        @Schema(description = "Código do cupom (6 caracteres alfanuméricos; caracteres especiais são removidos automaticamente)",
                example = "ABC!12")
        String code,

        @NotBlank
        @Schema(description = "Descrição do cupom", example = "10% de desconto em compras acima de R$ 100")
        String description,

        @NotNull
        @Schema(description = "Valor do desconto (mínimo 0.5)", example = "10.00")
        BigDecimal discountValue,

        @NotNull
        @Schema(description = "Data de expiração (deve ser futura)", example = "2027-01-01T00:00:00Z")
        Instant expirationDate,

        @Schema(description = "Indica se o cupom já é publicado ao ser criado", example = "false")
        boolean published
) {}
