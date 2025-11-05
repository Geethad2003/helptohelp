package com.backend.controller;

import com.backend.model.DashboardData;
import com.backend.model.Feedback;
import com.backend.model.Request;
import com.backend.model.User;
import com.backend.repository.FeedbackRepository;
import com.backend.repository.RequestRepository;
import com.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200") // ✅ Allow Angular frontend
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final FeedbackRepository feedbackRepository;

    public DashboardController(RequestRepository requestRepository, UserRepository userRepository,
            FeedbackRepository feedbackRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.feedbackRepository = feedbackRepository;
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

    // ⭐ Submit feedback for completed request
    @PostMapping("/feedback")
    public ResponseEntity<Map<String, Object>> submitFeedback(@RequestBody FeedbackRequest feedbackRequest) {
        try {
            // Validate rating
            if (feedbackRequest.getRating() < 1 || feedbackRequest.getRating() > 5) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Rating must be between 1 and 5"));
            }

            // Check if feedback already exists for this request and seeker
            boolean feedbackExists = feedbackRepository.existsByRequestIdAndSeekerEmail(
                    feedbackRequest.getRequestId(),
                    feedbackRequest.getSeekerEmail());

            if (feedbackExists) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Feedback already submitted for this request"));
            }

            // Create and save feedback entity
            Feedback feedback = new Feedback(
                    feedbackRequest.getRequestId(),
                    feedbackRequest.getHelperEmail(),
                    feedbackRequest.getSeekerEmail(),
                    feedbackRequest.getRating(),
                    feedbackRequest.getDescription(),
                    feedbackRequest.getRequestTitle());

            // Save to database
            Feedback savedFeedback = feedbackRepository.save(feedback);

            // Log success
            System.out.println("✅ Feedback saved to database:");
            System.out.println("ID: " + savedFeedback.getId());
            System.out.println("Request ID: " + savedFeedback.getRequestId());
            System.out.println("Helper Email: " + savedFeedback.getHelperEmail());
            System.out.println("Seeker Email: " + savedFeedback.getSeekerEmail());
            System.out.println("Rating: " + savedFeedback.getRating() + "/5");
            System.out.println("Description: " + savedFeedback.getDescription());
            System.out.println("Request Title: " + savedFeedback.getRequestTitle());
            System.out.println("Submitted At: " + savedFeedback.getSubmittedAt());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Feedback submitted and saved successfully",
                    "feedbackId", savedFeedback.getId()));
        } catch (Exception e) {
            System.err.println("❌ Error saving feedback: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to submit feedback: " + e.getMessage()));
        }
    }

    // � Get feedback for a helper
    @GetMapping("/feedback/helper/{helperEmail}")
    public ResponseEntity<Map<String, Object>> getHelperFeedback(@PathVariable String helperEmail) {
        try {
            List<Feedback> feedbacks = feedbackRepository.findByHelperEmail(helperEmail);

            if (feedbacks.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "feedbacks", feedbacks,
                        "averageRating", 0.0,
                        "totalFeedbacks", 0,
                        "message", "No feedback found for this helper"));
            }

            // Calculate average rating
            double averageRating = feedbacks.stream()
                    .mapToInt(Feedback::getRating)
                    .average()
                    .orElse(0.0);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "feedbacks", feedbacks,
                    "averageRating", Math.round(averageRating * 100.0) / 100.0,
                    "totalFeedbacks", feedbacks.size()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to retrieve feedback: " + e.getMessage()));
        }
    }

    // 📊 Get feedback summary for all helpers (admin view)
    @GetMapping("/feedback/summary")
    public ResponseEntity<Map<String, Object>> getFeedbackSummary() {
        try {
            List<Feedback> allFeedbacks = feedbackRepository.findAll();

            // Group feedback by helper email and calculate stats
            Map<String, Object> summary = new HashMap<>();
            Map<String, List<Feedback>> feedbackByHelper = allFeedbacks.stream()
                    .collect(Collectors.groupingBy(Feedback::getHelperEmail));

            for (Map.Entry<String, List<Feedback>> entry : feedbackByHelper.entrySet()) {
                String helperEmail = entry.getKey();
                List<Feedback> helperFeedbacks = entry.getValue();

                double averageRating = helperFeedbacks.stream()
                        .mapToInt(Feedback::getRating)
                        .average()
                        .orElse(0.0);

                summary.put(helperEmail, Map.of(
                        "averageRating", Math.round(averageRating * 100.0) / 100.0,
                        "totalFeedbacks", helperFeedbacks.size(),
                        "lastFeedback", helperFeedbacks.get(helperFeedbacks.size() - 1).getSubmittedAt()));
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "summary", summary,
                    "totalFeedbacks", allFeedbacks.size()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to retrieve feedback summary: " + e.getMessage()));
        }
    }

    // �📝 Feedback request DTO
    static class FeedbackRequest {
        private String requestId;
        private String helperEmail;
        private String seekerEmail;
        private int rating;
        private String description;
        private String requestTitle;
        private String submittedAt;

        // Getters and setters
        public String getRequestId() {
            return requestId;
        }

        public void setRequestId(String requestId) {
            this.requestId = requestId;
        }

        public String getHelperEmail() {
            return helperEmail;
        }

        public void setHelperEmail(String helperEmail) {
            this.helperEmail = helperEmail;
        }

        public String getSeekerEmail() {
            return seekerEmail;
        }

        public void setSeekerEmail(String seekerEmail) {
            this.seekerEmail = seekerEmail;
        }

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getRequestTitle() {
            return requestTitle;
        }

        public void setRequestTitle(String requestTitle) {
            this.requestTitle = requestTitle;
        }

        public String getSubmittedAt() {
            return submittedAt;
        }

        public void setSubmittedAt(String submittedAt) {
            this.submittedAt = submittedAt;
        }
    }
}
