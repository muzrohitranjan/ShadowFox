-- Demo data for Online Banking System
-- Run this after the application starts to populate with sample data

-- Insert demo users (passwords are 'password123' encrypted with BCrypt)
INSERT INTO users (username, email, password, role, created_at) VALUES
('john_doe', 'john@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P2.nRuvO.H.6ba', 'USER', NOW()),
('jane_smith', 'jane@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P2.nRuvO.H.6ba', 'USER', NOW()),
('admin_user', 'admin@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P2.nRuvO.H.6ba', 'ADMIN', NOW());

-- Insert demo accounts
INSERT INTO accounts (account_number, account_type, balance, user_id, created_at) VALUES
('ACC0000000001', 'SAVINGS', 5000.00, 1, NOW()),
('ACC0000000002', 'CURRENT', 2500.50, 1, NOW()),
('ACC0000000003', 'SAVINGS', 10000.00, 2, NOW()),
('ACC0000000004', 'FIXED_DEPOSIT', 50000.00, 2, NOW());

-- Insert demo transactions
INSERT INTO transactions (from_account_id, to_account_id, amount, type, description, timestamp, status) VALUES
(1, 3, 500.00, 'TRANSFER', 'Monthly transfer', DATE_SUB(NOW(), INTERVAL 5 DAY), 'COMPLETED'),
(3, 1, 200.00, 'TRANSFER', 'Refund payment', DATE_SUB(NOW(), INTERVAL 3 DAY), 'COMPLETED'),
(2, 4, 1000.00, 'TRANSFER', 'Investment transfer', DATE_SUB(NOW(), INTERVAL 1 DAY), 'COMPLETED'),
(1, 1, 1000.00, 'DEPOSIT', 'Salary deposit', NOW(), 'COMPLETED');