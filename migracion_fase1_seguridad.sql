-- ============================================================
-- FASE 1 — Migración de seguridad para ERP MiniMarket LAREDO
-- Ejecutar UNA SOLA VEZ en tu instancia de MySQL local
-- ============================================================

-- 1) Agregar columnas de bloqueo por intentos fallidos (Tarea 1.3)
ALTER TABLE usuario
    ADD COLUMN intentos_fallidos INT NOT NULL DEFAULT 0
        COMMENT 'Contador de contraseñas incorrectas consecutivas',
    ADD COLUMN bloqueo_hasta DATETIME NULL DEFAULT NULL
        COMMENT 'Cuenta bloqueada hasta esta fecha/hora (NULL = no bloqueada)';

-- 2) Hashear contraseñas existentes con jBCrypt factor 10
--    Las siguientes cadenas son el hash bcrypt de "1234"
--    Si tus usuarios tienen otra contraseña, genera su hash con:
--      BCrypt.hashpw("tu_password", BCrypt.gensalt(10))
--    y reemplaza el valor abajo.
UPDATE usuario SET contrasena = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lHHy'
WHERE contrasena = '1234';

-- NOTA: El hash '$2a$10$N9qo8uL...' corresponde al texto plano "1234"
--       Puedes verificarlo en: https://bcrypt.online/
-- ============================================================
