-- MySQL Workbench Synchronization
-- Generated: 2015-08-18 14:01
-- Model: New Model
-- Version: 1.0
-- Project: Name of the project
-- Author: Ambey Govenkar

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

ALTER SCHEMA `mapp_db`  DEFAULT CHARACTER SET utf8  DEFAULT COLLATE utf8_general_ci ;

CREATE TABLE IF NOT EXISTS `mapp_db`.`Customer` (
  `fName` VARCHAR(45) NOT NULL,
  `lName` VARCHAR(45) NOT NULL,
  `emailId` VARCHAR(60) NULL DEFAULT NULL,
  `cellphone` VARCHAR(15) NOT NULL,
  `dob` DATE NULL DEFAULT NULL,
  `location` VARCHAR(45) NULL DEFAULT NULL,
  `passwd` VARCHAR(512) NULL DEFAULT NULL,
  `gender` CHAR(1) NULL DEFAULT NULL,
  `idCity` INT(11) NOT NULL,
  `weight` DECIMAL(5,2) NULL DEFAULT NULL,
  `idCustomer` VARCHAR(20) NOT NULL,
  INDEX `fk_Customer_City1_idx` (`idCity` ASC),
  PRIMARY KEY (`idCustomer`),
  UNIQUE INDEX `cellphone_UNIQUE` (`cellphone` ASC),
  CONSTRAINT `fk_Customer_City1`
    FOREIGN KEY (`idCity`)
    REFERENCES `mapp_db`.`City` (`idCity`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `mapp_db`.`Appointment` (
  `idAppointment` INT(11) NOT NULL,
  `date` DATE NULL DEFAULT NULL,
  `from` TIME NULL DEFAULT NULL,
  `to` TIME NULL DEFAULT NULL,
  `idServProvHasServPt` INT(11) NOT NULL,
  `idCustomer` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`idAppointment`),
  UNIQUE INDEX `idAppointment_UNIQUE` (`idAppointment` ASC),
  INDEX `fk_Appointment_ServProvHasServPt1_idx` (`idServProvHasServPt` ASC),
  INDEX `fk_Appointment_Customer2_idx` (`idCustomer` ASC),
  CONSTRAINT `fk_Appointment_ServProvHasServPt1`
    FOREIGN KEY (`idServProvHasServPt`)
    REFERENCES `mapp_db`.`ServProvHasServPt` (`idServProvHasServPt`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Appointment_Customer2`
    FOREIGN KEY (`idCustomer`)
    REFERENCES `mapp_db`.`Customer` (`idCustomer`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Appointment_CustDependents1`
    FOREIGN KEY (`idCustomer`)
    REFERENCES `mapp_db`.`CustDependents` (`idCustomer`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `mapp_db`.`ServiceProvider` (
  `cellphone` VARCHAR(15) NOT NULL,
  `fName` VARCHAR(45) NULL DEFAULT NULL,
  `lName` VARCHAR(45) NULL DEFAULT NULL,
  `emailId` VARCHAR(60) NULL DEFAULT NULL,
  `passwd` VARCHAR(512) NULL DEFAULT NULL,
  `gender` CHAR(1) NULL DEFAULT NULL,
  `subscribed` TINYINT(4) NULL DEFAULT NULL,
  `subsDate` DATE NULL DEFAULT NULL,
  `qualification` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`cellphone`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `mapp_db`.`ServicePoint` (
  `name` VARCHAR(80) NOT NULL,
  `location` VARCHAR(45) NOT NULL,
  `idCity` INT(11) NOT NULL,
  `phone` VARCHAR(15) NULL DEFAULT NULL,
  `altPhone` VARCHAR(15) NULL DEFAULT NULL,
  `emailId` VARCHAR(60) NULL DEFAULT NULL,
  INDEX `fk_Clinic_City1_idx` (`idCity` ASC),
  PRIMARY KEY (`name`, `location`, `idCity`),
  CONSTRAINT `fk_Clinic_City1`
    FOREIGN KEY (`idCity`)
    REFERENCES `mapp_db`.`City` (`idCity`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `mapp_db`.`Report` (
  `idAppointment` INT(11) NOT NULL,
  `reportType` VARCHAR(45) NOT NULL,
  `scannedCopy` MEDIUMBLOB NULL DEFAULT NULL,
  PRIMARY KEY (`idAppointment`, `reportType`),
  INDEX `fk_RxDetails_Appointment1_idx` (`idAppointment` ASC),
  INDEX `fk_Report_ReportType1_idx` (`reportType` ASC),
  CONSTRAINT `fk_RxDetails_Appointment1`
    FOREIGN KEY (`idAppointment`)
    REFERENCES `mapp_db`.`Appointment` (`idAppointment`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Report_ReportType1`
    FOREIGN KEY (`reportType`)
    REFERENCES `mapp_db`.`ReportType` (`reportType`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `mapp_db`.`LabTestDetails` (
  `idLabTestDetails` INT(11) NOT NULL,
  `idAppointment` INT(11) NOT NULL,
  PRIMARY KEY (`idLabTestDetails`, `idAppointment`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `mapp_db`.`ScanDetails` (
  `idScanDetails` INT(11) NOT NULL,
  `idAppointment` INT(11) NOT NULL,
  PRIMARY KEY (`idScanDetails`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `mapp_db`.`City` (
  `idCity` INT(11) NOT NULL,
  `city` VARCHAR(45) NOT NULL,
  `state` VARCHAR(45) NOT NULL,
  `country` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idCity`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `mapp_db`.`CustomerHistoryData` (
  `idServProvHasServPt` INT(11) NOT NULL,
  `idCustomer` VARCHAR(20) NOT NULL,
  `date` DATE NOT NULL,
  `reportType` VARCHAR(45) NOT NULL,
  `scannedCopy` MEDIUMBLOB NOT NULL,
  PRIMARY KEY (`idServProvHasServPt`, `idCustomer`, `date`, `reportType`),
  INDEX `fk_CustomerHistoryData_ReportType1_idx` (`reportType` ASC),
  INDEX `fk_CustomerHistoryData_ServProvHasServPt1_idx` (`idServProvHasServPt` ASC),
  INDEX `fk_CustomerHistoryData_Customer2_idx` (`idCustomer` ASC),
  CONSTRAINT `fk_CustomerHistoryData_ReportType1`
    FOREIGN KEY (`reportType`)
    REFERENCES `mapp_db`.`ReportType` (`reportType`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_CustomerHistoryData_ServProvHasServPt1`
    FOREIGN KEY (`idServProvHasServPt`)
    REFERENCES `mapp_db`.`ServProvHasServPt` (`idServProvHasServPt`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_CustomerHistoryData_Customer2`
    FOREIGN KEY (`idCustomer`)
    REFERENCES `mapp_db`.`Customer` (`idCustomer`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_CustomerHistoryData_CustDependents1`
    FOREIGN KEY (`idCustomer`)
    REFERENCES `mapp_db`.`CustDependents` (`idCustomer`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `mapp_db`.`Service` (
  `name` VARCHAR(25) NOT NULL,
  PRIMARY KEY (`name`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `mapp_db`.`ServicePointType` (
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`name`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `mapp_db`.`ReportType` (
  `reportType` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`reportType`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `mapp_db`.`Prescription` (
  `idPrescription` INT(11) NOT NULL,
  PRIMARY KEY (`idPrescription`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `mapp_db`.`Bill` (
  `idBill` INT(11) NOT NULL,
  PRIMARY KEY (`idBill`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `mapp_db`.`ServProvHasLinks` (
  `spCellphone1` VARCHAR(15) NOT NULL,
  `spCellphone2` VARCHAR(15) NOT NULL,
  PRIMARY KEY (`spCellphone1`, `spCellphone2`),
  INDEX `fk_ServiceProvider_has_ServiceProvider_ServiceProvider2_idx` (`spCellphone2` ASC),
  INDEX `fk_ServiceProvider_has_ServiceProvider_ServiceProvider1_idx` (`spCellphone1` ASC),
  CONSTRAINT `fk_ServiceProvider_has_ServiceProvider_ServiceProvider1`
    FOREIGN KEY (`spCellphone1`)
    REFERENCES `mapp_db`.`ServiceProvider` (`cellphone`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ServiceProvider_has_ServiceProvider_ServiceProvider2`
    FOREIGN KEY (`spCellphone2`)
    REFERENCES `mapp_db`.`ServiceProvider` (`cellphone`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `mapp_db`.`ServProvHasServPt` (
  `servProvCellphone` VARCHAR(15) NOT NULL,
  `servPtName` VARCHAR(80) NOT NULL,
  `servPtLoc` VARCHAR(45) NOT NULL,
  `servPtCityId` INT(11) NOT NULL,
  `servName` VARCHAR(25) NOT NULL,
  `servPtType` VARCHAR(45) NOT NULL,
  `weeklyOff` VARCHAR(45) NULL DEFAULT NULL,
  `startTime` TIME NULL DEFAULT NULL,
  `endTime` TIME NULL DEFAULT NULL,
  `experience` DECIMAL(3,1) NULL DEFAULT NULL,
  `idServProvHasServPt` INT(11) NOT NULL,
  PRIMARY KEY (`idServProvHasServPt`),
  INDEX `fk_ServiceProvider_has_ServicePoint_ServicePoint1_idx` (`servPtName` ASC, `servPtLoc` ASC, `servPtCityId` ASC),
  INDEX `fk_ServiceProvider_has_ServicePoint_ServiceProvider1_idx` (`servProvCellphone` ASC),
  INDEX `fk_ServiceProvider_has_ServicePoint_Service1_idx` (`servName` ASC),
  INDEX `fk_ServiceProvider_has_ServicePoint_ServicePointType1_idx` (`servPtType` ASC),
  CONSTRAINT `fk_ServiceProvider_has_ServicePoint_ServiceProvider1`
    FOREIGN KEY (`servProvCellphone`)
    REFERENCES `mapp_db`.`ServiceProvider` (`cellphone`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ServiceProvider_has_ServicePoint_ServicePoint1`
    FOREIGN KEY (`servPtName` , `servPtLoc` , `servPtCityId`)
    REFERENCES `mapp_db`.`ServicePoint` (`name` , `location` , `idCity`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ServiceProvider_has_ServicePoint_Service1`
    FOREIGN KEY (`servName`)
    REFERENCES `mapp_db`.`Service` (`name`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ServiceProvider_has_ServicePoint_ServicePointType1`
    FOREIGN KEY (`servPtType`)
    REFERENCES `mapp_db`.`ServicePointType` (`name`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `mapp_db`.`CustDependents` (
  `idCustomer` VARCHAR(20) NOT NULL,
  `fName` VARCHAR(45) NOT NULL,
  `lName` VARCHAR(45) NOT NULL,
  `dob` DATE NULL DEFAULT NULL,
  `location` VARCHAR(45) NULL DEFAULT NULL,
  `idCity` INT(11) NULL DEFAULT NULL,
  `weight` DECIMAL(5,2) NULL DEFAULT NULL,
  `gender` CHAR(1) NULL DEFAULT NULL,
  `custCellphone` VARCHAR(15) NOT NULL,
  PRIMARY KEY (`idCustomer`),
  INDEX `fk_CustDependents_Customer1_idx` (`custCellphone` ASC),
  INDEX `fk_CustDependents_City1_idx` (`idCity` ASC),
  INDEX `u_CustDepenId_idx` (`fName`,`lName`,`custCellphone`),
  CONSTRAINT `u_CustDepenId`
    UNIQUE (`fName`,`lName`,`custCellphone`),
  CONSTRAINT `fk_CustDependents_Customer1`
    FOREIGN KEY (`custCellphone`)
    REFERENCES `mapp_db`.`Customer` (`cellphone`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_CustDependents_City1`
    FOREIGN KEY (`idCity`)
    REFERENCES `mapp_db`.`City` (`idCity`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
