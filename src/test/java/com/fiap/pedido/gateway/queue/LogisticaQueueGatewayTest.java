package com.fiap.pedido.gateway.queue;

import com.fiap.pedido.domain.Cliente;
import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.gateway.queue.json.EntregaPedidoJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LogisticaQueueGatewayTest {

    @Mock
    private StreamBridge streamBridge;

    @InjectMocks
    private LogisticaQueueGateway logisticaQueueGateway;

    private Pedido pedido;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setCpf("12345678900");
        cliente.setCep("12345-678");
        cliente.setEndereco("Rua Teste, 123");

        pedido = new Pedido();
        pedido.setId(1L);
        pedido.setCliente(cliente);
    }

    @Test
    void deveEnviarPedidoParaEntregaComSucesso() {
        when(streamBridge.send(eq(LogisticaQueueGateway.PEDIDO_ENTREGA_OUT_0), any(EntregaPedidoJson.class))).thenReturn(true);
        boolean resultado = logisticaQueueGateway.enviarPedidoEntrega(pedido);

        assertTrue(resultado);
        ArgumentCaptor<EntregaPedidoJson> captor = ArgumentCaptor.forClass(EntregaPedidoJson.class);

        verify(streamBridge, times(1)).send(eq(LogisticaQueueGateway.PEDIDO_ENTREGA_OUT_0), captor.capture());
        EntregaPedidoJson captura = captor.getValue();

        assertEquals(pedido.getId(), captura.getPedidoId());
        assertEquals(String.format("%s, %s", cliente.getCep(), cliente.getEndereco()), captura.getEnderecoEntrega());
        assertEquals(cliente.getCpf(), captura.getCpf());
    }

    @Test
    void deveRetornarFalsoQuandoNaoConseguirEnviarParaEntrega() {
        when(streamBridge.send(eq(LogisticaQueueGateway.PEDIDO_ENTREGA_OUT_0), any(EntregaPedidoJson.class))).thenReturn(false);
        boolean resultado = logisticaQueueGateway.enviarPedidoEntrega(pedido);

        assertFalse(resultado);
        ArgumentCaptor<EntregaPedidoJson> captor = ArgumentCaptor.forClass(EntregaPedidoJson.class);

        verify(streamBridge, times(1)).send(eq(LogisticaQueueGateway.PEDIDO_ENTREGA_OUT_0), captor.capture());
        EntregaPedidoJson captura = captor.getValue();
        assertEquals(pedido.getId(), captura.getPedidoId());
        assertEquals(String.format("%s, %s", cliente.getCep(), cliente.getEndereco()), captura.getEnderecoEntrega());
        assertEquals(cliente.getCpf(), captura.getCpf());
    }
}
