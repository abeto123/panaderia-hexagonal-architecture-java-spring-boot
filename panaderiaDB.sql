-- Eliminamos la base de datos si ya existe para empezar desde cero.
DROP DATABASE IF EXISTS PanaderiaBarriosDB;
-- Creamos la nueva base de datos.
CREATE DATABASE PanaderiaBarriosDB;
-- Seleccionamos la base de datos para trabajar sobre ella.
USE PanaderiaBarriosDB;

-- -----------------------------------------------------
-- Tabla: cliente
-- -----------------------------------------------------
CREATE TABLE cliente (
  id_cliente INT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(50),
  apellidos VARCHAR(100),
  email VARCHAR(100) UNIQUE,
  telefono VARCHAR(15),
  contrasenia VARCHAR(255),
  rol ENUM('CLIENTE', 'ADMIN') DEFAULT 'CLIENTE',
  ruc VARCHAR(11),
  razon_social VARCHAR(255),
  fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------------
-- Tablas de Ubicación Geográfica
-- -----------------------------------------------------
CREATE TABLE departamento (
  id_departamento INT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(100)
);

CREATE TABLE provincia (
  id_provincia INT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(100),
  id_departamento INT,
  FOREIGN KEY (id_departamento) REFERENCES departamento(id_departamento) ON DELETE CASCADE
);

CREATE TABLE distrito (
  id_distrito INT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(100),
  id_provincia INT,
  FOREIGN KEY (id_provincia) REFERENCES provincia(id_provincia) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Tabla: direccion
-- -----------------------------------------------------
CREATE TABLE direccion (
  id_direccion INT PRIMARY KEY AUTO_INCREMENT,
  calle VARCHAR(100),
  numero VARCHAR(10),
  referencia VARCHAR(255),
  alias VARCHAR(50),
  id_cliente INT,
  id_distrito INT,
  FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente) ON DELETE CASCADE,
  FOREIGN KEY (id_distrito) REFERENCES distrito(id_distrito) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Tablas Empresa y Sede
-- -----------------------------------------------------
CREATE TABLE empresa (
  id_empresa INT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(100),
  ruc VARCHAR(11)
);

CREATE TABLE sede (
  id_sede INT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(100),
  telefono VARCHAR(15),
  id_empresa INT,
  id_distrito INT,
  FOREIGN KEY (id_empresa) REFERENCES empresa(id_empresa) ON DELETE CASCADE,
  FOREIGN KEY (id_distrito) REFERENCES distrito(id_distrito) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Tabla: zona_disponible_envio
-- -----------------------------------------------------
CREATE TABLE zona_disponible_envio (
  id_zona INT PRIMARY KEY AUTO_INCREMENT,
  id_sede INT,
  id_distrito INT,
  costo_envio DECIMAL(10,2) DEFAULT 0,
  monto_minimo_gratis DECIMAL(10,2),
  tiempo_estimado_min INT,
  FOREIGN KEY (id_sede) REFERENCES sede(id_sede) ON DELETE CASCADE,
  FOREIGN KEY (id_distrito) REFERENCES distrito(id_distrito) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Tabla: categoria
-- -----------------------------------------------------
CREATE TABLE categoria (
  id_categoria INT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(50)
);

-- -----------------------------------------------------
-- Tabla: producto
-- -----------------------------------------------------
CREATE TABLE producto (
  id_producto INT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(100),
  descripcion TEXT,
  foto VARCHAR(255),
  precio DECIMAL(10,2),
  stock_minimo INT DEFAULT 0, -- lo minimo que se puede llevar un cliente,
  disponible BOOLEAN DEFAULT TRUE,
  id_categoria INT,
  FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Tabla: inventario
-- -----------------------------------------------------
CREATE TABLE inventario (
  id_inventario INT PRIMARY KEY AUTO_INCREMENT,
  id_sede INT default 1,
  id_producto INT,
  stock INT,
  fecha datetime,
  fecha_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_sede) REFERENCES sede(id_sede) ON DELETE CASCADE,
  FOREIGN KEY (id_producto) REFERENCES producto(id_producto) ON DELETE CASCADE
);
-- -----------------------------------------------------
-- Tabla: pedido
-- -----------------------------------------------------
CREATE TABLE pedido (
  id_pedido INT PRIMARY KEY AUTO_INCREMENT,
  id_cliente INT,
  id_sede INT default 1, 
  id_direccion_entrega INT,
  tipo_entrega ENUM('DOMICILIO', 'RECOJO_TIENDA'),
  fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
  fecha_entrega DATE,
  subtotal_productos DECIMAL(10,2),
  costo_envio DECIMAL(10,2) DEFAULT 0,
  descuento DECIMAL(10,2) DEFAULT 0,
  costo_total DECIMAL(10,2),
  estado ENUM(
    'PENDIENTE_PAGO',
    'PAGADO',
    'EN_PREPARACION',
    'LISTO_PARA_RECOJO',
    'EN_CAMINO',
    'ENTREGADO',
    'CANCELADO'
  ),
  tipo_comprobante ENUM('BOLETA', 'FACTURA'),
  FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente) ON DELETE CASCADE,
  FOREIGN KEY (id_sede) REFERENCES sede(id_sede) ON DELETE CASCADE,
  FOREIGN KEY (id_direccion_entrega) REFERENCES direccion(id_direccion) ON DELETE SET NULL
);

-- -----------------------------------------------------
-- Tabla: pedido_producto
-- -----------------------------------------------------
CREATE TABLE pedido_producto (
  id_pedido INT,
  id_producto INT,
  cantidad INT,
  precio_unitario_congelado DECIMAL(10,2),
  subtotal DECIMAL(10,2),
  PRIMARY KEY (id_pedido, id_producto),
  FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido) ON DELETE CASCADE,
  FOREIGN KEY (id_producto) REFERENCES producto(id_producto) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Tabla: pago
-- -----------------------------------------------------
CREATE TABLE pago (
  id_pago INT PRIMARY KEY AUTO_INCREMENT,
  id_pedido INT,
  monto DECIMAL(10,2),
  metodo_pago ENUM('YAPE', 'PLIN', 'EFECTIVO', 'CREDITO'),
  fecha_pago DATETIME DEFAULT CURRENT_TIMESTAMP,
  estado ENUM('PENDIENTE', 'CONFIRMADO', 'RECHAZADO') DEFAULT 'CONFIRMADO',
  referencia_pago VARCHAR(255) NULL,
  FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Tabla: carrito
-- -----------------------------------------------------
CREATE TABLE carrito (
  id_carrito INT PRIMARY KEY AUTO_INCREMENT,
  id_cliente INT UNIQUE,
  fecha_ultima_modificacion DATETIME DEFAULT CURRENT_TIMESTAMP 
  ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Tabla: carrito_producto
-- -----------------------------------------------------
CREATE TABLE carrito_producto (
  id_carrito INT,
  id_producto INT,
  cantidad INT,
  PRIMARY KEY (id_carrito, id_producto),
  FOREIGN KEY (id_carrito) REFERENCES carrito(id_carrito) ON DELETE CASCADE,
  FOREIGN KEY (id_producto) REFERENCES producto(id_producto) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Tabla: pack_producto
-- -----------------------------------------------------
CREATE TABLE pack_producto (
  id_pack_producto INT PRIMARY KEY AUTO_INCREMENT,
  id_pack INT,
  id_componente INT,
  cantidad INT,
  FOREIGN KEY (id_pack) REFERENCES producto(id_producto) ON DELETE CASCADE,
  FOREIGN KEY (id_componente) REFERENCES producto(id_producto) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS comprobante (
  id_comprobante INT PRIMARY KEY AUTO_INCREMENT,
  id_pedido INT UNIQUE,
  numero_comprobante VARCHAR(50) UNIQUE,
  tipo_comprobante ENUM('BOLETA', 'FACTURA'),
  fecha_generacion DATETIME DEFAULT CURRENT_TIMESTAMP,
  archivo_path VARCHAR(500),
  FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- DATOS
-- -----------------------------------------------------

SET NAMES utf8;

-- =====================================================
-- DATOS ADAPTADOS DE PanaderiaBarriosDB2
-- PARA INSERTAR EN PanaderiaBarriosDB
-- =====================================================

SET NAMES utf8;

-- =====================================================
-- EMPRESA
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

INSERT INTO producto(
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