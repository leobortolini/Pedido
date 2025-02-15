package com.fiap.pedido.config.exception;

import com.fiap.pedido.exception.SystemBaseException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorJson {
    private final String code;
    private final String message;

    public ErrorJson(SystemBaseException baseException) {
        this.code = baseException.getCode();
        this.message = baseException.getMessage();
    }
}
