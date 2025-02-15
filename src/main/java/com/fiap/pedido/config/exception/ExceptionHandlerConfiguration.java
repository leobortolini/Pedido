package com.fiap.pedido.config.exception;

import com.fiap.pedido.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerConfiguration {

    @ExceptionHandler(PedidoComStatusInvalidoParaPagamentoException.class)
    public ResponseEntity<ErrorJson> tratarErroAoCriarPagamentoParaPedido(PedidoComStatusInvalidoParaPagamentoException exception) {
        return ResponseEntity.status(HttpStatusCode.valueOf(exception.getHttpStatus())).body(new ErrorJson(exception.getCode(), exception.getMessage()));
    }

    @ExceptionHandler(PedidoNaoEncontradoException.class)
    public ResponseEntity<ErrorJson> tratarErroAoNaoEncontrarPedido(PedidoNaoEncontradoException exception) {
        return ResponseEntity.status(HttpStatusCode.valueOf(exception.getHttpStatus())).body(new ErrorJson(exception.getCode(), exception.getMessage()));
    }

    @ExceptionHandler(PagamentoComStatusInvalidoException.class)
    public ResponseEntity<ErrorJson> tratarErroAoTentarFinalizarPagamento(PagamentoComStatusInvalidoException exception) {
        return ResponseEntity.status(HttpStatusCode.valueOf(exception.getHttpStatus())).body(new ErrorJson(exception.getCode(), exception.getMessage()));
    }

    @ExceptionHandler(PagamentoNaoEncontradoException.class)
    public ResponseEntity<ErrorJson> tratarErroAoNaoEncontrarPedido(PagamentoNaoEncontradoException exception) {
        return ResponseEntity.status(HttpStatusCode.valueOf(exception.getHttpStatus())).body(new ErrorJson(exception.getCode(), exception.getMessage()));
    }

    @ExceptionHandler(ClienteNaoEncontradoException.class)
    public ResponseEntity<ErrorJson> tratarErroAoNaoEncontrarCliente(ClienteNaoEncontradoException exception) {
        return ResponseEntity.status(HttpStatusCode.valueOf(exception.getHttpStatus())).body(new ErrorJson(exception.getCode(), exception.getMessage()));
    }
}
