package com.panaderia_barrios.pedido.carrito.infraestructure.output.persistence;

import com.panaderia_barrios.pedido.carrito.domain.Carrito;
import com.panaderia_barrios.pedido.carrito.domain.CarritoItem;
import com.panaderia_barrios.pedido.carrito.domain.ports.output.CarritoRepository;
import com.panaderia_barrios.pedido.catalogo.infraestructure.output.persistence.JpaProductoRepository;
import com.panaderia_barrios.pedido.catalogo.infraestructure.output.persistence.ProductoEntity;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CarritoRepositoryAdapter implements CarritoRepository {

    private final JpaCarritoRepository carritoRepository;
    private final JpaCarritoProductoRepository carritoProductoRepository;
    private final JpaProductoRepository productoRepository;

    public CarritoRepositoryAdapter(JpaCarritoRepository carritoRepository,
                                    JpaCarritoProductoRepository carritoProductoRepository,
                                    JpaProductoRepository productoRepository) {
        this.carritoRepository = carritoRepository;
        this.carritoProductoRepository = carritoProductoRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    public Carrito obtenerPorClienteId(int clienteId) {
        Long carritoId = findOrCreateCarritoId(clienteId);
        List<CarritoItem> items = carritoProductoRepository.findByIdCarrito(carritoId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
        return new Carrito(clienteId, items);
    }

    @Override
    public Carrito agregarOActualizarItem(int clienteId, CarritoItem item) {
        Long carritoId = findOrCreateCarritoId(clienteId);
        CarritoProductoEntity existente = carritoProductoRepository
                .findByIdCarritoAndIdProducto(carritoId, item.getProductoId())
                .orElse(null);

        if (existente == null) {
            existente = new CarritoProductoEntity(carritoId, item.getProductoId(), item.getCantidad(), item.getMontoSolicitadoEntero());
        } else {
            existente.setCantidad(item.getCantidad());
            existente.setMontoSolicitadoEntero(item.getMontoSolicitadoEntero());
        }

        carritoProductoRepository.save(existente);
        return obtenerPorClienteId(clienteId);
    }

    @Override
    public boolean eliminarItem(int clienteId, int productoId) {
        Long carritoId = findOrCreateCarritoId(clienteId);
        if (carritoProductoRepository.findByIdCarritoAndIdProducto(carritoId, productoId).isPresent()) {
            carritoProductoRepository.deleteByIdCarritoAndIdProducto(carritoId, productoId);
            return true;
        }
        return false;
    }

    @Override
    public void vaciar(int clienteId) {
        Long carritoId = findOrCreateCarritoId(clienteId);
        carritoProductoRepository.deleteByIdCarrito(carritoId);
    }

    private Long findOrCreateCarritoId(int clienteId) {
        return carritoRepository.findFirstByIdCliente(clienteId)
                .map(CarritoEntity::getId)
                .orElseGet(() -> carritoRepository.save(new CarritoEntity(clienteId)).getId());
    }

    private CarritoItem toDomain(CarritoProductoEntity entity) {
        var producto = productoRepository.findById(entity.getIdProducto()).orElse(null);
        String nombre = producto != null ? producto.getNombre() : null;
        String foto = producto != null ? producto.getFoto() : null;
        double precioUnitario = producto != null ? calcularPrecioUnitario(producto) : 0.0;
        return new CarritoItem(
                entity.getIdProducto(),
                entity.getCantidad(),
                entity.getMontoSolicitadoEntero(),
                nombre,
                foto,
                precioUnitario
        );
    }

    private double calcularPrecioUnitario(ProductoEntity producto) {
        if (producto.getUnidadesBaseB2b() > 0 && producto.getSolesBaseB2b() > 0) {
            return producto.getSolesBaseB2b() / producto.getUnidadesBaseB2b();
        }
        return producto.getPrecioB2c();
    }
}