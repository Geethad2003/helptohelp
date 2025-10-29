package com.backend.controller;

import com.backend.dto.*;
import com.backend.service.AuthService;
import com.backend.service.OtpService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final OtpService otpService;

    public AuthController(AuthService authService, OtpService otpService) {
        this.authService = authService;
        this.otpService = otpService;
    }

    // Signup
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Validated @RequestBody SignupRequestDto dto) {
        try {
            authService.signup(dto.getName(), dto.getEmail(), dto.getPassword(), dto.getRole());
            return ResponseEntity.ok(new GenericResponse(true, "Signup successful"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new GenericResponse(false, e.getMessage()));
        }
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody AuthRequestDto dto) {
        try {
            var user = authService.login(dto.getEmail(), dto.getPassword());

            if (user == null) {
                return ResponseEntity.status(401).body(new GenericResponse(false, "Invalid credentials"));
            }

            if (!user.isVerified()) {
                return ResponseEntity.status(403)
                        .body(new GenericResponse(false, "User not verified. Please verify OTP."));
            }

            // For simplicity return user email and role. In prod return JWT.
            return ResponseEntity.ok(new GenericResponse(true, "Login successful", user.getRole(), user));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new GenericResponse(false, "Login failed: " + e.getMessage()));
        }
    }

    // Google Signin placeholder - frontend simply clicks button; call this if you
    // want to create a user record
    @PostMapping("/google")
    public ResponseEntity<?> googleSignIn(@RequestBody OtpRequestDto dto) {
        // In prod you would verify Google token. Here we just create user if not
        // exists.
        authService.setRoleAndVerify(dto.getEmail(), dto.getRole());
        return ResponseEntity.ok(new GenericResponse(true, "Google signin simulated", dto.getRole()));
    }

    // Step: role selected -> create OTP + send it
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody OtpRequestDto dto) {
        try {
            if (dto.getEmail() == null || dto.getRole() == null) {
                return ResponseEntity.badRequest().body(new GenericResponse(false, "email and role required"));
            }
            String otp = otpService.createAndSendOtp(dto.getEmail(), dto.getRole());
            // Do not return otp in prod; for debugging you may return it.
            return ResponseEntity.ok(new GenericResponse(true, "OTP sent", dto.getRole()));

        } catch (Exception e) {
            // Log the error for debugging
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse(false, "Failed to send OTP"));
        }

    }

    // Verify OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpRequestDto dto) {
        if (dto.getEmail() == null || dto.getOtp() == null) {
            return ResponseEntity.badRequest().body(new GenericResponse(false, "email and otp required"));
        }
        boolean ok = otpService.verifyOtp(dto.getEmail(), dto.getOtp());
        if (!ok)
            return ResponseEntity.status(400).body(new GenericResponse(false, "Invalid or expired OTP"));
        // OTP valid -> mark user as verified and set role
        authService.setRoleAndVerify(dto.getEmail(), dto.getRole());
        return ResponseEntity.ok(new GenericResponse(true, "OTP verified and user created/updated", dto.getRole()));
    }

    // Resend OTP
    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody OtpRequestDto dto) {
        if (dto.getEmail() == null || dto.getRole() == null) {
            return ResponseEntity.badRequest().body(new GenericResponse(false, "email and role required"));
        }
        otpService.createAndSendOtp(dto.getEmail(), dto.getRole());
        return ResponseEntity.ok(new GenericResponse(true, "OTP resent"));
    }
}
