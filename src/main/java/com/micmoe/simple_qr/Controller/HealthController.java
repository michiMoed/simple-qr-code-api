package com.micmoe.simple_qr.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("healthy");
    }
}
