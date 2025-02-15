package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.exception.PedidoNaoEncontradoException;
import com.fiap.pedido.gateway.PedidoGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class ProcessarRespostaLogisticaUsecase {
    private final PedidoGateway pedidoGateway;

    public void processarRespostaLogistica(Long pedidoId, String status) {
        Pedido.Status pedidoStatus;

        try {
            pedidoStatus = pedidoGateway.getPedidoStatus(pedidoId);
        } catch (PedidoNaoEncontradoException e) {
            log.error("Pedido com ID {} foi enviado pela logística para ser atualizado ao Status {} mas não foi encontrado", pedidoId, status);
            return;
        }

        if (pedidoStatus == Pedido.Status.ENVIADO_EXPEDICAO || pedidoStatus == Pedido.Status.EM_ENTREGA) {
            Pedido pedido = new Pedido();

            pedido.setId(pedidoId);
            pedido.setStatus(Pedido.Status.valueOf(status));

            boolean atualizado = pedidoGateway.atualizarStatusPedido(pedido);

            if (!atualizado) {
                log.error("Pedido com ID {} foi enviado pela logística para ser atualizado ao Status {} mas ocorreu um erro", pedidoId, status);
            }
        } else {
            log.warn("Logistica enviou respostas repetidas para o pedido {}", pedidoId);
        }

    }
}
