package com.fiap.pedido.gateway.rest;

import com.fiap.pedido.domain.Cliente;
import com.fiap.pedido.gateway.ClienteGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClienteGatewayHttpImpl implements ClienteGateway {

    private final RestTemplate restTemplate;

    @Value("${cliente.gateway.url}")
    private String clienteGatewayUrl;

    @Override
    public Cliente buscarCliente(String cpf) {
        String url = String.format("%s/%s/%s", clienteGatewayUrl, "api/v1/clientes",cpf);

        try {
            ResponseEntity<Cliente> clienteResponseEntity = restTemplate.getForEntity(url, Cliente.class);

            if (clienteResponseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
                return null;
            }

            return clienteResponseEntity.getBody();
        } catch (Exception e) {
            log.error("Erro ao buscar cliente: ", e);
            return null;
        }
    }
}
