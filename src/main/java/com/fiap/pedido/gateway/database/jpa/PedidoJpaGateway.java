package com.fiap.pedido.gateway.database.jpa;

import com.fiap.pedido.domain.Cliente;
import com.fiap.pedido.domain.Pagamento;
import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.domain.Produto;
import com.fiap.pedido.exception.PedidoNaoEncontradoException;
import com.fiap.pedido.gateway.PedidoGateway;
import com.fiap.pedido.gateway.database.jpa.entity.CompraProdutoEntity;
import com.fiap.pedido.gateway.database.jpa.entity.ClienteEntity;
import com.fiap.pedido.gateway.database.jpa.entity.PagamentoEntity;
import com.fiap.pedido.gateway.database.jpa.entity.PedidoEntity;
import com.fiap.pedido.gateway.database.jpa.repository.ClienteEntityRepository;
import com.fiap.pedido.gateway.database.jpa.repository.CompraProdutoEntityRepository;
import com.fiap.pedido.gateway.database.jpa.repository.PedidoEntityRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Component
@Slf4j
public class PedidoJpaGateway implements PedidoGateway {

    private final PedidoEntityRepository pedidoEntityRepository;
    private final ClienteEntityRepository clienteEntityRepository;
    private final CompraProdutoEntityRepository compraProdutoEntityRepository;

    @Override
    public Long criarPedido(Pedido pedido) {
        PedidoEntity novoPedido = new PedidoEntity();

        novoPedido.setStatus(pedido.getStatus());
        ClienteEntity clienteEntity = clienteEntityRepository.findFirstByCpf(pedido.getCliente().getCpf());

        if (clienteEntity != null) {
            novoPedido.setCliente(clienteEntity);
        } else {
            ClienteEntity newClienteEntity = toClienteEntity(pedido.getCliente());

            newClienteEntity = clienteEntityRepository.save(newClienteEntity);
            novoPedido.setCliente(newClienteEntity);
        }

        PedidoEntity pedidoEntity = pedidoEntityRepository.save(novoPedido);

        List<CompraProdutoEntity> compraProdutoEntities = pedido.getProdutoList().stream().map(produto -> toCompraProdutoEntity(produto, pedidoEntity)).toList();
        compraProdutoEntities = compraProdutoEntityRepository.saveAll(compraProdutoEntities);

        novoPedido.setProdutoList(compraProdutoEntities);

        return pedidoEntity.getId();
    }

    @Override
    public boolean atualizarStatusPedido(Pedido pedido) {
        return pedidoEntityRepository.findById(pedido.getId())
                .map(pedidoEntity -> {
                    pedidoEntity.setStatus(pedido.getStatus());
                    pedidoEntityRepository.save(pedidoEntity);
                    return true;
                })
                .orElseGet(() -> {
                    log.error("Pedido com ID {} não encontrado", pedido.getId());
                    return false;
                });
    }
    @Override
    public BigDecimal getValorPedido(Long pedidoId) {
        PedidoEntity pedidoEntity = pedidoEntityRepository.getReferenceById(pedidoId);

        return pedidoEntity.getProdutoList().stream().map(compraProdutoEntity ->
                compraProdutoEntity.getPreco().multiply(BigDecimal.valueOf(compraProdutoEntity.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Pedido.Status getPedidoStatus(Long pedidoId) {
        PedidoEntity pedidoEntity = pedidoEntityRepository.findById(pedidoId)
                .orElseThrow(PedidoNaoEncontradoException::new);

        return pedidoEntity.getStatus();
    }

    @Override
    public Cliente getCliente(Long pedidoId) {
        return toCliente(pedidoEntityRepository.getReferenceById(pedidoId).getCliente());
    }

    @Override
    public Pedido getPedido(Long pedidoId) {
        PedidoEntity pedidoEntity = pedidoEntityRepository.findById(pedidoId).orElseThrow(PedidoNaoEncontradoException::new);

        return toPedido(pedidoEntity);
    }

    private Pedido toPedido(PedidoEntity pedidoEntity) {
        Pedido pedido = new Pedido();

        pedido.setId(pedidoEntity.getId());
        pedido.setCliente(toCliente(pedidoEntity.getCliente()));
        pedido.setStatus(pedidoEntity.getStatus());
        pedido.setPagamento(toPagamento(pedidoEntity.getPagamento()));
        pedido.setProdutoList(toProdutoList(pedidoEntity.getProdutoList()));

        return pedido;
    }

    private List<Produto> toProdutoList(List<CompraProdutoEntity> compraProdutoEntities) {
        List<Produto> produtos = new ArrayList<>(compraProdutoEntities.size());

        for (CompraProdutoEntity compraProdutoEntity : compraProdutoEntities) {
            Produto produto = new Produto();

            produto.setId(compraProdutoEntity.getId());
            produto.setPreco(compraProdutoEntity.getPreco());
            produto.setQuantidade(compraProdutoEntity.getQuantidade());

            produtos.add(produto);
        }

        return produtos;
    }

    private Pagamento toPagamento(PagamentoEntity pagamentoEntity) {
        if (pagamentoEntity == null) return null;

        Pagamento pagamento = new Pagamento();

        pagamento.setId(pagamentoEntity.getId());
        pagamento.setValor(pagamentoEntity.getValor());
        pagamento.setStatus(pagamentoEntity.getStatus());

        return pagamento;
    }

    private Cliente toCliente(ClienteEntity clienteEntity) {
        Cliente cliente = new Cliente();

        cliente.setCep(clienteEntity.getCep());
        cliente.setEndereco(clienteEntity.getEndereco());
        cliente.setNome(clienteEntity.getNome());
        cliente.setCpf(clienteEntity.getCpf());

        return cliente;
    }

    private ClienteEntity toClienteEntity(Cliente cliente) {
        ClienteEntity clienteEntity = new ClienteEntity();

        clienteEntity.setCep(cliente.getCep());
        clienteEntity.setNome(cliente.getNome());
        clienteEntity.setCpf(cliente.getCpf());
        clienteEntity.setEndereco(cliente.getEndereco());

        return clienteEntity;
    }

    private CompraProdutoEntity toCompraProdutoEntity(Produto produto, PedidoEntity pedido) {
        CompraProdutoEntity compraProdutoEntity = new CompraProdutoEntity();

        compraProdutoEntity.setPedido(pedido);
        compraProdutoEntity.setProdutoId(produto.getId());
        compraProdutoEntity.setPreco(produto.getPreco());
        compraProdutoEntity.setQuantidade(produto.getQuantidade());

        return compraProdutoEntity;
    }
}
