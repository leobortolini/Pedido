package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.exception.PedidoNaoEncontradoException;
import com.fiap.pedido.gateway.PedidoGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@AllArgsConstructor
@Slf4j
public class ProcessarRespostaEstoqueUsecase {

    private final PedidoGateway pedidoGateway;

    @Transactional
    public void processarRespostaEstoque(Long pedidoId, boolean estoqueDisponivel) {
        Pedido.Status pedidoStatus;

        try {
            pedidoStatus = pedidoGateway.getPedidoStatus(pedidoId);
        } catch (PedidoNaoEncontradoException e) {
            log.error("Estoque enviou resposta para o pedido {} mas nao foi encontrado", pedidoId);
            throw e;
        }

        if (pedidoStatus == Pedido.Status.ENVIADO_PEDIDO_ESTOQUE) {
            atualizarStatusDoPedido(pedidoId, estoqueDisponivel);
        } else {
            log.warn("Estoque enviou respostas repetidas para o pedido {}", pedidoId);
        }
    }

    private void atualizarStatusDoPedido(Long pedidoId, boolean estoqueDisponivel) {
        Pedido pedido = new Pedido();

        pedido.setId(pedidoId);

        if (estoqueDisponivel) {
            pedido.setStatus(Pedido.Status.PENDENTE_PAGAMENTO);
        } else {
            pedido.setStatus(Pedido.Status.SEM_ESTOQUE);
        }

        pedidoGateway.atualizarStatusPedido(pedido);
    }
}
