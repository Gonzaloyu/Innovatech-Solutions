
INSERT INTO departamentos (nombre) VALUES ('TI');
INSERT INTO departamentos (nombre) VALUES ('Ventas');
INSERT INTO departamentos (nombre) VALUES ('Consultoría');

INSERT INTO cargos (nombre, nivel) VALUES ('Desarrollador', 'Junior'); -- ID: 1
INSERT INTO cargos (nombre, nivel) VALUES ('Desarrollador', 'Senior'); -- ID: 2
INSERT INTO cargos (nombre, nivel) VALUES ('Arquitecto', 'Senior');    -- ID: 3

-- Los 15 empleados con el nuevo campo valor_hora incorporado
INSERT INTO empleados (nombre, email, departamento_id, cargo_id, valor_hora) VALUES ('Benjamin Ojeda', 'benjamin.ojeda@innovatech.cl', 1, 1, 15.50);
INSERT INTO empleados (nombre, email, departamento_id, cargo_id, valor_hora) VALUES ('Gino Trujillo', 'gino.trujillo@innovatech.cl', 1, 2, 35.00);
INSERT INTO empleados (nombre, email, departamento_id, cargo_id, valor_hora) VALUES ('Samuel Deluque', 'samuel.deluque@innovatech.cl', 3, 3, 55.00);
INSERT INTO empleados (nombre, email, departamento_id, cargo_id, valor_hora) VALUES ('Guillermo Díaz', 'guillermo.diaz@innovatech.cl', 1, 2, 32.50);
INSERT INTO empleados (nombre, email, departamento_id, cargo_id, valor_hora) VALUES ('Renato Astete', 'renato.astete@innovatech.cl', 2, 1, 14.00);
INSERT INTO empleados (nombre, email, departamento_id, cargo_id, valor_hora) VALUES ('Franko Sanchez', 'franko.sanchez1@innovatech.cl', 1, 3, 58.00);
INSERT INTO empleados (nombre, email, departamento_id, cargo_id, valor_hora) VALUES ('Tiziano Piana', 'tiziano.piana@innovatech.cl', 3, 1, 16.00);
INSERT INTO empleados (nombre, email, departamento_id, cargo_id, valor_hora) VALUES ('Nicky Jam', 'nicky.jam@innovatech.cl', 2, 1, 15.00);
INSERT INTO empleados (nombre, email, departamento_id, cargo_id, valor_hora) VALUES ('Elvis Crespo', 'elvis.crespo@innovatech.cl', 2, 2, 30.00);
INSERT INTO empleados (nombre, email, departamento_id, cargo_id, valor_hora) VALUES ('Carlos Contreras', 'carlos.contreras@innovatech.cl', 3, 1, 17.50);
INSERT INTO empleados (nombre, email, departamento_id, cargo_id, valor_hora) VALUES ('Daddy Yankee', 'daddy.yankee@innovatech.cl', 1, 3, 62.00);
INSERT INTO empleados (nombre, email, departamento_id, cargo_id, valor_hora) VALUES ('Rene Puente', 'rene.puente@innovatech.cl', 2, 1, 12.50);
INSERT INTO empleados (nombre, email, departamento_id, cargo_id, valor_hora) VALUES ('Miguel Cornejo', 'miguel.cornejo@innovatech.cl', 3, 2, 34.00);
INSERT INTO empleados (nombre, email, departamento_id, cargo_id, valor_hora) VALUES ('Alexis Sanchez', 'alexis.sanchez@innovatech.cl', 2, 1, 18.00);
INSERT INTO empleados (nombre, email, departamento_id, cargo_id, valor_hora) VALUES ('Fabian Ortega', 'fabian.ortega@innovatech.cl', 1, 2, 36.50);