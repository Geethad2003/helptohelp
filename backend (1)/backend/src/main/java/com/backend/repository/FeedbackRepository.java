package com.backend.repository;

import com.backend.model.Feedback;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeedbackRepository extends MongoRepository<Feedback, String> {

    // Find feedback by request ID
    List<Feedback> findByRequestId(String requestId);

    // Find feedback by helper email
    List<Feedback> findByHelperEmail(String helperEmail);

    // Find feedback by seeker email
    List<Feedback> findBySeekerEmail(String seekerEmail);

    // Find feedback by helper email and rating (for filtering good/bad reviews)
    List<Feedback> findByHelperEmailAndRatingGreaterThanEqual(String helperEmail, int rating);

    // Find feedback by rating range
    List<Feedback> findByRatingBetween(int minRating, int maxRating);

    // Check if feedback already exists for a specific request and seeker
    boolean existsByRequestIdAndSeekerEmail(String requestId, String seekerEmail);
}