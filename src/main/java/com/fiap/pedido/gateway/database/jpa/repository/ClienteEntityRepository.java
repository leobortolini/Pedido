package com.fiap.pedido.gateway.database.jpa.repository;

import com.fiap.pedido.gateway.database.jpa.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteEntityRepository extends JpaRepository<ClienteEntity, Long> {

    ClienteEntity findFirstByCpf(String cpf);
}
