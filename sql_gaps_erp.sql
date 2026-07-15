-- =============================================================================
-- sql_gaps_erp.sql — Objetos de BD pendientes según plan-gaps-erp.md
-- Ejecutar en la base de datos sistemaventas (o la que uses para el proyecto)
-- =============================================================================

-- ─── GAP 2: RRHH — Tabla evaluación de desempeño ─────────────────────────────
-- Ajustada a los 5 criterios (0-10) que usa EvaluacionDAO.java y
-- IFrmEvaluacionDesempeno.java (puntualidad, productividad, trabajo_equipo,
-- actitud, cumplimiento). El promedio se calcula en Java para compatibilidad
-- con versiones de MySQL < 5.7.6 que no soportan columnas generadas.
CREATE TABLE IF NOT EXISTS evaluacion_desempeno (
  idEvaluacion      INT          NOT NULL AUTO_INCREMENT,
  idEmpleado        INT          NOT NULL,
  idEvaluador       INT          NOT NULL,
  periodo           VARCHAR(7)   NOT NULL COMMENT 'Formato YYYY-MM',
  puntualidad       TINYINT      NOT NULL DEFAULT 5 COMMENT '0-10',
  productividad     TINYINT      NOT NULL DEFAULT 5 COMMENT '0-10',
  trabajo_equipo    TINYINT      NOT NULL DEFAULT 5 COMMENT '0-10',
  actitud           TINYINT      NOT NULL DEFAULT 5 COMMENT '0-10',
  cumplimiento      TINYINT      NOT NULL DEFAULT 5 COMMENT '0-10',
  promedio          DECIMAL(4,2) NOT NULL DEFAULT 5.00,
  comentarios       VARCHAR(500) DEFAULT NULL,
  fecha_evaluacion  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (idEvaluacion),
  CONSTRAINT fk_eval_empleado  FOREIGN KEY (idEmpleado)  REFERENCES empleado(idEmpleado)  ON DELETE CASCADE,
  CONSTRAINT fk_eval_evaluador FOREIGN KEY (idEvaluador) REFERENCES usuario(idUsuario)    ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
  COMMENT='Módulo RRHH — Evaluación de desempeño del personal';

-- ─── GAP 3: Ventas — Boleta vs Factura ───────────────────────────────────────
-- La tabla Venta ya tiene tipoComprobante y rucCliente. No se requiere ALTER.

-- ─── GAP 4: Compras — Comparación de precios entre proveedores ───────────────
CREATE TABLE IF NOT EXISTS cotizacion_proveedor (
  idCotizacion     INT          NOT NULL AUTO_INCREMENT,
  idProducto       INT          NOT NULL,
  idProveedor      INT          NOT NULL,
  precioUnitario   DECIMAL(10,2) NOT NULL,
  fechaCotizacion  DATE         NOT NULL DEFAULT (CURRENT_DATE),
  vigente          TINYINT(1)   NOT NULL DEFAULT 1,
  PRIMARY KEY (idCotizacion),
  CONSTRAINT fk_cotprov_producto  FOREIGN KEY (idProducto)  REFERENCES producto(idProducto),
  CONSTRAINT fk_cotprov_proveedor FOREIGN KEY (idProveedor) REFERENCES proveedor(idProveedor)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
  COMMENT='GAP 4 — Cotizaciones de proveedores por producto para comparación de precios';

-- ─── GAP 6: Finanzas — Costo promedio en producto ────────────────────────────
-- Necesario para calcular Costo de Ventas en el Estado de Resultados.
-- ALTER TABLE producto
--  ADD COLUMN costoPromedio DECIMAL(10,2) NOT NULL DEFAULT 0.00
--      COMMENT 'Costo promedio ponderado (actualizado en cada compra)' AFTER precio;

-- ─── PROVEEDORES DE PRUEBA (para demo del GAP 4) ─────────────────────────────
-- Agrega 2 proveedores adicionales para que la comparación tenga sentido.
INSERT IGNORE INTO proveedor (razonSocial, ruc, telefono, direccion, correo)
VALUES
  ('Alicorp S.A.A.',         '20100055237', '01-315-0800', 'Av. Argentina 4793, Lima',      'proveedores@alicorp.com.pe'),
  ('Gloria S.A.',            '20100190797', '01-435-5335', 'Av. República de Panamá 4295',  'ventas@gloria.com.pe');

-- ─── VERIFICACIÓN FINAL ──────────────────────────────────────────────────────
SELECT 'evaluacion_desempeno' AS tabla, COUNT(*) AS columnas
  FROM INFORMATION_SCHEMA.COLUMNS
 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'evaluacion_desempeno'
UNION ALL
SELECT 'cotizacion_proveedor', COUNT(*)
  FROM INFORMATION_SCHEMA.COLUMNS
 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'cotizacion_proveedor';
