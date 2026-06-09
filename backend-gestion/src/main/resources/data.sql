-- Estados de Proyecto
INSERT INTO estados_proyecto (nombre) 
SELECT 'En Planificacion' WHERE NOT EXISTS (SELECT 1 FROM estados_proyecto WHERE nombre = 'En Planificacion');
INSERT INTO estados_proyecto (nombre) 
SELECT 'En Ejecucion' WHERE NOT EXISTS (SELECT 1 FROM estados_proyecto WHERE nombre = 'En Ejecucion');
INSERT INTO estados_proyecto (nombre) 
SELECT 'Finalizado' WHERE NOT EXISTS (SELECT 1 FROM estados_proyecto WHERE nombre = 'Finalizado');

-- Categorías
INSERT INTO categorias (nombre) 
SELECT 'Gestion' WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Gestion');
INSERT INTO categorias (nombre) 
SELECT 'Desarrrollo' WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Desarrrollo');
INSERT INTO categorias (nombre) 
SELECT 'Consultoria' WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Consultoria');

-- Clientes de ejemplo
INSERT INTO clientes (nombre_empresa, rut_empresa, contacto, email_contacto)
SELECT 'Empresa 1', '76.000.001-1', 'Juan Pérez', 'juan@demo1.cl'
WHERE NOT EXISTS (SELECT 1 FROM clientes WHERE rut_empresa = '76.000.001-1');

INSERT INTO clientes (nombre_empresa, rut_empresa, contacto, email_contacto)
SELECT 'Empresa 2', '76.000.002-2', 'María González', 'maria@demo2.cl'
WHERE NOT EXISTS (SELECT 1 FROM clientes WHERE rut_empresa = '76.000.002-2');