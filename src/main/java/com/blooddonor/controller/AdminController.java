package com.blooddonor.controller;

import com.blooddonor.dto.AdminLoginRequest;
import com.blooddonor.dto.ApiResponse;
import com.blooddonor.dto.DonorRegistrationRequest;
import com.blooddonor.dto.LoginResponse;
import com.blooddonor.model.Donor;
import com.blooddonor.service.DonorService;
import com.blooddonor.service.JwtService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final DonorService donorService;
    private final JwtService jwtService;

    @Value("${admin.username:admin}")
    private String adminUsername;

    @Value("${admin.password:M@noj98491}")
    private String adminPassword;

    public AdminController(DonorService donorService, JwtService jwtService) {
        this.donorService = donorService;
        this.jwtService = jwtService;
        log.info("AdminController initialized with username: '{}' and password: '{}'", adminUsername, adminPassword);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> loginAdmin(
            @Valid @RequestBody AdminLoginRequest loginRequest) {
        log.info("Admin login attempt for username: {}", loginRequest.getUsername());
        log.debug("Comparing credentials - Input username: '{}', Configured username: '{}'", 
            loginRequest.getUsername(), adminUsername);
        log.debug("Comparing credentials - Input password: '{}', Configured password: '{}'", 
            loginRequest.getPassword(), adminPassword);
        
        try {
            // Use environment variables for admin credentials
            if (adminUsername.equals(loginRequest.getUsername()) && adminPassword.equals(loginRequest.getPassword())) {
                log.info("Admin login successful for username: {}", loginRequest.getUsername());
                String token = jwtService.generateToken(loginRequest.getUsername(), "ADMIN", 0L);
                LoginResponse loginResponse = new LoginResponse(token, "Bearer", 0L, loginRequest.getUsername(), "Administrator", "ADMIN");
                return ResponseEntity.ok(
                    ApiResponse.success("Admin login successful!", loginResponse)
                );
            } else {
                log.warn("Admin login failed for username: {}", loginRequest.getUsername());
                log.warn("Username match: {}, Password match: {}", 
                    adminUsername.equals(loginRequest.getUsername()), 
                    adminPassword.equals(loginRequest.getPassword()));
                throw new RuntimeException("Invalid admin credentials");
            }
        } catch (Exception e) {
            log.error("Admin login failed for username: {}", loginRequest.getUsername(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }


    @GetMapping("/donors")
    public ResponseEntity<ApiResponse<List<Donor>>> getAllDonors() {
        log.info("Fetching all donors");
        try {
            List<Donor> donors = donorService.getAllDonors();
            log.info("Successfully retrieved {} donors", donors.size());
            return ResponseEntity.ok(
                ApiResponse.success("Donors retrieved successfully", donors)
            );
        } catch (Exception e) {
            log.error("Error retrieving all donors", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/donors")
    public ResponseEntity<ApiResponse<Donor>> addDonor(
            @Valid @RequestBody DonorRegistrationRequest request) {
        log.info("Adding new donor: {}", request.getEmail());
        try {
            Donor donor = new Donor();
            donor.setName(request.getName());
            donor.setEmail(request.getEmail());
            donor.setPhone(request.getPhone());
            donor.setBloodGroup(request.getBloodGroup());
            donor.setArea(request.getArea());
            donor.setCity(request.getCity());
            donor.setIsVerified(true);
            donor.setAvailabilityStatus(Donor.AvailabilityStatus.AVAILABLE);
            
            log.debug("Donor created with blood group: {} and city: {}", request.getBloodGroup(), request.getCity());
            return ResponseEntity.ok(
                ApiResponse.success("Donor added successfully", donor)
            );
        } catch (Exception e) {
            log.error("Error adding donor: {}", request.getEmail(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/donors/{id}")
    public ResponseEntity<ApiResponse<Donor>> updateDonor(
            @PathVariable Long id,
            @Valid @RequestBody DonorRegistrationRequest request) {
        log.info("Updating donor with ID: {}", id);
        try {
            Donor donor = donorService.updateDonor(id, request);
            log.info("Successfully updated donor with ID: {}", id);
            return ResponseEntity.ok(
                ApiResponse.success("Donor updated successfully", donor)
            );
        } catch (Exception e) {
            log.error("Error updating donor with ID: {}", id, e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/donors/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDonor(@PathVariable Long id) {
        log.info("Deleting donor with ID: {}", id);
        try {
            donorService.deleteDonor(id);
            log.info("Successfully deleted donor with ID: {}", id);
            return ResponseEntity.ok(
                ApiResponse.success("Donor deleted successfully")
            );
        } catch (Exception e) {
            log.error("Error deleting donor with ID: {}", id, e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/donors/{id}/status")
    public ResponseEntity<ApiResponse<Donor>> updateDonorStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) Integer monthsUnavailable) {
        log.info("Updating donor status for ID: {} to status: {}", id, status);
        try {
            Donor.AvailabilityStatus availabilityStatus = Donor.AvailabilityStatus.valueOf(status);
            Donor donor = donorService.updateDonorStatus(id, availabilityStatus, monthsUnavailable);
            log.info("Successfully updated donor status for ID: {} to {}", id, status);
            return ResponseEntity.ok(
                ApiResponse.success("Donor status updated successfully", donor)
            );
        } catch (Exception e) {
            log.error("Error updating donor status for ID: {}", id, e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
