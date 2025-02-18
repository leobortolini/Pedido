package com.fiap.pedido.exception;

public class ClienteNaoEncontradoException extends RuntimeException {
    private static final String CODE = "pedido-service.clienteNaoEncontrado";
    private static final String MESSAGE = "Cliente nao encontrado";
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