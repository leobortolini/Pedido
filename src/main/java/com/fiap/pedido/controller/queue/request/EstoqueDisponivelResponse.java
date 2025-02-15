package com.fiap.pedido.controller.queue.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EstoqueDisponivelResponse {
    private Long pedidoId;
    private boolean estoqueDisponivel;
}
