package com.backend.model;

import java.util.List;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardData {
    private int activeRequestsCount;
    private int completedRequestsCount;
    private int availableHelpers;
    private List<Request> activeRequests;
    private List<Request> completedRequestsList;
}
