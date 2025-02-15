package com.fiap.pedido.gateway.queue;

import com.fiap.pedido.domain.Cliente;
import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.domain.Produto;
import com.fiap.pedido.gateway.queue.json.ReservaProdutoJson;
import com.fiap.pedido.gateway.queue.json.ReservaProdutoJson.ItemPedidoReserva;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProdutoQueueGatewayTest {

    @Mock
    private StreamBridge streamBridge;

    @InjectMocks
    private ProdutoQueueGateway produtosQueueGateway;

    private Pedido pedido;
    private Produto produto1;
    private Produto produto2;

    @BeforeEach
    void setUp() {
        Cliente cliente = new Cliente();
        cliente.setCpf("12345678900");

        produto1 = new Produto();
        produto1.setId(1L);
        produto1.setQuantidade(2);

        produto2 = new Produto();
        produto2.setId(2L);
        produto2.setQuantidade(3);

        pedido = new Pedido();
        pedido.setId(100L);
        pedido.setCliente(cliente);
        pedido.setProdutoList(Arrays.asList(produto1, produto2));
    }

    @Test
    void deveEnviarReservaProdutoComSucesso() {
        // Arrange
        ReservaProdutoJson reservaProdutoJson = new ReservaProdutoJson();

        reservaProdutoJson.setPedidoId(pedido.getId());
        reservaProdutoJson.setCpf(pedido.getCliente().getCpf());
        reservaProdutoJson.setItens(Arrays.asList(
                new ItemPedidoReserva(produto1.getId(), produto1.getQuantidade()),
                new ItemPedidoReserva(produto2.getId(), produto2.getQuantidade())
        ));
        when(streamBridge.send(eq(ProdutoQueueGateway.PEDIDO_ESTOQUE_OUT_0), any(ReservaProdutoJson.class))).thenReturn(true);

        boolean resultado = produtosQueueGateway.enviarReservaProduto(pedido);

        assertTrue(resultado);
        ArgumentCaptor<ReservaProdutoJson> captor = ArgumentCaptor.forClass(ReservaProdutoJson.class);

        verify(streamBridge, times(1)).send(eq(ProdutoQueueGateway.PEDIDO_ESTOQUE_OUT_0), captor.capture());
        ReservaProdutoJson captura = captor.getValue();

        assertEquals(pedido.getId(), captura.getPedidoId());
        assertEquals(pedido.getCliente().getCpf(), captura.getCpf());
        assertEquals(2, captura.getItens().size());
        assertEquals(produto1.getId(), captura.getItens().get(0).getProdutoId());
        assertEquals(produto1.getQuantidade(), captura.getItens().get(0).getQuantidade());
        assertEquals(produto2.getId(), captura.getItens().get(1).getProdutoId());
        assertEquals(produto2.getQuantidade(), captura.getItens().get(1).getQuantidade());
    }

    @Test
    void deveRetornarFalsoQuandoNaoConseguirEnviarPedidoDeReserva() {
        ReservaProdutoJson reservaProdutoJson = new ReservaProdutoJson();

        reservaProdutoJson.setPedidoId(pedido.getId());
        reservaProdutoJson.setCpf(pedido.getCliente().getCpf());
        reservaProdutoJson.setItens(Arrays.asList(
                new ItemPedidoReserva(produto1.getId(), produto1.getQuantidade()),
                new ItemPedidoReserva(produto2.getId(), produto2.getQuantidade())
        ));
        when(streamBridge.send(eq(ProdutoQueueGateway.PEDIDO_ESTOQUE_OUT_0), any(ReservaProdutoJson.class))).thenReturn(false);

        boolean resultado = produtosQueueGateway.enviarReservaProduto(pedido);

        assertFalse(resultado);
        ArgumentCaptor<ReservaProdutoJson> captor = ArgumentCaptor.forClass(ReservaProdutoJson.class);

        verify(streamBridge, times(1)).send(eq(ProdutoQueueGateway.PEDIDO_ESTOQUE_OUT_0), captor.capture());
        ReservaProdutoJson captura = captor.getValue();

        assertEquals(pedido.getId(), captura.getPedidoId());
        assertEquals(pedido.getCliente().getCpf(), captura.getCpf());
        assertEquals(2, captura.getItens().size());
        assertEquals(produto1.getId(), captura.getItens().get(0).getProdutoId());
        assertEquals(produto1.getQuantidade(), captura.getItens().get(0).getQuantidade());
        assertEquals(produto2.getId(), captura.getItens().get(1).getProdutoId());
        assertEquals(produto2.getQuantidade(), captura.getItens().get(1).getQuantidade());
    }
}
