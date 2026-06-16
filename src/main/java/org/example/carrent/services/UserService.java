package org.example.carrent.services;

import org.example.carrent.models.User;
import org.example.carrent.repositories.IRentalRepository;
import org.example.carrent.repositories.IUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final IUserRepository userRepository;
    private final IRentalRepository rentalRepository;

    public UserService(IUserRepository userRepository,
                       IRentalRepository rentalRepository) {
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
    }

    public List<User> getUsers() {
        return userRepository.getUsers();
    }

    public List<User> findAllUsers() {
        return userRepository.getUsers();
    }

    public List<User> findAll() {
        return userRepository.getUsers();
    }

    public List<User> list() {
        return userRepository.getUsers();
    }

    public User findByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Nie znaleziono użytkownika: " + login
                ));
    }

    public User findById(String id) {
        return findByLogin(id);
    }

    public User get(String id) {
        return findByLogin(id);
    }

    public User getUser(String login) {
        return findByLogin(login);
    }

    public void add(User user) {
        userRepository.add(user);
    }

    public boolean update(User user) {
        return userRepository.update(user);
    }

    public boolean remove(String login) {
        checkCanDeleteUser(login);
        return userRepository.remove(login);
    }

    public boolean removeUser(String login) {
        checkCanDeleteUser(login);
        return userRepository.remove(login);
    }

    public boolean delete(String login) {
        checkCanDeleteUser(login);
        return userRepository.remove(login);
    }

    private void checkCanDeleteUser(String login) {
        findByLogin(login);

        if (rentalRepository.findActiveByUserLogin(login).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Nie można usunąć użytkownika, który ma aktywne wypożyczenie."
            );
        }
    }
}