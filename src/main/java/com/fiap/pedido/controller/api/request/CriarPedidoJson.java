package com.fiap.pedido.controller.api.request;

import com.fiap.pedido.domain.Produto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CriarPedidoJson {
    private String cpf;
    private List<Produto> itens;
}
