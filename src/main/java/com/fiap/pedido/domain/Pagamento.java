package com.fiap.pedido.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Pagamento {
    private Long id;
    private BigDecimal valor;
    private Status status;

    public enum Status {
        PENDENTE,
        FINALIZADO,
        RECUSADO;
    }
}
