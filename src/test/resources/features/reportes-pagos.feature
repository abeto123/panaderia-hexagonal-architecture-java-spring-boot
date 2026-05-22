# language: es
Característica: Emisión de reportes de pagos
  Como administrador
  Quiero emitir reportes de pagos
  Para tener control de pedidos facturados

  Regla: El administrador debe haber iniciado sesión en el sistema

    Escenario: Emisión de reporte de pagos con datos exitosa
      Dado que el sistema tiene registrados pagos realizados
      Cuando el administrador solicita emitir un reporte de pagos
      Entonces el reporte se genera correctamente
      Y el sistema devuelve la lista de pagos registrados
      Y la cantidad de pagos en el reporte es mayor a cero

    Escenario: Intento de emisión de reporte sin pagos registrados
      Dado que el sistema no tiene registrados pagos realizados
      Cuando el administrador solicita emitir un reporte de pagos
      Entonces el sistema rechaza la solicitud
      Y el sistema devuelve el mensaje "No existen pagos registrados"
      Y no se genera ningún reporte
