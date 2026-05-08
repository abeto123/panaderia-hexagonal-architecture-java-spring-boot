<?php

namespace Domain\Ports\Primary;

use Domain\Pedido;

interface PedidoUseCasePort {
    public function crearPedido(int $idCliente, string $fechaEntrega, float $subtotal): Pedido;
    public function agregarCostoEnvio(int $pedidoId, float $costo): void;
}