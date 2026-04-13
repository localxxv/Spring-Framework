package org.example.carrent;

import java.util.List;
import java.util.Optional;

public interface IUserRepository {
    Optional<User> findByLogin(String login);
    List<User> getUsers();
    void add(User user);
    boolean remove(String login);
    boolean update(User user);
}