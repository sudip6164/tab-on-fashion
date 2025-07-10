-- Use the tab_on_fashion database
USE tab_on_fashion;

-- Insert more sample products for better testing
INSERT INTO products (name, description, price, category, size, color, fabric_type, sustainability_score, stock_quantity) VALUES 
('Sustainable Yoga Pants', 'Comfortable yoga pants made from recycled plastic bottles', 45.99, 'Bottoms', 'M', 'Black', 'Recycled Polyester', 9.0, 25),
('Fair Trade Cotton Sweater', 'Cozy sweater made from fair trade organic cotton', 89.99, 'Tops', 'L', 'Cream', 'Organic Cotton', 9.3, 15),
('Eco-Friendly Denim Jacket', 'Classic denim jacket made with sustainable practices', 95.99, 'Outerwear', 'M', 'Blue', 'Organic Denim', 8.7, 20),
('Bamboo Fiber Socks', 'Soft and breathable socks made from bamboo fiber', 12.99, 'Accessories', 'One Size', 'White', 'Bamboo Fiber', 9.1, 100),
('Recycled Wool Scarf', 'Warm scarf made from recycled wool fibers', 35.99, 'Accessories', 'One Size', 'Gray', 'Recycled Wool', 8.9, 30),
('Organic Cotton Pajama Set', 'Comfortable pajama set made from certified organic cotton', 65.99, 'Sleepwear', 'M', 'Pink', 'Organic Cotton', 9.2, 40),
('Sustainable Running Shoes', 'Performance running shoes made from eco-friendly materials', 135.00, 'Shoes', '8', 'Green', 'Recycled Materials', 8.5, 18),
('Hemp Canvas Tote Bag', 'Durable tote bag made from hemp canvas', 25.99, 'Accessories', 'One Size', 'Natural', 'Hemp', 9.4, 50),
('Tencel Blend Blouse', 'Elegant blouse made from sustainable Tencel fiber', 75.99, 'Tops', 'S', 'Blue', 'Tencel', 8.8, 22),
('Recycled Cashmere Cardigan', 'Luxurious cardigan made from recycled cashmere', 125.99, 'Tops', 'L', 'Beige', 'Recycled Cashmere', 9.0, 12);

-- Create sample carts for existing users if they don't exist
INSERT IGNORE INTO carts (user_id, total_amount) VALUES 
(1, 0.00);  -- admin user

-- Add some sample cart items for testing
INSERT INTO cart_items (cart_id, product_id, quantity, unit_price) VALUES 
(1, 1, 2, 29.99),  -- admin cart: 2x Organic Cotton T-Shirt
(1, 3, 1, 89.99);  -- admin cart: 1x Bamboo Fiber Dress

-- Update cart total
UPDATE carts SET total_amount = 149.97 WHERE id = 1;

-- Create a sample order
INSERT INTO orders (user_id, total_amount, shipping_address, status, payment_status) VALUES 
(2, 79.99, '123 Main Street, Kathmandu, Nepal', 'DELIVERED', 'PAID');

-- Add order items
INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES 
(1, 2, 1, 79.99);  -- john_doe order: 1x Recycled Denim Jeans
