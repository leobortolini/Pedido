package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.gateway.PedidoGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarPedidoUsecaseTest {

    @Mock
    private PedidoGateway pedidoGateway;

    @InjectMocks
    private ListarPedidoUsecase listarPedidoUsecase;

    @Test
    void deveRetornarPedidoQuandoExistir() {
        Pedido pedidoMock = new Pedido();
        Long pedidoId = 1L;
        pedidoMock.setId(pedidoId);

        when(pedidoGateway.getPedido(pedidoId)).thenReturn(pedidoMock);

        Pedido pedido = listarPedidoUsecase.getPedido(pedidoId);

        assertNotNull(pedido);
        assertEquals(pedidoId, pedido.getId());
        verify(pedidoGateway).getPedido(pedidoId);
    }
}
