package com.fiap.pedido.exception;

import lombok.Getter;

@Getter
public class PedidoNaoEncontradoException extends SystemBaseException {
  private final String code = "pedido-service.pedidoNaoEncontrado";
  private final String message = "Pedido nao encontrado";
  private final Integer httpStatus = 404;
}