package com.fiap.pedido.gateway.queue.json;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntregaPedidoJson {
    private Long pedidoId;
    private String cpf;
    private String enderecoEntrega;
}
