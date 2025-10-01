/* =================================================================
   1. DROP ALL TABLES (IN CORRECT DEPENDENCY ORDER)
   ================================================================= */
IF OBJECT_ID('inventory_usage_log', 'U') IS NOT NULL DROP TABLE inventory_usage_log;
IF OBJECT_ID('inventory_purchases', 'U') IS NOT NULL DROP TABLE inventory_purchases;
IF OBJECT_ID('inventory_items', 'U') IS NOT NULL DROP TABLE inventory_items;
IF OBJECT_ID('inventory_categories', 'U') IS NOT NULL DROP TABLE inventory_categories;
IF OBJECT_ID('suppliers', 'U') IS NOT NULL DROP TABLE suppliers;
IF OBJECT_ID('notifications', 'U') IS NOT NULL DROP TABLE notifications;
IF OBJECT_ID('cart_items', 'U') IS NOT NULL DROP TABLE cart_items;
IF OBJECT_ID('shopping_carts', 'U') IS NOT NULL DROP TABLE shopping_carts;
IF OBJECT_ID('order_items', 'U') IS NOT NULL DROP TABLE order_items;
IF OBJECT_ID('event_bookings', 'U') IS NOT NULL DROP TABLE event_bookings;
IF OBJECT_ID('income', 'U') IS NOT NULL DROP TABLE income;
IF OBJECT_ID('orders', 'U') IS NOT NULL DROP TABLE orders;
IF OBJECT_ID('payments', 'U') IS NOT NULL DROP TABLE payments;
IF OBJECT_ID('reviews', 'U') IS NOT NULL DROP TABLE reviews;
IF OBJECT_ID('catalogue_item_photos', 'U') IS NOT NULL DROP TABLE catalogue_item_photos;
IF OBJECT_ID('catalogue_items', 'U') IS NOT NULL DROP TABLE catalogue_items;
IF OBJECT_ID('categories', 'U') IS NOT NULL DROP TABLE categories;
IF OBJECT_ID('users', 'U') IS NOT NULL DROP TABLE users;
IF OBJECT_ID('roles', 'U') IS NOT NULL DROP TABLE roles;
IF OBJECT_ID('cards', 'U') IS NOT NULL DROP TABLE cards;
GO

/* =================================================================
   2. CREATE ALL TABLES (FINAL SCHEMA)
   ================================================================= */

-- Core User Tables
CREATE TABLE roles (id INT IDENTITY(1,1) PRIMARY KEY, name VARCHAR(50) NOT NULL UNIQUE);
CREATE TABLE users (id BIGINT IDENTITY(1,1) PRIMARY KEY, username VARCHAR(50) NOT NULL UNIQUE, email VARCHAR(100) NOT NULL UNIQUE, password VARCHAR(255) NOT NULL, first_name NVARCHAR(50) NOT NULL, last_name NVARCHAR(50) NOT NULL, address_line1 NVARCHAR(255) NOT NULL, address_line2 NVARCHAR(255), city NVARCHAR(100) NOT NULL, primary_phone_no VARCHAR(20) NOT NULL, secondary_phone_no VARCHAR(20), role_id INT NOT NULL, FOREIGN KEY (role_id) REFERENCES roles(id));
CREATE TABLE cards (id BIGINT IDENTITY(1,1) PRIMARY KEY, card_number VARCHAR(20) NOT NULL UNIQUE, cvc VARCHAR(4) NOT NULL, expiry_month INT NOT NULL, expiry_year INT NOT NULL);
GO

-- Menu & Event Catalogue Tables
CREATE TABLE categories (id BIGINT IDENTITY(1,1) PRIMARY KEY, name NVARCHAR(100) NOT NULL UNIQUE);
CREATE TABLE catalogue_items (id BIGINT IDENTITY(1,1) PRIMARY KEY, name NVARCHAR(255) NOT NULL, description NVARCHAR(MAX), serving_size_person INT NOT NULL DEFAULT 1, price FLOAT NOT NULL, base_price FLOAT NULL, category_id BIGINT, FOREIGN KEY (category_id) REFERENCES categories(id));
CREATE TABLE catalogue_item_photos (item_id BIGINT NOT NULL, photo_url NVARCHAR(255), FOREIGN KEY (item_id) REFERENCES catalogue_items(id) ON DELETE CASCADE);
CREATE TABLE reviews (id BIGINT IDENTITY(1,1) PRIMARY KEY, catalogue_item_id BIGINT NOT NULL, user_id BIGINT NOT NULL, score INT NOT NULL, comment NVARCHAR(MAX), FOREIGN KEY (catalogue_item_id) REFERENCES catalogue_items(id) ON DELETE CASCADE, FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE);
GO

-- Order, Payment, and Cart Tables
CREATE TABLE payments (id BIGINT IDENTITY(1,1) PRIMARY KEY, method VARCHAR(50) NOT NULL, amount FLOAT NOT NULL, payment_date DATETIME NOT NULL DEFAULT GETDATE(), status VARCHAR(50) NOT NULL);
CREATE TABLE orders (id BIGINT IDENTITY(1,1) PRIMARY KEY, user_id BIGINT NOT NULL, payment_id BIGINT UNIQUE, delivery_person_id BIGINT, order_status VARCHAR(50) NOT NULL, order_date DATETIME NOT NULL DEFAULT GETDATE(), delivery_address NVARCHAR(255) NOT NULL, delivery_phone VARCHAR(20) NOT NULL, FOREIGN KEY (user_id) REFERENCES users(id), FOREIGN KEY (payment_id) REFERENCES payments(id), FOREIGN KEY (delivery_person_id) REFERENCES users(id));
CREATE TABLE order_items (id BIGINT IDENTITY(1,1) PRIMARY KEY, order_id BIGINT NOT NULL, catalogue_item_id BIGINT NOT NULL, quantity INT NOT NULL, price_per_item FLOAT NOT NULL, FOREIGN KEY (order_id) REFERENCES orders(id), FOREIGN KEY (catalogue_item_id) REFERENCES catalogue_items(id));
CREATE TABLE shopping_carts (id BIGINT IDENTITY(1,1) PRIMARY KEY, user_id BIGINT NOT NULL UNIQUE, FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE);
CREATE TABLE cart_items (id BIGINT IDENTITY(1,1) PRIMARY KEY, cart_id BIGINT NOT NULL, catalogue_item_id BIGINT NOT NULL, quantity INT NOT NULL, FOREIGN KEY (cart_id) REFERENCES shopping_carts(id) ON DELETE CASCADE, FOREIGN KEY (catalogue_item_id) REFERENCES catalogue_items(id) ON DELETE CASCADE);
GO

-- Event Booking Table
CREATE TABLE event_bookings (id BIGINT IDENTITY(1,1) PRIMARY KEY, user_id BIGINT NOT NULL, catalogue_item_id BIGINT NOT NULL, payment_id BIGINT, event_date_time DATETIME NOT NULL, status VARCHAR(255) NOT NULL, number_of_guests INT NOT NULL, special_requests NVARCHAR(MAX), FOREIGN KEY (user_id) REFERENCES users(id), FOREIGN KEY (catalogue_item_id) REFERENCES catalogue_items(id), FOREIGN KEY (payment_id) REFERENCES payments(id));
GO

-- Other Core Tables
CREATE TABLE income (id BIGINT IDENTITY(1,1) PRIMARY KEY, payment_id BIGINT NOT NULL, amount FLOAT NOT NULL, income_type VARCHAR(50) NOT NULL, income_date DATE NOT NULL, FOREIGN KEY (payment_id) REFERENCES payments(id));
CREATE TABLE notifications (id BIGINT IDENTITY(1,1) PRIMARY KEY, user_id BIGINT NOT NULL, message NVARCHAR(255) NOT NULL, link NVARCHAR(255), is_read BIT NOT NULL DEFAULT 0, created_at DATETIME NOT NULL DEFAULT GETDATE(), FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE);
GO

-- Inventory Tables (Separated)
CREATE TABLE inventory_categories (id BIGINT IDENTITY(1,1) PRIMARY KEY, name NVARCHAR(100) NOT NULL UNIQUE);
CREATE TABLE suppliers (id BIGINT IDENTITY(1,1) PRIMARY KEY, name NVARCHAR(255) NOT NULL UNIQUE, contact_info NVARCHAR(255));
CREATE TABLE inventory_items (id BIGINT IDENTITY(1,1) PRIMARY KEY, name NVARCHAR(255) NOT NULL UNIQUE, category_id BIGINT, measurement_unit NVARCHAR(50), low_stock_threshold FLOAT NOT NULL DEFAULT 0, current_quantity FLOAT NOT NULL DEFAULT 0, FOREIGN KEY (category_id) REFERENCES inventory_categories(id));
CREATE TABLE inventory_purchases (id BIGINT IDENTITY(1,1) PRIMARY KEY, inventory_item_id BIGINT NOT NULL, supplier_id BIGINT, quantity_purchased FLOAT NOT NULL, unit_price FLOAT NOT NULL, purchase_date DATE NOT NULL, expiry_date DATE, FOREIGN KEY (inventory_item_id) REFERENCES inventory_items(id), FOREIGN KEY (supplier_id) REFERENCES suppliers(id));
CREATE TABLE inventory_usage_log (id BIGINT IDENTITY(1,1) PRIMARY KEY, inventory_item_id BIGINT NOT NULL, quantity_used FLOAT NOT NULL, usage_date DATETIME NOT NULL DEFAULT GETDATE(), reason NVARCHAR(255), user_id BIGINT NOT NULL, FOREIGN KEY (inventory_item_id) REFERENCES inventory_items(id), FOREIGN KEY (user_id) REFERENCES users(id));
GO

/* =================================================================
   3. INSERT MOCK DATA
   ================================================================= */
-- Roles
INSERT INTO roles (name) VALUES ('ROLE_CUSTOMER'), ('ROLE_MANAGER'), ('ROLE_KITCHEN_SUPERVISOR'), ('ROLE_KITCHEN_STAFF'), ('ROLE_DELIVERY_PERSON'), ('ROLE_EVENT_COORDINATOR');
GO
-- Users
INSERT INTO users (username, email, password, first_name, last_name, address_line1, city, primary_phone_no, role_id) VALUES
('manager_user', 'manager@goldenflame.com', 'manager123', 'Manager', 'Account', '123 Restaurant St', 'Malabe', '0112233445', (SELECT id FROM roles WHERE name = 'ROLE_MANAGER')),
('customer_user', 'customer@example.com', 'customer123', 'John', 'Doe', '456 Customer Ave', 'Colombo', '0771234567', (SELECT id FROM roles WHERE name = 'ROLE_CUSTOMER')),
('delivery_user1', 'delivery1@goldenflame.com', 'delivery123', 'Delivery', 'One', '789 Rider Road', 'Kaduwela', '0711111111', (SELECT id FROM roles WHERE name = 'ROLE_DELIVERY_PERSON')),
('kitchen_user', 'kitchen@goldenflame.com', 'kitchen123', 'Kitchen', 'Supervisor', '123 Restaurant St', 'Malabe', '0112233446', (SELECT id FROM roles WHERE name = 'ROLE_KITCHEN_SUPERVISOR')),
('kitchen_staff_user', 'staff@goldenflame.com', 'staff123', 'Kitchen', 'Staff', '123 Restaurant St', 'Malabe', '0112233447', (SELECT id FROM roles WHERE name = 'ROLE_KITCHEN_STAFF')),
('event_coord', 'event@goldenflame.com', 'event123', 'Emily', 'Carter', '123 Restaurant St', 'Malabe', '0112233448', (SELECT id FROM roles WHERE name = 'ROLE_EVENT_COORDINATOR'));
GO
-- Menu & Event Categories
INSERT INTO categories (name) VALUES ('Appetizer'), ('Soup'), ('Salad'), ('Main Course'), ('Dessert'), ('Beverage'), ('Milestone Parties'), ('Family & Life Events');
GO
-- Inventory Categories
INSERT INTO inventory_categories (name) VALUES ('Produce'), ('Dry Goods'), ('Meats'), ('Dairy');
GO
-- Card
INSERT INTO cards (card_number, cvc, expiry_month, expiry_year) VALUES ('5040705240328972', '123', 12, 2028);
GO