package com.fiap.pedido.controller.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.pedido.controller.queue.request.EntregaStatusResponse;
import com.fiap.pedido.controller.queue.request.EstoqueDisponivelResponse;
import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.gateway.database.jpa.entity.PedidoEntity;
import com.fiap.pedido.gateway.database.jpa.repository.PedidoEntityRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class PedidoEntrypointIT {

    @Autowired
    private PedidoEntrypoint pedidoEntrypoint;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PedidoEntityRepository pedidoEntityRepository;

    private Consumer<String> consumidorEstoque;
    private Consumer<String> consumidorLogistica;

    @BeforeEach
    void setUp() {
        consumidorEstoque = pedidoEntrypoint.estoqueResposta();
        consumidorLogistica = pedidoEntrypoint.atualizaStatus();
    }

    @Test
    @Sql(scripts = "/pedido_pendente_estoque.sql")
    void deveProcessarRespostaEstoqueComSucesso() throws Exception {
        EstoqueDisponivelResponse response = new EstoqueDisponivelResponse(1L, true);
        String mensagem = objectMapper.writeValueAsString(response);

        consumidorEstoque.accept(mensagem);

        Optional<PedidoEntity> pedido = pedidoEntityRepository.findById(1L);
        assertTrue(pedido.isPresent());
        assertEquals(Pedido.Status.PENDENTE_PAGAMENTO, pedido.get().getStatus());
    }

    @Test
    @Sql(scripts = "/pedido_pendente_logistica.sql")
    void deveProcessarRespostaLogisticaComSucesso() throws Exception {
        EntregaStatusResponse response = new EntregaStatusResponse(1L, "FINALIZADO");
        String mensagem = objectMapper.writeValueAsString(response);

        consumidorLogistica.accept(mensagem);

        Optional<PedidoEntity> pedido = pedidoEntityRepository.findById(1L);
        assertTrue(pedido.isPresent());
        assertEquals(Pedido.Status.FINALIZADO, pedido.get().getStatus());
    }
}
