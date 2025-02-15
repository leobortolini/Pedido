package com.fiap.pedido.controller.api;

import com.fiap.pedido.controller.api.request.CompletarPagamentoJson;
import com.fiap.pedido.controller.api.request.CriarPagamentoJson;
import com.fiap.pedido.controller.api.request.CriarPedidoJson;
import com.fiap.pedido.domain.Pagamento;
import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.usecase.CriarPedidoUsecase;
import com.fiap.pedido.usecase.EfetuarPagamentoUsecase;
import com.fiap.pedido.usecase.ListarPedidoUsecase;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/pedido")
@AllArgsConstructor
public class PedidoController {

    private final CriarPedidoUsecase criarPedidoUsecase;
    private final EfetuarPagamentoUsecase efetuarPagamentoUsecase;
    private final ListarPedidoUsecase listarPedidoUsecase;

    @PostMapping
    public ResponseEntity<Pedido> criarPedido(@RequestBody CriarPedidoJson criarPedidoJson) {
        Pedido pedido = criarPedidoUsecase.criarPedido(criarPedidoJson.getCpf(), criarPedidoJson.getItens());

        return ResponseEntity.ok(pedido);
    }

    @PostMapping("/iniciarPagamento")
    public ResponseEntity<Pagamento> criarPagamento(@RequestBody CriarPagamentoJson criarPagamentoJson) {
        Pagamento pagamento = efetuarPagamentoUsecase.criarPagamento(criarPagamentoJson.getPedidoId());

        if (pagamento == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(pagamento);
    }

    @PostMapping("/completarPagamento")
    public ResponseEntity<Pagamento> completarPagamento(@RequestBody CompletarPagamentoJson completarPagamentoJson) {
        Pagamento pagamento = efetuarPagamentoUsecase.finalizarPagamento(completarPagamentoJson.getPagamentoId());

        return ResponseEntity.ok(pagamento);
    }

    @GetMapping("/{pedidoId}")
    public ResponseEntity<Pedido> getPagamento(@PathVariable long pedidoId) {
        return ResponseEntity.ok(listarPedidoUsecase.getPedido(pedidoId));
    }
}
