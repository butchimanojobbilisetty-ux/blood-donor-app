-- Railway MySQL Schema (Fixed for MySQL)
-- Database: railway (Railway default)

-- Table: donors
CREATE TABLE IF NOT EXISTS donors (
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
CREATE TABLE IF NOT EXISTS otp_verifications (
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
CREATE TABLE IF NOT EXISTS admin_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL
);

-- Table: reports
CREATE TABLE IF NOT EXISTS reports (
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
    INDEX idx_donor_id (donor_id),
    INDEX idx_report_status (report_status)
);
