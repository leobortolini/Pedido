package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Cliente;
import com.fiap.pedido.domain.Pagamento;
import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.exception.PagamentoComStatusInvalidoException;
import com.fiap.pedido.exception.PedidoComStatusInvalidoParaPagamentoException;
import com.fiap.pedido.gateway.LogisticaGateway;
import com.fiap.pedido.gateway.PagamentoGateway;
import com.fiap.pedido.gateway.PedidoGateway;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class EfetuarPagamentoUsecase {
    private final PagamentoGateway pagamentoGateway;
    private final PedidoGateway pedidoGateway;
    private final LogisticaGateway logisticaGateway;

    @Transactional
    public Pagamento criarPagamento(Long pedidoId) {
        Pedido.Status pedidoStatus = pedidoGateway.getPedidoStatus(pedidoId);

        if (pedidoStatus == Pedido.Status.PENDENTE_PAGAMENTO) {
            BigDecimal valor = pedidoGateway.getValorPedido(pedidoId);

            Pagamento pagamento = new Pagamento();
            pagamento.setValor(valor);
            pagamento.setStatus(Pagamento.Status.PENDENTE);

            Long pagamentoId = pagamentoGateway.criarPagamento(pedidoId, pagamento);

            pagamento.setId(pagamentoId);

            atualizarStatusDoPedido(pedidoId);

            return pagamento;
        } else {
            throw new PedidoComStatusInvalidoParaPagamentoException();
        }
    }

    @Transactional
    public Pagamento finalizarPagamento(Long pagamentoId) {
        Pagamento.Status pagamentoStatus = pagamentoGateway.getPagamentoStatus(pagamentoId);

        if (pagamentoStatus == Pagamento.Status.PENDENTE || pagamentoStatus == Pagamento.Status.RECUSADO) {
            Pagamento pagamento = new Pagamento();

            pagamento.setId(pagamentoId);

            pagamentoComSucesso(pagamentoId, pagamento);

            return pagamentoGateway.atualizarStatusPagamento(pagamento);
        } else {
            throw new PagamentoComStatusInvalidoException();
        }
    }

    private void atualizarStatusDoPedido(Long pedidoId) {
        Pedido pedido = new Pedido();

        pedido.setId(pedidoId);
        pedido.setStatus(Pedido.Status.PAGAMENTO_INICIADO);

        pedidoGateway.atualizarStatusPedido(pedido);
    }


    private void pagamentoComSucesso(Long pagamentoId, Pagamento pagamento) {
        pagamento.setStatus(Pagamento.Status.FINALIZADO);

        Long pedidoId = pagamentoGateway.getPedidoIdDoPagamento(pagamentoId);
        Cliente cliente = pedidoGateway.getCliente(pedidoId);
        Pedido pedido = new Pedido();

        pedido.setId(pedidoId);
        pedido.setCliente(cliente);

        enviarParaLogistica(pedido);
    }

    private void enviarParaLogistica(Pedido pedido) {
        boolean pedidoEnviadoParaLogistica = logisticaGateway.enviarPedidoEntrega(pedido);

        if (pedidoEnviadoParaLogistica) {
            pedido.setStatus(Pedido.Status.ENVIADO_EXPEDICAO);
        } else {
            pedido.setStatus(Pedido.Status.PENDENTE_ENVIAR_EXPEDICAO);
        }

        pedidoGateway.atualizarStatusPedido(pedido);
    }
}
