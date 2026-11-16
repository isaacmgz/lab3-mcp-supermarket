INSERT INTO category (name, description) VALUES
    ('Lácteos y huevos', 'Leche, quesos, yogur, mantequilla y huevos'),
    ('Panadería', 'Pan, arepas y productos horneados'),
    ('Frutas y verduras', 'Productos frescos'),
    ('Carnes y pollo', 'Res, cerdo y pollo'),
    ('Despensa', 'Arroz, granos, pasta, aceite y enlatados'),
    ('Bebidas', 'Café, jugos, gaseosas y agua'),
    ('Snacks y dulces', 'Papas fritas, galletas y dulces'),
    ('Aseo y hogar', 'Detergentes, limpiadores y papel');

INSERT INTO product (sku, name, description, unit, price, stock, category_id) VALUES
    ('LAC-001', 'Leche entera', 'Leche entera ultrapasteurizada en bolsa', '1 L', 4900, 120, (SELECT id FROM category WHERE name = 'Lácteos y huevos')),
    ('LAC-002', 'Leche deslactosada', 'Leche deslactosada ultrapasteurizada', '1 L', 5200, 60, (SELECT id FROM category WHERE name = 'Lácteos y huevos')),
    ('LAC-003', 'Huevos rojos AA x12', 'Bandeja de 12 huevos rojos tamaño AA', '12 unidades', 9800, 80, (SELECT id FROM category WHERE name = 'Lácteos y huevos')),
    ('LAC-004', 'Huevos rojos AA x30', 'Bandeja de 30 huevos rojos tamaño AA', '30 unidades', 21900, 40, (SELECT id FROM category WHERE name = 'Lácteos y huevos')),
    ('LAC-005', 'Queso campesino', 'Queso campesino fresco', '500 g', 11500, 35, (SELECT id FROM category WHERE name = 'Lácteos y huevos')),
    ('LAC-006', 'Queso mozzarella tajado', 'Queso mozzarella en tajadas', '250 g', 9900, 30, (SELECT id FROM category WHERE name = 'Lácteos y huevos')),
    ('LAC-007', 'Yogur de fresa', 'Yogur bebible sabor a fresa', '1 L', 8700, 45, (SELECT id FROM category WHERE name = 'Lácteos y huevos')),
    ('LAC-008', 'Mantequilla con sal', 'Barra de mantequilla con sal', '250 g', 8900, 25, (SELECT id FROM category WHERE name = 'Lácteos y huevos')),

    ('PAN-001', 'Pan tajado blanco', 'Pan blanco tajado para sándwich', '600 g', 7900, 50, (SELECT id FROM category WHERE name = 'Panadería')),
    ('PAN-002', 'Pan tajado integral', 'Pan integral tajado', '600 g', 8900, 40, (SELECT id FROM category WHERE name = 'Panadería')),
    ('PAN-003', 'Pandebono', 'Pandebono recién horneado, paquete de 6', '6 unidades', 9000, 25, (SELECT id FROM category WHERE name = 'Panadería')),
    ('PAN-004', 'Arepas de maíz blanco', 'Arepas de maíz blanco, paquete de 10', '10 unidades', 6500, 60, (SELECT id FROM category WHERE name = 'Panadería')),
    ('PAN-005', 'Croissant', 'Croissant de mantequilla', '1 unidad', 3500, 30, (SELECT id FROM category WHERE name = 'Panadería')),

    ('FRU-001', 'Banano', 'Banano maduro', '1 kg', 3800, 100, (SELECT id FROM category WHERE name = 'Frutas y verduras')),
    ('FRU-002', 'Aguacate Hass', 'Aguacate Hass', '1 unidad', 3200, 70, (SELECT id FROM category WHERE name = 'Frutas y verduras')),
    ('FRU-003', 'Tomate chonto', 'Tomate chonto', '1 kg', 4600, 90, (SELECT id FROM category WHERE name = 'Frutas y verduras')),
    ('FRU-004', 'Cebolla blanca', 'Cebolla blanca cabezona', '1 kg', 3900, 80, (SELECT id FROM category WHERE name = 'Frutas y verduras')),
    ('FRU-005', 'Papa pastusa', 'Papa pastusa', '2.5 kg', 8500, 60, (SELECT id FROM category WHERE name = 'Frutas y verduras')),
    ('FRU-006', 'Lulo', 'Lulo fresco', '1 kg', 7900, 40, (SELECT id FROM category WHERE name = 'Frutas y verduras')),
    ('FRU-007', 'Limón Tahití', 'Limón Tahití', '1 kg', 4200, 55, (SELECT id FROM category WHERE name = 'Frutas y verduras')),
    ('FRU-008', 'Plátano verde', 'Plátano verde', '1 kg', 3500, 75, (SELECT id FROM category WHERE name = 'Frutas y verduras')),

    ('CAR-001', 'Pechuga de pollo', 'Pechuga de pollo deshuesada', '1 kg', 19900, 40, (SELECT id FROM category WHERE name = 'Carnes y pollo')),
    ('CAR-002', 'Carne molida de res', 'Carne molida de res magra', '500 g', 13900, 35, (SELECT id FROM category WHERE name = 'Carnes y pollo')),
    ('CAR-003', 'Chuleta de cerdo', 'Chuleta de lomo de cerdo', '1 kg', 22500, 20, (SELECT id FROM category WHERE name = 'Carnes y pollo')),
    ('CAR-004', 'Chorizo antioqueño', 'Chorizo antioqueño, paquete de 5', '500 g', 12900, 30, (SELECT id FROM category WHERE name = 'Carnes y pollo')),

    ('DES-001', 'Arroz blanco', 'Arroz blanco de grano largo', '1 kg', 4500, 150, (SELECT id FROM category WHERE name = 'Despensa')),
    ('DES-002', 'Fríjol cargamanto', 'Fríjol cargamanto rojo', '500 g', 6900, 70, (SELECT id FROM category WHERE name = 'Despensa')),
    ('DES-003', 'Lentejas', 'Lentejas secas', '500 g', 4200, 65, (SELECT id FROM category WHERE name = 'Despensa')),
    ('DES-004', 'Espagueti', 'Pasta espagueti de trigo durum', '500 g', 3900, 90, (SELECT id FROM category WHERE name = 'Despensa')),
    ('DES-005', 'Aceite de girasol', 'Aceite de girasol para cocinar', '1 L', 11900, 50, (SELECT id FROM category WHERE name = 'Despensa')),
    ('DES-006', 'Panela', 'Panela en bloque', '500 g', 3600, 80, (SELECT id FROM category WHERE name = 'Despensa')),
    ('DES-007', 'Atún en agua', 'Atún en lomitos en agua', '160 g', 6900, 100, (SELECT id FROM category WHERE name = 'Despensa')),
    ('DES-008', 'Azúcar blanca', 'Azúcar blanca refinada', '1 kg', 4800, 85, (SELECT id FROM category WHERE name = 'Despensa')),

    ('BEB-001', 'Café molido', 'Café colombiano molido, tostión media', '500 g', 18900, 45, (SELECT id FROM category WHERE name = 'Bebidas')),
    ('BEB-002', 'Jugo de naranja', 'Jugo de naranja natural', '1 L', 8500, 40, (SELECT id FROM category WHERE name = 'Bebidas')),
    ('BEB-003', 'Agua mineral', 'Agua mineral sin gas', '600 ml', 2200, 200, (SELECT id FROM category WHERE name = 'Bebidas')),
    ('BEB-004', 'Gaseosa cola', 'Gaseosa sabor cola', '1.5 L', 5900, 90, (SELECT id FROM category WHERE name = 'Bebidas')),

    ('SNA-001', 'Papas fritas', 'Papas fritas clásicas con sal', '150 g', 6500, 60, (SELECT id FROM category WHERE name = 'Snacks y dulces')),
    ('SNA-002', 'Galletas con chips de chocolate', 'Galletas con chips de chocolate', '300 g', 7200, 50, (SELECT id FROM category WHERE name = 'Snacks y dulces')),
    ('SNA-003', 'Bocadillo veleño', 'Bocadillo de guayaba, paquete de 12', '12 unidades', 8900, 35, (SELECT id FROM category WHERE name = 'Snacks y dulces')),

    ('ASE-001', 'Detergente en polvo', 'Detergente en polvo para ropa', '2 kg', 24900, 30, (SELECT id FROM category WHERE name = 'Aseo y hogar')),
    ('ASE-002', 'Lavaloza líquido', 'Lavaloza líquido aroma limón', '750 ml', 7900, 45, (SELECT id FROM category WHERE name = 'Aseo y hogar')),
    ('ASE-003', 'Papel higiénico', 'Papel higiénico doble hoja, 12 rollos', '12 rollos', 21900, 40, (SELECT id FROM category WHERE name = 'Aseo y hogar')),
    ('ASE-004', 'Blanqueador', 'Blanqueador para el hogar', '1 L', 4900, 50, (SELECT id FROM category WHERE name = 'Aseo y hogar'));

INSERT INTO customer (full_name, phone, email, address, neighborhood, city) VALUES
    ('Laura Gómez', '3001234567', 'laura.gomez@example.com', 'Calle 50 # 40-20 Apto 301', 'Boston', 'Medellín'),
    ('Carlos Restrepo', '3109876543', 'carlos.restrepo@example.com', 'Carrera 70 # 44-10', 'Laureles', 'Medellín');
