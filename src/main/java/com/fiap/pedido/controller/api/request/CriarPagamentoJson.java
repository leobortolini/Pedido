package com.fiap.pedido.controller.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CriarPagamentoJson {
    private Long pedidoId;
}
