SET NAMES utf8;

INSERT INTO empresa(nombre, ruc) VALUES
('Panaderia y Pasteleria Barrios', '10004847356');

INSERT INTO departamento(nombre) VALUES
('Tacna');

INSERT INTO provincia(nombre, id_departamento) VALUES
('Tacna', 1);

INSERT INTO distrito(nombre, id_provincia) VALUES
('Tacna', 1),
('Ciudad Nueva', 1),
('Pocollay', 1),
('Cnel. Gregorio Albarracin', 1);

INSERT INTO sede(nombre, telefono, id_empresa, id_distrito) VALUES
('Sede Principal Natividad', '988954525', 1, 1);

INSERT INTO cliente(
  nombre,
  apellidos,
  email,
  telefono,
  contrasenia,
  rol,
  ruc,
  razon_social
) VALUES
(
  'Alberto',
  'Barrios Rivera',
  'albertix91@gmail.com',
  '988954525',
  '$2y$10$tyYy3kcalab4PtpCei9Tuec4dfEE/K6Tj4l0DvTQb/LB0hmzYNS9K',
  'CLIENTE',
  '20123456789',
  'Abarrote alberto'
),
(
  'Alberto',
  'Barrios Rivera',
  'abarriosriv@unjbg.edu.pe',
  '988954525',
  '$2y$10$tyYy3kcalab4PtpCei9Tuec4dfEE/K6Tj4l0DvTQb/LB0hmzYNS9K',
  'ADMIN',
  '20123456789',
  'Abarrote alberto'
);

INSERT INTO direccion(
  calle,
  numero,
  referencia,
  alias,
  id_cliente,
  id_distrito
) VALUES
(
  'Calle 8 de septiembre',
  '2127',
  'Abajo del colegio perubirf',
  'Casa',
  1,
  1
);

INSERT INTO zona_disponible_envio(
  id_sede,
  id_distrito,
  costo_envio,
  monto_minimo_gratis
) VALUES
(1, 1, 10.00, 75.00),
(1, 2, 15.00, 75.00),
(1, 3, 12.00, 75.00),
(1, 4, 15.00, 75.00);

INSERT INTO categoria(nombre) VALUES
('Panes Salados'),
('Panes Dulces'),
('Panes Integrales'),
('Especiales de Temporada'),
('Pastelería y Repostería'),
('Packs y Ofertas');

INSERT INTO producto(
  nombre,
  descripcion,
  foto,
  precio,
  stock,
  moneda,
  categoria,
  disponible,
  id_categoria
) VALUES
(
  'Marraqueta Tacneña',
  'Pan tradicional de Tacna.',
  'marraqueta.jpg',
  0.13,
  100,
  'PEN',
  'PANADERIA',
  TRUE,
  1
),
(
  'Pan Batido',
  'Pan suave y esponjoso.',
  'batido.jpg',
  0.13,
  100,
  'PEN',
  'PANADERIA',
  TRUE,
  1
),
(
  'Pan Hallulla',
  'Pan plano y sabroso.',
  'hallulla.jpg',
  0.13,
  100,
  'PEN',
  'PANADERIA',
  TRUE,
  1
),
(
  'Pan Integral',
  'Pan integral saludable.',
  'integral.jpg',
  0.13,
  80,
  'PEN',
  'PANADERIA',
  TRUE,
  3
),
(
  'Panetón Tradicional',
  'Panetón clásico navideño.',
  'paneton_pasas.jpg',
  19.00,
  20,
  'PEN',
  'PASTELERIA',
  TRUE,
  4
),
(
  'Empanada de queso',
  'Empanada rellena de queso.',
  'empanada.jpg',
  2.50,
  50,
  'PEN',
  'PASTELERIA',
  TRUE,
  5
);

INSERT INTO carrito (id_cliente)
SELECT id_cliente
FROM cliente
WHERE id_cliente NOT IN (
  SELECT id_cliente FROM carrito
);