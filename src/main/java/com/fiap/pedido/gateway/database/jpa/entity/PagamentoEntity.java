package com.fiap.pedido.gateway.database.jpa.entity;

import com.fiap.pedido.domain.Pagamento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "pagamento")
public class PagamentoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal valor;

    @OneToOne
    @JoinColumn(name = "pedido_id")
    private PedidoEntity pedido;

    @Enumerated(EnumType.STRING)
    private Pagamento.Status status;
}
