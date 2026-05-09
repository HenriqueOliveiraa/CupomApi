package com.henrique.cupom.repository;

import com.henrique.cupom.entity.CupomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CupomRepository extends JpaRepository<CupomEntity, UUID> {
}
