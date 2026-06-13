package org.example.carrent.services;

import org.example.carrent.models.Role;
import org.example.carrent.models.User;
import org.example.carrent.repositories.IUserRepository;
import org.example.carrent.security.JwtService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final IUserRepository userRepository;
    private final JwtService jwtService;

    public AuthService(IUserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public String register(String login, String password) {
        if (login == null || login.isBlank()) {
            throw new RuntimeException("Login jest wymagany.");
        }

        if (password == null || password.isBlank()) {
            throw new RuntimeException("Hasło jest wymagane.");
        }

        if (userRepository.findByLogin(login).isPresent()) {
            throw new RuntimeException("Użytkownik już istnieje.");
        }

        User user = new User(
                login,
                hashPassword(password),
                Role.USER
        );

        userRepository.add(user);

        return jwtService.generateToken(user);
    }

    public String login(String login, String password) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Niepoprawny login lub hasło."));

        if (!checkPassword(password, user.getPasswordHash())) {
            throw new RuntimeException("Niepoprawny login lub hasło.");
        }

        return jwtService.generateToken(user);
    }

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPassword(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }
}