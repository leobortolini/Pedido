package com.fiap.pedido.gateway.rest;

import com.fiap.pedido.domain.Cliente;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class ClienteGatewayHttpImplIT {

    @Autowired
    private ClienteGatewayHttpImpl clienteGatewayHttp;

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
    void deveRetornarCliente() {
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

        Cliente cliente = clienteGatewayHttp.buscarCliente("000.000.000-00");

        assertEquals("000.000.000-00", cliente.getCpf());
        assertEquals("Leonardo Bortolini", cliente.getNome());
        assertEquals("95180-000", cliente.getCep());
        assertEquals("endereco", cliente.getEndereco());
    }

    @Test
    void deveRetornarNullQuandoNaoEncontrarCliente() {
        String url = String.format("/api/vi/clientes/%s", "000.000.000-00");
        stubFor(get(urlEqualTo(url)).willReturn(notFound().withHeader("Content-Type", "application/json")));

        Cliente cliente = clienteGatewayHttp.buscarCliente("000.000.000-00");

        assertNull(cliente);
    }
}
