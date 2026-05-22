package com.panaderia.ecommerce.cliente.infrastructure.jpa;

import com.panaderia.ecommerce.cliente.domain.Cliente;
import com.panaderia.ecommerce.cliente.domain.Direccion;
import com.panaderia.ecommerce.cliente.domain.RazonSocial;
import com.panaderia.ecommerce.cliente.domain.Ruc;
import com.panaderia.ecommerce.autenticacion.domain.Rol;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClienteMapperImpl {

    public Cliente toDomain(ClienteEntity entity) {
        if (entity == null) {
            return null;
        }
        // Direcciones de cliente se cargan por JDBC en los controladores administrativos,
        // no por JPA, para mantener compatibilidad con el esquema actual de la tabla direccion.
        return new Cliente(entity.getId(), entity.getNombres(), entity.getApellidos(), entity.getEmail(), entity.getTelefono(),
            new Ruc(entity.getRuc()), new RazonSocial(entity.getRazonSocial()), List.of(), entity.getRol());
    }

    public ClienteEntity toEntity(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        ClienteEntity entity = new ClienteEntity();
        entity.setId(cliente.getId());
        entity.setNombres(cliente.getNombres());
        entity.setApellidos(cliente.getApellidos());
        entity.setEmail(cliente.getEmail());
        entity.setTelefono(cliente.getTelefono());
        entity.setRuc(cliente.getRuc() != null ? cliente.getRuc().getValor() : null);
        entity.setRazonSocial(cliente.getRazonSocial() != null ? cliente.getRazonSocial().getValor() : null);
        entity.setDirecciones(cliente.getDirecciones().stream()
                .map(d -> new DireccionEmbeddable(d.getAlias(), d.getCalle(), d.getNumero(), d.getDistrito(), d.getReferencia()))
                .collect(Collectors.toList()));
        entity.setRol(cliente.getRol());
        return entity;
    }
}
