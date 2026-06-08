package org.example.carrent.services;

import org.example.carrent.models.User;
import org.example.carrent.repositories.IUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
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
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika: " + login));
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
        return userRepository.remove(login);
    }

    public boolean removeUser(String login) {
        return userRepository.remove(login);
    }

    public boolean delete(String login) {
        return userRepository.remove(login);
    }
}