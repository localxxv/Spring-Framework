package org.example.carrent;

import java.util.List;

public interface IUserRepository {
    User getUser(String login);
    List<User> getUsers();
    void save();
    void load();
    boolean update(User user);
}