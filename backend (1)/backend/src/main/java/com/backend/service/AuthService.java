package com.backend.service;

import com.backend.model.User;
import com.backend.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User signup(String name, String email, String password, String role) {
        Optional<User> existing = userRepository.findByEmail(email);

        if (existing.isPresent()) {
            User user = existing.get();
            if (!user.isVerified()) {
                throw new RuntimeException("Please verify OTP before setting password");
            }

            // Update password for verified user
            String hash = passwordEncoder.encode(password);
            user.setName(name);
            user.setPasswordHash(hash);
            user.setRole(role);
            return userRepository.save(user);
        } else {
            // First-time signup
            String hash = passwordEncoder.encode(password);
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPasswordHash(hash);
            user.setRole(role);
            user.setVerified(false);
            return userRepository.save(user);
        }

        // First-time signup after OTP verification
        // String hash = passwordEncoder.encode(password);
        // User user = new User(email, hash, existing.get().getRole(), true); // role
        // can be set later
        // return userRepository.save(user);
    }

    public User login(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("No user with that email");
        }
        User user = optionalUser.get();

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }
        System.out.println("User logged in: " + user);
        return user;
    }

    public User setRoleAndVerify(String email, String role) {
        Optional<User> o = userRepository.findByEmail(email);
        User user;
        if (o.isPresent()) {
            user = o.get();
            user.setRole(role);
            user.setVerified(true);
        } else {
            // create user with no password (if they signed in with Google or only used OTP)
            user = new User();
            user.setName("Helper User"); // Default name, can be updated later
            user.setEmail(email);
            user.setPasswordHash(null);
            user.setRole(role);
            user.setVerified(true);
        }
        return userRepository.save(user);
    }
}
