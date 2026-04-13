package org.example.carrent;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class AuthService {
    private final IUserRepository userRepository;

    public AuthService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> login(String login, String password) {
        Optional<User> userOpt = userRepository.findByLogin(login);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userOpt.get();
        if (!BCrypt.checkpw(password, user.getPasswordHash())) {
            return Optional.empty();
        }

        return Optional.of(user);
    }

    public RegisterResult register(String login, String password) {
        if (login == null || login.isBlank()) {
            return RegisterResult.EMPTY_LOGIN;
        }

        if (password == null || password.length() < 4) {
            return RegisterResult.WEAK_PASSWORD;
        }

        if (userRepository.findByLogin(login).isPresent()) {
            return RegisterResult.LOGIN_ALREADY_EXISTS;
        }

        String passwordHash = hashPassword(password);
        userRepository.add(new User(login, passwordHash, Role.USER));
        return RegisterResult.SUCCESS;
    }

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}