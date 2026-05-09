package com.panaderia_barrios.pedido.pedido.infraestructure.output.persistence;

import com.panaderia_barrios.pedido.cliente.domain.IdCliente;
import com.panaderia_barrios.pedido.envio.infraestructure.output.persistence.TipoEntregaEntity;
import com.panaderia_barrios.pedido.pedido.domain.Pedido;
import com.panaderia_barrios.pedido.pedido.domain.PedidoItem;
import com.panaderia_barrios.pedido.pedido.domain.ports.output.PedidoRepository;
import com.panaderia_barrios.pedido.shared.common.Dinero;
import com.panaderia_barrios.pedido.shared.common.FechaEntrega;
import com.panaderia_barrios.pedido.shared.common.TipoEntrega;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PedidoRepositoryAdapter implements PedidoRepository {

    private final JpaPedidoRepository jpaRepository;
    private final JpaPedidoProductoRepository jpaPedidoProductoRepository;

    public PedidoRepositoryAdapter(JpaPedidoRepository jpaRepository,
                                   JpaPedidoProductoRepository jpaPedidoProductoRepository) {
        this.jpaRepository = jpaRepository;
        this.jpaPedidoProductoRepository = jpaPedidoProductoRepository;
    }

    @Override
    @Transactional
    public Pedido save(Pedido pedido) {
        PedidoEntity entity = toEntity(pedido);
        if (pedido.getId() != null) {
            entity.setId(pedido.getId());
        }
        PedidoEntity savedPedido = jpaRepository.save(entity);
        List<PedidoProductoEntity> productoEntities = pedido.getItems().stream()
                .map(item -> toEntity(item, savedPedido.getId()))
                .collect(Collectors.toList());
        jpaPedidoProductoRepository.saveAll(productoEntities);
        return toDomain(savedPedido);
    }

    @Override
    public Optional<Pedido> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Pedido> findByClienteId(int clienteId) {
        return jpaRepository.findByIdCliente(clienteId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private PedidoItem toDomain(PedidoProductoEntity entity) {
        return new PedidoItem(
                entity.getIdProducto(),
                entity.getCantidad(),
                entity.getMontoSolicitadoEntero(),
                entity.getPrecioUnitarioCongelado(),
                entity.getSubtotal()
        );
    }

    private PedidoEntity toEntity(Pedido pedido) {
        return new PedidoEntity(
                pedido.getIdCliente().getId(),
                pedido.getIdSede(),
                pedido.getIdDireccionEntrega(),
                TipoEntregaEntity.valueOf(pedido.getTipoEntrega().name()),
                java.time.LocalDate.parse(pedido.getFechaEntrega().getFecha()),
                pedido.getVentanaEntrega(),
                pedido.getSubtotal(),
                pedido.getCostoEnvio().getMonto(),
                pedido.getTotal(),
                pedido.getEstado(),
                pedido.getTipoComprobante()
        );
    }

    private PedidoProductoEntity toEntity(PedidoItem item, Long pedidoId) {
        return new PedidoProductoEntity(
                pedidoId,
                item.getProductoId(),
                item.getCantidad(),
                item.getMontoSolicitadoEntero(),
                item.getPrecioUnitarioCongelado(),
                item.getSubtotal()
        );
    }

    private Pedido toDomain(PedidoEntity entity) {
        List<PedidoItem> items = jpaPedidoProductoRepository.findByIdPedido(entity.getId()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
        return new Pedido(
                entity.getId(),
                new IdCliente(entity.getIdCliente()),
                entity.getIdSede(),
                entity.getIdDireccionEntrega(),
                TipoEntrega.valueOf(entity.getTipoEntrega().name()),
                new FechaEntrega(entity.getFechaEntrega().toString()),
                entity.getVentanaEntrega(),
                new Dinero(entity.getSubtotalProductos()),
                new Dinero(entity.getCostoEnvio()),
                entity.getEstado(),
                entity.getTipoComprobante(),
                items
        );
    }
}