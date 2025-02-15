package com.fiap.pedido.exception;

import lombok.Getter;

@Getter
public class ClienteNaoEncontradoException extends SystemBaseException {
  private final String code = "pedido-service.clienteNaoEncontrado";
  private final String message = "Cliente nao encontrado";
  private final Integer httpStatus = 404;
}