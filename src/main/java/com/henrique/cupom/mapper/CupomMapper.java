package com.henrique.cupom.mapper;

import com.henrique.cupom.domain.Cupom;
import com.henrique.cupom.entity.CupomEntity;

public class CupomMapper {

    private CupomMapper() {}

    public static CupomEntity toEntity(Cupom cupom) {
        CupomEntity entity = new CupomEntity();
        entity.setId(cupom.getId());
        entity.setCodigo(cupom.getCodigo());
        entity.setDescricao(cupom.getDescricao());
        entity.setValorDesconto(cupom.getValorDesconto());
        entity.setDataExpiracao(cupom.getDataExpiracao());
        entity.setPublicado(cupom.isPublicado());
        entity.setResgatado(cupom.isResgatado());
        entity.setStatus(cupom.getStatus());
        return entity;
    }

    public static Cupom toDomain(CupomEntity entity) {
        return Cupom.reconstituir(
                entity.getId(),
                entity.getCodigo(),
                entity.getDescricao(),
                entity.getValorDesconto(),
                entity.getDataExpiracao(),
                entity.isPublicado(),
                entity.isResgatado(),
                entity.getStatus()
        );
    }
}
