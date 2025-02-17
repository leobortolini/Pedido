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
class ProcessarRespostaEstoqueUsecaseIT {

    @Autowired
    private ProcessarRespostaEstoqueUsecase processarRespostaEstoqueUsecase;

    @Autowired
    private PedidoGateway pedidoGateway;

    @Test
    @Sql(scripts = { "/limpar_dados.sql", "/pedido_pendente_estoque.sql" })
    void deveAtualizarStatusDoPedidoParaPagamento() {
        processarRespostaEstoqueUsecase.processarRespostaEstoque(1L, true);

        Pedido pedido = pedidoGateway.getPedido(1L);
        assertEquals(Pedido.Status.PENDENTE_PAGAMENTO, pedido.getStatus());
    }

    @Test
    @Sql(scripts = { "/limpar_dados.sql", "/pedido_pendente_estoque.sql"})
    void deveAtualizarStatusDoPedidoParaSemEstoque() {
        processarRespostaEstoqueUsecase.processarRespostaEstoque(1L, false);

        Pedido pedido = pedidoGateway.getPedido(1L);
        assertEquals(Pedido.Status.SEM_ESTOQUE, pedido.getStatus());
    }
}
