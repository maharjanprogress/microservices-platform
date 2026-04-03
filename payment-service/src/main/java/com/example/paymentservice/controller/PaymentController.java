package com.example.paymentservice.controller;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @GetMapping("/ping")
    public Map<String, String> ping() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return Map.of("service", "payment-service-test1", "status", "ok");
    }

    @GetMapping("/authenticated")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> authenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Map.of(
                "service", "payment-service",
                "user", authentication.getName(),
                "roles", joinAuthorities(authentication.getAuthorities()),
                "message", "You have access"
        );
    }

    private String joinAuthorities(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }
}
