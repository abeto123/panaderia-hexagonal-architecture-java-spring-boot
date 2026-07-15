SET NAMES utf8;

-- =====================================================
-- EMPRESA  
-- docker exec -it panaderia-mysql mysql -uroot -proot PanaderiaBarriosDB
-- docker compose down -v
/*





*/


-- =====================================================

INSERT INTO empresa(nombre, ruc) VALUES
('Panaderia y Pasteleria Barrios', '10004847356');

-- =====================================================
-- DEPARTAMENTO
-- =====================================================

INSERT INTO departamento(nombre) VALUES
('Tacna');

-- =====================================================
-- PROVINCIA
-- =====================================================

INSERT INTO provincia(nombre, id_departamento) VALUES
('Tacna', 1);

-- =====================================================
-- DISTRITOS
-- =====================================================

INSERT INTO distrito(nombre, id_provincia) VALUES
('Tacna', 1),
('Ciudad Nueva', 1),
('Pocollay', 1),
('Cnel. Gregorio Albarracin', 1);

-- =====================================================
-- SEDE
-- =====================================================

INSERT INTO sede(nombre, telefono, id_empresa, id_distrito) VALUES
('Sede Principal Natividad', '988954525', 1, 1);

-- =====================================================
-- CLIENTES
-- Adaptación de roles:
-- EMPRESA_FACTURA -> CLIENTE
-- ADMIN -> ADMIN
-- =====================================================

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

-- =====================================================
-- DIRECCIONES
-- =====================================================

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
  'Tienda',
  1,
  1
);

-- =====================================================
-- ZONAS DE ENVÍO
-- =====================================================

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

-- =====================================================
-- CATEGORÍAS
-- =====================================================

INSERT INTO categoria(nombre) VALUES
('Panes Salados'),
('Panes Dulces'),
('Panes Integrales'),
('Especiales de Temporada'),
('Pastelería y Repostería'),
('Packs y Ofertas');

-- =====================================================
-- PRODUCTOS
-- Adaptación:
-- precio_b2c -> precio
-- disponible_b2c -> disponible
-- stock_minimo = 0
-- =====================================================

INSERT IGNORE INTO producto(
  nombre,
  descripcion,
  foto,
  precio,
  stock_minimo,
  disponible,
  id_categoria
) VALUES

-- Panes Salados
(
  'Marraqueta Tacneña',
  'El pan tradicional de Tacna, crujiente por fuera y suave por dentro. Indispensable en la mesa de cada día.',
  'marraqueta.jpg',
  0.13,
  75,
  TRUE,
  1
),
(
  'Pan Batido',
  'De miga suave y esponjosa, este pan es perfecto para sándwiches y lonches. Un clásico que no puede faltar.',
  'batido.jpg',
  0.13,
  75,
  TRUE,
  1
),
(
  'Pan Hallulla',
  'Un pan plano, denso y sabroso, ideal para untar o acompañar comidas. Su textura consistente lo hace muy versátil.',
  'hallulla.jpg',
  0.13,
  75,
  TRUE,
  1
),
(
  'Pan Sarnita',
  'Con su característica forma redondeada y su sabor tradicional, es el preferido por muchos para el desayuno diario.',
  'sarnita.jpg',
  0.13,
  0,
  TRUE,
  1
),
(
  'Pan de Leña',
  'De estilo rústico con una corteza dorada y una miga aireada, perfecto para quienes buscan un sabor más intenso.',
  'lenia.jpg',
  0.13,
  75,
  TRUE,
  1
),
(
  'Pan Cacho',
  'Suave y con un toque de manteca, este pan en forma de cuerno es un favorito para el lonche de la tarde.',
  'cacho.jpg',
  0.13,
  75,
  TRUE,
  1
),
(
  'Pan Carioca',
  'Ligero y de sabor neutro, el pan carioca es un acompañante ideal para cualquier comida, desde sopas hasta guisos.',
  'carioca.jpg',
  0.13,
  75,
  TRUE,
  1
),

-- Panes Integrales
(
  'Pan Integral',
  'Una opción saludable y deliciosa. Hecho con harina integral y una mezcla de semillas de chía y linaza.',
  'integral.jpg',
  0.13,
  75,
  TRUE,
  3
),

-- Especiales de Temporada
(
  'Panetón Tradicional (con Pasas)',
  'El clásico de Navidad. Esponjoso, lleno de pasas y frutas confitadas, y con el aroma inconfundible de la celebración.',
  'paneton_pasas.jpg',
  19.00,
  5,
  TRUE,
  4
),
(
  'Panetón Integral (Dietético)',
  'Una versión más ligera de la tradición. Endulzado con stevia y hecho con harina integral, para disfrutar sin culpas.',
  'paneton_integral.jpg',
  19.00,
  5,
  TRUE,
  4
),
(
  'Panetón Plano',
  'Igual que el paneton tradicional solo que sin molde',
  'paneton_plano.jpg',
  22.00,
  5,
  TRUE,
  4
),

-- Pastelería
(
  'Empanada de queso',
  'Un clásico irresistible. Rellena de una empanada de queso jugoso y lleno de sabor, horneada a la perfección.',
  'empanada.jpg',
  2.50,
  20,
  TRUE,
  5
);


-- =====================================================
-- CREAR CARRITOS AUTOMÁTICAMENTE
-- =====================================================

INSERT INTO carrito (id_cliente)
SELECT id_cliente
FROM cliente
WHERE id_cliente NOT IN (
  SELECT id_cliente FROM carrito
);