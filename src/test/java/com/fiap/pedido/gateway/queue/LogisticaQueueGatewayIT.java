package com.fiap.pedido.gateway.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.pedido.domain.Cliente;
import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.gateway.queue.json.EntregaPedidoJson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.EnableTestBinder;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.messaging.Message;

import static com.fiap.pedido.gateway.queue.LogisticaQueueGateway.PEDIDO_ENTREGA_OUT_0;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EnableTestBinder
class LogisticaQueueGatewayIT {

    @Autowired
    private LogisticaQueueGateway logisticaQueueGateway;

    @Autowired
    private OutputDestination output;

    private static final String DESTINO_ENTREGA = PEDIDO_ENTREGA_OUT_0;

    @AfterEach
    void cleanup() {
        output.clear();
    }

    @Test
    void deveEnviarPedidoEntregaComSucesso() throws Exception {
        Pedido pedido = criarPedido();
        EntregaPedidoJson entregaEsperada = toEntregaPedidoJson(pedido);

        boolean resultado = logisticaQueueGateway.enviarPedidoEntrega(pedido);

        assertTrue(resultado);
        Message<byte[]> mensagemRecebida = output.receive(5000, DESTINO_ENTREGA);

        assertNotNull(mensagemRecebida);
        ObjectMapper objectMapper = new ObjectMapper();
        EntregaPedidoJson entregaRecebida = objectMapper.readValue(mensagemRecebida.getPayload(), EntregaPedidoJson.class);

        assertEquals(entregaEsperada.getPedidoId(), entregaRecebida.getPedidoId());
        assertEquals(entregaEsperada.getEnderecoEntrega(), entregaRecebida.getEnderecoEntrega());
        assertEquals(entregaEsperada.getCpf(), entregaRecebida.getCpf());
    }

    private Pedido criarPedido() {
        Cliente cliente = new Cliente();
        cliente.setCep("12345-678");
        cliente.setEndereco("Rua Exemplo, 123");
        cliente.setCpf("111.222.333-44");

        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setCliente(cliente);

        return pedido;
    }

    private EntregaPedidoJson toEntregaPedidoJson(Pedido pedido) {
        EntregaPedidoJson entregaPedidoJson = new EntregaPedidoJson();
        entregaPedidoJson.setPedidoId(pedido.getId());
        entregaPedidoJson.setEnderecoEntrega(pedido.getCliente().getCep() + ", " + pedido.getCliente().getEndereco());
        entregaPedidoJson.setCpf(pedido.getCliente().getCpf());
        return entregaPedidoJson;
    }
}
