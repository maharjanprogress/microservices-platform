package com.example.userservice.controller;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.userservice.dto.LoginRequest;
import com.example.userservice.dto.LoginResponse;
import com.example.userservice.security.JwtUtil;
import com.example.userservice.service.UserAuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserAuthenticationService userAuthenticationService;
    private final JwtUtil jwtUtil;

    public UserController(UserAuthenticationService userAuthenticationService, JwtUtil jwtUtil) {
        this.userAuthenticationService = userAuthenticationService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        UserAuthenticationService.AuthUser authUser =
                userAuthenticationService.authenticate(loginRequest.username(), loginRequest.password());
        if (authUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Bad credentials"));
        }

        String token = jwtUtil.generateToken(loginRequest.username(), authUser.roles());
        return ResponseEntity.ok(new LoginResponse(token, loginRequest.username(), authUser.roles()));
    }

    @GetMapping("/ping")
    public Map<String, String> ping() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return Map.of("service", "user-service-test1", "status", "ok");
    }

    @GetMapping("/authenticated")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> authenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Map.of(
                "service", "user-service",
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
