package com.henrique.cupom.service;

import com.henrique.cupom.domain.Cupom;
import com.henrique.cupom.domain.exception.CupomNaoEncontradoException;
import com.henrique.cupom.dto.CriarCupomRequest;
import com.henrique.cupom.entity.CupomEntity;
import com.henrique.cupom.mapper.CupomMapper;
import com.henrique.cupom.repository.CupomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CupomService {

    private final CupomRepository repository;

    public CupomService(CupomRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Cupom criar(CriarCupomRequest request) {
        Cupom cupom = Cupom.criar(
                request.code(),
                request.description(),
                request.discountValue(),
                request.expirationDate(),
                request.published()
        );
        CupomEntity saved = repository.save(CupomMapper.toEntity(cupom));
        return CupomMapper.toDomain(saved);
    }

    @Transactional
    public void deletar(UUID id) {
        CupomEntity entity = repository.findById(id)
                .orElseThrow(() -> new CupomNaoEncontradoException("Cupom não encontrado: " + id));
        Cupom deletado = CupomMapper.toDomain(entity).deletar();
        repository.save(CupomMapper.toEntity(deletado));
    }
}
