package com.fiap.pedido.gateway.queue;

import com.fiap.pedido.domain.Cliente;
import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.gateway.LogisticaGateway;
import com.fiap.pedido.gateway.queue.json.EntregaPedidoJson;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogisticaQueueGateway implements LogisticaGateway {
    public static final String PEDIDO_ENTREGA_OUT_0 = "pedidoEntrega-out-0";
    private final StreamBridge streamBridge;

    @Override
    public boolean enviarPedidoEntrega(Pedido pedido) {
        EntregaPedidoJson entregaPedidoJson = toEntregaPedidoJson(pedido);

        return streamBridge.send(PEDIDO_ENTREGA_OUT_0, entregaPedidoJson);
    }

    private EntregaPedidoJson toEntregaPedidoJson(Pedido pedido) {
        EntregaPedidoJson entregaPedidoJson = new EntregaPedidoJson();

        entregaPedidoJson.setPedidoId(pedido.getId());

        Cliente cliente = pedido.getCliente();

        entregaPedidoJson.setEnderecoEntrega(String.format("%s, %s", cliente.getCep(), cliente.getEndereco()));
        entregaPedidoJson.setCpf(cliente.getCpf());

        return entregaPedidoJson;
    }
}
