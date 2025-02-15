package com.fiap.pedido.controller.api.request;

import com.fiap.pedido.domain.Produto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CriarPedidoJson {
    private String cpf;
    private List<Produto> itens;
}
