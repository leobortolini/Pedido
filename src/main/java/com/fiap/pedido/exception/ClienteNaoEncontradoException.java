package com.fiap.pedido.exception;

public class ClienteNaoEncontradoException extends RuntimeException {
    private static final String code = "pedido-service.clienteNaoEncontrado";
    private static final String message = "Cliente nao encontrado";
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