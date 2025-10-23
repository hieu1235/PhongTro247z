-- PhongTro247 Database Schema - PostgreSQL Version
-- Generated on October 23, 2025

-- Note: Run this on PostgreSQL database
-- The database should already be created in Render

-- ===========================================
-- TABLE CREATION STATEMENTS
-- ===========================================

CREATE TABLE roles (
    role_id SERIAL PRIMARY KEY,
    role_name VARCHAR(20) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(15),
    role_id INT NOT NULL DEFAULT 2,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    verification_code VARCHAR(10),
    reset_token VARCHAR(255),
    reset_token_expires TIMESTAMP,
    coins INT NOT NULL DEFAULT 0,
    is_pro BOOLEAN NOT NULL DEFAULT FALSE,
    pro_expires_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

CREATE TABLE post_status (
    status_id SERIAL PRIMARY KEY,
    status_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE posts (
    post_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    address VARCHAR(255) NOT NULL,
    lat DECIMAL(10, 8) NULL,
    lng DECIMAL(11, 8) NULL,
    price DECIMAL(12, 2) NOT NULL,
    area DECIMAL(10, 2) NOT NULL,
    status_id INT NOT NULL,
    facebook_post_id VARCHAR(50) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    scheduled_at TIMESTAMP NULL,
    auto_publish BOOLEAN NULL,
    published_at TIMESTAMP NULL,
    city VARCHAR(100) NULL,
    district VARCHAR(100) NULL,
    ward VARCHAR(100) NULL,
    FOREIGN KEY (status_id) REFERENCES post_status(status_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE post_images (
    image_id SERIAL PRIMARY KEY,
    post_id INT NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    is_thumbnail BOOLEAN NULL,
    FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE
);

CREATE TABLE facebook_settings (
    setting_id SERIAL PRIMARY KEY,
    page_id VARCHAR(100) NOT NULL,
    page_name VARCHAR(255) NULL,
    access_token TEXT NOT NULL,
    user_id INT NOT NULL,
    is_active BOOLEAN NULL,
    auto_post BOOLEAN NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_default BOOLEAN NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    UNIQUE(page_id)
);

CREATE TABLE post_facebook_pages (
    id SERIAL PRIMARY KEY,
    post_id INT NOT NULL,
    page_id VARCHAR(100) NOT NULL,
    facebook_post_id VARCHAR(100) NULL,
    posted_at TIMESTAMP NULL,
    status VARCHAR(20) NULL,
    error_message VARCHAR(500) NULL,
    FOREIGN KEY (page_id) REFERENCES facebook_settings(page_id),
    FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE,
    UNIQUE(post_id, page_id)
);

CREATE TABLE payment_orders (
    order_id SERIAL PRIMARY KEY,
    order_code VARCHAR(50) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    coins INT NULL,
    currency VARCHAR(3) NULL,
    payment_method VARCHAR(20) NULL,
    description VARCHAR(255) NULL,
    status VARCHAR(20) NULL,
    momo_transaction_id VARCHAR(100) NULL,
    momo_order_id VARCHAR(100) NULL,
    gateway_transaction_id VARCHAR(100) NULL,
    callback_data TEXT NULL,
    expires_at TIMESTAMP NOT NULL,
    paid_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    coins_amount INT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE momo_transactions (
    transaction_id SERIAL PRIMARY KEY,
    order_code VARCHAR(50) NOT NULL,
    partner_code VARCHAR(50) NULL,
    request_id VARCHAR(100) NULL,
    momo_trans_id VARCHAR(100) NULL,
    result_code VARCHAR(10) NULL,
    message VARCHAR(500) NULL,
    pay_type VARCHAR(50) NULL,
    response_time VARCHAR(50) NULL,
    extra_data TEXT NULL,
    signature VARCHAR(500) NULL,
    ipn_data TEXT NULL,
    processed BOOLEAN NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE coin_transactions (
    transaction_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    description VARCHAR(255) NULL,
    reference_id INT NULL,
    reference_type VARCHAR(20) NULL,
    balance_before DECIMAL(10, 2) NOT NULL,
    balance_after DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE user_balance (
    balance_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    total_coins DECIMAL(10, 2) NULL,
    spent_coins DECIMAL(10, 2) NULL,
    available_coins DECIMAL(10, 2) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE(user_id)
);

CREATE TABLE scheduled_posts_log (
    id SERIAL PRIMARY KEY,
    post_id INT NOT NULL,
    user_id INT NOT NULL,
    scheduled_time TIMESTAMP NOT NULL,
    actual_publish_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    error_message VARCHAR(500) NULL,
    facebook_pages_count INT NULL,
    facebook_success_count INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts(post_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE subscription_plans (
    plan_id SERIAL PRIMARY KEY,
    plan_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255) NULL,
    price_in_coins DECIMAL(10, 2) NOT NULL,
    price_in_vnd DECIMAL(12, 2) NOT NULL,
    daily_post_limit INT NOT NULL,
    facebook_posting BOOLEAN NULL,
    duration_days INT NOT NULL,
    is_active BOOLEAN NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_subscriptions (
    subscription_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    plan_id INT NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    is_active BOOLEAN NULL,
    auto_renew BOOLEAN NULL,
    purchase_price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (plan_id) REFERENCES subscription_plans(plan_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE daily_post_limits (
    limit_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    post_date DATE NOT NULL,
    homepage_posts INT NULL,
    facebook_posts INT NULL,
    total_posts INT NULL,
    max_allowed_posts INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE(user_id, post_date)
);

-- ===========================================
-- INDEXES
-- ===========================================

CREATE INDEX idx_posts_status ON posts(status_id);
CREATE INDEX idx_posts_user ON posts(user_id);
CREATE INDEX idx_posts_created ON posts(created_at);
CREATE INDEX idx_posts_scheduled ON posts(scheduled_at, auto_publish, status_id);
CREATE INDEX idx_posts_published ON posts(published_at, status_id);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_pro_status ON users(is_pro, pro_expires_at);
CREATE INDEX idx_payment_orders_user ON payment_orders(user_id);
CREATE INDEX idx_payment_orders_status ON payment_orders(status);
CREATE INDEX idx_payment_orders_expires ON payment_orders(expires_at);
CREATE INDEX idx_momo_trans_order ON momo_transactions(order_code);
CREATE INDEX idx_momo_trans_id ON momo_transactions(momo_trans_id);
CREATE INDEX idx_coin_trans_user ON coin_transactions(user_id);
CREATE INDEX idx_coin_trans_type ON coin_transactions(transaction_type);
CREATE INDEX idx_scheduled_posts_log_post ON scheduled_posts_log(post_id);
CREATE INDEX idx_scheduled_posts_log_user ON scheduled_posts_log(user_id);
CREATE INDEX idx_scheduled_posts_log_time ON scheduled_posts_log(scheduled_time);
CREATE INDEX idx_user_balance_user ON user_balance(user_id);

-- ===========================================
-- SAMPLE DATA INSERTION
-- ===========================================

-- Insert roles
INSERT INTO roles (role_name, description) VALUES
('admin', 'Administrator with full access'),
('user', 'Regular user'),
('moderator', 'Content moderator');

-- Insert post status
INSERT INTO post_status (status_name, description) VALUES
('draft', 'Post is in draft mode'),
('published', 'Post has been published'),
('scheduled', 'Post is scheduled for future publishing'),
('expired', 'Post has expired');

-- Insert subscription plans
INSERT INTO subscription_plans (plan_name, description, price_in_coins, price_in_vnd, daily_post_limit, facebook_posting, duration_days) VALUES
('Basic', 'Basic plan with limited features', 100.00, 50000.00, 5, false, 30),
('Pro', 'Professional plan with more features', 500.00, 200000.00, 20, true, 30),
('Premium', 'Premium plan with all features', 1000.00, 500000.00, 50, true, 30);

-- Insert sample admin user (password should be hashed in production)
INSERT INTO users (username, password, full_name, email, phone, role_id, is_verified) VALUES
('admin', '$2a$10$hashedpasswordhere', 'Administrator', 'admin@phongtro247.com', '0123456789', 1, true);

-- Insert sample regular user
INSERT INTO users (username, password, full_name, email, phone, role_id, is_verified) VALUES
('user1', '$2a$10$hashedpasswordhere', 'Nguyễn Văn A', 'user1@example.com', '0987654321', 2, true);

-- ===========================================
-- SUCCESS MESSAGE
-- ===========================================

SELECT 'PhongTro247 PostgreSQL database created successfully!' AS status;