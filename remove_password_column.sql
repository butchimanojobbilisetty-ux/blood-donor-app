-- Remove password column from donors table
-- This migration removes the password column as we're moving to OTP-based authentication only

ALTER TABLE donors DROP COLUMN password;
