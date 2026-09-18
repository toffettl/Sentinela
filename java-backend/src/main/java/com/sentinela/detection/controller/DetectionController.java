package com.sentinela.detection.controller;

import com.sentinela.detection.dto.DetectionRequest;
import com.sentinela.detection.dto.DetectionResponse;
import com.sentinela.detection.service.DetectionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/detections")
public class DetectionController {

    private final DetectionService detectionService;

    public DetectionController(DetectionService detectionService) {
        this.detectionService = detectionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DetectionResponse receive(@Valid @RequestBody DetectionRequest request) {
        return detectionService.receive(request);
    }
}
