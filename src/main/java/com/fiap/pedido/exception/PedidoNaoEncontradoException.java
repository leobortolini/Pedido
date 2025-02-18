package com.fiap.pedido.exception;

import lombok.Getter;

@Getter
public class PedidoNaoEncontradoException extends RuntimeException {
    private static final String CODE = "pedido-service.pedidoNaoEncontrado";
    private static final String MESSAGE = "Pedido nao encontrado";
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