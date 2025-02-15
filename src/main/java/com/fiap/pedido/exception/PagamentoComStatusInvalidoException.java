package com.fiap.pedido.exception;

import lombok.Getter;

@Getter
public class PagamentoComStatusInvalidoException extends SystemBaseException {
    private final String code = "pedido-service.pagamentoComStatusInvalido";
    private final String message = "Pagamento com status invalido para ser finalizado";
    private final Integer httpStatus = 400;
}