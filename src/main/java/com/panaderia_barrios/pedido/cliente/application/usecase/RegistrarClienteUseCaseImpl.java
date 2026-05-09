package com.panaderia_barrios.pedido.cliente.application.usecase;

import com.panaderia_barrios.pedido.cliente.domain.Cliente;
import com.panaderia_barrios.pedido.cliente.domain.input.RegistrarClienteUseCase;
import com.panaderia_barrios.pedido.cliente.domain.output.ClienteRepository;

import org.springframework.stereotype.Service;

@Service
public class RegistrarClienteUseCaseImpl implements RegistrarClienteUseCase {

    private final ClienteRepository clienteRepository;

    public RegistrarClienteUseCaseImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public Cliente registrar(Cliente cliente) {
        // Check if email exists
        if (clienteRepository.findByEmail(cliente.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email ya registrado");
        }
        // Save
        Cliente saved = clienteRepository.save(cliente);
        // TODO: Crear carrito automáticamente
        return saved;
    }
}