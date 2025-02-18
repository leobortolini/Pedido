package com.fiap.pedido.exception;

import lombok.Getter;

@Getter
public class PagamentoNaoEncontradoException extends RuntimeException {
    private static final String CODE = "pedido-service.pagamentoNaoEncontrado";
    private static final String MESSAGE = "Pagamento nao encontrado";
    private static final Integer HTTPSTATUS = 404;

    public int getHttpStatus() {
       return HTTPSTATUS;
    }

    public String getCode(){
       return CODE;
    }

    @Override
    public String getMessage() { return MESSAGE; }
}