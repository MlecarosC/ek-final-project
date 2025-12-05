USE `eureka-project-db`;

-- Insertar datos mock en Departments
INSERT INTO departments (name) VALUES
('Ventas'),
('Recursos Humanos'),
('Contabilidad');

-- Insertar datos mock en Users
INSERT INTO users (name, email, department_id) VALUES
('Juan Pérez', 'juan.perez@example.com', 1),
('María González', 'maria.gonzalez@example.com', 1),
('Pedro Sánchez', 'pedro.sanchez@example.com', 2),
('Ana Martínez', 'ana.martinez@example.com', 2),
('Luis Rodríguez', 'luis.rodriguez@example.com', 3),
('Sofía López', 'sofia.lopez@example.com', 3),
('Carlos Fernández', 'carlos.fernandez@example.com', 1),
('Lucía Ramírez', 'lucia.ramirez@example.com', 2),
('Diego Torres', 'diego.torres@example.com', 3);
