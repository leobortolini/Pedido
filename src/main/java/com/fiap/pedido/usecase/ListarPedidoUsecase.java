package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.gateway.PedidoGateway;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ListarPedidoUsecase {
    private final PedidoGateway pedidoGateway;

    public Pedido getPedido(Long pedidoId) {
        return pedidoGateway.getPedido(pedidoId);
    }
}
