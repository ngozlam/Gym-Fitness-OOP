-- Xóa bảng cũ nếu có (chạy lại nhiều lần không bị lỗi)
DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS subscriptions;
DROP TABLE IF EXISTS membership_plans;
DROP TABLE IF EXISTS members;
DROP TABLE IF EXISTS trainers;
DROP TABLE IF EXISTS staff;

-- 1. staff (độc lập)
CREATE TABLE staff (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('ADMIN','TRAINER','RECEPTIONIST') NOT NULL
);

-- 2. trainers (độc lập, phải trước members vì members tham chiếu trainers)
CREATE TABLE trainers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    specialty VARCHAR(100),
    phone VARCHAR(20)
);

-- 3. members (tham chiếu trainers)
CREATE TABLE members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    date_of_birth DATE,
    joined_date DATE NOT NULL,
    trainer_id BIGINT,
    FOREIGN KEY (trainer_id) REFERENCES trainers(id)
);

-- 4. membership_plans (độc lập)
CREATE TABLE membership_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    duration_months INT NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    pricing_strategy VARCHAR(30) NOT NULL DEFAULT 'STANDARD'
);

-- 5. subscriptions (tham chiếu members, membership_plans)
CREATE TABLE subscriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    plan_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status ENUM('ACTIVE','EXPIRED','CANCELLED') NOT NULL DEFAULT 'ACTIVE',
    final_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (member_id) REFERENCES members(id),
    FOREIGN KEY (plan_id) REFERENCES membership_plans(id)
);

-- 6. payments (tham chiếu subscriptions)
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subscription_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_date DATETIME NOT NULL,
    method ENUM('CASH','CARD','TRANSFER') NOT NULL,
    FOREIGN KEY (subscription_id) REFERENCES subscriptions(id)
);

-- 7. attendance (tham chiếu members)
CREATE TABLE attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    check_in TIMESTAMP NOT NULL,
    check_out TIMESTAMP NULL,
    FOREIGN KEY (member_id) REFERENCES members(id)
);