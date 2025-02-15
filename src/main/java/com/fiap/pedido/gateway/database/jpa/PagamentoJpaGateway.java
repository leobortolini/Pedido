package com.fiap.pedido.gateway.database.jpa;

import com.fiap.pedido.domain.Pagamento;
import com.fiap.pedido.exception.PagamentoNaoEncontradoException;
import com.fiap.pedido.gateway.PagamentoGateway;
import com.fiap.pedido.gateway.database.jpa.entity.PagamentoEntity;
import com.fiap.pedido.gateway.database.jpa.entity.PedidoEntity;
import com.fiap.pedido.gateway.database.jpa.repository.PagamentoEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class PagamentoJpaGateway implements PagamentoGateway {

    private final PagamentoEntityRepository pagamentoEntityRepository;

    @Override
    public Long criarPagamento(Long pedidoId, Pagamento pagamento) {

        PagamentoEntity pagamentoEntity = new PagamentoEntity();

        pagamentoEntity.setStatus(pagamento.getStatus());
        pagamentoEntity.setValor(pagamento.getValor());

        PedidoEntity pedido = new PedidoEntity();

        pedido.setId(pedidoId);

        pagamentoEntity.setPedido(pedido);

        return pagamentoEntityRepository.save(pagamentoEntity).getId();
    }

    @Override
    public Pagamento.Status getPagamentoStatus(Long pagamentoId) {
        PagamentoEntity pagamentoEntity = pagamentoEntityRepository.findById(pagamentoId).orElseThrow(PagamentoNaoEncontradoException::new);
        return pagamentoEntity.getStatus();
    }

    @Override
    public Pagamento atualizarStatusPagamento(Pagamento pagamento) {
        PagamentoEntity pagamentoEntity = pagamentoEntityRepository.getReferenceById(pagamento.getId());

        pagamentoEntity.setStatus(pagamento.getStatus());
        pagamentoEntityRepository.save(pagamentoEntity);

        pagamento.setValor(pagamentoEntity.getValor());

        return pagamento;
    }

    @Override
    public Long getPedidoIdDoPagamento(Long pagamentoId) {
        return pagamentoEntityRepository.getReferenceById(pagamentoId).getPedido().getId();
    }
}
