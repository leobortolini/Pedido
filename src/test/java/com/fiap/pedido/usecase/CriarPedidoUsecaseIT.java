package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.domain.Produto;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.EnableTestBinder;

import java.math.BigDecimal;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@EnableTestBinder
class CriarPedidoUsecaseIT {

    @Autowired
    private CriarPedidoUsecase criarPedidoUsecase;

    private WireMockServer wireMockServer;

    @BeforeEach
    public void setUp() {
        wireMockServer = new WireMockServer(9191, 0);
        wireMockServer.start();
        WireMock.configureFor("localhost", 9191);
    }

    @AfterEach
    public void tearDown() {
        WireMock.reset();
        wireMockServer.stop();
    }

    @Test
    void deveCriarPedido() {
        String url = String.format("/api/v1/clientes/%s", "000.000.000-00");
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
