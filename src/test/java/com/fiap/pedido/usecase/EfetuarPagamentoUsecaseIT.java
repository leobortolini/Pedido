package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Pagamento;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.EnableTestBinder;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@EnableTestBinder
class EfetuarPagamentoUsecaseIT {

    @Autowired
    private EfetuarPagamentoUsecase efetuarPagamentoUsecase;

    @Test
    @Sql(scripts = { "/limpar_dados.sql", "/pedido_pendente_criar_pagamento.sql" })
    void deveCriarPagamento() {
        Pagamento pagamento = efetuarPagamentoUsecase.criarPagamento(1L);

        assertEquals(Pagamento.Status.PENDENTE, pagamento.getStatus());
        assertEquals("30.00", pagamento.getValor().toPlainString());
        assertEquals(1L, pagamento.getId());
    }


    @Test
    @Sql(scripts = { "/limpar_dados.sql", "/pedido_pendente_finalizar_pagamento.sql" })
    void deveFinalizarPagamento() {
        Pagamento pagamento = efetuarPagamentoUsecase.finalizarPagamento(1L);

        assertEquals(Pagamento.Status.FINALIZADO, pagamento.getStatus());
        assertEquals("30.00", pagamento.getValor().toPlainString());
        assertEquals(1L, pagamento.getId());
    }
}
