package com.fiap.pedido.gateway.rest;

import com.fiap.pedido.domain.Cliente;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ClienteGatewayHttpImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ClienteGatewayHttpImpl clienteGatewayHttpImpl;

    @Test
    void deveBuscarClienteExistente() {
        String cpf = "12345678900";
        Cliente clienteEsperado = new Cliente("Nome", "95180-000", "000.000.000-00", "Endereco");
        ResponseEntity<Cliente> responseEntity = new ResponseEntity<>(clienteEsperado, HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(Cliente.class)))
                .thenReturn(responseEntity);

        Cliente clienteRetornado = clienteGatewayHttpImpl.buscarCliente(cpf);

        assertNotNull(clienteRetornado);
        assertEquals(clienteEsperado, clienteRetornado);
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(Cliente.class));
    }

    @Test
    void deveRetornarNullQuandoNaoEncontrarCliente() {
        String cpf = "12345678900";
        ResponseEntity<Cliente> responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        when(restTemplate.getForEntity(anyString(), eq(Cliente.class)))
                .thenReturn(responseEntity);

        Cliente clienteRetornado = clienteGatewayHttpImpl.buscarCliente(cpf);

        assertNull(clienteRetornado);
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(Cliente.class));
    }

    @Test
    void deveRetornarNullQuandoExcecaoForLancada() {
        String cpf = "12345678900";
        ResponseEntity<Cliente> responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        when(restTemplate.getForEntity(anyString(), eq(Cliente.class)))
                .thenThrow(HttpClientErrorException.NotFound.class);

        Cliente clienteRetornado = clienteGatewayHttpImpl.buscarCliente(cpf);

        assertNull(clienteRetornado);
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(Cliente.class));
    }
}
