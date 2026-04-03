package com.example.apigateway.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private static final String LOGIN_PATH = "/api/users/login";
    private static final String API_PREFIX = "/api/";
    private static final String UI_PREFIX = "/ui";
    private static final String ACTUATOR_PREFIX = "/actuator";
    private static final String USERNAME_HEADER = "X-User-Name";
    private static final String ROLES_HEADER = "X-User-Roles";
    private static final String GATEWAY_SECRET_HEADER = "X-Gateway-Secret";

    private final String jwtSecret;
    private final String gatewaySecret;

    public JwtAuthenticationFilter(
            @Value("${app.security.jwt-secret}") String jwtSecret,
            @Value("${app.security.gateway-secret}") String gatewaySecret) {
        this.jwtSecret = jwtSecret;
        this.gatewaySecret = gatewaySecret;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        if (path.startsWith(UI_PREFIX) || path.startsWith(ACTUATOR_PREFIX) || !path.startsWith(API_PREFIX)) {
            return chain.filter(exchange);
        }

        ServerHttpRequest.Builder requestBuilder = exchange.getRequest().mutate()
                .headers(headers -> headers.set(GATEWAY_SECRET_HEADER, gatewaySecret));

        if (isPublicApiPath(path)) {
            return chain.filter(exchange.mutate().request(requestBuilder.build()).build());
        }

        String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        try {
            Claims claims = parseClaims(authorization.substring(7));
            String username = claims.getSubject();
            String roles = claims.get("roles", String.class);

            requestBuilder.headers(headers -> {
                headers.set(USERNAME_HEADER, username);
                headers.set(ROLES_HEADER, roles == null ? "" : roles);
            });

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    toAuthorities(roles)
            );

            ServerWebExchange mutatedExchange = exchange.mutate().request(requestBuilder.build()).build();
            return chain.filter(mutatedExchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        } catch (Exception exception) {
            return unauthorized(exchange);
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey(jwtSecret))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isPublicApiPath(String path) {
        return LOGIN_PATH.equals(path) || path.endsWith("/ping");
    }

    private Collection<SimpleGrantedAuthority> toAuthorities(String roles) {
        if (roles == null || roles.isBlank()) {
            return List.of();
        }
        return Arrays.stream(roles.split(","))
                .map(String::trim)
                .filter(role -> !role.isEmpty())
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    private SecretKey signingKey(String secret) {
        try {
            byte[] keyBytes = MessageDigest.getInstance("SHA-256")
                    .digest(secret.getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Unable to create signing key", exception);
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
