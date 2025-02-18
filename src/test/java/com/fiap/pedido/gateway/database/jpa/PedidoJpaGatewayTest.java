package com.fiap.pedido.gateway.database.jpa;

import com.fiap.pedido.domain.Cliente;
import com.fiap.pedido.domain.Pagamento;
import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.domain.Produto;
import com.fiap.pedido.gateway.database.jpa.entity.ClienteEntity;
import com.fiap.pedido.gateway.database.jpa.entity.CompraProdutoEntity;
import com.fiap.pedido.gateway.database.jpa.entity.PagamentoEntity;
import com.fiap.pedido.gateway.database.jpa.entity.PedidoEntity;
import com.fiap.pedido.gateway.database.jpa.repository.ClienteEntityRepository;
import com.fiap.pedido.gateway.database.jpa.repository.CompraProdutoEntityRepository;
import com.fiap.pedido.gateway.database.jpa.repository.PedidoEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PedidoJpaGatewayTest {

    @Mock
    private PedidoEntityRepository pedidoEntityRepository;

    @Mock
    private ClienteEntityRepository clienteEntityRepository;

    @Mock
    private CompraProdutoEntityRepository compraProdutoEntityRepository;

    @InjectMocks
    private PedidoJpaGateway pedidoJpaGateway;

    private Pedido pedido;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setCpf("12345678900");
        cliente.setNome("Cliente Teste");
        cliente.setEndereco("Rua Teste");
        cliente.setCep("12345-678");

        Pagamento pagamento = new Pagamento();

        pagamento.setId(1L);
        pagamento.setValor(BigDecimal.valueOf(100.00));
        pagamento.setStatus(Pagamento.Status.PENDENTE);

        pedido = new Pedido();
        pedido.setId(1L);
        pedido.setCliente(cliente);
        pedido.setPagamento(pagamento);
        pedido.setStatus(Pedido.Status.PENDENTE_ESTOQUE);

        Produto produto = new Produto();

        pedido.setProdutoList(List.of(produto));
    }

    @Test
    void deveCriarPedidoParaClienteNovo() {
        when(clienteEntityRepository.findFirstByCpf(cliente.getCpf())).thenReturn(null);
        PedidoEntity pedidoEntity = new PedidoEntity();

        pedidoEntity.setId(1L);
        when(pedidoEntityRepository.save(any(PedidoEntity.class))).thenReturn(pedidoEntity);

        Long pedidoId = pedidoJpaGateway.criarPedido(pedido);

        assertNotNull(pedidoId);
        verify(clienteEntityRepository, times(1)).save(any());
        verify(pedidoEntityRepository, times(1)).save(any());
    }

    @Test
    void deveCriarPedidoParaClienteExistente() {
        ClienteEntity clienteEntity = new ClienteEntity();
        when(clienteEntityRepository.findFirstByCpf(cliente.getCpf())).thenReturn(clienteEntity);
        PedidoEntity pedidoEntity = new PedidoEntity();

        pedidoEntity.setId(1L);
        when(pedidoEntityRepository.save(any(PedidoEntity.class))).thenReturn(pedidoEntity);

        Long pedidoId = pedidoJpaGateway.criarPedido(pedido);

        assertNotNull(pedidoId);
        verify(clienteEntityRepository, never()).save(any());
        verify(pedidoEntityRepository, times(1)).save(any());
    }

    @Test
    void deveAtualizarStatusDoPedido() {
        when(pedidoEntityRepository.findById(pedido.getId())).thenReturn(Optional.of(new PedidoEntity()));

        boolean result = pedidoJpaGateway.atualizarStatusPedido(pedido);

        assertTrue(result);
        verify(pedidoEntityRepository, times(1)).save(any());
    }

    @Test
    void deveRetornarFalsoQuandoTentarAtualizarPedidoInexistente() {
        when(pedidoEntityRepository.findById(pedido.getId())).thenReturn(Optional.empty());

        boolean result = pedidoJpaGateway.atualizarStatusPedido(pedido);

        assertFalse(result);
        verify(pedidoEntityRepository, never()).save(any());
    }

    @Test
    void deveRetornarValorDoPedido() {
        PedidoEntity pedidoEntity = new PedidoEntity();
        pedidoEntity.setProdutoList(new ArrayList<>());
        when(pedidoEntityRepository.getReferenceById(pedido.getId())).thenReturn(pedidoEntity);

        BigDecimal valor = pedidoJpaGateway.getValorPedido(pedido.getId());

        assertNotNull(valor);
        assertEquals(BigDecimal.ZERO, valor);
    }

    @Test
    void deveRetornarStatusDoPedido() {
        PedidoEntity pedidoEntity = new PedidoEntity();
        pedidoEntity.setStatus(Pedido.Status.PENDENTE_ESTOQUE);
        when(pedidoEntityRepository.findById(pedido.getId())).thenReturn(Optional.of(pedidoEntity));

        Pedido.Status status = pedidoJpaGateway.getPedidoStatus(pedido.getId());

        assertNotNull(status);
        assertEquals(Pedido.Status.PENDENTE_ESTOQUE, status);
    }

    @Test
    void deveRetornarClienteDoPedido() {
        PedidoEntity pedidoEntity = new PedidoEntity();
        ClienteEntity clienteEntity = new ClienteEntity();
        clienteEntity.setCpf("12345678900");
        pedidoEntity.setCliente(clienteEntity);
        when(pedidoEntityRepository.getReferenceById(pedido.getId())).thenReturn(pedidoEntity);

        Cliente clienteRetornado = pedidoJpaGateway.getCliente(pedido.getId());

        assertNotNull(clienteRetornado);
        assertEquals("12345678900", clienteRetornado.getCpf());
    }

    @Test
    void deveRetornarPedido() {
        PedidoEntity pedidoEntity = new PedidoEntity();
        pedidoEntity.setId(1L);
        pedidoEntity.setCliente(new ClienteEntity());
        pedidoEntity.setStatus(Pedido.Status.PENDENTE_ESTOQUE);
        CompraProdutoEntity compraProdutoEntity = new CompraProdutoEntity();
        List<CompraProdutoEntity> produtoList = List.of(compraProdutoEntity);

        pedidoEntity.setProdutoList(produtoList);
        PagamentoEntity pagamentoEntity = new PagamentoEntity();

        pedidoEntity.setPagamento(pagamentoEntity);

        when(pedidoEntityRepository.findById(pedido.getId())).thenReturn(Optional.of(pedidoEntity));

        Pedido pedidoResult = pedidoJpaGateway.getPedido(pedido.getId());

        assertNotNull(pedidoResult);
        assertEquals(pedido.getId(), pedidoResult.getId());
    }

    @Test
    void deveRetornarPedidoSemPagamento() {
        PedidoEntity pedidoEntity = new PedidoEntity();
        pedidoEntity.setId(1L);
        pedidoEntity.setCliente(new ClienteEntity());
        pedidoEntity.setStatus(Pedido.Status.PENDENTE_ESTOQUE);
        CompraProdutoEntity compraProdutoEntity = new CompraProdutoEntity();
        List<CompraProdutoEntity> produtoList = List.of(compraProdutoEntity);

        pedidoEntity.setProdutoList(produtoList);
        when(pedidoEntityRepository.findById(pedido.getId())).thenReturn(Optional.of(pedidoEntity));

        Pedido pedidoResult = pedidoJpaGateway.getPedido(pedido.getId());

        assertNotNull(pedidoResult);
        assertEquals(pedido.getId(), pedidoResult.getId());
    }
}
