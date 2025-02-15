package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Cliente;
import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.domain.Produto;
import com.fiap.pedido.exception.ClienteNaoEncontradoException;
import com.fiap.pedido.gateway.ClienteGateway;
import com.fiap.pedido.gateway.PedidoGateway;
import com.fiap.pedido.gateway.ProdutoGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class CriarPedidoUsecase {

    private final PedidoGateway pedidoGateway;
    private final ClienteGateway clienteGateway;
    private final ProdutoGateway produtoGateway;

    @Transactional
    public Pedido criarPedido(String cpf, List<Produto> itens) {
        Pedido pedido = new Pedido();
        Cliente cliente  = clienteGateway.buscarCliente(cpf);

        if (cliente == null) {
            throw new ClienteNaoEncontradoException();
        }
        pedido.setCliente(cliente);
        pedido.setProdutoList(itens);
        pedido.setStatus(Pedido.Status.PENDENTE_ESTOQUE);
        Long pedidoId = pedidoGateway.criarPedido(pedido);

        pedido.setId(pedidoId);
        boolean pedidoEnviadoAoEstoque = produtoGateway.enviarReservaProduto(pedido);

        if (pedidoEnviadoAoEstoque) {
            pedido.setStatus(Pedido.Status.ENVIADO_PEDIDO_ESTOQUE);

            pedidoGateway.atualizarStatusPedido(pedido);
        }
        log.info("Pedido {} enviado ao estoque: {}", pedidoId, pedidoEnviadoAoEstoque);

        return pedido;
    }

}
