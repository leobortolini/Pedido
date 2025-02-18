package com.fiap.pedido.exception;

public class PedidoComStatusInvalidoParaPagamentoException extends RuntimeException {
    private static final String CODE = "pedido-service.erroAoCriarPagamento";
    private static final String MESSAGE = "Pedido com status invalido para realizar pagamento";
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
