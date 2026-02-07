package com.blooddonor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    
    private static final String RESEND_API_URL = "https://api.resend.com/emails";

    @Value("${resend.api.key:re_eaY3jL5A_FcjVqJTw5YR5xWSH1GJpYgti}")
    private String resendApiKey;

    @Value("${resend.from.email:onboarding@resend.dev}")
    private String fromEmail;

    private final RestTemplate restTemplate;

    public EmailService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendOtpEmail(String toEmail, String otp, String purpose) {
        log.info("Sending OTP email to: {} for purpose: {}", toEmail, purpose);
        try {
            String emailBody = buildEmailBody(otp, purpose);
            
            Map<String, Object> requestBody = Map.of(
                "from", fromEmail,
                "to", new String[]{toEmail},
                "subject", "Blood Donor App - OTP Verification",
                "html", emailBody
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + resendApiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(RESEND_API_URL, request, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("OTP email sent successfully to: {} with ID: {}", toEmail, response.getBody().get("id"));
            } else {
                throw new RuntimeException("Failed to send email: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error sending OTP email to: {}. Logging OTP for testing: {}", toEmail, otp, e);
            // For testing purposes, log the OTP instead of failing
            log.warn("=== OTP FOR TESTING ===");
            log.warn("Email: {}", toEmail);
            log.warn("OTP: {}", otp);
            log.warn("Purpose: {}", purpose);
            log.warn("=== END OTP ===");
            // Don't throw exception for now - comment this out for production
            // throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    public void sendReportNotification(String toEmail, String donorName, String reporterName, String reason) {
        log.info("Sending report notification email to: {} for donor: {}", toEmail, donorName);
        try {
            String emailBody = buildReportEmailBody(donorName, reporterName, reason);
            
            Map<String, Object> requestBody = Map.of(
                "from", fromEmail,
                "to", new String[]{toEmail},
                "subject", "Blood Donor App - Report Notification",
                "html", emailBody
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + resendApiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(RESEND_API_URL, request, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Report notification email sent successfully to: {} with ID: {}", toEmail, response.getBody().get("id"));
            } else {
                throw new RuntimeException("Failed to send email: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error sending report notification email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send report notification email", e);
        }
    }

    private String buildEmailBody(String otp, String purpose) {
        return String.format(
                "<p>Dear User,</p>" +
                "<p>Your OTP for <strong>%s</strong> is: <strong>%s</strong></p>" +
                "<p>This OTP is valid for 10 minutes.</p>" +
                "<p>Please do not share this OTP with anyone.</p>" +
                "<br>" +
                "<p>Thank you,<br>" +
                "Blood Donor Team</p>",
                purpose.replace("_", " ").toLowerCase(),
                otp);
    }

    private String buildReportEmailBody(String donorName, String reporterName, String reason) {
        return String.format(
                "<p>Dear %s,</p>" +
                "<p>Someone has reported that you are currently unavailable for blood donation.</p>" +
                "<p><strong>Reporter:</strong> %s</p>" +
                "<p><strong>Reason:</strong> %s</p>" +
                "<p>If this is incorrect, please contact the admin immediately.</p>" +
                "<p>If you don't respond within 24 hours, your status will be automatically updated to 'Not Available'.</p>" +
                "<br>" +
                "<p>Thank you,<br>" +
                "Blood Donor Team</p>",
                donorName, 
                reporterName != null ? reporterName : "Anonymous", 
                reason);
    }
}
