package com.fiap.pedido.gateway.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.pedido.domain.Cliente;
import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.domain.Produto;
import com.fiap.pedido.gateway.queue.json.ReservaProdutoJson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.EnableTestBinder;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.messaging.Message;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EnableTestBinder
public class ProdutoQueueGatewayIT {

    @Autowired
    private ProdutoQueueGateway produtoQueueGateway;

    @Autowired
    private OutputDestination output;

    @Test
    void deveEnviarReservaProdutoComSucesso() throws Exception {
        Pedido pedido = criarPedido();
        ReservaProdutoJson reservaEsperada = toReservaProdutoJson(pedido);

        boolean resultado = produtoQueueGateway.enviarReservaProduto(pedido);

        assertTrue(resultado);
        Message<byte[]> mensagemRecebida = output.receive();
        assertNotNull(mensagemRecebida);

        ObjectMapper objectMapper = new ObjectMapper();
        ReservaProdutoJson reservaRecebida = objectMapper.readValue(mensagemRecebida.getPayload(), ReservaProdutoJson.class);

        assertEquals(reservaEsperada.getPedidoId(), reservaRecebida.getPedidoId());
        assertEquals(reservaEsperada.getCpf(), reservaRecebida.getCpf());
        assertEquals(reservaEsperada.getItens().size(), reservaRecebida.getItens().size());

        for (int i = 0; i < reservaEsperada.getItens().size(); i++) {
            assertEquals(reservaEsperada.getItens().get(i).getProdutoId(), reservaRecebida.getItens().get(i).getProdutoId());
            assertEquals(reservaEsperada.getItens().get(i).getQuantidade(), reservaRecebida.getItens().get(i).getQuantidade());
        }
    }

    private Pedido criarPedido() {
        Cliente cliente = new Cliente();
        cliente.setCpf("111.222.333-44");

        Produto produto1 = new Produto(1L, BigDecimal.TEN, 1);
        Produto produto2 = new Produto(2L, BigDecimal.TEN, 3);

        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setCliente(cliente);
        pedido.setProdutoList(List.of(produto1, produto2));

        return pedido;
    }

    private ReservaProdutoJson toReservaProdutoJson(Pedido pedido) {
        ReservaProdutoJson reservaProdutoJson = new ReservaProdutoJson();
        reservaProdutoJson.setPedidoId(pedido.getId());
        reservaProdutoJson.setCpf(pedido.getCliente().getCpf());
        reservaProdutoJson.setItens(pedido.getProdutoList().stream().map(produto -> {
            ReservaProdutoJson.ItemPedidoReserva item = new ReservaProdutoJson.ItemPedidoReserva();
            item.setProdutoId(produto.getId());
            item.setQuantidade(produto.getQuantidade());
            return item;
        }).toList());
        return reservaProdutoJson;
    }
}
