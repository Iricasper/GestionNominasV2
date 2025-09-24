-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Versión del servidor:         11.6.2-MariaDB - mariadb.org binary distribution
-- SO del servidor:              Win64
-- HeidiSQL Versión:             12.8.0.6908
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Volcando estructura de base de datos para gestion_nominas
CREATE DATABASE IF NOT EXISTS `gestion_nominas` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci */;
USE `gestion_nominas`;

-- Volcando estructura para tabla gestion_nominas.empleados
CREATE TABLE IF NOT EXISTS `empleados` (
  `dni` char(9) NOT NULL,
  `nombre` varchar(50) DEFAULT NULL,
  `sexo` char(1) DEFAULT NULL,
  `anyos` int(11) DEFAULT NULL,
  `categoria` int(11) DEFAULT NULL,
  PRIMARY KEY (`dni`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- Volcando datos para la tabla gestion_nominas.empleados: ~2 rows (aproximadamente)
REPLACE INTO `empleados` (`dni`, `nombre`, `sexo`, `anyos`, `categoria`) VALUES
	('32000031R', 'Ada Lovelace', 'F', 1, 1),
	('32000032G', 'James Cosling', 'M', 7, 9);

-- Volcando estructura para tabla gestion_nominas.nominas
CREATE TABLE IF NOT EXISTS `nominas` (
  `dni` char(9) NOT NULL,
  `sueldo` int(11) DEFAULT NULL,
  PRIMARY KEY (`dni`),
  CONSTRAINT `dni` FOREIGN KEY (`dni`) REFERENCES `empleados` (`dni`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- Volcando datos para la tabla gestion_nominas.nominas: ~2 rows (aproximadamente)
REPLACE INTO `nominas` (`dni`, `sueldo`) VALUES
	('32000031R', 55000),
	('32000032G', 245000);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
