package com.blooddonor.service;

import com.blooddonor.dto.DonorRegistrationRequest;
import com.blooddonor.dto.DonorSearchRequest;
import com.blooddonor.dto.LoginResponse;
import com.blooddonor.dto.OtpLoginRequest;
import com.blooddonor.model.Donor;
import com.blooddonor.model.OtpVerification;
import com.blooddonor.repository.DonorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class DonorService {

    private static final Logger log = LoggerFactory.getLogger(DonorService.class);

    private final DonorRepository donorRepository;
    private final OtpService otpService;
    private final JwtService jwtService;

    public DonorService(DonorRepository donorRepository, OtpService otpService, JwtService jwtService) {
        this.donorRepository = donorRepository;
        this.otpService = otpService;
        this.jwtService = jwtService;
    }

    @Transactional
    public void initiateRegistration(DonorRegistrationRequest request) {
        log.info("Initiating registration for email: {}", request.getEmail());
        // Check if email already exists
        if (donorRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration attempt with already registered email: {}", request.getEmail());
            throw new RuntimeException("Email already registered");
        }

        log.debug("Generating and sending OTP for email: {}", request.getEmail());
        // Send OTP
        try {
            otpService.generateAndSendOtp(request.getEmail(), OtpVerification.OtpPurpose.DONOR_REGISTRATION);
            log.info("OTP generated and sent for email: {}", request.getEmail());
        } catch (RuntimeException e) {
            log.error("Failed to send OTP for email: {} - Error: {}", request.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Failed to send OTP email. Please try again later.");
        } catch (Exception e) {
            log.error("Unexpected error during OTP generation for email: {} - Error: {}", request.getEmail(), e.getMessage(), e);
            throw new RuntimeException("An unexpected error occurred during OTP generation. Please try again later.");
        }
    }

    @Transactional
    public Donor completeRegistration(DonorRegistrationRequest request, String otp) {
        log.info("Completing registration for email: {}", request.getEmail());
        // Verify OTP
        boolean isValid = otpService.verifyOtp(request.getEmail(), otp, OtpVerification.OtpPurpose.DONOR_REGISTRATION);
        
        if (!isValid) {
            log.warn("Invalid OTP attempt for email: {}", request.getEmail());
            throw new RuntimeException("Invalid or expired OTP");
        }

        log.debug("OTP verified successfully for email: {}", request.getEmail());
        // Create donor
        Donor donor = new Donor();
        donor.setName(request.getName());
        donor.setEmail(request.getEmail());
        donor.setPhone(request.getPhone());
        donor.setBloodGroup(request.getBloodGroup());
        donor.setArea(request.getArea());
        donor.setCity(request.getCity());
        donor.setIsVerified(true);
        donor.setAvailabilityStatus(Donor.AvailabilityStatus.AVAILABLE);

        Donor savedDonor = donorRepository.save(donor);
        log.info("Donor registered successfully with ID: {} and email: {}", savedDonor.getId(), request.getEmail());
        return savedDonor;
    }

    public List<Donor> searchDonors(DonorSearchRequest searchRequest) {
        log.info("Searching donors with blood group: {}, city: {}, status: {}", 
            searchRequest.getBloodGroup(), searchRequest.getCity(), searchRequest.getAvailabilityStatus());
        
        List<Donor> results;
        if (searchRequest.getBloodGroup() != null && searchRequest.getCity() != null) {
            if (searchRequest.getAvailabilityStatus() != null) {
                Donor.AvailabilityStatus status = Donor.AvailabilityStatus.valueOf(searchRequest.getAvailabilityStatus());
                results = donorRepository.findByBloodGroupAndCityAndAvailabilityStatus(
                    searchRequest.getBloodGroup(),
                    searchRequest.getCity(),
                    status
                );
            } else {
                results = donorRepository.findByBloodGroupAndCity(searchRequest.getBloodGroup(), searchRequest.getCity());
            }
        } else if (searchRequest.getBloodGroup() != null) {
            results = donorRepository.findByBloodGroup(searchRequest.getBloodGroup());
        } else if (searchRequest.getCity() != null) {
            results = donorRepository.findByCity(searchRequest.getCity());
        } else {
            results = donorRepository.findByIsVerifiedTrue();
        }
        
        log.info("Found {} donors matching search criteria", results.size());
        return results;
    }

    public Donor getDonorById(Long id) {
        log.debug("Fetching donor with ID: {}", id);
        Donor donor = donorRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Donor not found with ID: {}", id);
                return new RuntimeException("Donor not found");
            });
        log.debug("Successfully retrieved donor with ID: {}", id);
        return donor;
    }

    @Transactional
    public Donor updateDonorStatus(Long donorId, Donor.AvailabilityStatus status, Integer monthsUnavailable) {
        log.info("Updating donor status for ID: {} to status: {}", donorId, status);
        Donor donor = getDonorById(donorId);
        donor.setAvailabilityStatus(status);
        
        if (status == Donor.AvailabilityStatus.NOT_AVAILABLE && monthsUnavailable != null) {
            donor.setNotAvailableUntil(LocalDate.now().plusMonths(monthsUnavailable));
            log.debug("Donor ID: {} set unavailable until: {}", donorId, donor.getNotAvailableUntil());
        } else if (status == Donor.AvailabilityStatus.AVAILABLE) {
            donor.setNotAvailableUntil(null);
            log.debug("Donor ID: {} set as available", donorId);
        }
        
        Donor updatedDonor = donorRepository.save(donor);
        log.info("Donor status updated successfully for ID: {}", donorId);
        return updatedDonor;
    }

    @Transactional
    public void deleteDonor(Long id) {
        log.info("Deleting donor with ID: {}", id);
        if (!donorRepository.existsById(id)) {
            log.error("Donor not found for deletion with ID: {}", id);
            throw new RuntimeException("Donor not found");
        }
        donorRepository.deleteById(id);
        log.info("Donor deleted successfully with ID: {}", id);
    }

    public List<Donor> getAllDonors() {
        log.debug("Fetching all donors");
        List<Donor> donors = donorRepository.findAll();
        log.info("Retrieved {} donors from database", donors.size());
        return donors;
    }

    @Transactional
    public Donor updateDonor(Long id, DonorRegistrationRequest request) {
        log.info("Updating donor with ID: {}", id);
        Donor donor = getDonorById(id);
        donor.setName(request.getName());
        donor.setPhone(request.getPhone());
        donor.setBloodGroup(request.getBloodGroup());
        donor.setArea(request.getArea());
        donor.setCity(request.getCity());
        
        Donor updatedDonor = donorRepository.save(donor);
        log.info("Donor updated successfully with ID: {}", id);
        return updatedDonor;
    }

    
    public LoginResponse authenticateDonorWithOtp(OtpLoginRequest otpLoginRequest) {
        log.info("Authenticating donor with OTP for email: {}", otpLoginRequest.getEmail());
        
        // Find donor by email
        Donor donor = donorRepository.findByEmail(otpLoginRequest.getEmail())
            .orElseThrow(() -> new RuntimeException("Donor not found"));
        
        // Verify OTP
        boolean isValid = otpService.verifyOtp(
            otpLoginRequest.getEmail(), 
            otpLoginRequest.getOtp(), 
            OtpVerification.OtpPurpose.DONOR_REGISTRATION
        );
        
        if (!isValid) {
            log.warn("Invalid OTP for email: {}", otpLoginRequest.getEmail());
            throw new RuntimeException("Invalid or expired OTP");
        }
        
        // Generate JWT token
        String token = jwtService.generateToken(donor.getEmail(), "DONOR", donor.getId());
        
        log.info("OTP authentication successful for email: {}", otpLoginRequest.getEmail());
        return new LoginResponse(token, "Bearer", donor.getId(), donor.getEmail(), donor.getName(), "DONOR");
    }
}
