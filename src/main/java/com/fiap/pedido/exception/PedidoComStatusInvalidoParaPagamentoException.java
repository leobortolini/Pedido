package com.fiap.pedido.exception;

import lombok.Getter;

public class PedidoComStatusInvalidoParaPagamentoException extends RuntimeException {
    private static final String code = "pedido-service.erroAoCriarPagamento";
    private static final String message = "Pedido com status invalido para realizar pagamento";
    private static final Integer httpStatus = 400;

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getCode(){
        return code;
    }

    @Override
    public String getMessage() { return message; }

}
