package com.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class OtpRequestDto {
    @Email
    @NotBlank
    private String email;

    // for verify-otp endpoint:
    private String otp;

    // selected role from frontend (seeker/helper)
    private String role;

    // Password to be set after OTP verification
    @NotBlank(message = "Password is required")
    private String password;

    // getters/setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
