package com.apex_aura.content_manager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/health")
public class HealthCheck {
    @GetMapping
    public String healthCheck() {
        RestTemplate restTemplate = new RestTemplate();
//        var a = restTemplate.getForEntity("http://localhost:9091/content/health", String.class);
        return "Content Manager is up and running! ";
    }
    @GetMapping("/health")
    public String healthCheck1() {

        return "Content Manager is up and running for new!";
    }
}
