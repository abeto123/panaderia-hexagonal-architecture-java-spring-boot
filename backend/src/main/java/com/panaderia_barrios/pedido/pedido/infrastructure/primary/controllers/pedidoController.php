<?php

namespace Infrastructure\Primary\Controllers;

use Domain\Ports\Primary\PedidoUseCasePort;

class PedidoController {
    private PedidoUseCasePort $pedidoUseCase;

    public function __construct(PedidoUseCasePort $pedidoUseCase) {
        $this->pedidoUseCase = $pedidoUseCase;
    }

    public function crearPedido($request) {
        // Simulación de HTTP
        $idCliente = $request['idCliente'];
        $fechaEntrega = $request['fechaEntrega'];
        $subtotal = $request['subtotal'];

        $pedido = $this->pedidoUseCase->crearPedido($idCliente, $fechaEntrega, $subtotal);

        return [
            'status' => 'success',
            'total' => $pedido->getTotal()
        ];
    }

    public function agregarCostoEnvio($pedidoId, $costo) {
        $this->pedidoUseCase->agregarCostoEnvio($pedidoId, $costo);
        return ['status' => 'costo de envío agregado'];
    }
}