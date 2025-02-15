package com.fiap.pedido.gateway.queue;

import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.gateway.ProdutoGateway;
import com.fiap.pedido.gateway.queue.json.ReservaProdutoJson;
import com.fiap.pedido.gateway.queue.json.ReservaProdutoJson.ItemPedidoReserva;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProdutoQueueGateway implements ProdutoGateway {

    public static final String PEDIDO_ESTOQUE_OUT_0 = "pedidoEstoque-out-0";
    private final StreamBridge streamBridge;

    @Override
    public boolean enviarReservaProduto(Pedido pedido) {
        ReservaProdutoJson reservaProdutoJson = toReservaProdutoJson(pedido);

        return streamBridge.send(PEDIDO_ESTOQUE_OUT_0, reservaProdutoJson);
    }

    private ReservaProdutoJson toReservaProdutoJson(Pedido pedido) {
        ReservaProdutoJson reservaProdutoJson = new ReservaProdutoJson();

        reservaProdutoJson.setPedidoId(pedido.getId());
        reservaProdutoJson.setCpf(pedido.getCliente().getCpf());
        reservaProdutoJson.setItens(pedido.getProdutoList().stream().map(produto -> {
            ItemPedidoReserva itemPedidoReserva = new ItemPedidoReserva();

            itemPedidoReserva.setProdutoId(produto.getId());
            itemPedidoReserva.setQuantidade(produto.getQuantidade());

            return itemPedidoReserva;
        }).toList());

        return reservaProdutoJson;
    }
}
