package com.fiap.pedido.gateway.database.jpa;

import com.fiap.pedido.domain.Cliente;
import com.fiap.pedido.domain.Pagamento;
import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.domain.Produto;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class PedidoJpaGatewayIT {
    @Autowired
    private PedidoJpaGateway pedidoJpaGateway;

    @Test
    void deveCriarPedido() {
        Pedido pedido = new Pedido();

        pedido.setStatus(Pedido.Status.PENDENTE_ESTOQUE);
        Cliente cliente = new Cliente();

        cliente.setCpf("000.000.000-00");
        cliente.setEndereco("Endereco");
        cliente.setCep("00.000-000");
        cliente.setNome("Leonardo Bortolini");
        pedido.setCliente(cliente);
        pedido.setProdutoList(List.of(new Produto(1L, BigDecimal.TEN, 1)));

        Long pedidoId = pedidoJpaGateway.criarPedido(pedido);

        assertNotNull(pedidoId);
    }

    @Test
    @Sql(scripts = "/pedido_pendente_estoque.sql")
    void deveAtualizarStatusDoPedido() {
        Pedido pedido = new Pedido();

        pedido.setId(1L);
        pedido.setStatus(Pedido.Status.SEM_ESTOQUE);

        boolean pedidoAtualizado = pedidoJpaGateway.atualizarStatusPedido(pedido);

        assertTrue(pedidoAtualizado);
    }

    @Test
    @Sql(scripts = "/pedido_pendente_criar_pagamento.sql")
    void deveRetornarValorDoPedido() {
        BigDecimal valorPedido = pedidoJpaGateway.getValorPedido(1L);

        assertEquals("30.00", valorPedido.toPlainString());
    }

    @Test
    @Sql(scripts = "/pedido_finalizado.sql")
    void deveRetornarStatusDoPedido() {
        Pedido.Status pedidoStatus = pedidoJpaGateway.getPedidoStatus(1L);

        assertEquals(Pedido.Status.FINALIZADO, pedidoStatus);
    }

    @Test
    @Sql(scripts = "/pedido_finalizado.sql")
    void deveRetornarClienteDoPedido() {
        Cliente cliente = pedidoJpaGateway.getCliente(1L);

        assertNotNull(cliente);
        assertEquals("000.000.000-00", cliente.getCpf());
    }

    @Test
    @Sql(scripts = "/pedido_finalizado.sql")
    void deveRetornarPedido() {
        Pedido pedido = pedidoJpaGateway.getPedido(1L);

        assertEquals(1L, pedido.getId());
        assertEquals(Pedido.Status.FINALIZADO, pedido.getStatus());
        assertCliente(pedido.getCliente());
        assertPagamento(pedido.getPagamento());
        assertProduto(pedido.getProdutoList().getFirst());
    }

    private static void assertProduto(Produto produto) {
        assertEquals(1L, produto.getId());
        assertEquals(3, produto.getQuantidade());
        assertEquals("10.00", produto.getPreco().toPlainString());
    }

    private static void assertCliente(Cliente cliente) {
        assertEquals("000.000.000-00", cliente.getCpf());
        assertEquals("95180-000", cliente.getCep());
        assertEquals("endereco", cliente.getEndereco());
        assertEquals("Leonardo Bortolini", cliente.getNome());
    }

    private static void assertPagamento(Pagamento pagamento) {
        assertEquals(1L, pagamento.getId());
        assertEquals(Pagamento.Status.FINALIZADO, pagamento.getStatus());
        assertEquals("30.00", pagamento.getValor().toPlainString());
    }
}
