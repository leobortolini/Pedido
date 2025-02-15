package com.fiap.pedido.gateway;

import com.fiap.pedido.domain.Cliente;

public interface ClienteGateway {

    Cliente buscarCliente(String cpf);
}
