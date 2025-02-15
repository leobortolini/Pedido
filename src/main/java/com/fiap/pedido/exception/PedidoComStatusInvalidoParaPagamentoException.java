package com.fiap.pedido.exception;

import lombok.Getter;

@Getter
public class PedidoComStatusInvalidoParaPagamentoException extends SystemBaseException {
    private final String code = "pedido-service.erroAoCriarPagamento";
    private final String message = "Pedido com status invalido para realizar pagamento";
    private final Integer httpStatus = 400;
}
