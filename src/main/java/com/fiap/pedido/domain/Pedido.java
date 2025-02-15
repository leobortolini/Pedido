package com.fiap.pedido.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Pedido {
    private Long id;
    private List<Produto> produtoList;
    private Cliente cliente;
    private Pagamento pagamento;
    private Status status;

    public enum Status {
        PENDENTE_ESTOQUE,
        ENVIADO_PEDIDO_ESTOQUE,
        SEM_ESTOQUE,
        PENDENTE_PAGAMENTO,
        PAGAMENTO_INICIADO,
        PENDENTE_ENVIAR_EXPEDICAO,
        ENVIADO_EXPEDICAO,
        EM_ENTREGA,
        FINALIZADO
    }
}
