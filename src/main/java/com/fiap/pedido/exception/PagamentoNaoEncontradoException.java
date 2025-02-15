package com.fiap.pedido.exception;

import lombok.Getter;

@Getter
public class PagamentoNaoEncontradoException extends SystemBaseException {
  private final String code = "pedido-service.pagamentoNaoEncontrado";
  private final String message = "Pagamento nao encontrado";
  private final Integer httpStatus = 404;
}