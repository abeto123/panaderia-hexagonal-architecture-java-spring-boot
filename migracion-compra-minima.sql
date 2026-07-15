-- =====================================================
-- MIGRACIÓN: compra mínima por producto
-- -----------------------------------------------------
-- Tu aplicación usa spring.sql.init.mode=never, es decir que
-- schema.sql/data.sql NO se ejecutan automáticamente contra tu
-- base de datos ya existente. Ejecuta este script UNA VEZ en tu
-- cliente de MySQL (Workbench, DBeaver, consola, etc.) para que
-- la nueva columna quede disponible.
-- =====================================================

ALTER TABLE producto
  ADD COLUMN IF NOT EXISTS compra_minima INT NOT NULL DEFAULT 1;

-- Ejemplo pedido en la tarea: la Marraqueta requiere mínimo 75 unidades.
UPDATE producto SET compra_minima = 75 WHERE nombre = 'Marraqueta Tacneña';

-- Si quieres exigir un mínimo distinto para otro producto, cambia el nombre:
-- UPDATE producto SET compra_minima = 10 WHERE nombre = 'Pan Batido';
