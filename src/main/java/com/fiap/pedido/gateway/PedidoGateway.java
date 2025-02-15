package com.fiap.pedido.gateway;

import com.fiap.pedido.domain.Cliente;
import com.fiap.pedido.domain.Pedido;

import java.math.BigDecimal;

public interface PedidoGateway {
    Long criarPedido(Pedido pedido);
    boolean atualizarStatusPedido(Pedido pedido);
    BigDecimal getValorPedido(Long pedidoId);
    Pedido.Status getPedidoStatus(Long pedidoId);
    Cliente getCliente(Long pedidoId);
    Pedido getPedido(Long pedidoId);
}
