package com.fiap.pedido.gateway.database.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "compra_produto")
public class CompraProdutoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long produtoId;
    private BigDecimal preco;
    private Integer quantidade;
    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private PedidoEntity pedido;
}
