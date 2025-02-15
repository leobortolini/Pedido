package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.exception.PedidoNaoEncontradoException;
import com.fiap.pedido.gateway.PedidoGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(OutputCaptureExtension.class)
class ProcessarRespostaLogisticaUsecaseTest {

    @Mock
    private PedidoGateway pedidoGateway;

    @InjectMocks
    private ProcessarRespostaLogisticaUsecase usecase;

    private final Long pedidoId = 1L;
    private final String status = "EM_ENTREGA";

    @ParameterizedTest
    @EnumSource(value = Pedido.Status.class, names = { "ENVIADO_EXPEDICAO", "EM_ENTREGA" })
    void deveAtualizarStatusQuandoPedidoEstaEmEstadoValido(Pedido.Status status) {
        when(pedidoGateway.getPedidoStatus(pedidoId)).thenReturn(status);
        when(pedidoGateway.atualizarStatusPedido(any(Pedido.class))).thenReturn(true);

        usecase.processarRespostaLogistica(pedidoId, status.name());

        verify(pedidoGateway).atualizarStatusPedido(any(Pedido.class));
    }

    @Test
    void deveIgnorarAtualizacaoQuandoPedidoNaoEstiverEmEstadoValido() {
        when(pedidoGateway.getPedidoStatus(pedidoId)).thenReturn(Pedido.Status.PENDENTE_PAGAMENTO);

        usecase.processarRespostaLogistica(pedidoId, status);

        verifyNoMoreInteractions(pedidoGateway);
    }

    @Test
    void deveLoggarErroQuandoNaoEncontrarPedido(CapturedOutput capturedOutput) {
        when(pedidoGateway.getPedidoStatus(pedidoId)).thenThrow(PedidoNaoEncontradoException.class);

        usecase.processarRespostaLogistica(pedidoId, status);

        assertTrue(capturedOutput.getOut()
                .contains("Pedido com ID 1 foi enviado pela logística para ser atualizado ao Status EM_ENTREGA mas não foi encontrado"));
    }

    @Test
    void deveLoggarErroQuandoNaoAtualizarStatusPedido(CapturedOutput capturedOutput) {
        when(pedidoGateway.getPedidoStatus(pedidoId)).thenReturn(Pedido.Status.ENVIADO_EXPEDICAO);
        when(pedidoGateway.atualizarStatusPedido(any(Pedido.class))).thenReturn(false);

        usecase.processarRespostaLogistica(pedidoId, status);

        assertTrue(capturedOutput.getOut()
                .contains("Pedido com ID 1 foi enviado pela logística para ser atualizado ao Status EM_ENTREGA mas ocorreu um erro"));
    }
}
