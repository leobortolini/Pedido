package com.fiap.pedido.bdd;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.pedido.controller.api.request.CompletarPagamentoJson;
import com.fiap.pedido.controller.api.request.CriarPagamentoJson;
import com.fiap.pedido.controller.api.request.CriarPedidoJson;
import com.fiap.pedido.controller.queue.request.EntregaStatusResponse;
import com.fiap.pedido.controller.queue.request.EstoqueDisponivelResponse;
import com.fiap.pedido.domain.Pagamento;
import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.domain.Produto;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
public class DefinicaoPassos {

    private final String endpoint = "http://localhost:8081/api/v1/pedido";
    private Pedido pedido;
    private Pagamento pagamento;

    @Autowired
    private StreamBridge streamBridge;

    @Autowired
    private ObjectMapper objectMapper;

    @Quando("criar novo pedido")
    @Dado("que exista um pedido")
    public void criarNovoPedido() {
        CriarPedidoJson criarPedidoJson = new CriarPedidoJson();

        criarPedidoJson.setCpf("000.000.000-09");
        criarPedidoJson.setItens(List.of(new Produto(1L, BigDecimal.TEN, 1)));

        Response response = given().contentType(MediaType.APPLICATION_JSON_VALUE).body(criarPedidoJson).when().post(endpoint);
        pedido = response.then().extract().as(Pedido.class);
    }

    @Dado("que exista um pedido enviado para o estoque")
    public void existaUmPedido() {
        criarNovoPedido();
    }

    @Entao("o sistema deve retornar o pedido criado")
    public void pedidoCriado() {
        assertNotNull(pedido.getId());
    }

    @Quando("o estoque enviar resposta")
    public void estoqueResposta() {
        EstoqueDisponivelResponse estoqueDisponivelResponse = new EstoqueDisponivelResponse();
        estoqueDisponivelResponse.setPedidoId(pedido.getId());
        estoqueDisponivelResponse.setEstoqueDisponivel(true);

        String mensagemJson;
        try {
            mensagemJson = objectMapper.writeValueAsString(estoqueDisponivelResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao converter mensagem para JSON", e);
        }

        boolean send = streamBridge.send("estoque-resposta-dlx", mensagemJson);

        assertTrue(send, "A mensagem não foi enviada corretamente");

        esperarSistemaConsumirEvento();
    }

    private static void esperarSistemaConsumirEvento() {
        try {
            Thread.sleep(1000); // para esperar o sistema consumir o evento
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrompida durante o sleep", e);
        }
    }

    @Entao("o pedido deve aguardar pagamento")
    public void pedidoDeveAguardarPagamento() {
        listarPedido();

        assertEquals(Pedido.Status.PENDENTE_PAGAMENTO, pedido.getStatus());
    }

    private void listarPedido() {
        Response response = given().contentType(MediaType.APPLICATION_JSON_VALUE).when().get(endpoint + "/" + pedido.getId());

        pedido = response.then().extract().as(Pedido.class);
    }

    @Dado("um pedido pendente de pagamento")
    public void pedidoPendentePagamento() {
        criarNovoPedido();
        estoqueResposta();
    }

    @Quando("o cliente inicia o pagamento para o pedido")
    public void clienteIniciaPagamentoPedido() {
        CriarPagamentoJson criarPagamentoJson = new CriarPagamentoJson(pedido.getId());
        Response response = given().contentType(MediaType.APPLICATION_JSON_VALUE).body(criarPagamentoJson).when()
                .post(endpoint + "/iniciarPagamento");

        pagamento = response.then().extract().as(Pagamento.class);
    }

    @Entao("o sistema deve retornar os detalhes do pagamento")
    public void pagamentoCriado() {
        assertNotNull(pagamento.getId());
    }

    @Dado("um pedido com pagamento pendente")
    public void umPedidoComPagamentoPendente() {
        pedidoPendentePagamento();
        clienteIniciaPagamentoPedido();
    }

    @Quando("completar o pagamento")
    public void completarOPagamento() {
        CompletarPagamentoJson criarPagamentoJson = new CompletarPagamentoJson(pagamento.getId());
        Response response = given().contentType(MediaType.APPLICATION_JSON_VALUE).body(criarPagamentoJson).when()
                .post(endpoint + "/completarPagamento");
        pagamento = response.then().extract().as(Pagamento.class);
    }

    @Entao("o pedido deve ser enviado para logistica")
    public void oPedidoDeveSerEnviadoParaLogistica() {
        listarPedido();

        assertEquals(Pedido.Status.ENVIADO_EXPEDICAO, pedido.getStatus());
    }

    @E("o pagamento ser finalizado")
    public void oPagamentoSerFinalizado() {
        assertEquals(Pagamento.Status.FINALIZADO, pagamento.getStatus());
    }

    @Dado("um pedido enviado para logistica")
    public void umPedidoEnviadoParaLogistica() {
        umPedidoComPagamentoPendente();
        completarOPagamento();
    }

    @Quando("a logistica enviar uma resposta")
    public void aLogisticaEnviarUmaResposta() {
        EntregaStatusResponse entregaStatusResponse = new EntregaStatusResponse();

        entregaStatusResponse.setPedidoId(pedido.getId());
        entregaStatusResponse.setStatus(Pedido.Status.FINALIZADO.name());

        String mensagemJson;
        try {
            mensagemJson = objectMapper.writeValueAsString(entregaStatusResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao converter mensagem para JSON", e);
        }

        boolean send = streamBridge.send("atualiza-status-dlx", mensagemJson);

        assertTrue(send, "A mensagem não foi enviada corretamente");
        esperarSistemaConsumirEvento();
    }

    @Entao("o pedido deve ser atualizado")
    public void oPedidoDeveSerAtualizado() {
        listarPedido();
        assertEquals(Pedido.Status.FINALIZADO, pedido.getStatus());
    }

    @Entao("o sistema deve retornar os detalhes do pedido")
    public void oPedidoDeveSerRetornado() {
        assertNotNull(pedido.getId());
    }

    @Quando("o cliente solicita o pedido pelo ID")
    public void oClienteSolicitaOPedidoPeloID() {
        listarPedido();
    }
}
