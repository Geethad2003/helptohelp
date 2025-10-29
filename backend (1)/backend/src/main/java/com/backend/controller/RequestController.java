package com.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.backend.model.Request;
import com.backend.repository.RequestRepository;

@CrossOrigin(origins = "http://localhost:4200") // ✅ allow Angular frontend
@RestController
@RequestMapping("/api/requests")
public class RequestController {

    @Autowired
    private RequestRepository requestRepository;

    // ✅ Create new request
    @PostMapping("/new")
    public ResponseEntity<Request> createRequest(@RequestBody Request request) {
        Request saved = requestRepository.save(request);
        return ResponseEntity.ok(saved);
    }

    // ✅ Optional: Get all requests
    @GetMapping
    public ResponseEntity<?> getAllRequests() {
        return ResponseEntity.ok(requestRepository.findAll());
    }
}
