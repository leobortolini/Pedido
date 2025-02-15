package com.fiap.pedido.controller.api;

import com.fiap.pedido.controller.api.request.CompletarPagamentoJson;
import com.fiap.pedido.controller.api.request.CriarPagamentoJson;
import com.fiap.pedido.controller.api.request.CriarPedidoJson;
import com.fiap.pedido.domain.Cliente;
import com.fiap.pedido.domain.Pagamento;
import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.domain.Produto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.stream.binder.test.EnableTestBinder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@EnableTestBinder
class PedidoControllerIT {
    @LocalServerPort
    private int port;

    @Autowired
    private PedidoController pedidoController;

    @MockitoBean
    private RestTemplate restTemplate;


    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        Cliente clienteEsperado = new Cliente("Nome", "95180-000", "000.000.000-00", "Endereco");
        when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(Cliente.class)))
                .thenReturn(new ResponseEntity<>(clienteEsperado, HttpStatus.OK));
    }

    @Test
    @Sql(scripts = "/limpar_dados.sql")
    void deveCriarPedido() {
        CriarPedidoJson criarPedidoJson = new CriarPedidoJson("12345678900", List.of(new Produto(1L, BigDecimal.TEN, 1)));

        Pedido novoPedido = given()
                .contentType(ContentType.JSON)
                .body(criarPedidoJson)
                .when()
                .post("api/v1/pedido")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(Pedido.class);

        assertNotNull(novoPedido.getId());
    }

    @Test
    @Sql(scripts = { "/limpar_dados.sql", "/pedido_pendente_criar_pagamento.sql" })
    void deveIniciarPagamento() {
        CriarPagamentoJson criarPagamentoJson = new CriarPagamentoJson(1L);

        Pagamento pagamento = given()
                .contentType(ContentType.JSON)
                .body(criarPagamentoJson)
                .when()
                .post("api/v1/pedido/iniciarPagamento")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(Pagamento.class);

        assertNotNull(pagamento.getId());
    }

    @Test
    @Sql(scripts = { "/limpar_dados.sql", "/pedido_pendente_finalizar_pagamento.sql" })
    void deveFinalizarPagamento() {
        CompletarPagamentoJson completarPagamentoJson = new CompletarPagamentoJson(1L);

        Pagamento pagamento = given()
                .contentType(ContentType.JSON)
                .body(completarPagamentoJson)
                .when()
                .post("api/v1/pedido/completarPagamento")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(Pagamento.class);

        assertEquals(Pagamento.Status.FINALIZADO, pagamento.getStatus());
    }

    @Test
    @Sql(scripts = { "/limpar_dados.sql", "/pedido_finalizado.sql" })
    void deveRetornarPedido() {
        Pedido pedido = given()
                .contentType(ContentType.JSON)
                .when()
                .get("api/v1/pedido/1")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(Pedido.class);

        assertEquals(1L, pedido.getId());
        assertEquals(Pedido.Status.FINALIZADO, pedido.getStatus());
        assertCliente(pedido.getCliente());
        assertPagamento(pedido.getPagamento());
        assertProduto(pedido.getProdutoList().getFirst());
    }

    private static void assertProduto(Produto produto) {
        assertEquals(1L, produto.getId());
        assertEquals(3, produto.getQuantidade());
        assertEquals("10.00", produto.getPreco().toPlainString());
    }

    private static void assertCliente(Cliente cliente) {
        assertEquals("000.000.000-00", cliente.getCpf());
        assertEquals("95180-000", cliente.getCep());
        assertEquals("endereco", cliente.getEndereco());
        assertEquals("Leonardo Bortolini", cliente.getNome());
    }

    private static void assertPagamento(Pagamento pagamento) {
        assertEquals(1L, pagamento.getId());
        assertEquals(Pagamento.Status.FINALIZADO, pagamento.getStatus());
        assertEquals("30.00", pagamento.getValor().toPlainString());
    }
}
