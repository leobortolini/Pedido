package com.fiap.pedido.usecase;

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

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class ListarPedidoUsecaseIT {

    @Autowired
    private ListarPedidoUsecase listarPedidoUsecase;

    @Test
    @Sql(scripts = "/pedido_finalizado.sql")
    void deveRetornarPedido() {
        Pedido pedido = listarPedidoUsecase.getPedido(1L);

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
