package com.fiap.pedido.gateway;

import com.fiap.pedido.domain.Pedido;

public interface ProdutoGateway {

    boolean enviarReservaProduto(Pedido pedido);

}
