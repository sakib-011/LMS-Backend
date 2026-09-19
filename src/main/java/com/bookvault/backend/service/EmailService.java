package com.bookvault.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:your-email@gmail.com}")
    private String fromEmail;

    /**
     * Send email verification link to user
     */
    public boolean sendVerificationEmail(String recipientEmail, String studentName, String verificationToken) {
        String subject = "Verify your BookGrid Account";
        String verificationUrl = "http://localhost:5173/verify-email?token=" + verificationToken + "&email=" + recipientEmail;

        String htmlContent = "<div style='font-family: Arial, sans-serif; padding: 20px; color: #2d3748; background-color: #fffaf0; border-radius: 8px;'>"
                + "<h2 style='color: #dd6b20;'>Welcome to BookGrid, " + studentName + "!</h2>"
                + "<p>Thank you for registering. Please click the button below to verify your email address and activate your account:</p>"
                + "<div style='margin: 25px 0;'>"
                + "<a href='" + verificationUrl + "' style='background-color: #dd6b20; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold;'>Verify Email Address</a>"
                + "</div>"
                + "<p style='font-size: 0.875rem; color: #718096;'>Or copy and paste this link into your browser:<br/>"
                + "<a href='" + verificationUrl + "'>" + verificationUrl + "</a></p>"
                + "<hr style='border: none; border-top: 1px solid #e2e8f0; margin: 20px 0;'/>"
                + "<p style='font-size: 0.75rem; color: #a0aec0;'>If you did not request this email, please ignore it.</p>"
                + "</div>";

        return sendHtmlEmail(recipientEmail, subject, htmlContent);
    }

    /**
     * Send password reset link to user
     */
    public boolean sendPasswordResetEmail(String recipientEmail, String resetToken) {
        String subject = "Reset your BookGrid Password";
        String resetUrl = "http://localhost:5173/reset-password?token=" + resetToken;

        String htmlContent = "<div style='font-family: Arial, sans-serif; padding: 20px; color: #2d3748; background-color: #edf2f7; border-radius: 8px;'>"
                + "<h2 style='color: #3182ce;'>BookGrid Password Reset</h2>"
                + "<p>You requested a password reset for your library account. Click below to set a new password:</p>"
                + "<div style='margin: 25px 0;'>"
                + "<a href='" + resetUrl + "' style='background-color: #3182ce; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold;'>Reset Password</a>"
                + "</div>"
                + "<p style='font-size: 0.875rem; color: #718096;'>Link valid for 1 hour.<br/>"
                + "<a href='" + resetUrl + "'>" + resetUrl + "</a></p>"
                + "</div>";

        return sendHtmlEmail(recipientEmail, subject, htmlContent);
    }

    /**
     * Helper method to send MIME HTML emails
     */
    private boolean sendHtmlEmail(String to, String subject, String htmlBody) {
        if (mailSender == null) {
            System.out.println("EmailService: JavaMailSender is not configured. Simulating mail send to: " + to);
            System.out.println("Subject: " + subject);
            return true;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);
            System.out.println("EmailService: Email successfully sent to " + to);
            return true;
        } catch (Exception e) {
            System.err.println("EmailService: Failed to send email to " + to + ": " + e.getMessage());
            return false;
        }
    }
}
