package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.exception.PedidoNaoEncontradoException;
import com.fiap.pedido.gateway.PedidoGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessarRespostaEstoqueUsecaseTest {

    @Mock
    private PedidoGateway pedidoGateway;

    @InjectMocks
    private ProcessarRespostaEstoqueUsecase processarRespostaEstoqueUsecase;

    private final Long pedidoId = 1L;

    @Test
    void deveAtualizarStatusParaPendentePagamentoQuandoEstoqueDisponivel() {
        when(pedidoGateway.getPedidoStatus(pedidoId)).thenReturn(Pedido.Status.ENVIADO_PEDIDO_ESTOQUE);

        processarRespostaEstoqueUsecase.processarRespostaEstoque(pedidoId, true);

        verify(pedidoGateway).getPedidoStatus(pedidoId);
        verify(pedidoGateway).atualizarStatusPedido(argThat(pedido ->
                pedido.getId().equals(pedidoId) && pedido.getStatus() == Pedido.Status.PENDENTE_PAGAMENTO));
    }

    @Test
    void deveAtualizarStatusParaSemEstoqueQuandoEstoqueNaoDisponivel() {
        when(pedidoGateway.getPedidoStatus(pedidoId)).thenReturn(Pedido.Status.ENVIADO_PEDIDO_ESTOQUE);

        processarRespostaEstoqueUsecase.processarRespostaEstoque(pedidoId, false);

        verify(pedidoGateway).getPedidoStatus(pedidoId);
        verify(pedidoGateway).atualizarStatusPedido(argThat(pedido ->
                pedido.getId().equals(pedidoId) && pedido.getStatus() == Pedido.Status.SEM_ESTOQUE));
    }

    @Test
    void deveIgnorarProcessamentoSePedidoNaoForEncontrado() {
        when(pedidoGateway.getPedidoStatus(pedidoId)).thenThrow(new PedidoNaoEncontradoException());

        assertThrows(PedidoNaoEncontradoException.class,
                () -> processarRespostaEstoqueUsecase.processarRespostaEstoque(pedidoId, true));
    }

    @Test
    void deveIgnorarSeStatusDiferenteDeEnviadoPedidoEstoque() {
        when(pedidoGateway.getPedidoStatus(pedidoId)).thenReturn(Pedido.Status.PENDENTE_PAGAMENTO);

        processarRespostaEstoqueUsecase.processarRespostaEstoque(pedidoId, true);

        verify(pedidoGateway).getPedidoStatus(pedidoId);
        verifyNoMoreInteractions(pedidoGateway);
    }
}
