package com.blooddonor.controller;

import com.blooddonor.dto.*;
import com.blooddonor.model.Donor;
import com.blooddonor.service.DonorService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donors")
@CrossOrigin(origins = "*")
public class DonorController {

    private static final Logger log = LoggerFactory.getLogger(DonorController.class);

    private final DonorService donorService;

    public DonorController(DonorService donorService) {
        this.donorService = donorService;
    }

    @PostMapping("/register/initiate")
    public ResponseEntity<ApiResponse<Void>> initiateRegistration(
            @Valid @RequestBody DonorRegistrationRequest request) {
        log.info("Initiating registration for email: {}", request.getEmail());
        try {
            donorService.initiateRegistration(request);
            log.info("OTP sent successfully to: {}", request.getEmail());
            return ResponseEntity.ok(
                ApiResponse.success("OTP sent to your email. Please verify to complete registration.")
            );
        } catch (Exception e) {
            log.error("Error initiating registration for email: {}", request.getEmail(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/register/complete")
    public ResponseEntity<ApiResponse<Donor>> completeRegistration(
            @Valid @RequestBody DonorRegistrationRequest request,
            @RequestParam String otp) {
        log.info("Completing registration for email: {}", request.getEmail());
        try {
            Donor donor = donorService.completeRegistration(request, otp);
            log.info("Registration completed successfully for donor ID: {}", donor.getId());
            return ResponseEntity.ok(
                ApiResponse.success("Registration completed successfully!", donor)
            );
        } catch (Exception e) {
            log.error("Error completing registration for email: {}", request.getEmail(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> loginDonor(
            @Valid @RequestBody DonorLoginRequest loginRequest) {
        log.info("Login attempt for email: {}", loginRequest.getEmail());
        try {
            LoginResponse loginResponse = donorService.authenticateDonor(loginRequest);
            log.info("Login successful for email: {}", loginRequest.getEmail());
            return ResponseEntity.ok(
                ApiResponse.success("Login successful!", loginResponse)
            );
        } catch (Exception e) {
            log.error("Login failed for email: {}", loginRequest.getEmail(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<Donor>>> searchDonors(
            @RequestBody DonorSearchRequest request) {
        log.info("Searching donors with blood group: {}, city: {}, status: {}", 
            request.getBloodGroup(), request.getCity(), request.getAvailabilityStatus());
        try {
            List<Donor> donors = donorService.searchDonors(request);
            log.info("Found {} donors matching search criteria", donors.size());
            return ResponseEntity.ok(
                ApiResponse.success("Donors retrieved successfully", donors)
            );
        } catch (Exception e) {
            log.error("Error searching donors", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Donor>> getDonorById(@PathVariable Long id) {
        log.info("Fetching donor with ID: {}", id);
        try {
            Donor donor = donorService.getDonorById(id);
            log.info("Successfully retrieved donor with ID: {}", id);
            return ResponseEntity.ok(
                ApiResponse.success("Donor retrieved successfully", donor)
            );
        } catch (Exception e) {
            log.error("Error retrieving donor with ID: {}", id, e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
