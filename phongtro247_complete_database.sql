CREATE DATABASE phongtro247_db;
USE phongtro247_db;

CREATE TABLE roles (
    role_id INT PRIMARY KEY IDENTITY(1,1),
    role_name VARCHAR(20) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE users (
    user_id INT PRIMARY KEY IDENTITY(1,1),
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name NVARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(15),
    role_id INT NOT NULL DEFAULT 2,
    is_verified BIT NOT NULL DEFAULT 0,
    verification_code VARCHAR(10),
    reset_token VARCHAR(255),
    reset_token_expires DATETIME,
    coins INT NOT NULL DEFAULT 0,
    is_pro BIT NOT NULL DEFAULT 0,
    pro_expires_at DATETIME,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

CREATE TABLE post_status (
    status_id INT PRIMARY KEY IDENTITY(1,1),
    status_name VARCHAR(20) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE posts (
    post_id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    title NVARCHAR(255) NOT NULL,
    description NTEXT,
    price DECIMAL(15,2),
    area DECIMAL(10,2),
    address NVARCHAR(500),
    ward NVARCHAR(100),
    district NVARCHAR(100),
    city NVARCHAR(100),
    room_type NVARCHAR(50),
    status_id INT NOT NULL DEFAULT 2,
    scheduled_at DATETIME,
    auto_publish BIT DEFAULT 0,
    published_at DATETIME,
    auto_post_facebook BIT DEFAULT 0,
    facebook_post_id VARCHAR(255),
    facebook_posted_at DATETIME,
    facebook_error NVARCHAR(500),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (status_id) REFERENCES post_status(status_id)
);

CREATE TABLE post_images (
    image_id INT PRIMARY KEY IDENTITY(1,1),
    post_id INT NOT NULL,
    image_path VARCHAR(500) NOT NULL,
    is_primary BIT DEFAULT 0,
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE
);

CREATE TABLE facebook_settings (
    setting_id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    page_id VARCHAR(100) NOT NULL,
    page_name NVARCHAR(255),
    access_token NTEXT NOT NULL,
    is_active BIT DEFAULT 1,
    token_expires_at DATETIME,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE post_facebook_pages (
    id INT IDENTITY(1,1) PRIMARY KEY,
    post_id INT NOT NULL,
    page_id VARCHAR(100) NOT NULL,
    facebook_post_id VARCHAR(255),
    posted_at DATETIME,
    status NVARCHAR(50) DEFAULT 'pending',
    error_message NVARCHAR(500),
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE,
    UNIQUE(post_id, page_id)
);

CREATE TABLE payment_orders (
    order_id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    order_code VARCHAR(50) NOT NULL UNIQUE,
    amount DECIMAL(15,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'VND',
    description NVARCHAR(255),
    payment_method VARCHAR(20) DEFAULT 'momo',
    status VARCHAR(20) DEFAULT 'pending',
    momo_trans_id VARCHAR(100),
    momo_request_id VARCHAR(100),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    expires_at DATETIME,
    completed_at DATETIME,
    coins_amount INT DEFAULT 0,
    subscription_type VARCHAR(20),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE momo_transactions (
    id INT PRIMARY KEY IDENTITY(1,1),
    order_code VARCHAR(50) NOT NULL,
    momo_trans_id VARCHAR(100),
    amount DECIMAL(15,2),
    result_code INT,
    message NVARCHAR(255),
    payment_option VARCHAR(50),
    pay_trans_id VARCHAR(100),
    trans_id VARCHAR(100),
    response_time DATETIME,
    signature VARCHAR(500),
    raw_data NTEXT,
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (order_code) REFERENCES payment_orders(order_code)
);

CREATE TABLE coin_transactions (
    transaction_id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    amount INT NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    description NVARCHAR(255),
    reference_id VARCHAR(100),
    balance_before INT NOT NULL,
    balance_after INT NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    payment_order_id INT,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (payment_order_id) REFERENCES payment_orders(order_id)
);

CREATE TABLE user_balance (
    balance_id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL UNIQUE,
    total_coins DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    spent_coins DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    available_coins DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE scheduled_posts_log (
    log_id INT PRIMARY KEY IDENTITY(1,1),
    post_id INT NOT NULL,
    user_id INT NOT NULL,
    scheduled_time DATETIME NOT NULL,
    actual_publish_time DATETIME,
    status VARCHAR(20) NOT NULL,
    attempt_count INT DEFAULT 1,
    error_message NVARCHAR(500),
    facebook_posted BIT DEFAULT 0,
    facebook_error NVARCHAR(500),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (post_id) REFERENCES posts(post_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE subscription_plans (
    plan_id INT PRIMARY KEY IDENTITY(1,1),
    plan_name VARCHAR(50) NOT NULL UNIQUE,
    description NVARCHAR(255),
    price_in_coins INT NOT NULL,
    price_in_vnd DECIMAL(15,2) NOT NULL,
    daily_post_limit INT NOT NULL,
    facebook_posting BIT DEFAULT 1,
    duration_days INT NOT NULL,
    is_active BIT DEFAULT 1,
    created_at DATETIME DEFAULT GETDATE()
);

CREATE TABLE user_subscriptions (
    subscription_id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    plan_id INT NOT NULL,
    coins_paid INT NOT NULL,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    is_active BIT DEFAULT 1,
    created_at DATETIME DEFAULT GETDATE(),
    payment_order_id INT,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (plan_id) REFERENCES subscription_plans(plan_id),
    FOREIGN KEY (payment_order_id) REFERENCES payment_orders(order_id)
);

CREATE TABLE daily_post_limits (
    limit_id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    date_posted DATE NOT NULL,
    posts_count INT DEFAULT 0,
    max_posts_allowed INT NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    UNIQUE(user_id, date_posted)
);

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

ALTER TABLE payment_orders ALTER COLUMN coins INT NULL;
ALTER TABLE payment_orders ADD coins_amount INT DEFAULT 0;
ALTER TABLE payment_orders ADD gateway_transaction_id VARCHAR(200);
ALTER TABLE payment_orders ADD callback_data NVARCHAR(MAX);

