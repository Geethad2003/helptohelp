package com.backend.controller;

import com.backend.model.DashboardData;
import com.backend.model.Request;
import com.backend.model.User;
import com.backend.repository.RequestRepository;
import com.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200") // ✅ Allow Angular frontend
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    public DashboardController(RequestRepository requestRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/seeker")
    public DashboardData getSeekerDashboard() {
        DashboardData dashboard = new DashboardData();

        // ✅ Fetch active and completed requests
        List<Request> activeRequests = requestRepository.findByStatus("ACTIVE");
        List<Request> completedRequests = requestRepository.findByStatus("COMPLETED");

        // If no status is set, treat as active (for existing data)
        List<Request> allRequests = requestRepository.findAll();
        for (Request request : allRequests) {
            if (request.getStatus() == null) {
                request.setStatus("ACTIVE");
                requestRepository.save(request);
            }
        }

        // Re-fetch after migration
        activeRequests = requestRepository.findByStatus("ACTIVE");
        completedRequests = requestRepository.findByStatus("COMPLETED");

        // ✅ Count actual helpers from users collection
        long helperCount = userRepository.countByRole("HELPER");

        dashboard.setActiveRequestsCount(activeRequests.size());
        dashboard.setCompletedRequestsCount(completedRequests.size());
        dashboard.setAvailableHelpers((int) helperCount);
        dashboard.setActiveRequests(activeRequests);
        dashboard.setCompletedRequestsList(completedRequests);

        return dashboard;
    }

    @GetMapping("/helper")
    public DashboardData getHelperDashboard() {
        DashboardData dashboard = new DashboardData();

        // ✅ Fetch active and completed requests
        List<Request> activeRequests = requestRepository.findByStatus("ACTIVE");
        List<Request> completedRequests = requestRepository.findByStatus("COMPLETED");

        // ✅ Count actual helpers from users collection
        long helperCount = userRepository.countByRole("HELPER");

        dashboard.setActiveRequestsCount(activeRequests.size());
        dashboard.setCompletedRequestsCount(completedRequests.size());
        dashboard.setAvailableHelpers((int) helperCount);
        dashboard.setActiveRequests(activeRequests);
        dashboard.setCompletedRequestsList(completedRequests);

        return dashboard;
    }

    @GetMapping("/helpers")
    public ResponseEntity<List<User>> getAvailableHelpers() {
        try {
            // ✅ Fetch all users with role "HELPER"
            List<User> helpers = userRepository.findByRole("HELPER");
            return ResponseEntity.ok(helpers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/offer-help")
    public ResponseEntity<?> offerHelp(@RequestBody OfferHelpRequest request) {
        try {
            // Find and update the request
            Request helpRequest = requestRepository.findById(request.getRequestId()).orElse(null);

            if (helpRequest == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Request not found"));
            }

            if ("COMPLETED".equals(helpRequest.getStatus())) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Request already completed by another helper"));
            }

            // Mark request as completed
            helpRequest.setStatus("COMPLETED");
            helpRequest.setHelperEmail(request.getHelperEmail());
            requestRepository.save(helpRequest);

            System.out.println("Request " + request.getRequestId() + " completed by: " + request.getHelperEmail());

            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "message", "Help offer accepted! Request marked as completed."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to submit help offer: " + e.getMessage()));
        }
    }

    @PostMapping("/contact-helper")
    public ResponseEntity<?> contactHelper(@RequestBody ContactHelperRequest request) {
        try {
            // In a real application, you would send an actual email here
            // For now, we'll just log the contact request
            System.out.println("Contact request from: " + request.getSeekerEmail() +
                    " to helper: " + request.getHelperEmail() +
                    " with message: " + request.getMessage());

            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "message", "Contact request sent successfully! The helper will receive your message."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to send contact request: " + e.getMessage()));
        }
    }

    // DTO for offer help request
    public static class OfferHelpRequest {
        private String helperEmail;
        private String requestId;

        public String getHelperEmail() {
            return helperEmail;
        }

        public void setHelperEmail(String helperEmail) {
            this.helperEmail = helperEmail;
        }

        public String getRequestId() {
            return requestId;
        }

        public void setRequestId(String requestId) {
            this.requestId = requestId;
        }
    }

    // DTO for contact helper request
    public static class ContactHelperRequest {
        private String seekerEmail;
        private String helperEmail;
        private String message;

        public String getSeekerEmail() {
            return seekerEmail;
        }

        public void setSeekerEmail(String seekerEmail) {
            this.seekerEmail = seekerEmail;
        }

        public String getHelperEmail() {
            return helperEmail;
        }

        public void setHelperEmail(String helperEmail) {
            this.helperEmail = helperEmail;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
