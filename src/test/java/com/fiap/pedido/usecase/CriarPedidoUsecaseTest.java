package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Cliente;
import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.domain.Produto;
import com.fiap.pedido.exception.ClienteNaoEncontradoException;
import com.fiap.pedido.gateway.ClienteGateway;
import com.fiap.pedido.gateway.PedidoGateway;
import com.fiap.pedido.gateway.ProdutoGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriarPedidoUsecaseTest {

    @Mock
    private PedidoGateway pedidoGateway;

    @Mock
    private ClienteGateway clienteGateway;

    @Mock
    private ProdutoGateway produtoGateway;

    @InjectMocks
    private CriarPedidoUsecase criarPedidoUsecase;

    private Cliente cliente;
    private Produto produto;
    private List<Produto> itens;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setCpf("12345678900");

        produto = new Produto();
        itens = Collections.singletonList(produto);
    }

    @Test
    void deveCriarPedidoComSucesso() {
        Pedido pedidoEsperado = new Pedido();
        pedidoEsperado.setId(1L);
        pedidoEsperado.setCliente(cliente);
        pedidoEsperado.setProdutoList(itens);
        pedidoEsperado.setStatus(Pedido.Status.PENDENTE_ESTOQUE);

        when(clienteGateway.buscarCliente(cliente.getCpf())).thenReturn(cliente);
        when(pedidoGateway.criarPedido(any(Pedido.class))).thenReturn(1L);
        when(produtoGateway.enviarReservaProduto(any(Pedido.class))).thenReturn(true);

        Pedido pedidoCriado = criarPedidoUsecase.criarPedido(cliente.getCpf(), itens);

        assertNotNull(pedidoCriado);
        assertEquals(1L, pedidoCriado.getId());
        assertEquals(Pedido.Status.ENVIADO_PEDIDO_ESTOQUE, pedidoCriado.getStatus());

        verify(clienteGateway).buscarCliente(cliente.getCpf());
        verify(pedidoGateway).criarPedido(any(Pedido.class));
        verify(produtoGateway).enviarReservaProduto(any(Pedido.class));
        verify(pedidoGateway).atualizarStatusPedido(any(Pedido.class));
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {
        when(clienteGateway.buscarCliente(cliente.getCpf())).thenReturn(null);

        assertThrows(ClienteNaoEncontradoException.class, () ->
                criarPedidoUsecase.criarPedido(cliente.getCpf(), itens)
        );

        verify(clienteGateway).buscarCliente(cliente.getCpf());
        verifyNoInteractions(pedidoGateway);
        verifyNoInteractions(produtoGateway);
    }

    @Test
    void deveCriarPedidoMasNaoAtualizarStatusQuandoFalhaNoEnvioAoEstoque() {
        when(clienteGateway.buscarCliente(cliente.getCpf())).thenReturn(cliente);
        when(pedidoGateway.criarPedido(any(Pedido.class))).thenReturn(1L);
        when(produtoGateway.enviarReservaProduto(any(Pedido.class))).thenReturn(false);

        Pedido pedidoCriado = criarPedidoUsecase.criarPedido(cliente.getCpf(), itens);

        assertNotNull(pedidoCriado);
        assertEquals(1L, pedidoCriado.getId());
        assertEquals(Pedido.Status.PENDENTE_ESTOQUE, pedidoCriado.getStatus());

        verify(clienteGateway).buscarCliente(cliente.getCpf());
        verify(pedidoGateway).criarPedido(any(Pedido.class));
        verify(produtoGateway).enviarReservaProduto(any(Pedido.class));
        verify(pedidoGateway, never()).atualizarStatusPedido(any(Pedido.class));
    }
}
