package com.panaderia_barrios.pedido.cliente.domain.input;

import com.panaderia_barrios.pedido.cliente.domain.Cliente;

public interface RegistrarClienteUseCase {
    Cliente registrar(Cliente cliente);
}