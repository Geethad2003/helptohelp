package com.backend.service;

import com.backend.model.OtpEntry;
import com.backend.repository.OtpRepository;
import com.backend.util.RandomUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.Duration;
import java.util.Optional;

@Service
public class OtpService {
    private final OtpRepository otpRepository;
    private final EmailService emailService;
    private final long expirySeconds;

    public OtpService(OtpRepository otpRepository,
            EmailService emailService,
            @Value("${app.otp.expiry.seconds:300}") long expirySeconds) {
        this.otpRepository = otpRepository;
        this.emailService = emailService;
        this.expirySeconds = expirySeconds;
    }

    public String createAndSendOtp(String email, String role) {
        String otp = RandomUtil.generateOtp(6);
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expirySeconds);

        // remove old OTPs for this email
        otpRepository.deleteByEmail(email);

        OtpEntry entry = new OtpEntry(email, otp, now, exp, role);
        otpRepository.save(entry);

        // send email (or print to console if mail not configured)
        try {
            emailService.sendOtpEmail(email, otp);
        } catch (Exception ex) {
            // If we fail to send mail, still keep OTP and throw later if needed.
            System.out.println("Failed to send email — OTP is: " + otp);
        }
        return otp;
    }

    public boolean verifyOtp(String email, String otp) {
        Optional<OtpEntry> found = otpRepository.findByEmailAndOtp(email, otp);
        if (found.isEmpty())
            return false;
        OtpEntry entry = found.get();
        if (Instant.now().isAfter(entry.getExpiresAt())) {
            otpRepository.deleteByEmail(email);
            return false;
        }
        // valid
        otpRepository.deleteByEmail(email);
        return true;
    }

    public Optional<OtpEntry> latestOtpForEmail(String email) {
        return otpRepository.findTopByEmailOrderByCreatedAtDesc(email);
    }
}
