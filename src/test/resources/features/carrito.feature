Feature: Gestión del Carrito de Compras

  Scenario: Cliente agrega un producto con stock suficiente
    Given un cliente autenticado
    And un producto "Pan Francés" con precio 2.50 y stock 100
    When el cliente agrega 5 unidades del producto al carrito
    Then el carrito debe contener 5 unidades del producto
    And el subtotal debe ser 12.50

  Scenario: Cliente intenta agregar más productos que el stock disponible
    Given un cliente autenticado
    And un producto "Torta" con precio 25.00 y stock 2
    When el cliente intenta agregar 5 unidades del producto al carrito
    Then debe recibir un mensaje de error "Stock insuficiente"