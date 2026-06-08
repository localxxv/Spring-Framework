package org.example.carrent.services;

import org.example.carrent.models.Role;
import org.example.carrent.models.User;
import org.example.carrent.repositories.IUserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final IUserRepository userRepository;

    public AuthService(IUserRepository userRepository) {
        this.userRepository = userRepository;
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

        User user = new User(login, hashPassword(password), Role.USER);
        userRepository.add(user);

        return "Użytkownik zarejestrowany.";
    }

    public Optional<User> login(String login, String password) {
        Optional<User> userOptional = userRepository.findByLogin(login);

        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();

        if (!checkPassword(password, user.getPasswordHash())) {
            return Optional.empty();
        }

        return Optional.of(user);
    }

    public User authenticate(String login, String password) {
        return login(login, password)
                .orElseThrow(() -> new RuntimeException("Niepoprawny login lub hasło."));
    }

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }



    public static boolean checkPassword(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }
}