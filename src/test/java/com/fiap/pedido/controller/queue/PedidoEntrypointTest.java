package com.fiap.pedido.controller.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.pedido.controller.queue.request.EntregaStatusResponse;
import com.fiap.pedido.controller.queue.request.EstoqueDisponivelResponse;
import com.fiap.pedido.usecase.ProcessarRespostaEstoqueUsecase;
import com.fiap.pedido.usecase.ProcessarRespostaLogisticaUsecase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Consumer;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoEntrypointTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ProcessarRespostaEstoqueUsecase processarRespostaEstoqueUsecase;

    @Mock
    private ProcessarRespostaLogisticaUsecase processarRespostaLogisticaUsecase;

    @InjectMocks
    private PedidoEntrypoint pedidoEntrypoint;

    private String estoqueMessage;
    private String entregaMessage;
    private EstoqueDisponivelResponse estoqueDisponivelResponse;
    private EntregaStatusResponse entregaStatusResponse;

    @BeforeEach
    void setUp() {
        estoqueDisponivelResponse = new EstoqueDisponivelResponse();
        estoqueDisponivelResponse.setPedidoId(1L);
        estoqueDisponivelResponse.setEstoqueDisponivel(true);

        entregaStatusResponse = new EntregaStatusResponse();
        entregaStatusResponse.setPedidoId(1L);
        entregaStatusResponse.setStatus("ENTREGUE");

        estoqueMessage = "{\"pedidoId\":1,\"estoqueDisponivel\":true}";
        entregaMessage = "{\"pedidoId\":1,\"status\":\"FINALIZADO\"}";
    }

    @Test
    void deveEnviarRespostaDoEstoqueParaProcessamento() throws JsonProcessingException {
        when(objectMapper.readValue(estoqueMessage, EstoqueDisponivelResponse.class)).thenReturn(estoqueDisponivelResponse);

        Consumer<String> estoqueRespostaConsumer = pedidoEntrypoint.estoqueResposta();

        estoqueRespostaConsumer.accept(estoqueMessage);
        verify(processarRespostaEstoqueUsecase, times(1)).processarRespostaEstoque(estoqueDisponivelResponse.getPedidoId(), estoqueDisponivelResponse.isEstoqueDisponivel());
    }

    @Test
    void naoDeveProcessarRespostaDoEstoqueComFormatoInesperado() throws JsonProcessingException {
        when(objectMapper.readValue(estoqueMessage, EstoqueDisponivelResponse.class)).thenThrow(JsonProcessingException.class);

        Consumer<String> estoqueRespostaConsumer = pedidoEntrypoint.estoqueResposta();

        estoqueRespostaConsumer.accept(estoqueMessage);

        verify(processarRespostaEstoqueUsecase, never()).processarRespostaEstoque(anyLong(), anyBoolean());
    }

    @Test
    void deveEnviarRespostaDaLogisticaParaProcessamento() throws JsonProcessingException {
        when(objectMapper.readValue(entregaMessage, EntregaStatusResponse.class)).thenReturn(entregaStatusResponse);

        Consumer<String> atualizaStatusConsumer = pedidoEntrypoint.atualizaStatus();
        atualizaStatusConsumer.accept(entregaMessage);

        verify(processarRespostaLogisticaUsecase, times(1)).processarRespostaLogistica(entregaStatusResponse.getPedidoId(), entregaStatusResponse.getStatus());
    }

    @Test
    void naoDeveProcessarRespostaDaLogisticaComFormatoInesperado() throws JsonProcessingException {
        when(objectMapper.readValue(entregaMessage, EntregaStatusResponse.class)).thenThrow(JsonProcessingException.class);

        Consumer<String> atualizaStatusConsumer = pedidoEntrypoint.atualizaStatus();

        atualizaStatusConsumer.accept(entregaMessage);
        verify(processarRespostaLogisticaUsecase, never()).processarRespostaLogistica(anyLong(), anyString());
    }
}
