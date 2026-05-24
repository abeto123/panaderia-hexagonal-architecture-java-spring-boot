SET NAMES utf8;

CREATE TABLE IF NOT EXISTS cliente (
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

CREATE TABLE IF NOT EXISTS departamento (
  id_departamento INT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS provincia (
  id_provincia INT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(100),
  id_departamento INT,
  FOREIGN KEY (id_departamento) REFERENCES departamento(id_departamento) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS distrito (
  id_distrito INT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(100),
  id_provincia INT,
  FOREIGN KEY (id_provincia) REFERENCES provincia(id_provincia) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS direccion (
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

CREATE TABLE IF NOT EXISTS empresa (
  id_empresa INT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(100),
  ruc VARCHAR(11)
);

CREATE TABLE IF NOT EXISTS sede (
  id_sede INT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(100),
  telefono VARCHAR(15),
  id_empresa INT,
  id_distrito INT,
  FOREIGN KEY (id_empresa) REFERENCES empresa(id_empresa) ON DELETE CASCADE,
  FOREIGN KEY (id_distrito) REFERENCES distrito(id_distrito) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS zona_disponible_envio (
  id_zona INT PRIMARY KEY AUTO_INCREMENT,
  id_sede INT,
  id_distrito INT,
  costo_envio DECIMAL(10,2) DEFAULT 0,
  monto_minimo_gratis DECIMAL(10,2),
  tiempo_estimado_min INT,
  FOREIGN KEY (id_sede) REFERENCES sede(id_sede) ON DELETE CASCADE,
  FOREIGN KEY (id_distrito) REFERENCES distrito(id_distrito) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS categoria (
  id_categoria INT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS producto (
  id_producto INT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(100),
  descripcion TEXT,
  foto VARCHAR(255),
  precio DECIMAL(10,2),
  stock INT DEFAULT 0,
  moneda VARCHAR(3) DEFAULT 'PEN',
  categoria VARCHAR(50),
  stock_minimo INT DEFAULT 0,
  disponible BOOLEAN DEFAULT TRUE,
  id_categoria INT,
  FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS cliente_direcciones (
  cliente_id INT,
  alias VARCHAR(50),
  calle VARCHAR(100),
  numero VARCHAR(10),
  distrito VARCHAR(100),
  referencia VARCHAR(255),
  FOREIGN KEY (cliente_id) REFERENCES cliente(id_cliente) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS inventario (
  id_inventario INT PRIMARY KEY AUTO_INCREMENT,
  id_sede INT,
  id_producto INT,
  stock INT,
  fecha_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_sede) REFERENCES sede(id_sede) ON DELETE CASCADE,
  FOREIGN KEY (id_producto) REFERENCES producto(id_producto) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS pedido (
  id_pedido INT PRIMARY KEY AUTO_INCREMENT,
  id_cliente INT,
  id_sede INT,
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

CREATE TABLE IF NOT EXISTS pedido_producto (
  id_pedido INT,
  id_producto INT,
  cantidad INT,
  precio_unitario_congelado DECIMAL(10,2),
  subtotal DECIMAL(10,2),
  PRIMARY KEY (id_pedido, id_producto),
  FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido) ON DELETE CASCADE,
  FOREIGN KEY (id_producto) REFERENCES producto(id_producto) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS pago (
  id_pago INT PRIMARY KEY AUTO_INCREMENT,
  id_pedido INT,
  monto DECIMAL(10,2),
  metodo_pago ENUM('YAPE', 'PLIN', 'EFECTIVO', 'CREDITO'),
  fecha_pago DATETIME DEFAULT CURRENT_TIMESTAMP,
  estado ENUM('PENDIENTE', 'CONFIRMADO', 'RECHAZADO') DEFAULT 'CONFIRMADO',
  referencia_pago VARCHAR(255) NULL,
  FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS carrito (
  id_carrito INT PRIMARY KEY AUTO_INCREMENT,
  id_cliente INT UNIQUE,
  fecha_ultima_modificacion DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS carrito_producto (
  id_carrito INT,
  id_producto INT,
  cantidad INT,
  PRIMARY KEY (id_carrito, id_producto),
  FOREIGN KEY (id_carrito) REFERENCES carrito(id_carrito) ON DELETE CASCADE,
  FOREIGN KEY (id_producto) REFERENCES producto(id_producto) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS pack_producto (
  id_pack_producto INT PRIMARY KEY AUTO_INCREMENT,
  id_pack INT,
  id_componente INT,
  cantidad INT,
  FOREIGN KEY (id_pack) REFERENCES producto(id_producto) ON DELETE CASCADE,
  FOREIGN KEY (id_componente) REFERENCES producto(id_producto) ON DELETE CASCADE
);
