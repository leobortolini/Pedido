package com.fiap.pedido.gateway.database.jpa;

import com.fiap.pedido.domain.Pagamento;
import com.fiap.pedido.gateway.database.jpa.entity.PagamentoEntity;
import com.fiap.pedido.gateway.database.jpa.entity.PedidoEntity;
import com.fiap.pedido.gateway.database.jpa.repository.PagamentoEntityRepository;
import com.fiap.pedido.exception.PagamentoNaoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PagamentoJpaGatewayTest {

    @Mock
    private PagamentoEntityRepository pagamentoEntityRepository;

    @InjectMocks
    private PagamentoJpaGateway pagamentoJpaGateway;

    private Pagamento pagamento;
    private PagamentoEntity pagamentoEntity;
    private PedidoEntity pedidoEntity;

    @BeforeEach
    void setUp() {
        pagamento = new Pagamento();
        pagamento.setId(1L);
        pagamento.setStatus(Pagamento.Status.FINALIZADO);
        pagamento.setValor(new BigDecimal("100.00"));

        pagamentoEntity = new PagamentoEntity();
        pagamentoEntity.setId(1L);
        pagamentoEntity.setStatus(Pagamento.Status.FINALIZADO);
        pagamentoEntity.setValor(new BigDecimal("100.00"));

        pedidoEntity = new PedidoEntity();
        pedidoEntity.setId(1L);
        pagamentoEntity.setPedido(pedidoEntity);
    }

    @Test
    void deveCriarPagamentoComSucesso() {
        when(pagamentoEntityRepository.save(any(PagamentoEntity.class))).thenReturn(pagamentoEntity);

        Long pagamentoId = pagamentoJpaGateway.criarPagamento(1L, pagamento);

        assertNotNull(pagamentoId);
        assertEquals(pagamentoEntity.getId(), pagamentoId);
        ArgumentCaptor<PagamentoEntity> captor = ArgumentCaptor.forClass(PagamentoEntity.class);

        verify(pagamentoEntityRepository, times(1)).save(captor.capture());
        PagamentoEntity capturado = captor.getValue();

        assertEquals(pagamento.getStatus(), capturado.getStatus());
        assertEquals(pagamento.getValor(), capturado.getValor());
        assertEquals(pedidoEntity.getId(), capturado.getPedido().getId());
    }

    @Test
    void deveRetornarStatusPagamentoComSucesso() {
        when(pagamentoEntityRepository.findById(1L)).thenReturn(java.util.Optional.of(pagamentoEntity));

        Pagamento.Status status = pagamentoJpaGateway.getPagamentoStatus(1L);

        assertEquals(Pagamento.Status.FINALIZADO, status);
    }

    @Test
    void deveLancarExcecaoQuandoNaoEncontrarPagamento() {
        when(pagamentoEntityRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(PagamentoNaoEncontradoException.class, () -> pagamentoJpaGateway.getPagamentoStatus(1L));
    }

    @Test
    void deveAtualizarStatusPagamento() {
        pagamento.setStatus(Pagamento.Status.PENDENTE);
        when(pagamentoEntityRepository.getReferenceById(1L)).thenReturn(pagamentoEntity);
        when(pagamentoEntityRepository.save(any(PagamentoEntity.class))).thenReturn(pagamentoEntity);

        Pagamento pagamentoAtualizado = pagamentoJpaGateway.atualizarStatusPagamento(pagamento);

        assertEquals(Pagamento.Status.PENDENTE, pagamentoAtualizado.getStatus());
        assertEquals(pagamentoEntity.getValor(), pagamentoAtualizado.getValor());
        ArgumentCaptor<PagamentoEntity> captor = ArgumentCaptor.forClass(PagamentoEntity.class);

        verify(pagamentoEntityRepository, times(1)).save(captor.capture());
        PagamentoEntity capturado = captor.getValue();

        assertEquals(Pagamento.Status.PENDENTE, capturado.getStatus());
    }

    @Test
    void deveRetornarPagamentoPorId() {
        when(pagamentoEntityRepository.getReferenceById(1L)).thenReturn(pagamentoEntity);

        Long pedidoId = pagamentoJpaGateway.getPedidoIdDoPagamento(1L);

        assertEquals(pedidoEntity.getId(), pedidoId);
    }
}
