package com.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "requests")
public class Request {
    @Id
    private String id;
    private String title;
    private String description;
    private String category;
    private String location;
    private String urgency;
    private String seekerEmail;
    private String status; // "ACTIVE", "COMPLETED"
    private String helperEmail; // Email of helper who completed the request
}
