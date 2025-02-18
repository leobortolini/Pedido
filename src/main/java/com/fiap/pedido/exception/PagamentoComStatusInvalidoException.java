package com.fiap.pedido.exception;

import lombok.Getter;

@Getter
public class PagamentoComStatusInvalidoException extends RuntimeException {
    private static final String CODE = "pedido-service.pagamentoComStatusInvalido";
    private static final String MESSAGE = "Pagamento com status invalido para ser finalizado";
    private static final Integer HTTPSTATUS = 400;

    public int getHttpStatus() {
        return HTTPSTATUS;
    }

    public String getCode(){
        return CODE;
    }

    @Override
    public String getMessage() { return MESSAGE; }
}