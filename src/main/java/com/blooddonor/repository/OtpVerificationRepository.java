package com.blooddonor.repository;

import com.blooddonor.model.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findByEmailAndOtpCodeAndPurposeAndIsUsedFalse(
            String email,
            String otpCode,
            OtpVerification.OtpPurpose purpose
    );

    List<OtpVerification> findByExpiresAtBefore(LocalDateTime dateTime);

    int deleteByExpiresAtBefore(LocalDateTime dateTime);

    void deleteByEmailAndPurpose(String email, OtpVerification.OtpPurpose purpose);
}
