package org.example.carrent.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "CarRent API działa. Endpointy: /vehicles, /auth/me, /users, /rentals";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}