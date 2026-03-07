package com.financecontrol.api.adapters.controller;

import com.financecontrol.api.domain.shared.ApiConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/v1/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<Map<String, String>> getHealth() {
        return ResponseEntity.ok(Map.of(ApiConstants.STATUS_KEY, ApiConstants.STATUS_UP));
    }
}

