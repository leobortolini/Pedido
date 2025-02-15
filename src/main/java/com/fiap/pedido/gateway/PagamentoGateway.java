package com.fiap.pedido.gateway;

import com.fiap.pedido.domain.Pagamento;

public interface PagamentoGateway {
    Long criarPagamento(Long pedidoId, Pagamento pagamento);
    Pagamento.Status getPagamentoStatus(Long pagamentoId);
    Pagamento atualizarStatusPagamento(Pagamento pagamento);
    Long getPedidoIdDoPagamento(Long pagamentoId);
}
