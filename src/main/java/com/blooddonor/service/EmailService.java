package com.blooddonor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String toEmail, String otp, String purpose) {
        log.info("Sending OTP email to: {} for purpose: {}", toEmail, purpose);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Blood Donor App - OTP Verification");

            String emailBody = buildEmailBody(otp, purpose);
            message.setText(emailBody);

            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Error sending OTP email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    public void sendReportNotification(String toEmail, String donorName, String reporterName, String reason) {
        log.info("Sending report notification email to: {} for donor: {}", toEmail, donorName);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Blood Donor App - Report Notification");

            String emailBody = String.format(
                    "Dear %s,\n\n" +
                            "Someone has reported that you are currently unavailable for blood donation.\n\n" +
                            "Reporter: %s\n" +
                            "Reason: %s\n\n" +
                            "If this is incorrect, please contact the admin immediately.\n" +
                            "If you don't respond within 24 hours, your status will be automatically updated to 'Not Available'.\n\n"
                            +
                            "Thank you,\n" +
                            "Blood Donor Team",
                    donorName, reporterName != null ? reporterName : "Anonymous", reason);

            message.setText(emailBody);
            mailSender.send(message);
            log.info("Report notification email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Error sending report notification email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send report notification email", e);
        }
    }

    private String buildEmailBody(String otp, String purpose) {
        return String.format(
                "Dear User,\n\n" +
                        "Your OTP for %s is: %s\n\n" +
                        "This OTP is valid for 10 minutes.\n" +
                        "Please do not share this OTP with anyone.\n\n" +
                        "Thank you,\n" +
                        "Blood Donor Team",
                purpose.replace("_", " ").toLowerCase(),
                otp);
    }
}
