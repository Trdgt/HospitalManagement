CREATE DATABASE IF NOT EXISTS hospital_management;
USE hospital_management;

CREATE TABLE IF NOT EXISTS staff (
    national_code VARCHAR(20) PRIMARY KEY, -- کد ملی (شناسه منحصربه‌فرد)
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    job_title VARCHAR(50) NOT NULL,
    salary DECIMAL(10, 2) NOT NULL
);
DESCRIBE staff;


CREATE TABLE patients (
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    nationalCode VARCHAR(10) PRIMARY KEY,
    disease VARCHAR(100)
   
);
DESCRIBE patients;



    CREATE TABLE IF NOT EXISTS rooms (
    roomNumber INT PRIMARY KEY,   -- شماره اتاق
    isOccupied BOOLEAN           -- وضعیت اشغال بودن اتاق (TRUE برای اشغال و FALSE برای خالی)
);
DESCRIBE rooms;

SHOW TABLES;

