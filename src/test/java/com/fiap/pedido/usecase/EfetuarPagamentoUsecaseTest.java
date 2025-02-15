package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Cliente;
import com.fiap.pedido.domain.Pagamento;
import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.exception.PagamentoComStatusInvalidoException;
import com.fiap.pedido.exception.PedidoComStatusInvalidoParaPagamentoException;
import com.fiap.pedido.gateway.LogisticaGateway;
import com.fiap.pedido.gateway.PagamentoGateway;
import com.fiap.pedido.gateway.PedidoGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EfetuarPagamentoUsecaseTest {

    @Mock
    private PagamentoGateway pagamentoGateway;

    @Mock
    private PedidoGateway pedidoGateway;

    @Mock
    private LogisticaGateway logisticaGateway;

    @InjectMocks
    private EfetuarPagamentoUsecase efetuarPagamentoUsecase;

    private final Long pedidoId = 1L;
    private final Long pagamentoId = 2L;

    @Test
    void deveCriarPagamentoComSucesso() {
        when(pedidoGateway.getPedidoStatus(pedidoId)).thenReturn(Pedido.Status.PENDENTE_PAGAMENTO);
        when(pedidoGateway.getValorPedido(pedidoId)).thenReturn(BigDecimal.valueOf(100.00));
        when(pagamentoGateway.criarPagamento(eq(pedidoId), any(Pagamento.class))).thenReturn(pagamentoId);

        Pagamento pagamento = efetuarPagamentoUsecase.criarPagamento(pedidoId);

        assertNotNull(pagamento);
        assertEquals(pagamentoId, pagamento.getId());
        assertEquals(Pagamento.Status.PENDENTE, pagamento.getStatus());

        verify(pedidoGateway).getPedidoStatus(pedidoId);
        verify(pedidoGateway).getValorPedido(pedidoId);
        verify(pagamentoGateway).criarPagamento(eq(pedidoId), any(Pagamento.class));
        verify(pedidoGateway).atualizarStatusPedido(any(Pedido.class));
    }

    @Test
    void deveLancarExcecaoAoCriarPagamentoComPedidoInvalido() {
        when(pedidoGateway.getPedidoStatus(pedidoId)).thenReturn(Pedido.Status.ENVIADO_EXPEDICAO);

        assertThrows(PedidoComStatusInvalidoParaPagamentoException.class, () ->
                efetuarPagamentoUsecase.criarPagamento(pedidoId)
        );

        verify(pedidoGateway).getPedidoStatus(pedidoId);
        verifyNoInteractions(pagamentoGateway);
    }

    @ParameterizedTest
    @EnumSource(value = Pagamento.Status.class, names = { "PENDENTE", "RECUSADO" })
    void deveFinalizarPagamentoComSucesso(Pagamento.Status status) {
        when(pagamentoGateway.getPagamentoStatus(pagamentoId)).thenReturn(status);
        when(pagamentoGateway.atualizarStatusPagamento(any(Pagamento.class))).thenReturn(new Pagamento(pagamentoId, BigDecimal.valueOf(100.00), Pagamento.Status.FINALIZADO));

        Pagamento pagamentoFinalizado = efetuarPagamentoUsecase.finalizarPagamento(pagamentoId);

        assertNotNull(pagamentoFinalizado);
        assertEquals(Pagamento.Status.FINALIZADO, pagamentoFinalizado.getStatus());

        verify(pagamentoGateway).getPagamentoStatus(pagamentoId);
        verify(pagamentoGateway).atualizarStatusPagamento(any(Pagamento.class));
    }

    @Test
    void deveLancarExcecaoAoFinalizarPagamentoComStatusInvalido() {
        when(pagamentoGateway.getPagamentoStatus(pagamentoId)).thenReturn(Pagamento.Status.FINALIZADO);

        assertThrows(PagamentoComStatusInvalidoException.class, () ->
                efetuarPagamentoUsecase.finalizarPagamento(pagamentoId)
        );

        verify(pagamentoGateway).getPagamentoStatus(pagamentoId);
        verifyNoMoreInteractions(pagamentoGateway);
    }

    @Test
    void deveEnviarPedidoParaLogisticaQuandoPagamentoForFinalizado() {
        when(pagamentoGateway.getPagamentoStatus(pagamentoId)).thenReturn(Pagamento.Status.PENDENTE);
        when(pagamentoGateway.getPedidoIdDoPagamento(pagamentoId)).thenReturn(pedidoId);
        when(pedidoGateway.getCliente(pedidoId)).thenReturn(new Cliente());
        when(logisticaGateway.enviarPedidoEntrega(any(Pedido.class))).thenReturn(true);
        when(pagamentoGateway.atualizarStatusPagamento(any(Pagamento.class))).thenReturn(new Pagamento(pagamentoId, BigDecimal.valueOf(100.00), Pagamento.Status.FINALIZADO));

        Pagamento pagamentoFinalizado = efetuarPagamentoUsecase.finalizarPagamento(pagamentoId);

        assertNotNull(pagamentoFinalizado);
        assertEquals(Pagamento.Status.FINALIZADO, pagamentoFinalizado.getStatus());
        verify(logisticaGateway).enviarPedidoEntrega(any(Pedido.class));
        verify(pedidoGateway).atualizarStatusPedido(any(Pedido.class));
    }
}
