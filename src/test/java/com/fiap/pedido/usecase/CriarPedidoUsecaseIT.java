package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.domain.Produto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.EnableTestBinder;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import java.math.BigDecimal;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@EnableTestBinder
@EnableWireMock({@ConfigureWireMock(port = 8080, httpsPort = 0)})
class CriarPedidoUsecaseIT {

    @Autowired
    private CriarPedidoUsecase criarPedidoUsecase;

    @Test
    void deveCriarPedido() {
        String url = String.format("/api/vi/clientes?cpf=%s", "000.000.000-00");
        String clienteJson = """
                {
                    "cpf": "000.000.000-00",
                    "nome": "Leonardo Bortolini",
                    "cep": "95180-000",
                    "endereco": "endereco"
                }
                """;
        stubFor(get(urlEqualTo(url)).willReturn(ok(clienteJson).withHeader("Content-Type", "application/json")));

        List<Produto> itens = List.of(new Produto(1L, BigDecimal.TEN, 1));
        Pedido pedido = criarPedidoUsecase.criarPedido("000.000.000-00", itens);

        assertNotNull(pedido);
    }
}
