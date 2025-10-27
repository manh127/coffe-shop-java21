-- Seed admin user
-- Password: Admin#123 (BCrypt hash with strength 12)
INSERT INTO users (id, email, password_hash, enabled, created_at)
VALUES (
    'a0000000-0000-0000-0000-000000000001',
    'admin@local',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5XP.Y.V.zYwG.',
    TRUE,
    NOW()
);

INSERT INTO user_roles (user_id, role)
VALUES ('a0000000-0000-0000-0000-000000000001', 'ADMIN');

INSERT INTO user_roles (user_id, role)
VALUES ('a0000000-0000-0000-0000-000000000001', 'USER');

-- Seed regular user
-- Password: User#123 (BCrypt hash with strength 12)
INSERT INTO users (id, email, password_hash, enabled, created_at)
VALUES (
    'b0000000-0000-0000-0000-000000000002',
    'user@local',
    '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.',
    TRUE,
    NOW()
);

INSERT INTO user_roles (user_id, role)
VALUES ('b0000000-0000-0000-0000-000000000002', 'USER');

-- Seed sample products
INSERT INTO products (id, name, sku, price, stock_quantity, created_at, updated_at)
VALUES
    ('c0000000-0000-0000-0000-000000000001', 'Espresso', 'COFFEE-ESP-001', 2.50, 100, NOW(), NOW()),
    ('c0000000-0000-0000-0000-000000000002', 'Cappuccino', 'COFFEE-CAP-001', 3.50, 80, NOW(), NOW()),
    ('c0000000-0000-0000-0000-000000000003', 'Latte', 'COFFEE-LAT-001', 4.00, 90, NOW(), NOW()),
    ('c0000000-0000-0000-0000-000000000004', 'Americano', 'COFFEE-AME-001', 2.75, 120, NOW(), NOW()),
    ('c0000000-0000-0000-0000-000000000005', 'Mocha', 'COFFEE-MOC-001', 4.50, 60, NOW(), NOW()),
    ('c0000000-0000-0000-0000-000000000006', 'Croissant', 'PASTRY-CRO-001', 3.00, 50, NOW(), NOW()),
    ('c0000000-0000-0000-0000-000000000007', 'Blueberry Muffin', 'PASTRY-MUF-001', 3.25, 40, NOW(), NOW()),
    ('c0000000-0000-0000-0000-000000000008', 'Chocolate Cookie', 'PASTRY-COO-001', 2.00, 70, NOW(), NOW());



