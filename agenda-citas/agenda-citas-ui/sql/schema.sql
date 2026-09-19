CREATE DATABASE IF NOT EXISTS agenda_citas;

USE agenda_citas;

CREATE TABLE IF NOT EXISTS citas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente VARCHAR(100) NOT NULL,
    fecha_hora DATETIME NOT NULL,
    servicio VARCHAR(200) NOT NULL,
    duracion_minutos INT NOT NULL,
    estado VARCHAR(20) NOT NULL
);