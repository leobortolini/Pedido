package com.fiap.pedido.gateway.database.jpa.repository;

import com.fiap.pedido.gateway.database.jpa.entity.PagamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoEntityRepository extends JpaRepository<PagamentoEntity, Long> {
}
