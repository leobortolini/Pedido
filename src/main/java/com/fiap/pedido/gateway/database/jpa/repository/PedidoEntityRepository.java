package com.fiap.pedido.gateway.database.jpa.repository;

import com.fiap.pedido.gateway.database.jpa.entity.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoEntityRepository extends JpaRepository<PedidoEntity, Long> {
}
