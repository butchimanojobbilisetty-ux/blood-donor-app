-- Blood Donor Application Database Schema
-- MySQL Database

CREATE DATABASE IF NOT EXISTS blood_donor_db;
USE blood_donor_db;

-- Table: donors
CREATE TABLE donors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(15) NOT NULL,
    blood_group VARCHAR(5) NOT NULL,
    area VARCHAR(100) NOT NULL,
    city VARCHAR(50) NOT NULL,
    availability_status ENUM('AVAILABLE', 'NOT_AVAILABLE') DEFAULT 'AVAILABLE',
    not_available_until DATE NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_verified BOOLEAN DEFAULT FALSE,
    INDEX idx_blood_group (blood_group),
    INDEX idx_city (city),
    INDEX idx_availability (availability_status)
);

-- Table: otp_verifications
CREATE TABLE otp_verifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    otp_code VARCHAR(6) NOT NULL,
    purpose ENUM('DONOR_REGISTRATION', 'REPORT_CONFIRMATION', 'STATUS_UPDATE') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    is_used BOOLEAN DEFAULT FALSE,
    INDEX idx_email_purpose (email, purpose),
    INDEX idx_expires_at (expires_at)
);

-- Table: admin_users
CREATE TABLE admin_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL
);

-- Table: reports
CREATE TABLE reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    donor_id BIGINT NOT NULL,
    reporter_name VARCHAR(100),
    reporter_email VARCHAR(100),
    reporter_phone VARCHAR(15),
    reason ENUM('ALREADY_DONATED', 'WRONG_NUMBER', 'REFUSED_TO_DONATE', 'OTHER') NOT NULL,
    reason_details TEXT,
    report_status ENUM('PENDING', 'CONFIRMED', 'REJECTED', 'AUTO_CONFIRMED') DEFAULT 'PENDING',
    reported_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL,
    FOREIGN KEY (donor_id) REFERENCES donors(id) ON DELETE CASCADE,
    INDEX idx_donor_id (donor_id),
    INDEX idx_report_status (report_status)
);

-- Insert default admin user (password: admin123 - please change in production!)
-- Password is BCrypt hashed
INSERT INTO admin_users (username, password, email, full_name) 
VALUES ('admin', '$2a$10$rZo8VqLZqGLJJz3L6K5YyuXRZQqXqJGKqVqKQH0fGqQH0fGqQH0fG', 'admin@blooddonor.com', 'System Administrator');

-- Sample data for testing (optional)
INSERT INTO donors (name, email, phone, blood_group, area, city, is_verified) VALUES
('Rajesh Kumar', 'rajesh@example.com', '9876543210', 'O+', 'Banjara Hills', 'Hyderabad', TRUE),
('Priya Sharma', 'priya@example.com', '9876543211', 'A+', 'Jubilee Hills', 'Hyderabad', TRUE),
('Amit Patel', 'amit@example.com', '9876543212', 'B+', 'Gachibowli', 'Hyderabad', TRUE),
('Sneha Reddy', 'sneha@example.com', '9876543213', 'AB+', 'Kukatpally', 'Hyderabad', TRUE),
('Vijay Singh', 'vijay@example.com', '9876543214', 'O-', 'Madhapur', 'Hyderabad', TRUE);
