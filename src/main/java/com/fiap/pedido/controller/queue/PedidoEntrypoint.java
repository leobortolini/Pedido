package com.fiap.pedido.controller.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.pedido.controller.queue.request.EntregaStatusResponse;
import com.fiap.pedido.controller.queue.request.EstoqueDisponivelResponse;
import com.fiap.pedido.usecase.ProcessarRespostaEstoqueUsecase;
import com.fiap.pedido.usecase.ProcessarRespostaLogisticaUsecase;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@AllArgsConstructor
@Slf4j
public class PedidoEntrypoint {

    private final ObjectMapper objectMapper;
    private final ProcessarRespostaEstoqueUsecase processarRespostaEstoqueUsecase;
    private final ProcessarRespostaLogisticaUsecase processarRespostaLogisticaUsecase;

    @Bean
    public Consumer<String> estoqueResposta() {
        return message -> {
            try {
                log.info("Estoque resposta recebido");
                EstoqueDisponivelResponse estoqueDisponivelResponse = objectMapper.readValue(message, EstoqueDisponivelResponse.class);

                processarRespostaEstoqueUsecase.processarRespostaEstoque(estoqueDisponivelResponse.getPedidoId(), estoqueDisponivelResponse.isEstoqueDisponivel());

            } catch (JsonProcessingException e) {
                log.error("Erro ao ler evento enviado pelo sistema de estoque: ", e);
            }
        };
    }

    @Bean
    public Consumer<String> atualizaStatus() {
        return message -> {
            try {
                log.info("Atualiza status recebido");
                EntregaStatusResponse entregaStatusResponse = objectMapper.readValue(message, EntregaStatusResponse.class);

                processarRespostaLogisticaUsecase.processarRespostaLogistica(entregaStatusResponse.getPedidoId(), entregaStatusResponse.getStatus());
            } catch (JsonProcessingException e) {
                log.error("Erro ao ler evento enviado pelo sistema de logistica: ", e);
            }
        };
    }
}