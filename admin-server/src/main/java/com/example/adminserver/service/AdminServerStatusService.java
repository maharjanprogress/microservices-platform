package com.example.adminserver.service;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class AdminServerStatusService {

    public Map<String, String> status() {
        return Map.of(
                "service", "admin-server",
                "status", "ok"
        );
    }
}
