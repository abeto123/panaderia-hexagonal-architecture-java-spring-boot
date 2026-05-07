<?php

namespace Infrastructure\Secondary\Persistence;

// Nota: si usaras Doctrine, aquí irían anotaciones @Entity, @Column, etc.
class PedidoEntity {
    public int $id;
    public int $idCliente;
    public string $fechaEntrega;
    public float $subtotal;
    public float $costoEnvio;
    public float $total;
}