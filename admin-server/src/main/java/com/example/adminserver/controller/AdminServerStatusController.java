package com.example.adminserver.controller;

import java.util.Map;

import com.example.adminserver.service.AdminServerStatusService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminServerStatusController {

    private final AdminServerStatusService adminServerStatusService;

    public AdminServerStatusController(AdminServerStatusService adminServerStatusService) {
        this.adminServerStatusService = adminServerStatusService;
    }

    @GetMapping("/ping")
    public Map<String, String> ping() {
        return adminServerStatusService.status();
    }
}
