-- =================================================================
-- 1. DROP ALL EXISTING TABLES (IN CORRECT DEPENDENCY ORDER)
-- =================================================================
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS cart_items;
DROP TABLE IF EXISTS shopping_carts;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS income;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS catalogue_item_photos;
DROP TABLE IF EXISTS catalogue_items;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS cards;


-- =================================================================
-- 2. CREATE ALL TABLES (FINAL SCHEMA)
-- =================================================================

-- USER AND ROLE TABLES
CREATE TABLE roles (
    id INT PRIMARY KEY IDENTITY(1,1),
    name VARCHAR(50) NOT NULL UNIQUE
);
CREATE TABLE users (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name NVARCHAR(50) NOT NULL,
    last_name NVARCHAR(50) NOT NULL,
    address_line1 NVARCHAR(255) NOT NULL,
    address_line2 NVARCHAR(255),
    city NVARCHAR(100) NOT NULL,
    primary_phone_no VARCHAR(20) NOT NULL,
    secondary_phone_no VARCHAR(20),
    role_id INT NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- CATALOGUE, PHOTOS, AND REVIEW TABLES
CREATE TABLE catalogue_items (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(255) NOT NULL,
    description NVARCHAR(MAX),
    serving_size_person INT NOT NULL DEFAULT 1,
    item_type NVARCHAR(50) NOT NULL,
    price FLOAT NOT NULL
);
CREATE TABLE catalogue_item_photos (
    item_id BIGINT NOT NULL,
    photo_url NVARCHAR(255),
    FOREIGN KEY (item_id) REFERENCES catalogue_items(id) ON DELETE CASCADE
);
CREATE TABLE reviews (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    catalogue_item_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    score INT NOT NULL,
    comment NVARCHAR(MAX),
    FOREIGN KEY (catalogue_item_id) REFERENCES catalogue_items(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- PAYMENT AND ORDER TABLES
CREATE TABLE payments (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    method VARCHAR(50) NOT NULL,
    amount FLOAT NOT NULL,
    payment_date DATETIME NOT NULL DEFAULT GETDATE(),
    status VARCHAR(50) NOT NULL
);
CREATE TABLE orders (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user_id BIGINT NOT NULL,
    payment_id BIGINT UNIQUE,
    delivery_person_id BIGINT,
    order_status VARCHAR(50) NOT NULL,
    order_date DATETIME NOT NULL DEFAULT GETDATE(),
    delivery_address NVARCHAR(255) NOT NULL,
    delivery_phone VARCHAR(20) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (payment_id) REFERENCES payments(id),
    FOREIGN KEY (delivery_person_id) REFERENCES users(id)
);
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    order_id BIGINT NOT NULL,
    catalogue_item_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price_per_item FLOAT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (catalogue_item_id) REFERENCES catalogue_items(id)
);

-- INCOME, CARD, CART, AND NOTIFICATION TABLES
CREATE TABLE income (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    payment_id BIGINT NOT NULL,
    amount FLOAT NOT NULL,
    income_type VARCHAR(50) NOT NULL,
    income_date DATE NOT NULL,
    FOREIGN KEY (payment_id) REFERENCES payments(id)
);
CREATE TABLE cards (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    card_number VARCHAR(20) NOT NULL UNIQUE,
    cvc VARCHAR(4) NOT NULL,
    expiry_month INT NOT NULL,
    expiry_year INT NOT NULL
);
CREATE TABLE shopping_carts (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user_id BIGINT NOT NULL UNIQUE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
CREATE TABLE cart_items (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    cart_id BIGINT NOT NULL,
    catalogue_item_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (cart_id) REFERENCES shopping_carts(id) ON DELETE CASCADE,
    FOREIGN KEY (catalogue_item_id) REFERENCES catalogue_items(id) ON DELETE CASCADE
);
CREATE TABLE notifications (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user_id BIGINT NOT NULL,
    message NVARCHAR(255) NOT NULL,
    link NVARCHAR(255),
    is_read BIT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);


-- =================================================================
-- 3. INSERT MOCK DATA
-- =================================================================
INSERT INTO roles (name) VALUES ('ROLE_CUSTOMER'), ('ROLE_MANAGER'), ('ROLE_KITCHEN_SUPERVISOR'), ('ROLE_KITCHEN_STAFF'), ('ROLE_DELIVERY_PERSON');
INSERT INTO users (username, email, password, first_name, last_name, address_line1, city, primary_phone_no, role_id) VALUES
('manager_user', 'manager@goldenflame.com', 'manager123', 'Manager', 'Account', '123 Restaurant St', 'Malabe', '0112233445', (SELECT id FROM roles WHERE name = 'ROLE_MANAGER')),
('customer_user', 'customer@example.com', 'customer123', 'John', 'Doe', '456 Customer Ave', 'Colombo', '0771234567', (SELECT id FROM roles WHERE name = 'ROLE_CUSTOMER')),
('delivery_user1', 'delivery1@goldenflame.com', 'delivery123', 'Delivery', 'One', '789 Rider Road', 'Kaduwela', '0711111111', (SELECT id FROM roles WHERE name = 'ROLE_DELIVERY_PERSON')),
('kitchen_user', 'kitchen@goldenflame.com', 'kitchen123', 'Kitchen', 'Supervisor', '123 Restaurant St', 'Malabe', '0112233446', (SELECT id FROM roles WHERE name = 'ROLE_KITCHEN_SUPERVISOR')),
('kitchen_staff_user', 'staff@goldenflame.com', 'staff123', 'Kitchen', 'Staff', '123 Restaurant St', 'Malabe', '0112233447', (SELECT id FROM roles WHERE name = 'ROLE_KITCHEN_STAFF'));

INSERT INTO catalogue_items (name, description, serving_size_person, item_type, price) VALUES ('Bruschetta', 'Toasted bread with fresh tomatoes.', 1, 'APPETIZER', 10.00);
DECLARE @BruschettaId BIGINT = SCOPE_IDENTITY();
INSERT INTO catalogue_item_photos (item_id, photo_url) VALUES (@BruschettaId, '/images/bruschetta.jpg');
INSERT INTO reviews (catalogue_item_id, user_id, score, comment) VALUES (@BruschettaId, (SELECT id FROM users WHERE username = 'customer_user'), 5, 'Loved it!');

INSERT INTO cards (card_number, cvc, expiry_month, expiry_year) VALUES ('5040705240328972', '123', 12, 2028);