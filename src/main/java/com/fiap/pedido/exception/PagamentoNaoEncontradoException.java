package com.fiap.pedido.exception;

import lombok.Getter;

@Getter
public class PagamentoNaoEncontradoException extends RuntimeException {
    private static final String code = "pedido-service.pagamentoNaoEncontrado";
    private static final String message = "Pagamento nao encontrado";
    private static final Integer httpStatus = 404;

    public int getHttpStatus() {
       return httpStatus;
    }

    public String getCode(){
       return code;
    }

    @Override
    public String getMessage() { return message; }
}