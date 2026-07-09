<?php

namespace Infrastructure\Secondary\Persistence;

use Domain\Pedido;
use Domain\ValueObjects\FechaEntrega;
use Domain\ValueObjects\Dinero;
use Domain\Ports\Secondary\PedidoRepositoryPort;

class PedidoRepositoryAdapter implements PedidoRepositoryPort {
    // Simulación de EntityManager
    private $entityManager;

    public function __construct($entityManager) {
        $this->entityManager = $entityManager;
    }

    public function guardar(Pedido $pedido): void {
        $entity = new PedidoEntity();
        $entity->idCliente = $pedido->getIdCliente(); // necesitas getter
        $entity->fechaEntrega = $pedido->getFechaEntrega();
        $entity->subtotal = $pedido->getSubtotal();
        $entity->costoEnvio = $pedido->getCostoEnvio(); // agregar método
        $entity->total = $pedido->getTotal();

        $this->entityManager->persist($entity);
        $this->entityManager->flush();
    }

    public function buscarPorId(int $id): ?Pedido {
        $entity = $this->entityManager->find(PedidoEntity::class, $id);
        if (!$entity) return null;

        return new Pedido(
            $entity->idCliente,
            new FechaEntrega($entity->fechaEntrega),
            new Dinero($entity->subtotal)
        );
    }
}