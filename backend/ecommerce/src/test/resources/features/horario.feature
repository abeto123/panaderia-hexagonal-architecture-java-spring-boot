Feature: Validación de Horario de Corte para Pedidos

  Scenario: Cliente pide antes de las 8 PM
    Given la hora actual es "19:00" en zona "America/Lima"
    When el cliente intenta programar una entrega
    Then la fecha mínima de entrega debe ser mañana

  Scenario: Cliente pide después de las 8 PM
    Given la hora actual es "21:00" en zona "America/Lima"
    When el cliente intenta programar una entrega
    Then la fecha mínima de entrega debe ser pasado mañana