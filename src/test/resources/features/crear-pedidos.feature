# language: es
Característica: Creación de Pedidos
  Como administrador
  Quiero crear pedidos
  Para realizar ventas presenciales

  Regla: El administrador debe haber iniciado sesión en el sistema
  Regla: Solo se pueden crear pedidos con productos que existen y tienen stock

    Escenario: Creación de pedido exitosa con stock disponible
      Dado que existen productos disponibles en el sistema
      Y exista stock suficiente de los productos seleccionados
      Cuando el administrador registra un nuevo pedido
      Entonces el pedido se crea correctamente en la base de datos
      Y el stock de cada producto se decrementa según la cantidad solicitada
      Y el pedido recibe un identificador único

    Escenario: Intento de creación de pedido con stock insuficiente
      Dado que existen productos disponibles en el sistema
      Y no exista stock suficiente para uno o más productos seleccionados
      Cuando el administrador intenta registrar un pedido
      Entonces el sistema rechaza la creación del pedido
      Y no se crea ningún registro en la base de datos
      Y el stock de productos permanece sin cambios
      Y el sistema devuelve el error "Stock insuficiente"

    Escenario: Intento de creación de pedido con productos inexistentes
      Dado que el administrador intenta crear un pedido
      Y selecciona productos que no existen en el sistema
      Cuando el administrador registra el pedido
      Entonces el sistema rechaza la creación
      Y el sistema devuelve el error "Producto inexistente"
      Y el pedido no es creado
