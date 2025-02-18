package com.fiap.pedido.exception;

import lombok.Getter;

@Getter
public class PagamentoComStatusInvalidoException extends RuntimeException {
    private static final String code = "pedido-service.pagamentoComStatusInvalido";
    private static final String message = "Pagamento com status invalido para ser finalizado";
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