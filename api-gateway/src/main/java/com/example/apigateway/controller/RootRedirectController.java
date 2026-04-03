package com.example.apigateway.controller;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootRedirectController {

    @GetMapping("/")
    public ResponseEntity<Void> redirectToUi() {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/ui/index.html"));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
