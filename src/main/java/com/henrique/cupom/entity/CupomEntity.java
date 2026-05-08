package com.henrique.cupom.entity;

import com.henrique.cupom.domain.CupomStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "cupons")
public class CupomEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, length = 6)
    private String codigo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private BigDecimal valorDesconto;

    @Column(nullable = false)
    private Instant dataExpiracao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CupomStatus status;

    @Column(nullable = false)
    private boolean publicado;

    @Column(nullable = false)
    private boolean resgatado;
}