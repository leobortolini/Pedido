package com.fiap.pedido.gateway.queue.json;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservaProdutoJson {
    private Long pedidoId;
    private String cpf;
    private List<ItemPedidoReserva> itens;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemPedidoReserva {
        private Long produtoId;
        private Integer quantidade;
    }
}
