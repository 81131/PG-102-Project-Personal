-- Create the roles table to store user roles
CREATE TABLE roles (
    id INT PRIMARY KEY IDENTITY(1,1),
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Create the users table
CREATE TABLE users (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role_id INT,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Insert the roles defined in your project documents
INSERT INTO roles (name) VALUES
('ROLE_CUSTOMER'),
('ROLE_MANAGER'),
('ROLE_SYSTEM_ADMINISTRATOR'),
('ROLE_EVENT_COORDINATOR'),
('ROLE_KITCHEN_SUPERVISOR'),
('ROLE_DELIVERY_PERSON');

-- Insert some mock users for testing
INSERT INTO users (username, email, password, role_id) VALUES
('manager_user', 'manager@goldenflame.com', 'manager123', (SELECT id FROM roles WHERE name = 'ROLE_MANAGER')),
('customer_user', 'customer@example.com', 'customer123', (SELECT id FROM roles WHERE name = 'ROLE_CUSTOMER')),
('kitchen_user', 'kitchen@goldenflame.com', 'kitchen123', (SELECT id FROM roles WHERE name = 'ROLE_KITCHEN_SUPERVISOR'));



select * FROM users;

SELECT * FROM roles;