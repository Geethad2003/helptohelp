package com.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.backend.model.Request;
import java.util.List;

public interface RequestRepository extends MongoRepository<Request, String> {
    List<Request> findByStatus(String status);

    List<Request> findBySeekerEmail(String seekerEmail);

    List<Request> findByHelperEmail(String helperEmail);

    long countByStatus(String status);
}