package com.backend.repository;

import com.backend.model.OtpEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface OtpRepository extends MongoRepository<OtpEntry, String> {
    Optional<OtpEntry> findByEmailAndOtp(String email, String otp);

    Optional<OtpEntry> findTopByEmailOrderByCreatedAtDesc(String email);

    void deleteByEmail(String email);
}
