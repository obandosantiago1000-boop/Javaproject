-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema banco
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `banco` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
SHOW WARNINGS;
USE `banco` ;

-- -----------------------------------------------------
-- Table `banco`.`personas`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banco`.`personas` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL DEFAULT NULL,
  `documento` VARCHAR(20) NULL DEFAULT NULL,
  `correo` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 13
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `banco`.`auditoria`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banco`.`auditoria` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `fecha_hora` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `usuario_id` INT NOT NULL,
  `tipo_usuario` VARCHAR(20) NOT NULL,
  `modulo` VARCHAR(30) NOT NULL,
  `accion` VARCHAR(60) NOT NULL,
  `detalle` VARCHAR(500) NULL,
  PRIMARY KEY (`id`),
  INDEX `1_fk_idx` (`usuario_id` ASC) VISIBLE,
  CONSTRAINT `1_fk`
    FOREIGN KEY (`usuario_id`)
    REFERENCES `banco`.`personas` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 68
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `banco`.`clientes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banco`.`clientes` (
  `id` INT NOT NULL,
  `telefono` VARCHAR(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `clientes_ibfk_1`
    FOREIGN KEY (`id`)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banco`.`empleados` (
  `id` INT NOT NULL,
  `rol` ENUM('ADMINISTRADOR', 'EMPLEADO') NULL DEFAULT NULL,
  `salario` DOUBLE NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `empleados_ibfk_1`
    FOREIGN KEY (`id`)
    REFERENCES `banco`.`personas` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `banco`.`solicitudes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banco`.`solicitudes` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `monto` DOUBLE NULL DEFAULT NULL,
  `cuotas` INT NULL DEFAULT NULL,
  `cliente_fk` INT NULL DEFAULT NULL,
  `estado` ENUM('PENDIENTE', 'UTILIZADA') NOT NULL DEFAULT 'PENDIENTE',
  PRIMARY KEY (`id`),
  INDEX `cliente_fk` (`cliente_fk` ASC) VISIBLE,
  CONSTRAINT `solicitudes_ibfk_1`
    FOREIGN KEY (`cliente_fk`)
    REFERENCES `banco`.`clientes` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 6
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `banco`.`prestamos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banco`.`prestamos` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `interes` DOUBLE NULL DEFAULT NULL,
  `fecha_inicio` DATE NULL DEFAULT NULL,
  `estado` ENUM('ACTIVO', 'PAGADO') NULL DEFAULT NULL,
  `empleado_fk` INT NULL DEFAULT NULL,
  `solicitud_fk` INT NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `empleado_fk` (`empleado_fk` ASC) VISIBLE,
  INDEX `solicitud_fk` (`solicitud_fk` ASC) VISIBLE,
  CONSTRAINT `prestamos_ibfk_1`
    FOREIGN KEY (`empleado_fk`)
    REFERENCES `banco`.`empleados` (`id`),
  CONSTRAINT `prestamos_ibfk_2`
    FOREIGN KEY (`solicitud_fk`)
    REFERENCES `banco`.`solicitudes` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `banco`.`cuota`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banco`.`cuota` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `fecha_pago` DATE NOT NULL,
  `valor` DOUBLE NOT NULL,
  `prestamo_fk` INT NOT NULL,
  `valor_pagado` DOUBLE NOT NULL DEFAULT '0',
  `estado` ENUM('PENDIENTE', 'PAGADA') NOT NULL DEFAULT 'PENDIENTE',
  PRIMARY KEY (`id`),
  INDEX `1_c_fk_idx` (`prestamo_fk` ASC) VISIBLE,
  CONSTRAINT `1_c_fk`
    FOREIGN KEY (`prestamo_fk`)
    REFERENCES `banco`.`prestamos` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 15
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `banco`.`metodo_pagos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banco`.`metodo_pagos` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `metodo` ENUM('EFECTIVO', 'TRANSFERENCIA', 'TARJETA', 'CONSIGNACION') NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `banco`.`pagos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banco`.`pagos` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `fecha_pago` DATE NULL DEFAULT NULL,
  `monto` DOUBLE NULL DEFAULT NULL,
  `prestamo_fk` INT NULL DEFAULT NULL,
  `metodo_fk` INT NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `prestamo_fk` (`prestamo_fk` ASC) VISIBLE,
  INDEX `metodo_fk` (`metodo_fk` ASC) VISIBLE,
  CONSTRAINT `pagos_ibfk_1`
    FOREIGN KEY (`prestamo_fk`)
    REFERENCES `banco`.`prestamos` (`id`),
  CONSTRAINT `pagos_ibfk_2`
    FOREIGN KEY (`metodo_fk`)
    REFERENCES `banco`.`metodo_pagos` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `banco`.`reportes_generados`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banco`.`reportes_generados` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `titulo` VARCHAR(100) NOT NULL,
  `fecha` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `contenido` LONGTEXT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 7
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
-- -----------------------------------------------------
-- Table `banco`.`empleados`

