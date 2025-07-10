-- Use the tab_on_fashion database
USE tab_on_fashion;

-- Insert sample admin user (password: admin123)
INSERT INTO users (username, email, password, role) VALUES 
('admin', 'admin@tabonfashion.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM5lIaU0VPDmBOmZKHze', 'ADMIN');

-- Insert sample regular users (password: user123)
INSERT INTO users (username, email, password, role) VALUES 
('john_doe', 'user@gmail.com', '$2a$10$zkwjwbI3.37DWmMdJkcsS.GZNeuPSqK1uRc2xaHPKXwEk3frDF8W.', 'USER'),
('jane_smith', 'jane@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM5lIaU0VPDmBOmZKHze', 'USER'),
('eco_lover', 'eco@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM5lIaU0VPDmBOmZKHze', 'USER');

-- Insert sample products
INSERT INTO products (name, description, price, category, size, color, fabric_type, sustainability_score, stock_quantity) VALUES 
('Organic Cotton T-Shirt', 'Comfortable organic cotton t-shirt made from sustainable materials', 29.99, 'Tops', 'M', 'White', 'Organic Cotton', 9.5, 50),
('Recycled Denim Jeans', 'Stylish jeans made from recycled denim with eco-friendly processing', 79.99, 'Bottoms', 'L', 'Blue', 'Recycled Denim', 8.8, 30),
('Bamboo Fiber Dress', 'Elegant dress made from sustainable bamboo fiber', 89.99, 'Dresses', 'S', 'Green', 'Bamboo Fiber', 9.2, 25),
('Hemp Blend Hoodie', 'Cozy hoodie made from hemp and organic cotton blend', 65.99, 'Tops', 'L', 'Gray', 'Hemp Blend', 8.5, 40),
('Eco-Friendly Sneakers', 'Sustainable sneakers made from recycled materials', 120.00, 'Shoes', '9', 'Black', 'Recycled Materials', 8.0, 20),
('Linen Summer Shirt', 'Breathable linen shirt perfect for summer', 55.99, 'Tops', 'M', 'Beige', 'Linen', 7.8, 35);

-- Create carts for sample users
INSERT INTO carts (user_id, total_amount) VALUES 
(2, 0.00),  -- john_doe
(3, 0.00),  -- jane_smith
(4, 0.00);  -- eco_lover
