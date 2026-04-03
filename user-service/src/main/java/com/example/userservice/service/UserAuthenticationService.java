package com.example.userservice.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class UserAuthenticationService {

    private static final Map<String, AuthUser> USERS = Map.of(
            "user", new AuthUser("user123", List.of("ROLE_USER")),
            "admin", new AuthUser("admin123", List.of("ROLE_ADMIN"))
    );

    public AuthUser authenticate(String username, String password) {
        AuthUser authUser = USERS.get(username);
        if (authUser == null || !authUser.password().equals(password)) {
            return null;
        }
        return authUser;
    }

    public record AuthUser(String password, List<String> roles) {
    }
}
