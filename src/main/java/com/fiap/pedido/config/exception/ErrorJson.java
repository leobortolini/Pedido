package com.fiap.pedido.config.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorJson {
    private final String code;
    private final String message;
}
