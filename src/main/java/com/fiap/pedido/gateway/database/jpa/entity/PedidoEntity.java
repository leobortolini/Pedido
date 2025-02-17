package com.fiap.pedido.gateway.database.jpa.entity;

import com.fiap.pedido.domain.Pedido;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "pedido")
public class PedidoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pedido", fetch = FetchType.EAGER)
    private List<CompraProdutoEntity> produtoList;

    @ManyToOne
    private ClienteEntity cliente;

    @OneToOne(mappedBy = "pedido")
    private PagamentoEntity pagamento;

    @Enumerated(EnumType.STRING)
    private Pedido.Status status;
}
