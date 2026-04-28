package org.example.carrent;

import java.util.List;

public class UserService {
    private final IUserRepository userRepository;
    private final RentalService rentalService;

    public UserService(IUserRepository userRepository, RentalService rentalService) {
        this.userRepository = userRepository;
        this.rentalService = rentalService;
    }

    public List<User> findAllUsers() {
        return userRepository.getUsers();
    }

    public User findByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika."));
    }

    public void removeUser(String login) {
        if (rentalService.hasActiveRental(login)) {
            throw new IllegalStateException("Nie można usunąć użytkownika, bo ma wypożyczony pojazd.");
        }

        if (!userRepository.remove(login)) {
            throw new IllegalArgumentException("Nie znaleziono użytkownika.");
        }
    }
}