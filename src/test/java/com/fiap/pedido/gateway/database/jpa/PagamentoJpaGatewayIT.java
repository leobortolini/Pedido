package com.fiap.pedido.gateway.database.jpa;

import com.fiap.pedido.domain.Pagamento;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class PagamentoJpaGatewayIT {

    @Autowired
    private PagamentoJpaGateway pagamentoJpaGateway;

    @Test
    @Sql(scripts = "/pedido_pendente_criar_pagamento.sql")
    void deveCriarPagamento() {
        Pagamento pagamento = new Pagamento();

        pagamento.setStatus(Pagamento.Status.PENDENTE);
        pagamento.setValor(BigDecimal.TEN);
        Long id = pagamentoJpaGateway.criarPagamento(1L, pagamento);

        assertNotNull(id);
    }

    @Test
    @Sql(scripts = "/pedido_finalizado.sql")
    void deveRetornarStatusDoPagamento() {
        Pagamento.Status status = pagamentoJpaGateway.getPagamentoStatus(1L);

        assertEquals(Pagamento.Status.FINALIZADO, status);
    }

    @Test
    @Sql(scripts = "/pedido_finalizado.sql")
    void deveRetornarIdDoPedidoDoPagamento() {
        Long pedidoId = pagamentoJpaGateway.getPedidoIdDoPagamento(1L);

        assertEquals(1L, pedidoId);
    }

    @Test
    @Sql(scripts = "/pedido_pendente_finalizar_pagamento.sql")
    void deveAtualizarStatusDoPagamento() {
        Pagamento pagamento = new Pagamento();
        
        pagamento.setId(1L);
        pagamento.setStatus(Pagamento.Status.FINALIZADO);

        Pagamento pagamentoAtualizado = pagamentoJpaGateway.atualizarStatusPagamento(pagamento);

        assertEquals(Pagamento.Status.FINALIZADO, pagamentoAtualizado.getStatus());
    }
}
