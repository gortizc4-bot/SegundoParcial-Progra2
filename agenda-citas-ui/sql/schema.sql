-- Base de datos para el sistema de agenda de citas
CREATE DATABASE IF NOT EXISTS agenda_citas;

USE agenda_citas;

-- Tabla principal de citas.
--
-- id:
-- Se utiliza INT AUTO_INCREMENT porque cada cita necesita un identificador
-- único generado automáticamente por la base de datos.
--
-- cliente:
-- Se utiliza VARCHAR(100) porque almacena el nombre del cliente.
-- Se establece NOT NULL porque el nombre es obligatorio.
--
-- fecha_hora:
-- Se utiliza DATETIME para guardar la fecha y la hora de la cita
-- en una sola columna. Desde Java se maneja mediante LocalDateTime.
-- La aplicación valida que la fecha y hora no hayan pasado.
--
-- servicio:
-- Se utiliza VARCHAR(200) para almacenar la descripción del servicio.
-- En la interfaz el usuario selecciona el servicio entre las opciones
-- disponibles. Se establece NOT NULL porque es un dato obligatorio.
--
-- duracion_minutos:
-- Se utiliza INT porque la duración se almacena como una cantidad
-- de minutos enteros. La aplicación valida que sea mayor que cero.
--
-- estado:
-- Se utiliza VARCHAR(20) para almacenar uno de los tres estados permitidos:
-- pendiente, confirmada o cancelada.
-- La interfaz limita la selección a estos valores mediante un JComboBox.
--
-- Todos los campos necesarios para una cita utilizan NOT NULL para evitar
-- registros incompletos.

CREATE TABLE IF NOT EXISTS citas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente VARCHAR(100) NOT NULL,
    fecha_hora DATETIME NOT NULL,
    servicio VARCHAR(200) NOT NULL,
    duracion_minutos INT NOT NULL,
    estado VARCHAR(20) NOT NULL
);