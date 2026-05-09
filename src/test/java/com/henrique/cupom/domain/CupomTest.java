package com.henrique.cupom.domain;

import com.henrique.cupom.domain.exception.CupomInvalidoException;
import com.henrique.cupom.domain.exception.CupomJaDeletadoException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.*;

class CupomTest {

    private static final Instant AMANHA = Instant.now().plus(1, ChronoUnit.DAYS);

    @Test
    void deveCriarCupomValido() {
        Cupom cupom = Cupom.criar("ABC123", "Desconto de 10%", new BigDecimal("10.00"), AMANHA, false);

        assertThat(cupom.getCodigo()).isEqualTo("ABC123");
        assertThat(cupom.getDescricao()).isEqualTo("Desconto de 10%");
        assertThat(cupom.getValorDesconto()).isEqualByComparingTo("10.00");
        assertThat(cupom.getDataExpiracao()).isEqualTo(AMANHA);
        assertThat(cupom.isPublicado()).isFalse();
        assertThat(cupom.isResgatado()).isFalse();
        assertThat(cupom.getStatus()).isEqualTo(CupomStatus.ATIVO);
        assertThat(cupom.getId()).isNotNull();
    }

    @Test
    void deveCriarCupomJaPublicado() {
        Cupom cupom = Cupom.criar("ABC123", "Desconto", new BigDecimal("5.00"), AMANHA, true);
        assertThat(cupom.isPublicado()).isTrue();
    }

    @Test
    void deveRemoverCaracteresEspeciaisDoCodigo() {
        // "A!B@C1#23" remove !@# => "ABC123" (6 chars)
        Cupom cupom = Cupom.criar("A!B@C1#23", "Desconto", new BigDecimal("5.00"), AMANHA, false);
        assertThat(cupom.getCodigo()).isEqualTo("ABC123");
        assertThat(cupom.getCodigo()).hasSize(6);
    }

    @Test
    void deveRemoverCaracteresEspeciaisEManterExatamente6Caracteres() {
        // "A!B@C#1$2%3" remove !@#$% => "ABC123" (6 chars)
        Cupom cupom = Cupom.criar("A!B@C#1$2%3", "Desconto", new BigDecimal("5.00"), AMANHA, false);
        assertThat(cupom.getCodigo()).hasSize(6);
        assertThat(cupom.getCodigo()).isEqualTo("ABC123");
    }

    @Test
    void deveLancarExcecaoQuandoCodigoNulo() {
        assertThatThrownBy(() -> Cupom.criar(null, "Desconto", new BigDecimal("5.00"), AMANHA, false))
                .isInstanceOf(CupomInvalidoException.class)
                .hasMessageContaining("nulo");
    }

    @Test
    void deveLancarExcecaoQuandoCodigoMenosDe6Caracteres() {
        assertThatThrownBy(() -> Cupom.criar("ABC", "Desconto", new BigDecimal("5.00"), AMANHA, false))
                .isInstanceOf(CupomInvalidoException.class)
                .hasMessageContaining("6 caracteres");
    }

    @Test
    void deveLancarExcecaoQuandoCodigoMaisDe6CaracteresSemEspeciais() {
        assertThatThrownBy(() -> Cupom.criar("ABCDEF1", "Desconto", new BigDecimal("5.00"), AMANHA, false))
                .isInstanceOf(CupomInvalidoException.class)
                .hasMessageContaining("6 caracteres");
    }

    @Test
    void deveLancarExcecaoQuandoDescontoMenorQueMinimo() {
        assertThatThrownBy(() -> Cupom.criar("ABC123", "Desconto", new BigDecimal("0.4"), AMANHA, false))
                .isInstanceOf(CupomInvalidoException.class)
                .hasMessageContaining("0,5");
    }

    @Test
    void deveLancarExcecaoQuandoDescontoNulo() {
        assertThatThrownBy(() -> Cupom.criar("ABC123", "Desconto", null, AMANHA, false))
                .isInstanceOf(CupomInvalidoException.class);
    }

    @Test
    void deveAceitarDescontoExatamenteNoMinimo() {
        Cupom cupom = Cupom.criar("ABC123", "Desconto", new BigDecimal("0.5"), AMANHA, false);
        assertThat(cupom.getValorDesconto()).isEqualByComparingTo("0.5");
    }

    @Test
    void deveLancarExcecaoQuandoDataExpiracaoNoPassado() {
        Instant passado = Instant.now().minus(1, ChronoUnit.DAYS);
        assertThatThrownBy(() -> Cupom.criar("ABC123", "Desconto", new BigDecimal("5.00"), passado, false))
                .isInstanceOf(CupomInvalidoException.class)
                .hasMessageContaining("futuro");
    }

    @Test
    void deveLancarExcecaoQuandoDataExpiracaoNula() {
        assertThatThrownBy(() -> Cupom.criar("ABC123", "Desconto", new BigDecimal("5.00"), null, false))
                .isInstanceOf(CupomInvalidoException.class)
                .hasMessageContaining("futuro");
    }

    @Test
    void deveDeletarCupomAtivo() {
        Cupom cupom = Cupom.criar("ABC123", "Desconto", new BigDecimal("5.00"), AMANHA, false);
        Cupom deletado = cupom.deletar();

        assertThat(deletado.isDeletado()).isTrue();
        assertThat(deletado.getStatus()).isEqualTo(CupomStatus.DELETADO);
        assertThat(deletado.getId()).isEqualTo(cupom.getId());
    }

    @Test
    void deveLancarExcecaoAoDeletarCupomJaDeletado() {
        Cupom cupom = Cupom.criar("ABC123", "Desconto", new BigDecimal("5.00"), AMANHA, false);
        Cupom deletado = cupom.deletar();

        assertThatThrownBy(deletado::deletar)
                .isInstanceOf(CupomJaDeletadoException.class)
                .hasMessageContaining("já foi deletado");
    }

    @Test
    void deveReconstituirCupomCorretamente() {
        Cupom cupom = Cupom.criar("ABC123", "Desconto", new BigDecimal("5.00"), AMANHA, true);
        Cupom reconstituido = Cupom.reconstituir(
                cupom.getId(),
                cupom.getCodigo(),
                cupom.getDescricao(),
                cupom.getValorDesconto(),
                cupom.getDataExpiracao(),
                cupom.isPublicado(),
                cupom.isResgatado(),
                cupom.getStatus()
        );

        assertThat(reconstituido.getId()).isEqualTo(cupom.getId());
        assertThat(reconstituido.getCodigo()).isEqualTo(cupom.getCodigo());
        assertThat(reconstituido.getStatus()).isEqualTo(CupomStatus.ATIVO);
    }

    @Test
    void deveSanitizarCodigo() {
        assertThat(Cupom.sanitizarCodigo("A!B@C1")).isEqualTo("ABC1");
        assertThat(Cupom.sanitizarCodigo("AB CD12")).isEqualTo("ABCD12");
        assertThat(Cupom.sanitizarCodigo("A!B@C1#23")).isEqualTo("ABC123");
    }
}
