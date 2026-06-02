package org.example.carrent.controllers;

import org.example.carrent.dto.RegisterRequest;
import org.example.carrent.models.User;
import org.example.carrent.services.AuthService;
import org.example.carrent.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request.login(), request.password()));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        User user = userService.findByLogin(authentication.getName());

        return ResponseEntity.ok(Map.of(
                "login", user.getLogin(),
                "role", user.getRole().name()
        ));
    }
}