package com.fiap.pedido.gateway;

import com.fiap.pedido.domain.Pedido;

public interface LogisticaGateway {
    boolean enviarPedidoEntrega(Pedido pedido);
}
