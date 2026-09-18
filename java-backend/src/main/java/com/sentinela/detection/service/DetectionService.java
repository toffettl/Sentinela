package com.sentinela.detection.service;

import com.sentinela.detection.dto.DetectionRequest;
import com.sentinela.detection.dto.DetectionResponse;
import org.springframework.stereotype.Service;

@Service
public class DetectionService {

    public DetectionResponse receive(DetectionRequest request) {
        return DetectionResponse.fromRequest(request);
    }
}
