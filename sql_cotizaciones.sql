-- ============================================================
-- Módulo 3.4: Cotizaciones / Prospectos
-- Minimarket LAREDO ERP — Fase 3
-- ============================================================

CREATE TABLE IF NOT EXISTS cotizacion (
    idCotizacion   INT AUTO_INCREMENT PRIMARY KEY,
    idCliente      INT NOT NULL,
    idUsuario      INT NOT NULL,
    fecha          DATETIME DEFAULT CURRENT_TIMESTAMP,
    vigencia       DATE NOT NULL COMMENT 'Fecha límite de validez de la cotización',
    subtotal       DECIMAL(10,2) NOT NULL DEFAULT 0,
    igv            DECIMAL(10,2) NOT NULL DEFAULT 0,
    total          DECIMAL(10,2) NOT NULL DEFAULT 0,
    estado         ENUM('Pendiente','Aprobada','Rechazada','Convertida') DEFAULT 'Pendiente',
    observaciones  TEXT NULL,
    FOREIGN KEY (idCliente)  REFERENCES cliente(idCliente),
    FOREIGN KEY (idUsuario)  REFERENCES usuario(idUsuario)
);

CREATE TABLE IF NOT EXISTS detalle_cotizacion (
    idDetalle      INT AUTO_INCREMENT PRIMARY KEY,
    idCotizacion   INT NOT NULL,
    idProducto     INT NOT NULL,
    cantidad       INT NOT NULL,
    precioUnitario DECIMAL(10,2) NOT NULL,
    subtotal       DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (idCotizacion) REFERENCES cotizacion(idCotizacion) ON DELETE CASCADE,
    FOREIGN KEY (idProducto)   REFERENCES producto(idProducto)
);
