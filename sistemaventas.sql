-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: sistemaventas
-- ------------------------------------------------------
-- Server version	9.7.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ 'acb4ab8c-733f-11f1-be3d-30138bbf1d47:1-138,
b1a8b68d-6f6f-11f1-996e-18c04da1c788:1-67';

--
-- Table structure for table `bitacora`
--

DROP TABLE IF EXISTS `bitacora`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bitacora` (
  `idBitacora` int NOT NULL AUTO_INCREMENT,
  `idUsuario` int NOT NULL,
  `modulo` varchar(50) NOT NULL,
  `accion` varchar(100) NOT NULL,
  `detalles` text,
  `ipEquipo` varchar(45) DEFAULT NULL,
  `fecha` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `detalle` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`idBitacora`),
  KEY `fk_bitacora_usuario` (`idUsuario`),
  CONSTRAINT `fk_bitacora_usuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`)
) ENGINE=InnoDB AUTO_INCREMENT=117 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bitacora`
--

LOCK TABLES `bitacora` WRITE;
/*!40000 ALTER TABLE `bitacora` DISABLE KEYS */;
INSERT INTO `bitacora` VALUES (1,1,'LOGIN','LOGIN',NULL,NULL,'2026-06-28 19:46:14','Usuario ADMIN inició sesión - Rol: Administrador'),(2,1,'LOGIN','LOGIN',NULL,NULL,'2026-06-28 19:57:25','Usuario ADMIN inició sesión - Rol: Administrador'),(3,1,'LOGIN','LOGIN',NULL,NULL,'2026-06-28 20:04:28','Usuario ADMIN inició sesión - Rol: Administrador'),(4,1,'LOGIN','LOGIN',NULL,NULL,'2026-06-28 20:13:46','Usuario ADMIN inició sesión - Rol: Administrador'),(5,1,'LOGIN','LOGIN',NULL,NULL,'2026-06-28 20:15:59','Usuario ADMIN inició sesión - Rol: Administrador'),(6,1,'LOGIN','LOGIN',NULL,NULL,'2026-06-29 00:22:39','Usuario ADMIN inició sesión - Rol: Administrador'),(7,1,'LOGIN','LOGIN',NULL,NULL,'2026-06-29 00:35:49','Usuario ADMIN inició sesión - Rol: Administrador'),(8,1,'LOGIN','LOGIN',NULL,NULL,'2026-06-29 00:48:28','Usuario ADMIN inició sesión - Rol: Administrador'),(9,1,'LOGIN','LOGIN',NULL,NULL,'2026-06-29 12:47:24','Usuario ADMIN inició sesión - Rol: Administrador'),(10,1,'LOGIN','LOGIN',NULL,NULL,'2026-06-29 17:11:01','Usuario ADMIN inició sesión - Rol: Gerente'),(11,1,'LOGIN','LOGIN',NULL,NULL,'2026-06-29 17:13:35','Usuario ADMIN inició sesión - Rol: Gerente'),(12,1,'LOGIN','LOGIN',NULL,NULL,'2026-06-29 17:30:18','Usuario ADMIN inició sesión - Rol: Gerente'),(13,2,'LOGIN','LOGIN',NULL,NULL,'2026-06-29 17:32:35','Usuario jramirez inició sesión - Rol: Vendedor'),(14,1,'LOGIN','LOGIN',NULL,NULL,'2026-06-29 17:33:14','Usuario ADMIN inició sesión - Rol: Gerente'),(15,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-01 22:44:43','Usuario ADMIN inició sesión - Rol: Gerente'),(16,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-01 23:18:44','Usuario ADMIN inició sesión - Rol: Gerente'),(17,2,'LOGIN','LOGIN',NULL,NULL,'2026-07-02 00:27:04','Usuario jramirez inició sesión - Rol: Vendedor'),(18,1,'VENTAS','REGISTRAR',NULL,NULL,'2026-07-02 00:29:03','Venta #2 registrada por S/ 11,80 - Método: Tarjeta de Crédito'),(19,1,'VENTAS','ANULAR',NULL,NULL,'2026-07-02 00:29:31','Venta #1 anulada'),(20,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-02 00:42:28','Usuario ADMIN inició sesión - Rol: Gerente'),(21,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-11 23:23:52','Usuario ADMIN inició sesión - Rol: Gerente'),(22,1,'VENTAS','REGISTRAR',NULL,NULL,'2026-07-11 23:27:07','Venta #3 registrada por S/ 11,80 - Método: Tarjeta de Crédito'),(23,1,'VENTAS','REGISTRAR',NULL,NULL,'2026-07-11 23:34:41','Venta #4 registrada por S/ 132,75 - Método: Efectivo'),(24,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-11 23:49:44','Usuario ADMIN inició sesión - Rol: Gerente'),(25,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-12 00:08:59','Usuario ADMIN inició sesión - Rol: Gerente'),(26,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-12 00:15:37','Usuario ADMIN inició sesión - Rol: Gerente'),(27,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-12 00:28:53','Usuario ADMIN inició sesión - Rol: Gerente'),(28,1,'COMPRAS','REGISTRAR',NULL,NULL,'2026-07-12 00:33:50','Compra #2 doc: F004 por S/ 118,00'),(29,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-12 00:43:20','Usuario ADMIN inició sesión - Rol: Gerente'),(30,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-12 00:48:49','Usuario ADMIN inició sesión - Rol: Gerente'),(31,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-12 01:00:16','Usuario ADMIN inició sesión - Rol: Gerente'),(32,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-12 01:26:09','Usuario ADMIN inició sesión - Rol: Gerente'),(33,1,'COMPRAS','REGISTRAR',NULL,NULL,'2026-07-12 01:29:26','Compra #3 doc: F003 por S/ 29,50'),(34,1,'COMPRAS','REGISTRAR',NULL,NULL,'2026-07-12 01:34:01','Compra #4 doc: F006 por S/ 106,20'),(35,1,'VENTAS','REGISTRAR',NULL,NULL,'2026-07-12 01:52:46','Venta #5 registrada por S/ 10,00 - Método: Efectivo'),(36,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-12 01:59:25','Usuario ADMIN inició sesión - Rol: Gerente'),(37,1,'COMPRAS','REGISTRAR',NULL,NULL,'2026-07-12 02:02:03','Compra #5 doc: F007 por S/ 70,80'),(38,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-12 12:16:37','Usuario ADMIN inició sesión - Rol: Gerente'),(39,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-12 12:19:44','Usuario ADMIN inició sesión - Rol: Gerente'),(40,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-12 12:33:40','Usuario ADMIN inició sesión - Rol: Gerente'),(41,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-12 12:41:46','Usuario ADMIN inició sesión - Rol: Gerente'),(42,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-12 12:49:42','Usuario ADMIN inició sesión - Rol: Gerente'),(43,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-12 13:01:50','Usuario ADMIN inició sesión - Rol: Gerente'),(44,1,'COMPRAS','REGISTRAR',NULL,NULL,'2026-07-12 13:06:46','Compra #6 doc: F009 por S/ 32,45'),(45,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-12 13:14:10','Usuario ADMIN inició sesión - Rol: Gerente'),(46,1,'COMPRAS','REGISTRAR',NULL,NULL,'2026-07-12 13:16:02','Compra #7 doc: F005 por S/ 198,24'),(47,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-12 13:26:03','Usuario ADMIN inició sesión - Rol: Gerente'),(48,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-12 13:29:49','Usuario ADMIN inició sesión - Rol: Gerente'),(49,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-12 13:34:09','Usuario ADMIN inició sesión - Rol: Gerente'),(50,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-12 13:39:08','Usuario ADMIN inició sesión - Rol: Gerente'),(51,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-12 13:44:24','Usuario ADMIN inició sesión - Rol: Gerente'),(52,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-12 13:57:07','Usuario ADMIN inició sesión - Rol: Gerente'),(53,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-12 14:33:39','Usuario ADMIN inició sesión - Rol: Gerente'),(54,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-12 14:34:56','Usuario ADMIN inició sesión - Rol: Gerente'),(55,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-12 15:32:17','Usuario ADMIN inició sesión - Rol: Gerente'),(56,1,'VENTAS','REGISTRAR',NULL,NULL,'2026-07-12 16:08:23','Venta #6 registrada por S/ 35,50 - Método: Tarjeta de Débito'),(57,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-12 16:15:27','Usuario ADMIN inició sesión - Rol: Gerente'),(58,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-12 23:04:05','Usuario ADMIN inició sesión - Rol: Gerente'),(59,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-12 23:14:48','Usuario ADMIN inició sesión - Rol: Gerente'),(60,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-12 23:23:48','Usuario ADMIN inició sesión - Rol: Gerente'),(61,1,'VENTAS','REGISTRAR',NULL,NULL,'2026-07-12 23:25:44','Venta #7 registrada por S/ 30,00 - Método: Efectivo'),(62,1,'VENTAS','CANCELAR',NULL,NULL,'2026-07-12 23:27:24','Venta #7 cancelada - Cambio de Opinión'),(63,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-12 23:39:51','Usuario ADMIN inició sesión - Rol: Gerente'),(64,1,'VENTAS','REGISTRAR',NULL,NULL,'2026-07-12 23:43:00','Venta #8 registrada por S/ 90,00 - Método: Efectivo'),(65,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-12 23:50:15','Usuario ADMIN inició sesión - Rol: Gerente'),(66,1,'LOGIN','LOGIN',NULL,NULL,'2026-07-12 23:57:17','Usuario ADMIN inició sesión - Rol: Gerente'),(67,3,'SISTEMA','LOGIN',NULL,NULL,'2026-07-13 00:23:42','Usuario CAJERO1 inició sesión - Rol: Vendedor'),(68,1,'SISTEMA','LOGIN',NULL,NULL,'2026-07-13 00:27:53','Usuario ADMIN inició sesión - Rol: Gerente'),(69,3,'SISTEMA','LOGIN',NULL,NULL,'2026-07-13 00:31:32','Usuario CAJERO1 inició sesión - Rol: Vendedor'),(70,1,'SISTEMA','LOGIN',NULL,NULL,'2026-07-13 00:32:09','Usuario ADMIN inició sesión - Rol: Gerente'),(71,4,'SISTEMA','LOGIN',NULL,NULL,'2026-07-13 00:33:42','Usuario CAJERO2 inició sesión - Rol: Almacenero'),(72,3,'SISTEMA','LOGIN',NULL,NULL,'2026-07-13 00:33:57','Usuario CAJERO1 inició sesión - Rol: Vendedor'),(73,3,'SISTEMA','LOGIN',NULL,NULL,'2026-07-13 00:34:38','Usuario CAJERO1 inició sesión - Rol: Vendedor'),(74,1,'SISTEMA','LOGIN',NULL,NULL,'2026-07-13 00:35:55','Usuario ADMIN inició sesión - Rol: Gerente'),(75,1,'SISTEMA','LOGIN',NULL,NULL,'2026-07-13 22:35:21','Usuario ADMIN inició sesión - Rol: Gerente'),(76,1,'SISTEMA','LOGIN',NULL,NULL,'2026-07-13 22:42:34','Usuario ADMIN inició sesión - Rol: Gerente'),(77,1,'SISTEMA','LOGIN',NULL,NULL,'2026-07-13 22:52:11','Usuario ADMIN inició sesión - Rol: Gerente'),(78,1,'SISTEMA','LOGIN',NULL,NULL,'2026-07-13 22:57:18','Usuario ADMIN inició sesión - Rol: Gerente'),(79,1,'SISTEMA','LOGIN',NULL,NULL,'2026-07-13 23:16:58','Usuario ADMIN inició sesión - Rol: Gerente'),(80,3,'SISTEMA','LOGIN',NULL,NULL,'2026-07-13 23:32:28','Usuario CAJERO1 inició sesión - Rol: Vendedor'),(81,1,'SISTEMA','LOGIN',NULL,NULL,'2026-07-13 23:33:00','Usuario ADMIN inició sesión - Rol: Gerente'),(82,2,'SISTEMA','LOGIN',NULL,NULL,'2026-07-13 23:34:00','Usuario GERENTE inició sesión - Rol: Vendedor'),(83,1,'SISTEMA','LOGIN',NULL,NULL,'2026-07-13 23:34:58','Usuario ADMIN inició sesión - Rol: Gerente'),(84,1,'SISTEMA','LOGIN',NULL,NULL,'2026-07-14 00:14:02','Usuario ADMIN inició sesión - Rol: Gerente'),(85,1,'VENTAS','REGISTRAR',NULL,NULL,'2026-07-14 00:17:28','Venta #9 registrada por S/ 12,50 - Método: Efectivo'),(86,1,'COMPRAS','REGISTRAR',NULL,NULL,'2026-07-14 00:20:08','Compra #8 doc: F011 por S/ 56,64'),(87,1,'SISTEMA','LOGIN',NULL,NULL,'2026-07-14 00:40:38','Usuario ADMIN inició sesión - Rol: Gerente'),(88,1,'VENTAS','REGISTRAR',NULL,NULL,'2026-07-14 00:41:24','Venta #10 registrada por S/ 10,00 - Método: Tarjeta de Débito'),(89,1,'VENTAS','REGISTRAR',NULL,NULL,'2026-07-14 00:42:38','Venta #11 registrada por S/ 55,00 - Método: Efectivo'),(90,1,'COMPRAS','REGISTRAR',NULL,NULL,'2026-07-14 00:54:54','Compra #9 doc: F012 por S/ 212,40'),(91,1,'COMPRAS','REGISTRAR',NULL,NULL,'2026-07-14 00:58:11','Compra #10 doc: F014 por S/ 8,85'),(92,1,'VENTAS','REGISTRAR',NULL,NULL,'2026-07-14 01:00:25','Venta #12 registrada por S/ 210,00 - Método: Efectivo'),(93,1,'SISTEMA','LOGIN',NULL,NULL,'2026-07-14 12:04:20','Usuario ADMIN inició sesión - Rol: Gerente'),(94,1,'VENTAS','REGISTRAR',NULL,NULL,'2026-07-14 12:10:33','Venta #13 registrada por S/ 800.00 - Método: Efectivo'),(95,1,'VENTAS','REGISTRAR',NULL,NULL,'2026-07-14 12:13:56','Venta #14 registrada por S/ 20.00 - Método: Efectivo'),(96,1,'VENTAS','REGISTRAR',NULL,NULL,'2026-07-14 12:16:41','Venta #15 registrada por S/ 25.00 - Método: Efectivo'),(97,3,'SISTEMA','LOGIN',NULL,NULL,'2026-07-14 12:45:47','Usuario CAJERO1 inició sesión - Rol: Vendedor'),(98,1,'VENTAS','REGISTRAR',NULL,NULL,'2026-07-14 12:47:41','Venta #16 registrada por S/ 160.00 - Método: Efectivo'),(99,1,'SISTEMA','LOGIN',NULL,NULL,'2026-07-14 12:51:31','Usuario ADMIN inició sesión - Rol: Gerente'),(100,1,'SISTEMA','LOGIN',NULL,NULL,'2026-07-14 14:16:02','Usuario ADMIN inició sesión - Rol: Gerente'),(101,4,'SISTEMA','LOGIN',NULL,NULL,'2026-07-14 14:39:20','Usuario CAJERO2 inició sesión - Rol: Almacenero'),(102,3,'SISTEMA','LOGIN',NULL,NULL,'2026-07-14 14:39:42','Usuario CAJERO1 inició sesión - Rol: Vendedor'),(103,1,'SISTEMA','LOGIN',NULL,NULL,'2026-07-14 14:40:36','Usuario ADMIN inició sesión - Rol: Gerente'),(104,1,'SISTEMA','LOGIN',NULL,NULL,'2026-07-14 15:04:56','Usuario ADMIN inició sesión - Rol: Gerente'),(105,1,'SISTEMA','LOGIN',NULL,NULL,'2026-07-14 15:38:40','Usuario ADMIN inició sesión - Rol: Gerente'),(106,1,'SISTEMA','LOGIN',NULL,NULL,'2026-07-14 15:51:16','Usuario ADMIN inició sesión - Rol: Gerente'),(107,1,'SISTEMA','LOGIN',NULL,NULL,'2026-07-14 16:00:27','Usuario ADMIN inició sesión - Rol: Gerente'),(108,1,'SISTEMA','LOGIN',NULL,NULL,'2026-07-14 16:08:24','Usuario ADMIN inició sesión - Rol: Gerente'),(109,1,'SISTEMA','LOGIN',NULL,NULL,'2026-07-14 16:09:01','Usuario ADMIN inició sesión - Rol: Gerente'),(110,1,'SISTEMA','LOGIN',NULL,NULL,'2026-07-14 16:15:36','Usuario ADMIN inició sesión - Rol: Gerente'),(111,1,'SISTEMA','LOGIN',NULL,NULL,'2026-07-14 16:24:44','Usuario ADMIN inició sesión - Rol: Gerente'),(112,1,'SISTEMA','LOGIN',NULL,NULL,'2026-07-14 16:49:20','Usuario ADMIN inició sesión - Rol: Gerente'),(113,1,'VENTAS','REGISTRAR',NULL,NULL,'2026-07-14 16:54:02','Venta #17 registrada por S/ 8.00 - Método: Yape'),(114,1,'VENTAS','REGISTRAR',NULL,NULL,'2026-07-14 17:06:25','Venta #18 registrada por S/ 5.00 - Método: Plin'),(115,1,'SISTEMA','LOGIN',NULL,NULL,'2026-07-14 17:09:38','Usuario ADMIN inició sesión - Rol: Gerente'),(116,1,'VENTAS','REGISTRAR',NULL,NULL,'2026-07-14 17:18:06','Venta #19 registrada por S/ 8.00 - Método: Tarjeta');
/*!40000 ALTER TABLE `bitacora` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cajachica`
--

DROP TABLE IF EXISTS `cajachica`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cajachica` (
  `idCaja` int NOT NULL AUTO_INCREMENT,
  `idEmpleado` int NOT NULL,
  `montoApertura` decimal(10,2) NOT NULL,
  `montoCierre` decimal(10,2) DEFAULT NULL,
  `totalIngresos` decimal(10,2) DEFAULT '0.00',
  `totalEgresos` decimal(10,2) DEFAULT '0.00',
  `montoEsperado` decimal(10,2) DEFAULT '0.00',
  `diferencia` decimal(10,2) DEFAULT '0.00',
  `fechaApertura` datetime DEFAULT CURRENT_TIMESTAMP,
  `fechaCierre` datetime DEFAULT NULL,
  `estado` enum('Abierta','Cerrada') DEFAULT 'Abierta',
  `observaciones` text,
  `retiros` decimal(10,2) DEFAULT '0.00',
  PRIMARY KEY (`idCaja`),
  KEY `idEmpleado` (`idEmpleado`),
  CONSTRAINT `cajachica_ibfk_1` FOREIGN KEY (`idEmpleado`) REFERENCES `empleado` (`idEmpleado`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cajachica`
--

LOCK TABLES `cajachica` WRITE;
/*!40000 ALTER TABLE `cajachica` DISABLE KEYS */;
INSERT INTO `cajachica` VALUES (1,3,200.00,160.00,160.00,16.00,344.00,-184.00,'2026-07-14 12:46:01','2026-07-14 12:50:58','Cerrada','devolucion 16 soles',160.00),(2,3,200.00,NULL,0.00,0.00,0.00,0.00,'2026-07-14 14:39:48',NULL,'Abierta',NULL,0.00);
/*!40000 ALTER TABLE `cajachica` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cargo`
--

DROP TABLE IF EXISTS `cargo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cargo` (
  `idCargo` int NOT NULL AUTO_INCREMENT,
  `nombreCargo` varchar(50) NOT NULL,
  `descripcion` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`idCargo`),
  UNIQUE KEY `nombreCargo` (`nombreCargo`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cargo`
--

LOCK TABLES `cargo` WRITE;
/*!40000 ALTER TABLE `cargo` DISABLE KEYS */;
INSERT INTO `cargo` VALUES (1,'Gerente','Cargo principal del sistema'),(2,'Vendedor','Atención al cliente y ventas'),(3,'Almacenero','Control de inventario y stock'),(4,'Contador','Gestión financiera y contable');
/*!40000 ALTER TABLE `cargo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categoria`
--

DROP TABLE IF EXISTS `categoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categoria` (
  `idCategoria` int NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(100) NOT NULL,
  `estado` tinyint NOT NULL DEFAULT '1',
  PRIMARY KEY (`idCategoria`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categoria`
--

LOCK TABLES `categoria` WRITE;
/*!40000 ALTER TABLE `categoria` DISABLE KEYS */;
INSERT INTO `categoria` VALUES (1,'Bebidas',1),(2,'Lácteos',1),(3,'Snacks',1),(4,'Limpieza',1),(5,'Panadería',1),(6,'Abarrotes',1),(7,'Congelados',1),(8,'Higiene Personal',1),(9,'Bebidas Alcohólicas',1);
/*!40000 ALTER TABLE `categoria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cliente`
--

DROP TABLE IF EXISTS `cliente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cliente` (
  `idCliente` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `apellido` varchar(50) NOT NULL,
  `dni_ruc` varchar(11) NOT NULL,
  `telefono` varchar(15) NOT NULL,
  `correo` varchar(100) DEFAULT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`idCliente`),
  UNIQUE KEY `dni_ruc` (`dni_ruc`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cliente`
--

LOCK TABLES `cliente` WRITE;
/*!40000 ALTER TABLE `cliente` DISABLE KEYS */;
INSERT INTO `cliente` VALUES (1,'Lucas','Sanchezs','8103971','987123654','',1),(2,'JOSE','LUCANO','3691472','987321654','',1),(3,'José David','Venegas Aguirre','60877738','959649719','davidaguirre0311@gmail.com',1),(4,'Cristian','Alfaro','60779626','922891674','',1),(5,'Yordi','Infantes','75656964','923827134','',1);
/*!40000 ALTER TABLE `cliente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `compra`
--

DROP TABLE IF EXISTS `compra`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `compra` (
  `idCompra` int NOT NULL AUTO_INCREMENT,
  `idProveedor` int NOT NULL,
  `idUsuario` int NOT NULL,
  `nroDocumento` varchar(20) NOT NULL,
  `fecha` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `subtotal` decimal(10,2) NOT NULL DEFAULT '0.00',
  `igv` decimal(10,2) NOT NULL DEFAULT '0.00',
  `total` decimal(10,2) NOT NULL DEFAULT '0.00',
  `estado` tinyint(1) NOT NULL DEFAULT '1',
  `condicionPago` varchar(50) NOT NULL DEFAULT 'Contado',
  `estadoPago` varchar(20) NOT NULL DEFAULT 'Pagado',
  PRIMARY KEY (`idCompra`),
  KEY `fk_compra_proveedor` (`idProveedor`),
  KEY `fk_compra_usuario` (`idUsuario`),
  CONSTRAINT `fk_compra_proveedor` FOREIGN KEY (`idProveedor`) REFERENCES `proveedor` (`idProveedor`),
  CONSTRAINT `fk_compra_usuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `compra`
--

LOCK TABLES `compra` WRITE;
/*!40000 ALTER TABLE `compra` DISABLE KEYS */;
INSERT INTO `compra` VALUES (1,1,1,'F001','2026-06-27 17:59:29',12.50,2.25,14.75,1,'Contado','Pagado'),(2,1,1,'F004','2026-07-12 00:33:50',100.00,18.00,118.00,1,'Contado','Pagado'),(4,1,1,'F006','2026-07-12 01:34:01',90.00,16.20,106.20,1,'Contado','Pagado'),(5,1,1,'F007','2026-07-12 02:02:03',60.00,10.80,70.80,1,'Contado','Pagado'),(6,1,1,'F009','2026-07-12 13:06:46',27.50,4.95,32.45,1,'Contado','Pagado'),(7,1,1,'F005','2026-07-12 13:16:02',168.00,30.24,198.24,1,'Crédito 30 días','Pagado'),(8,1,1,'F011','2026-07-14 00:20:08',48.00,8.64,56.64,1,'Contado','Pagado'),(9,1,1,'F012','2026-07-14 00:54:54',180.00,32.40,212.40,1,'Contado','Pagado'),(10,1,1,'F014','2026-07-14 00:58:10',7.50,1.35,8.85,1,'Crédito 30 días','Pagado');
/*!40000 ALTER TABLE `compra` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `configuracion`
--

DROP TABLE IF EXISTS `configuracion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `configuracion` (
  `idConfig` int NOT NULL AUTO_INCREMENT,
  `razonSocial` varchar(150) NOT NULL,
  `ruc` varchar(11) NOT NULL,
  `direccion` varchar(255) NOT NULL,
  `telefono` varchar(15) DEFAULT NULL,
  `correo` varchar(100) DEFAULT NULL,
  `igvPorcentaje` decimal(5,2) NOT NULL DEFAULT '18.00',
  `mpToken` varchar(255) DEFAULT NULL,
  `mpPublicKey` varchar(255) DEFAULT NULL,
  `mpClientId` varchar(100) DEFAULT NULL,
  `fechaActualizacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`idConfig`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `configuracion`
--

LOCK TABLES `configuracion` WRITE;
/*!40000 ALTER TABLE `configuracion` DISABLE KEYS */;
INSERT INTO `configuracion` VALUES (1,'Minimarket LAREDO','20123456789','Av. Principal 123, Laredo','963147852','MLaredo@gmail.com',18.00,'','','','2026-06-28 20:06:52');
/*!40000 ALTER TABLE `configuracion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cuentascobrarpagar`
--

DROP TABLE IF EXISTS `cuentascobrarpagar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuentascobrarpagar` (
  `idCuenta` int NOT NULL AUTO_INCREMENT,
  `tipo` enum('COBRAR','PAGAR') NOT NULL,
  `idCliente` int DEFAULT NULL,
  `idProveedor` int DEFAULT NULL,
  `nroDocumento` varchar(20) NOT NULL,
  `montoTotal` decimal(10,2) NOT NULL,
  `saldoPendiente` decimal(10,2) NOT NULL,
  `fechaEmision` date NOT NULL,
  `fechaVencimiento` date NOT NULL,
  `estado` enum('Pendiente','Parcial','Pagado','Vencido') NOT NULL DEFAULT 'Pendiente',
  `condicionPago` varchar(50) DEFAULT NULL,
  `idCompra` int DEFAULT NULL,
  PRIMARY KEY (`idCuenta`),
  KEY `fk_ccp_cliente` (`idCliente`),
  KEY `fk_ccp_proveedor` (`idProveedor`),
  CONSTRAINT `fk_ccp_cliente` FOREIGN KEY (`idCliente`) REFERENCES `cliente` (`idCliente`),
  CONSTRAINT `fk_ccp_proveedor` FOREIGN KEY (`idProveedor`) REFERENCES `proveedor` (`idProveedor`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cuentascobrarpagar`
--

LOCK TABLES `cuentascobrarpagar` WRITE;
/*!40000 ALTER TABLE `cuentascobrarpagar` DISABLE KEYS */;
INSERT INTO `cuentascobrarpagar` VALUES (1,'PAGAR',NULL,1,'F005',198.24,0.00,'2026-07-12','2026-08-11','Pagado','Crédito 30 días',7),(2,'PAGAR',NULL,1,'F014',8.85,0.00,'2026-07-14','2026-08-13','Pagado','Crédito 30 días',10);
/*!40000 ALTER TABLE `cuentascobrarpagar` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detallecompra`
--

DROP TABLE IF EXISTS `detallecompra`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detallecompra` (
  `idDetalleCompra` int NOT NULL AUTO_INCREMENT,
  `idCompra` int NOT NULL,
  `idProducto` int NOT NULL,
  `cantidad` int NOT NULL,
  `precioUnitario` decimal(10,2) NOT NULL,
  `lote` varchar(50) DEFAULT NULL,
  `fechaVencimiento` date DEFAULT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  PRIMARY KEY (`idDetalleCompra`),
  KEY `fk_dc_compra` (`idCompra`),
  KEY `fk_dc_producto` (`idProducto`),
  CONSTRAINT `fk_dc_compra` FOREIGN KEY (`idCompra`) REFERENCES `compra` (`idCompra`),
  CONSTRAINT `fk_dc_producto` FOREIGN KEY (`idProducto`) REFERENCES `producto` (`idProducto`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detallecompra`
--

LOCK TABLES `detallecompra` WRITE;
/*!40000 ALTER TABLE `detallecompra` DISABLE KEYS */;
INSERT INTO `detallecompra` VALUES (1,1,1,5,2.50,'L-2026-001','2027-01-31',12.50),(2,2,2,50,2.00,'Lt-001','2027-01-05',100.00),(4,4,4,20,4.50,'Lt-006','2027-02-15',90.00),(5,5,1,30,2.00,'Lt-008','2027-01-14',60.00),(6,6,1,5,2.50,'Lt-007','2027-01-05',12.50),(7,6,2,6,2.50,'Lt-007','2027-01-05',15.00),(8,7,5,35,4.80,'Lt-010','2027-01-10',168.00),(9,8,5,10,4.80,'Lt-011','2026-11-09',48.00),(10,9,4,40,4.50,'Lt-012','2027-01-21',180.00),(11,10,3,15,0.50,'Lt-013','2027-01-18',7.50);
/*!40000 ALTER TABLE `detallecompra` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalleventa`
--

DROP TABLE IF EXISTS `detalleventa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalleventa` (
  `idDetalleVenta` int NOT NULL AUTO_INCREMENT,
  `idVenta` int NOT NULL,
  `idProducto` int NOT NULL,
  `cantidad` int NOT NULL,
  `precioUnitario` decimal(10,2) NOT NULL,
  `descuento` decimal(10,2) NOT NULL DEFAULT '0.00',
  `subtotal` decimal(10,2) NOT NULL,
  PRIMARY KEY (`idDetalleVenta`),
  KEY `fk_dv_venta` (`idVenta`),
  KEY `fk_dv_producto` (`idProducto`),
  CONSTRAINT `fk_dv_producto` FOREIGN KEY (`idProducto`) REFERENCES `producto` (`idProducto`),
  CONSTRAINT `fk_dv_venta` FOREIGN KEY (`idVenta`) REFERENCES `venta` (`idVenta`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalleventa`
--

LOCK TABLES `detalleventa` WRITE;
/*!40000 ALTER TABLE `detalleventa` DISABLE KEYS */;
INSERT INTO `detalleventa` VALUES (1,1,1,2,2.50,0.00,5.00),(2,2,1,4,2.50,0.00,10.00),(3,3,1,4,2.50,0.00,10.00),(4,4,1,45,2.50,0.00,112.50),(5,5,2,4,2.50,0.00,10.00),(6,6,5,1,5.50,0.00,5.50),(7,6,4,5,6.00,0.00,30.00),(8,7,2,1,2.50,0.00,2.50),(9,7,5,5,5.50,0.00,27.50),(10,8,4,15,6.00,0.00,90.00),(11,9,1,5,2.50,0.00,12.50),(12,10,1,4,2.50,0.00,10.00),(13,11,5,10,5.50,0.00,55.00),(14,12,4,35,6.00,0.00,210.00),(15,13,6,100,8.00,0.00,800.00),(16,14,3,20,1.00,0.00,20.00),(17,15,3,25,1.00,0.00,25.00),(18,16,6,20,8.00,0.00,160.00),(19,17,6,1,8.00,0.00,8.00),(20,18,3,5,1.00,0.00,5.00),(21,19,6,1,8.00,0.00,8.00);
/*!40000 ALTER TABLE `detalleventa` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `devolucion`
--

DROP TABLE IF EXISTS `devolucion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `devolucion` (
  `idDevolucion` int NOT NULL AUTO_INCREMENT,
  `idVenta` int NOT NULL,
  `idProducto` int NOT NULL,
  `idUsuario` int NOT NULL,
  `cantidad` int NOT NULL,
  `motivo` varchar(100) NOT NULL,
  `tipoReembolso` enum('Efectivo','Nota de Crédito','Cambio de Producto') NOT NULL DEFAULT 'Efectivo',
  `montoReembolso` decimal(10,2) NOT NULL DEFAULT '0.00',
  `fecha` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `estado` tinyint(1) NOT NULL DEFAULT '1',
  `tipoDev` enum('Parcial','Cancelacion') NOT NULL DEFAULT 'Parcial',
  PRIMARY KEY (`idDevolucion`),
  KEY `fk_dev_venta` (`idVenta`),
  KEY `fk_dev_producto` (`idProducto`),
  KEY `fk_dev_usuario` (`idUsuario`),
  CONSTRAINT `fk_dev_producto` FOREIGN KEY (`idProducto`) REFERENCES `producto` (`idProducto`),
  CONSTRAINT `fk_dev_usuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`),
  CONSTRAINT `fk_dev_venta` FOREIGN KEY (`idVenta`) REFERENCES `venta` (`idVenta`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `devolucion`
--

LOCK TABLES `devolucion` WRITE;
/*!40000 ALTER TABLE `devolucion` DISABLE KEYS */;
INSERT INTO `devolucion` VALUES (1,2,1,1,4,'Cambio de Opinión','Efectivo',10.00,'2026-07-02 00:30:57',1,'Parcial'),(2,7,2,1,1,'Cambio de Opinión','Efectivo',2.50,'2026-07-12 23:27:24',1,'Cancelacion'),(3,7,5,1,5,'Cambio de Opinión','Efectivo',27.50,'2026-07-12 23:27:24',1,'Cancelacion'),(4,13,6,1,10,'Cambio de Opinión','Efectivo',80.00,'2026-07-14 12:12:27',1,'Parcial'),(5,14,3,1,10,'Cambio de Opinión','Efectivo',10.00,'2026-07-14 12:15:31',1,'Parcial'),(6,15,3,1,5,'Cambio de Opinión','Efectivo',5.00,'2026-07-14 12:17:29',1,'Parcial'),(7,16,6,1,2,'Cambio de Opinión','Efectivo',16.00,'2026-07-14 12:48:51',1,'Parcial'),(8,18,3,1,5,'El cliente se paso de neutro','Efectivo',5.00,'2026-07-14 17:14:17',1,'Parcial');
/*!40000 ALTER TABLE `devolucion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `empleado`
--

DROP TABLE IF EXISTS `empleado`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `empleado` (
  `idEmpleado` int NOT NULL AUTO_INCREMENT,
  `nombres` varchar(50) NOT NULL,
  `apellidos` varchar(50) NOT NULL,
  `dni` char(8) NOT NULL,
  `telefono` varchar(15) DEFAULT NULL,
  `direccion` varchar(150) DEFAULT NULL,
  `idCargo` int NOT NULL,
  `fechaIngreso` date NOT NULL,
  `estado` tinyint NOT NULL DEFAULT '1',
  PRIMARY KEY (`idEmpleado`),
  UNIQUE KEY `dni` (`dni`),
  KEY `FK_Empleado_Cargo` (`idCargo`),
  CONSTRAINT `FK_Empleado_Cargo` FOREIGN KEY (`idCargo`) REFERENCES `cargo` (`idCargo`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `empleado`
--

LOCK TABLES `empleado` WRITE;
/*!40000 ALTER TABLE `empleado` DISABLE KEYS */;
INSERT INTO `empleado` VALUES (1,'Caleb','Rordriguez','12345678','963258741','Av. Los Pinos 123, Laredo',1,'2026-06-27',1),(2,'Maria','Gonzalez Torres','45678902','987654322','Jr. Las Flores 456, Laredo',2,'2022-03-01',1),(3,'Juan','Ramirez Lopez','45678903','987654323','Calle Los Olivos 789, Laredo',2,'2022-06-15',1),(4,'Ana','Sanchez Rios','45678904','987654324','Av. Principal 321, Laredo',3,'2023-01-10',1),(5,'Carlos','Mendoza Silva','45678905','987654325','Jr. Union 654, Laredo',3,'2023-03-20',1),(6,'Rosa','Vargas Diaz','45678906','987654326','Av. Los Cedros 987, Laredo',4,'2023-06-01',1);
/*!40000 ALTER TABLE `empleado` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fidelizacion`
--

DROP TABLE IF EXISTS `fidelizacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fidelizacion` (
  `idFidelizacion` int NOT NULL AUTO_INCREMENT,
  `idCliente` int NOT NULL,
  `puntosAcum` int NOT NULL DEFAULT '0',
  `puntosCanjeados` int NOT NULL DEFAULT '0',
  `solesPorPunto` decimal(6,2) NOT NULL DEFAULT '10.00',
  `ultimaCompra` datetime DEFAULT NULL,
  `fechaRegistro` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idFidelizacion`),
  UNIQUE KEY `idCliente` (`idCliente`),
  CONSTRAINT `fk_fidel_cliente` FOREIGN KEY (`idCliente`) REFERENCES `cliente` (`idCliente`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fidelizacion`
--

LOCK TABLES `fidelizacion` WRITE;
/*!40000 ALTER TABLE `fidelizacion` DISABLE KEYS */;
/*!40000 ALTER TABLE `fidelizacion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fidelizacioncanje`
--

DROP TABLE IF EXISTS `fidelizacioncanje`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fidelizacioncanje` (
  `idCanje` int NOT NULL AUTO_INCREMENT,
  `idCliente` int NOT NULL,
  `premio` varchar(150) NOT NULL,
  `puntosUsados` int NOT NULL,
  `idUsuario` int NOT NULL,
  `fecha` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idCanje`),
  KEY `fk_canje_cliente` (`idCliente`),
  KEY `fk_canje_usuario` (`idUsuario`),
  CONSTRAINT `fk_canje_cliente` FOREIGN KEY (`idCliente`) REFERENCES `cliente` (`idCliente`),
  CONSTRAINT `fk_canje_usuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fidelizacioncanje`
--

LOCK TABLES `fidelizacioncanje` WRITE;
/*!40000 ALTER TABLE `fidelizacioncanje` DISABLE KEYS */;
/*!40000 ALTER TABLE `fidelizacioncanje` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flujocaja`
--

DROP TABLE IF EXISTS `flujocaja`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flujocaja` (
  `idFlujo` int NOT NULL AUTO_INCREMENT,
  `tipo` enum('INGRESO','EGRESO') NOT NULL,
  `concepto` varchar(255) NOT NULL,
  `monto` decimal(10,2) NOT NULL,
  `fecha` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `idUsuario` int NOT NULL,
  `referencia` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`idFlujo`),
  KEY `fk_flujo_usuario` (`idUsuario`),
  CONSTRAINT `fk_flujo_usuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flujocaja`
--

LOCK TABLES `flujocaja` WRITE;
/*!40000 ALTER TABLE `flujocaja` DISABLE KEYS */;
INSERT INTO `flujocaja` VALUES (1,'INGRESO','Venta #9 - Efectivo',12.50,'2026-07-14 00:17:28',1,'VENTA #9'),(2,'EGRESO','Compra #8 - F011',56.64,'2026-07-14 00:20:09',1,'COMPRA #8'),(3,'INGRESO','Venta #10 - Tarjeta de Débito',10.00,'2026-07-14 00:41:24',1,'VENTA #10'),(4,'INGRESO','Venta #11 - Efectivo',55.00,'2026-07-14 00:42:38',1,'VENTA #11'),(5,'EGRESO','Compra #9 - F012',212.40,'2026-07-14 00:54:54',1,'COMPRA #9'),(6,'EGRESO','Pago deuda - F014',8.85,'2026-07-14 00:58:45',1,'CXP #2'),(7,'INGRESO','Venta #12 - Efectivo',210.00,'2026-07-14 01:00:25',1,'VENTA #12'),(8,'INGRESO','Venta #13 - Efectivo',800.00,'2026-07-14 12:10:33',1,'VENTA #13'),(9,'EGRESO','Devolución parcial Venta #13',80.00,'2026-07-14 12:12:27',1,'DEV VENTA #13'),(10,'INGRESO','Venta #14 - Efectivo',20.00,'2026-07-14 12:13:56',1,'VENTA #14'),(11,'EGRESO','Devolución parcial Venta #14',10.00,'2026-07-14 12:15:31',1,'DEV VENTA #14'),(12,'INGRESO','Venta #15 - Efectivo',25.00,'2026-07-14 12:16:41',1,'VENTA #15'),(13,'EGRESO','Devolución parcial Venta #15',5.00,'2026-07-14 12:17:29',1,'DEV VENTA #15'),(14,'INGRESO','Venta #16 - Efectivo',160.00,'2026-07-14 12:47:41',1,'VENTA #16'),(15,'EGRESO','Devolución parcial Venta #16',16.00,'2026-07-14 12:48:51',1,'DEV VENTA #16'),(16,'EGRESO','Retiro de caja - Juan Ramirez Lopez',160.00,'2026-07-14 14:43:05',1,'RETIRO CAJA #1'),(17,'INGRESO','Venta #17 - Yape',8.00,'2026-07-14 16:54:03',1,'VENTA #17'),(18,'INGRESO','Venta #18 - Plin',5.00,'2026-07-14 17:06:25',1,'VENTA #18'),(19,'EGRESO','Devolución parcial Venta #18',5.00,'2026-07-14 17:14:17',1,'DEV VENTA #18'),(20,'INGRESO','Venta #19 - Tarjeta',8.00,'2026-07-14 17:18:06',1,'VENTA #19');
/*!40000 ALTER TABLE `flujocaja` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kardex`
--

DROP TABLE IF EXISTS `kardex`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kardex` (
  `idKardex` int NOT NULL AUTO_INCREMENT,
  `idProducto` int NOT NULL,
  `tipoMovimiento` enum('ENTRADA','SALIDA','AJUSTE') NOT NULL,
  `cantidad` int NOT NULL,
  `stockAnterior` int NOT NULL,
  `stockActual` int NOT NULL,
  `referencia` varchar(100) DEFAULT NULL,
  `fecha` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `idUsuario` int NOT NULL,
  PRIMARY KEY (`idKardex`),
  KEY `fk_kardex_producto` (`idProducto`),
  KEY `fk_kardex_usuario` (`idUsuario`),
  CONSTRAINT `fk_kardex_producto` FOREIGN KEY (`idProducto`) REFERENCES `producto` (`idProducto`),
  CONSTRAINT `fk_kardex_usuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kardex`
--

LOCK TABLES `kardex` WRITE;
/*!40000 ALTER TABLE `kardex` DISABLE KEYS */;
INSERT INTO `kardex` VALUES (1,1,'ENTRADA',5,50,55,'COMPRA #1','2026-06-27 17:59:29',1),(2,1,'SALIDA',2,55,53,'VENTA #1','2026-06-27 20:03:14',1),(3,1,'SALIDA',4,53,49,'VENTA #2','2026-07-02 00:29:03',1),(4,1,'SALIDA',4,53,49,'VENTA #3','2026-07-11 23:27:08',1),(5,1,'SALIDA',45,49,4,'VENTA #4','2026-07-11 23:34:41',1),(6,2,'ENTRADA',50,0,50,'COMPRA #2','2026-07-12 00:33:50',1),(7,3,'ENTRADA',25,0,25,'COMPRA #3','2026-07-12 01:29:26',1),(8,4,'ENTRADA',20,0,20,'COMPRA #4','2026-07-12 01:34:01',1),(9,2,'SALIDA',4,50,46,'VENTA #5','2026-07-12 01:52:46',1),(10,1,'ENTRADA',30,4,34,'COMPRA #5','2026-07-12 02:02:04',1),(11,1,'ENTRADA',5,34,39,'COMPRA #6','2026-07-12 13:06:46',1),(12,2,'ENTRADA',6,46,52,'COMPRA #6','2026-07-12 13:06:46',1),(13,5,'ENTRADA',35,0,35,'COMPRA #7','2026-07-12 13:16:03',1),(14,5,'SALIDA',1,35,34,'VENTA #6','2026-07-12 16:08:24',1),(15,4,'SALIDA',5,20,15,'VENTA #6','2026-07-12 16:08:24',1),(16,2,'SALIDA',1,52,51,'VENTA #7','2026-07-12 23:25:45',1),(17,5,'SALIDA',5,34,29,'VENTA #7','2026-07-12 23:25:45',1),(18,4,'SALIDA',15,15,0,'VENTA #8','2026-07-12 23:43:00',1),(19,1,'SALIDA',5,39,34,'VENTA #9','2026-07-14 00:17:29',1),(20,5,'ENTRADA',10,34,44,'COMPRA #8','2026-07-14 00:20:08',1),(21,1,'SALIDA',4,34,30,'VENTA #10','2026-07-14 00:41:24',1),(22,5,'SALIDA',10,44,34,'VENTA #11','2026-07-14 00:42:39',1),(23,4,'ENTRADA',40,0,40,'COMPRA #9','2026-07-14 00:54:54',1),(24,3,'ENTRADA',15,25,40,'COMPRA #10','2026-07-14 00:58:11',1),(25,4,'SALIDA',35,40,5,'VENTA #12','2026-07-14 01:00:26',1),(26,6,'SALIDA',100,150,50,'VENTA #13','2026-07-14 12:10:33',1),(27,3,'SALIDA',20,40,20,'VENTA #14','2026-07-14 12:13:56',1),(28,3,'SALIDA',25,30,5,'VENTA #15','2026-07-14 12:16:41',1),(29,6,'SALIDA',20,60,40,'VENTA #16','2026-07-14 12:47:41',1),(30,6,'SALIDA',1,42,41,'VENTA #17','2026-07-14 16:54:03',1),(31,3,'SALIDA',5,10,5,'VENTA #18','2026-07-14 17:06:25',1),(32,6,'SALIDA',1,41,40,'VENTA #19','2026-07-14 17:18:06',1);
/*!40000 ALTER TABLE `kardex` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `libromayor`
--

DROP TABLE IF EXISTS `libromayor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `libromayor` (
  `idAsiento` int NOT NULL AUTO_INCREMENT,
  `fecha` date NOT NULL,
  `glosa` varchar(255) NOT NULL,
  `cuentaDebe` varchar(100) NOT NULL,
  `cuentaHaber` varchar(100) NOT NULL,
  `debe` decimal(10,2) NOT NULL DEFAULT '0.00',
  `haber` decimal(10,2) NOT NULL DEFAULT '0.00',
  `nroAsiento` varchar(20) NOT NULL,
  `idUsuario` int NOT NULL,
  PRIMARY KEY (`idAsiento`),
  KEY `fk_lm_usuario` (`idUsuario`),
  CONSTRAINT `fk_lm_usuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `libromayor`
--

LOCK TABLES `libromayor` WRITE;
/*!40000 ALTER TABLE `libromayor` DISABLE KEYS */;
INSERT INTO `libromayor` VALUES (1,'2026-07-14','Venta #10 - Tarjeta de Débito','101 Efectivo','',10.00,0.00,'VTA-10',1),(2,'2026-07-14','Venta #10 - Tarjeta de Débito','','701 Ventas',0.00,8.47,'VTA-10',1),(3,'2026-07-14','Venta #10 - Tarjeta de Débito','','4011 IGV por Pagar',0.00,1.53,'VTA-10',1),(4,'2026-07-14','Venta #11 - Efectivo','101 Efectivo','',55.00,0.00,'VTA-11',1),(5,'2026-07-14','Venta #11 - Efectivo','','701 Ventas',0.00,46.61,'VTA-11',1),(6,'2026-07-14','Venta #11 - Efectivo','','4011 IGV por Pagar',0.00,8.39,'VTA-11',1),(7,'2026-07-14','Compra #9 - F012','201 Mercaderías','',180.00,0.00,'CMP-9',1),(8,'2026-07-14','Compra #9 - F012','4011 IGV Crédito Fiscal','',32.40,0.00,'CMP-9',1),(9,'2026-07-14','Compra #9 - F012','','101 Efectivo',0.00,212.40,'CMP-9',1),(10,'2026-07-14','Compra #10 - F014 (Crédito)','201 Mercaderías','',7.50,0.00,'CMP-10',1),(11,'2026-07-14','Compra #10 - F014 (Crédito)','4011 IGV Crédito Fiscal','',1.35,0.00,'CMP-10',1),(12,'2026-07-14','Compra #10 - F014 (Crédito)','','421 Cuentas por Pagar',0.00,8.85,'CMP-10',1),(13,'2026-07-14','Pago deuda - F014','421 Cuentas por Pagar','',8.85,0.00,'PAG-2',1),(14,'2026-07-14','Pago deuda - F014','','101 Efectivo',0.00,8.85,'PAG-2',1),(15,'2026-07-14','Venta #12 - Efectivo','101 Efectivo','',210.00,0.00,'VTA-12',1),(16,'2026-07-14','Venta #12 - Efectivo','','701 Ventas',0.00,177.97,'VTA-12',1),(17,'2026-07-14','Venta #12 - Efectivo','','4011 IGV por Pagar',0.00,32.03,'VTA-12',1),(18,'2026-07-14','Venta #13 - Efectivo','101 Efectivo','',800.00,0.00,'VTA-13',1),(19,'2026-07-14','Venta #13 - Efectivo','','701 Ventas',0.00,677.97,'VTA-13',1),(20,'2026-07-14','Venta #13 - Efectivo','','4011 IGV por Pagar',0.00,122.03,'VTA-13',1),(21,'2026-07-14','Devolución Venta #13','701 Ventas','',80.00,0.00,'DEV-13',1),(22,'2026-07-14','Devolución Venta #13','','101 Efectivo',0.00,80.00,'DEV-13',1),(23,'2026-07-14','Venta #14 - Efectivo','101 Efectivo','',20.00,0.00,'VTA-14',1),(24,'2026-07-14','Venta #14 - Efectivo','','701 Ventas',0.00,16.95,'VTA-14',1),(25,'2026-07-14','Venta #14 - Efectivo','','4011 IGV por Pagar',0.00,3.05,'VTA-14',1),(26,'2026-07-14','Devolución Venta #14','701 Ventas','',10.00,0.00,'DEV-14',1),(27,'2026-07-14','Devolución Venta #14','','101 Efectivo',0.00,10.00,'DEV-14',1),(28,'2026-07-14','Venta #15 - Efectivo','101 Efectivo','',25.00,0.00,'VTA-15',1),(29,'2026-07-14','Venta #15 - Efectivo','','701 Ventas',0.00,21.19,'VTA-15',1),(30,'2026-07-14','Venta #15 - Efectivo','','4011 IGV por Pagar',0.00,3.81,'VTA-15',1),(31,'2026-07-14','Devolución Venta #15','701 Ventas','',5.00,0.00,'DEV-15',1),(32,'2026-07-14','Devolución Venta #15','','101 Efectivo',0.00,5.00,'DEV-15',1),(33,'2026-07-14','Venta #16 - Efectivo','101 Efectivo','',160.00,0.00,'VTA-16',1),(34,'2026-07-14','Venta #16 - Efectivo','','701 Ventas',0.00,135.59,'VTA-16',1),(35,'2026-07-14','Venta #16 - Efectivo','','4011 IGV por Pagar',0.00,24.41,'VTA-16',1),(36,'2026-07-14','Devolución Venta #16','701 Ventas','',16.00,0.00,'DEV-16',1),(37,'2026-07-14','Devolución Venta #16','','101 Efectivo',0.00,16.00,'DEV-16',1),(38,'2026-07-14','Venta #17 - Yape','101 Efectivo','',8.00,0.00,'VTA-17',1),(39,'2026-07-14','Venta #17 - Yape','','701 Ventas',0.00,6.78,'VTA-17',1),(40,'2026-07-14','Venta #17 - Yape','','4011 IGV por Pagar',0.00,1.22,'VTA-17',1),(41,'2026-07-14','Venta #18 - Plin','101 Efectivo','',5.00,0.00,'VTA-18',1),(42,'2026-07-14','Venta #18 - Plin','','701 Ventas',0.00,4.24,'VTA-18',1),(43,'2026-07-14','Venta #18 - Plin','','4011 IGV por Pagar',0.00,0.76,'VTA-18',1),(44,'2026-07-14','Devolución Venta #18','701 Ventas','',5.00,0.00,'DEV-18',1),(45,'2026-07-14','Devolución Venta #18','','101 Efectivo',0.00,5.00,'DEV-18',1),(46,'2026-07-14','Venta #19 - Tarjeta','101 Efectivo','',8.00,0.00,'VTA-19',1),(47,'2026-07-14','Venta #19 - Tarjeta','','701 Ventas',0.00,6.78,'VTA-19',1),(48,'2026-07-14','Venta #19 - Tarjeta','','4011 IGV por Pagar',0.00,1.22,'VTA-19',1);
/*!40000 ALTER TABLE `libromayor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `planillaasistencia`
--

DROP TABLE IF EXISTS `planillaasistencia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `planillaasistencia` (
  `idAsistencia` int NOT NULL AUTO_INCREMENT,
  `idEmpleado` int NOT NULL,
  `fecha` date NOT NULL,
  `horaEntrada` time DEFAULT NULL,
  `horaSalida` time DEFAULT NULL,
  `horas` decimal(4,2) DEFAULT NULL,
  `estado` enum('Presente','Ausente','Tardanza','Permiso','Vacaciones') NOT NULL DEFAULT 'Presente',
  `observacion` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`idAsistencia`),
  UNIQUE KEY `uq_emp_fecha` (`idEmpleado`,`fecha`),
  CONSTRAINT `fk_asist_empleado` FOREIGN KEY (`idEmpleado`) REFERENCES `empleado` (`idEmpleado`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `planillaasistencia`
--

LOCK TABLES `planillaasistencia` WRITE;
/*!40000 ALTER TABLE `planillaasistencia` DISABLE KEYS */;
INSERT INTO `planillaasistencia` VALUES (1,1,'2026-06-29','17:14:39','00:00:00',0.00,'Presente',NULL),(2,1,'2026-07-12','08:00:00','17:00:00',9.00,'Presente',NULL),(3,3,'2026-07-13','00:23:54','00:35:43',0.20,'Presente',NULL),(4,2,'2026-07-13','23:34:11','23:34:50',0.00,'Presente',NULL),(5,3,'2026-07-14','12:45:51','14:40:16',1.92,'Presente',NULL);
/*!40000 ALTER TABLE `planillaasistencia` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `planillamensual`
--

DROP TABLE IF EXISTS `planillamensual`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `planillamensual` (
  `idPlanilla` int NOT NULL AUTO_INCREMENT,
  `idEmpleado` int NOT NULL,
  `mes` tinyint NOT NULL,
  `anio` year NOT NULL,
  `diasTrabajados` int NOT NULL DEFAULT '0',
  `faltas` int NOT NULL DEFAULT '0',
  `diasVacaciones` int NOT NULL DEFAULT '0',
  `salarioBase` decimal(10,2) NOT NULL,
  `bonificacion` decimal(10,2) NOT NULL DEFAULT '0.00',
  `descuento` decimal(10,2) NOT NULL DEFAULT '0.00',
  `pagoFinal` decimal(10,2) NOT NULL DEFAULT '0.00',
  `fechaGeneracion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `idUsuario` int NOT NULL,
  PRIMARY KEY (`idPlanilla`),
  UNIQUE KEY `uq_emp_mes_anio` (`idEmpleado`,`mes`,`anio`),
  KEY `fk_plan_usuario` (`idUsuario`),
  CONSTRAINT `fk_plan_empleado` FOREIGN KEY (`idEmpleado`) REFERENCES `empleado` (`idEmpleado`),
  CONSTRAINT `fk_plan_usuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `planillamensual`
--

LOCK TABLES `planillamensual` WRITE;
/*!40000 ALTER TABLE `planillamensual` DISABLE KEYS */;
/*!40000 ALTER TABLE `planillamensual` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `producto`
--

DROP TABLE IF EXISTS `producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto` (
  `idProducto` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `idCategoria` int NOT NULL,
  `precio` decimal(10,2) NOT NULL,
  `cantidad` int NOT NULL,
  `lote` varchar(50) DEFAULT NULL,
  `vencimiento` date DEFAULT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`idProducto`),
  KEY `FK_Producto_Categoria` (`idCategoria`),
  CONSTRAINT `FK_Producto_Categoria` FOREIGN KEY (`idCategoria`) REFERENCES `categoria` (`idCategoria`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `producto`
--

LOCK TABLES `producto` WRITE;
/*!40000 ALTER TABLE `producto` DISABLE KEYS */;
INSERT INTO `producto` VALUES (1,'Inca Kola','500ml',1,2.50,30,NULL,NULL,1),(2,'COCA COLA','600ml',1,2.50,52,NULL,NULL,1),(3,'CHOCO SODA','CHOCO SODA',3,1.00,10,NULL,NULL,1),(4,'CERVEZA','305ml',9,6.00,5,NULL,NULL,1),(5,'ROSQUILLAS','ROSQUILLAS',5,5.50,34,NULL,NULL,1),(6,'TRIGO NEGRA','310 ml',9,8.00,40,NULL,NULL,1);
/*!40000 ALTER TABLE `producto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `proveedor`
--

DROP TABLE IF EXISTS `proveedor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `proveedor` (
  `idProveedor` int NOT NULL AUTO_INCREMENT,
  `razonSocial` varchar(100) NOT NULL,
  `ruc` char(11) NOT NULL,
  `telefono` varchar(15) DEFAULT NULL,
  `direccion` varchar(150) DEFAULT NULL,
  `correo` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`idProveedor`),
  UNIQUE KEY `ruc` (`ruc`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `proveedor`
--

LOCK TABLES `proveedor` WRITE;
/*!40000 ALTER TABLE `proveedor` DISABLE KEYS */;
INSERT INTO `proveedor` VALUES (1,'Corporación Lindley S.A','20100070970','933258741','Av. Naranjal 780, Los Olivos, Lima','Lindley@gmail.com');
/*!40000 ALTER TABLE `proveedor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tipocambio`
--

DROP TABLE IF EXISTS `tipocambio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipocambio` (
  `idTipoCambio` int NOT NULL AUTO_INCREMENT,
  `valor` decimal(8,4) NOT NULL DEFAULT '3.7200',
  `fecha` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `idUsuario` int NOT NULL,
  PRIMARY KEY (`idTipoCambio`),
  KEY `fk_tc_usuario` (`idUsuario`),
  CONSTRAINT `fk_tc_usuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tipocambio`
--

LOCK TABLES `tipocambio` WRITE;
/*!40000 ALTER TABLE `tipocambio` DISABLE KEYS */;
INSERT INTO `tipocambio` VALUES (1,3.7200,'2026-06-27 13:25:55',1);
/*!40000 ALTER TABLE `tipocambio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `idUsuario` int NOT NULL AUTO_INCREMENT,
  `usuario` varchar(50) NOT NULL,
  `contrasena` varchar(100) NOT NULL,
  `idEmpleado` int NOT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`idUsuario`),
  KEY `idEmpleado` (`idEmpleado`),
  CONSTRAINT `usuario_ibfk_1` FOREIGN KEY (`idEmpleado`) REFERENCES `empleado` (`idEmpleado`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,'ADMIN','1234',1,1),(2,'GERENTE','1234',2,1),(3,'CAJERO1','1234',3,1),(4,'CAJERO2','1234',4,1);
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `venta`
--

DROP TABLE IF EXISTS `venta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `venta` (
  `idVenta` int NOT NULL AUTO_INCREMENT,
  `idCliente` int NOT NULL,
  `idUsuario` int NOT NULL,
  `fecha` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `subtotal` decimal(10,2) NOT NULL DEFAULT '0.00',
  `igv` decimal(10,2) NOT NULL DEFAULT '0.00',
  `total` decimal(10,2) NOT NULL DEFAULT '0.00',
  `metodoPago` varchar(50) NOT NULL DEFAULT 'Efectivo',
  `estado` enum('Activa','Parcial','Anulada') NOT NULL DEFAULT 'Activa',
  PRIMARY KEY (`idVenta`),
  KEY `fk_venta_cliente` (`idCliente`),
  KEY `fk_venta_usuario` (`idUsuario`),
  CONSTRAINT `fk_venta_cliente` FOREIGN KEY (`idCliente`) REFERENCES `cliente` (`idCliente`),
  CONSTRAINT `fk_venta_usuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `venta`
--

LOCK TABLES `venta` WRITE;
/*!40000 ALTER TABLE `venta` DISABLE KEYS */;
INSERT INTO `venta` VALUES (1,1,1,'2026-06-27 20:03:14',5.00,0.90,5.90,'Efectivo','Anulada'),(2,1,1,'2026-07-02 00:29:02',10.00,1.80,11.80,'Tarjeta de Crédito','Parcial'),(3,1,1,'2026-07-11 23:27:07',10.00,1.80,11.80,'Tarjeta de Crédito','Activa'),(4,2,1,'2026-07-11 23:34:41',112.50,20.25,132.75,'Efectivo','Activa'),(5,2,1,'2026-07-12 01:52:45',8.47,1.53,10.00,'Efectivo','Activa'),(6,1,1,'2026-07-12 16:08:23',30.08,5.42,35.50,'Tarjeta de Débito','Activa'),(7,2,1,'2026-07-12 23:25:44',25.42,4.58,30.00,'Efectivo','Anulada'),(8,2,1,'2026-07-12 23:43:00',76.27,13.73,90.00,'Efectivo','Activa'),(9,2,1,'2026-07-14 00:17:28',10.59,1.91,12.50,'Efectivo','Activa'),(10,2,1,'2026-07-14 00:41:24',8.47,1.53,10.00,'Tarjeta de Débito','Activa'),(11,2,1,'2026-07-14 00:42:38',46.61,8.39,55.00,'Efectivo','Activa'),(12,2,1,'2026-07-14 01:00:25',177.97,32.03,210.00,'Efectivo','Activa'),(13,3,1,'2026-07-14 12:10:33',677.97,122.03,800.00,'Efectivo','Parcial'),(14,3,1,'2026-07-14 12:13:56',16.95,3.05,20.00,'Efectivo','Parcial'),(15,3,1,'2026-07-14 12:16:41',21.19,3.81,25.00,'Efectivo','Parcial'),(16,4,1,'2026-07-14 12:47:41',135.59,24.41,160.00,'Efectivo','Parcial'),(17,4,1,'2026-07-14 16:54:02',6.78,1.22,8.00,'Yape','Activa'),(18,3,1,'2026-07-14 17:06:25',4.24,0.76,5.00,'Plin','Parcial'),(19,5,1,'2026-07-14 17:18:06',6.78,1.22,8.00,'Tarjeta','Activa');
/*!40000 ALTER TABLE `venta` ENABLE KEYS */;
UNLOCK TABLES;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-14 19:43:20
