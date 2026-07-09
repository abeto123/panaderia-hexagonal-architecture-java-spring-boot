<?php

namespace Domain\Ports\Secondary;

use Domain\Pedido;

interface PedidoRepositoryPort {
    public function guardar(Pedido $pedido): void;
    public function buscarPorId(int $id): ?Pedido;
}