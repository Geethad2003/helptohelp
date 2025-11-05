package com.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "feedbacks")
public class Feedback {
    @Id
    private String id;
    private String requestId;
    private String helperEmail;
    private String seekerEmail;
    private int rating;
    private String description;
    private String requestTitle;
    private LocalDateTime submittedAt;

    // Default constructor
    public Feedback() {
    }

    // Constructor with parameters
    public Feedback(String requestId, String helperEmail, String seekerEmail,
            int rating, String description, String requestTitle) {
        this.requestId = requestId;
        this.helperEmail = helperEmail;
        this.seekerEmail = seekerEmail;
        this.rating = rating;
        this.description = description;
        this.requestTitle = requestTitle;
        this.submittedAt = LocalDateTime.now();
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "id='" + id + '\'' +
                ", requestId='" + requestId + '\'' +
                ", helperEmail='" + helperEmail + '\'' +
                ", seekerEmail='" + seekerEmail + '\'' +
                ", rating=" + rating +
                ", description='" + description + '\'' +
                ", requestTitle='" + requestTitle + '\'' +
                ", submittedAt=" + submittedAt +
                '}';
    }
}