<?php

namespace Application\Services;

use Domain\Pedido;
use Domain\ValueObjects\FechaEntrega;
use Domain\ValueObjects\Dinero;
use Domain\Ports\Primary\PedidoUseCasePort;
use Domain\Ports\Secondary\PedidoRepositoryPort;

class PedidoService implements PedidoUseCasePort {
    private PedidoRepositoryPort $pedidoRepository;

    public function __construct(PedidoRepositoryPort $pedidoRepository) {
        $this->pedidoRepository = $pedidoRepository;
    }

    public function crearPedido(int $idCliente, string $fechaEntrega, float $subtotal): Pedido {
        $fecha = new FechaEntrega($fechaEntrega);
        $dinero = new Dinero($subtotal);
        $pedido = new Pedido($idCliente, $fecha, $dinero);
        $this->pedidoRepository->guardar($pedido);
        return $pedido;
    }

    public function agregarCostoEnvio(int $pedidoId, float $costo): void {
        $pedido = $this->pedidoRepository->buscarPorId($pedidoId);
        if (!$pedido) {
            throw new \Exception("Pedido no encontrado");
        }
        $pedido->agregarCostoEnvio(new Dinero($costo));
        $this->pedidoRepository->guardar($pedido);
    }
}
