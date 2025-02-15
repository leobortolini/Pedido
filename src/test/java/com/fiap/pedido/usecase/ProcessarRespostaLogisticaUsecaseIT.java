package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.gateway.PedidoGateway;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class ProcessarRespostaLogisticaUsecaseIT {

    @Autowired
    private ProcessarRespostaLogisticaUsecase processarRespostaLogisticaUsecase;

    @Autowired
    private PedidoGateway pedidoGateway;

    @Test
    @Sql(scripts = "/pedido_pendente_logistica.sql")
    void deveAtualizarStatusDoPedido() {
        processarRespostaLogisticaUsecase.processarRespostaLogistica(1L, Pedido.Status.EM_ENTREGA.name());

        Pedido.Status pedidoStatus = pedidoGateway.getPedidoStatus(1L);

        assertEquals(Pedido.Status.EM_ENTREGA, pedidoStatus);
    }

}
