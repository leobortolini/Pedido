package com.fiap.pedido.gateway.database.jpa.repository;

import com.fiap.pedido.gateway.database.jpa.entity.CompraProdutoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompraProdutoEntityRepository extends JpaRepository<CompraProdutoEntity, Long> {
}
