package com.henrique.cupom.service;

import com.henrique.cupom.domain.CupomStatus;
import com.henrique.cupom.domain.exception.CupomJaDeletadoException;
import com.henrique.cupom.domain.exception.CupomNaoEncontradoException;
import com.henrique.cupom.dto.CriarCupomRequest;
import com.henrique.cupom.entity.CupomEntity;
import com.henrique.cupom.repository.CupomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CupomServiceTest {

    @Mock
    private CupomRepository repository;

    @InjectMocks
    private CupomService service;

    private final Instant amanha = Instant.now().plus(1, ChronoUnit.DAYS);

    private CupomEntity entityAtiva() {
        CupomEntity e = new CupomEntity();
        e.setId(UUID.randomUUID());
        e.setCodigo("ABC123");
        e.setDescricao("Desconto 10%");
        e.setValorDesconto(new BigDecimal("10.00"));
        e.setDataExpiracao(amanha);
        e.setPublicado(false);
        e.setResgatado(false);
        e.setStatus(CupomStatus.ATIVO);
        return e;
    }

    @Test
    void deveCriarCupom() {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        CriarCupomRequest request = new CriarCupomRequest("ABC123", "Desconto 10%",
                new BigDecimal("10.00"), amanha, false);

        var cupom = service.criar(request);

        assertThat(cupom.getCodigo()).isEqualTo("ABC123");
        assertThat(cupom.getStatus()).isEqualTo(CupomStatus.ATIVO);
        verify(repository).save(any());
    }

    @Test
    void deveCriarCupomComCaracteresEspeciaisNoCodigo() {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        // "A!B@C1#23" remove !@# => "ABC123" (6 chars)
        CriarCupomRequest request = new CriarCupomRequest("A!B@C1#23", "Desconto",
                new BigDecimal("5.00"), amanha, false);

        var cupom = service.criar(request);

        assertThat(cupom.getCodigo()).isEqualTo("ABC123");
    }

    @Test
    void deveDeletarCupom() {
        CupomEntity entity = entityAtiva();
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));

        service.deletar(entity.getId());

        verify(repository).save(argThat(e -> e.getStatus() == CupomStatus.DELETADO));
    }

    @Test
    void deveLancarExcecaoAoDeletarCupomInexistente() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deletar(id))
                .isInstanceOf(CupomNaoEncontradoException.class);
    }

    @Test
    void deveLancarExcecaoAoDeletarCupomJaDeletado() {
        CupomEntity entity = entityAtiva();
        entity.setStatus(CupomStatus.DELETADO);
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.deletar(entity.getId()))
                .isInstanceOf(CupomJaDeletadoException.class);
    }

    @Test
    void deveCriarCupomPublicado() {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        CriarCupomRequest request = new CriarCupomRequest("ABC123", "Desconto",
                new BigDecimal("5.00"), amanha, true);

        var cupom = service.criar(request);

        assertThat(cupom.isPublicado()).isTrue();
    }
}
