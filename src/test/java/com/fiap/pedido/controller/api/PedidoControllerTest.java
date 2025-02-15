package com.fiap.pedido.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.pedido.controller.api.request.CompletarPagamentoJson;
import com.fiap.pedido.controller.api.request.CriarPagamentoJson;
import com.fiap.pedido.controller.api.request.CriarPedidoJson;
import com.fiap.pedido.domain.Pagamento;
import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.domain.Produto;
import com.fiap.pedido.usecase.CriarPedidoUsecase;
import com.fiap.pedido.usecase.EfetuarPagamentoUsecase;
import com.fiap.pedido.usecase.ListarPedidoUsecase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PedidoController.class)
@ExtendWith(MockitoExtension.class)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CriarPedidoUsecase criarPedidoUsecase;

    @MockitoBean
    private EfetuarPagamentoUsecase efetuarPagamentoUsecase;

    @MockitoBean
    private ListarPedidoUsecase listarPedidoUsecase;

    @Autowired
    private ObjectMapper objectMapper;

    private CriarPedidoJson criarPedidoJson;
    private CriarPagamentoJson criarPagamentoJson;
    private CompletarPagamentoJson completarPagamentoJson;
    private Pedido pedido;
    private Pagamento pagamento;

    @BeforeEach
    void setUp() {
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setPreco(BigDecimal.valueOf(100.0));
        produto.setQuantidade(2);

        criarPedidoJson = new CriarPedidoJson();
        criarPedidoJson.setCpf("12345678901");
        criarPedidoJson.setItens(List.of(produto));

        criarPagamentoJson = new CriarPagamentoJson(1L);
        completarPagamentoJson = new CompletarPagamentoJson(1L);

        pedido = new Pedido();
        pedido.setId(1L);

        pagamento = new Pagamento();
        pagamento.setId(1L);
        pagamento.setStatus(Pagamento.Status.PENDENTE);
    }

    @Test
    void deveCriarPedido() throws Exception {
        when(criarPedidoUsecase.criarPedido(any(), any())).thenReturn(pedido);

        mockMvc.perform(post("/api/v1/pedido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarPedidoJson)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deveCriarPagamento() throws Exception {
        when(efetuarPagamentoUsecase.criarPagamento(any())).thenReturn(pagamento);

        mockMvc.perform(post("/api/v1/pedido/iniciarPagamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarPagamentoJson)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDENTE"));
    }

    @Test
    void deveRetornarNotFoundQuandoNaoCriarpagamento() throws Exception {
        when(efetuarPagamentoUsecase.criarPagamento(any())).thenReturn(null);

        mockMvc.perform(post("/api/v1/pedido/iniciarPagamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarPagamentoJson)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveCompletarPagamento() throws Exception {
        when(efetuarPagamentoUsecase.finalizarPagamento(any())).thenReturn(pagamento);

        mockMvc.perform(post("/api/v1/pedido/completarPagamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(completarPagamentoJson)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDENTE"));
    }

    @Test
    void deveRetornarPagamento() throws Exception {
        when(listarPedidoUsecase.getPedido(anyLong())).thenReturn(pedido);

        mockMvc.perform(get("/api/v1/pedido/{pedidoId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
