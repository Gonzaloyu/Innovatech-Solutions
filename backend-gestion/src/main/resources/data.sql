-- Estados de Proyecto
INSERT INTO estados_proyecto (nombre) 
SELECT 'En Planificación' WHERE NOT EXISTS (SELECT 1 FROM estados_proyecto WHERE nombre = 'En Planificación');
INSERT INTO estados_proyecto (nombre) 
SELECT 'En Ejecución' WHERE NOT EXISTS (SELECT 1 FROM estados_proyecto WHERE nombre = 'En Ejecución');
INSERT INTO estados_proyecto (nombre) 
SELECT 'Finalizado' WHERE NOT EXISTS (SELECT 1 FROM estados_proyecto WHERE nombre = 'Finalizado');

-- Categorías
INSERT INTO categorias (nombre) 
SELECT 'Desarrollo' WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Desarrollo');
INSERT INTO categorias (nombre) 
SELECT 'Infraestructura' WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Infraestructura');
INSERT INTO categorias (nombre) 
SELECT 'Consultoría' WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Consultoría');

-- Clientes de ejemplo
INSERT INTO clientes (nombre_empresa, rut_empresa, contacto, email_contacto)
SELECT 'Empresa Demo 1', '76.000.001-1', 'Juan Pérez', 'juan@demo1.cl'
WHERE NOT EXISTS (SELECT 1 FROM clientes WHERE rut_empresa = '76.000.001-1');

INSERT INTO clientes (nombre_empresa, rut_empresa, contacto, email_contacto)
SELECT 'Empresa Demo 2', '76.000.002-2', 'María González', 'maria@demo2.cl'
WHERE NOT EXISTS (SELECT 1 FROM clientes WHERE rut_empresa = '76.000.002-2');