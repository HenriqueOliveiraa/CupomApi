package com.henrique.cupom.domain;

import com.henrique.cupom.domain.exception.CupomInvalidoException;
import com.henrique.cupom.domain.exception.CupomJaDeletadoException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Cupom {

    private static final int TAMANHO_CODIGO = 6;
    private static final BigDecimal DESCONTO_MINIMO = new BigDecimal("0.5");

    private final UUID id;
    private final String codigo;
    private final String descricao;
    private final BigDecimal valorDesconto;
    private final Instant dataExpiracao;
    private final boolean publicado;
    private final boolean resgatado;
    private final CupomStatus status;

    private Cupom(UUID id, String codigo, String descricao, BigDecimal valorDesconto,
                  Instant dataExpiracao, boolean publicado, boolean resgatado, CupomStatus status) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
        this.valorDesconto = valorDesconto;
        this.dataExpiracao = dataExpiracao;
        this.publicado = publicado;
        this.resgatado = resgatado;
        this.status = status;
    }

    public static Cupom criar(String codigoBruto, String descricao,
                              BigDecimal valorDesconto, Instant dataExpiracao, boolean publicado) {
        String codigo = sanitizarCodigo(codigoBruto);
        validarCodigo(codigo);
        validarDesconto(valorDesconto);
        validarDataExpiracao(dataExpiracao);

        return new Cupom(UUID.randomUUID(), codigo, descricao,
                valorDesconto, dataExpiracao, publicado, false, CupomStatus.ATIVO);
    }

    public static Cupom reconstituir(UUID id, String codigo, String descricao,
                                     BigDecimal valorDesconto, Instant dataExpiracao,
                                     boolean publicado, boolean resgatado, CupomStatus status) {
        return new Cupom(id, codigo, descricao, valorDesconto,
                dataExpiracao, publicado, resgatado, status);
    }

    public Cupom deletar() {
        if (this.isDeletado()) {
            throw new CupomJaDeletadoException("Cupom já foi deletado.");
        }
        return new Cupom(id, codigo, descricao, valorDesconto,
                dataExpiracao, publicado, resgatado, CupomStatus.DELETADO);
    }

    public boolean isDeletado() {
        return CupomStatus.DELETADO.equals(this.status);
    }

    static String sanitizarCodigo(String codigo) {
        if (codigo == null) {
            throw new CupomInvalidoException("O código do cupom não pode ser nulo.");
        }
        return codigo.replaceAll("[^a-zA-Z0-9]", "");
    }

    private static void validarCodigo(String codigo) {
        if (codigo.length() != TAMANHO_CODIGO) {
            throw new CupomInvalidoException(
                    "O código deve ter exatamente 6 caracteres alfanuméricos. Tamanho obtido: " + codigo.length()
            );
        }
    }

    private static void validarDesconto(BigDecimal valorDesconto) {
        if (valorDesconto == null || valorDesconto.compareTo(DESCONTO_MINIMO) < 0) {
            throw new CupomInvalidoException("O valor do desconto deve ser no mínimo 0,5.");
        }
    }

    private static void validarDataExpiracao(Instant dataExpiracao) {
        if (dataExpiracao == null || dataExpiracao.isBefore(Instant.now())) {
            throw new CupomInvalidoException("A data de expiração deve ser no futuro.");
        }
    }

    public UUID getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getDescricao() { return descricao; }
    public BigDecimal getValorDesconto() { return valorDesconto; }
    public Instant getDataExpiracao() { return dataExpiracao; }
    public boolean isPublicado() { return publicado; }
    public boolean isResgatado() { return resgatado; }
    public CupomStatus getStatus() { return status; }
}