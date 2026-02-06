package com.blooddonor.service;

import com.blooddonor.model.OtpVerification;
import com.blooddonor.repository.OtpVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    private static final Logger log = LoggerFactory.getLogger(OtpService.class);

    private final OtpVerificationRepository otpRepository;
    private final EmailService emailService;

    @Value("${otp.expiration.minutes:10}")
    private int otpExpirationMinutes;

    @Value("${otp.length:6}")
    private int otpLength;

    public OtpService(OtpVerificationRepository otpRepository, EmailService emailService) {
        this.otpRepository = otpRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void generateAndSendOtp(String email, OtpVerification.OtpPurpose purpose) {
        log.info("Generating and sending OTP for email: {} with purpose: {}", email, purpose);
        
        // Delete any existing OTPs for this email and purpose
        log.debug("Deleting existing OTPs for email: {} and purpose: {}", email, purpose);
        otpRepository.deleteByEmailAndPurpose(email, purpose);

        // Generate OTP
        String otp = generateOtp();
        log.debug("Generated OTP for email: {}", email);

        // Save OTP
        OtpVerification otpVerification = new OtpVerification();
        otpVerification.setEmail(email);
        otpVerification.setOtpCode(otp);
        otpVerification.setPurpose(purpose);
        otpVerification.setExpiresAt(LocalDateTime.now().plusMinutes(otpExpirationMinutes));
        otpRepository.save(otpVerification);
        log.info("OTP saved for email: {} with expiration: {} minutes", email, otpExpirationMinutes);

        // Send email
        log.debug("Sending OTP email to: {}", email);
        emailService.sendOtpEmail(email, otp, purpose.name());
        log.info("OTP email sent successfully to: {}", email);
    }

    public boolean verifyOtp(String email, String otp, OtpVerification.OtpPurpose purpose) {
        log.info("Verifying OTP for email: {} with purpose: {}", email, purpose);
        
        OtpVerification otpVerification = otpRepository
            .findByEmailAndOtpCodeAndPurposeAndIsUsedFalse(email, otp, purpose)
            .orElse(null);

        if (otpVerification == null) {
            log.warn("OTP verification failed: OTP not found or already used for email: {}", email);
            return false;
        }

        // Check if expired
        if (otpVerification.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("OTP verification failed: OTP expired for email: {}", email);
            return false;
        }

        log.debug("OTP verified successfully for email: {}", email);
        // Mark as used
        otpVerification.setIsUsed(true);
        otpRepository.save(otpVerification);
        log.info("OTP marked as used for email: {}", email);

        return true;
    }

    private String generateOtp() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    @Transactional
    public void cleanupExpiredOtps() {
        log.info("Cleaning up expired OTPs");
        int deletedCount = otpRepository.deleteByExpiresAtBefore(LocalDateTime.now());
        log.info("Deleted {} expired OTP records", deletedCount);
    }
}
