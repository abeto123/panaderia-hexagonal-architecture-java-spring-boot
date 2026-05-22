# language: es
Característica: Gestión de Productos
  Como administrador
  Quiero editar, actualizar o eliminar productos del catálogo
  Para mantener actualizada la tienda

  Regla: Solo se pueden modificar productos que existen en el sistema

    # Actualización de productos
    Escenario: Actualización de producto con precio válido
      Dado que el producto existe en el sistema
      Cuando el administrador actualiza el precio del producto con un valor válido
      Entonces el producto es actualizado correctamente en la base de datos
      Y el nuevo precio se refleja en el catálogo

    Escenario: Actualización de producto con precio inválido (negativo)
      Dado que el producto existe en el sistema
      Cuando el administrador intenta actualizar el precio con un valor negativo
      Entonces el sistema rechaza la actualización
      Y el precio original permanece sin cambios
      Y el sistema devuelve el error "Precio inválido"

    # Agregación de productos
    Escenario: Agregación de producto con datos válidos
      Dado que el administrador tiene acceso al catálogo de productos
      Cuando el administrador registra un nuevo producto con datos completos y válidos
      Entonces el producto se crea correctamente en la base de datos
      Y el nuevo producto es visible en el catálogo
      Y el producto recibe un identificador único

    Escenario: Agregación de producto con datos inválidos
      Dado que el administrador tiene acceso al catálogo de productos
      Cuando el administrador intenta registrar un producto con datos incompletos o inválidos
      Entonces el sistema rechaza el registro
      Y no se crea ningún producto
      Y el sistema devuelve el error "Datos inválidos"

    # Eliminación de productos
    Escenario: Eliminación de producto sin dependencias
      Dado que el producto existe en el sistema
      Y el producto no está asociado a ningún pedido
      Cuando el administrador elimina el producto
      Entonces el producto se elimina correctamente de la base de datos
      Y el producto ya no aparece en el catálogo

    Escenario: Intento de eliminación de producto asociado a pedidos
      Dado que el producto existe en el sistema
      Y el producto está asociado a pedidos existentes
      Cuando el administrador intenta eliminar el producto
      Entonces el sistema rechaza la eliminación
      Y el producto permanece en la base de datos
      Y el sistema devuelve el error "Producto asociado a pedidos, no puede eliminarse"
