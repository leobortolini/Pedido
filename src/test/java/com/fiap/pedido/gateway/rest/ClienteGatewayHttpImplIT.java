package com.fiap.pedido.gateway.rest;

import com.fiap.pedido.domain.Cliente;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@EnableWireMock({@ConfigureWireMock(port = 8080, httpsPort = 0)})
class ClienteGatewayHttpImplIT {


    @Autowired
    private ClienteGatewayHttpImpl clienteGatewayHttp;

    @Test
    void deveRetornarCliente() {
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

        Cliente cliente = clienteGatewayHttp.buscarCliente("000.000.000-00");

        assertEquals("000.000.000-00", cliente.getCpf());
        assertEquals("Leonardo Bortolini", cliente.getNome());
        assertEquals("95180-000", cliente.getCep());
        assertEquals("endereco", cliente.getEndereco());
    }

    @Test
    void deveRetornarNullQuandoNaoEncontrarCliente() {
        String url = String.format("/api/vi/clientes?cpf=%s", "000.000.000-00");
        stubFor(get(urlEqualTo(url)).willReturn(notFound().withHeader("Content-Type", "application/json")));

        Cliente cliente = clienteGatewayHttp.buscarCliente("000.000.000-00");

        assertNull(cliente);
    }
}
