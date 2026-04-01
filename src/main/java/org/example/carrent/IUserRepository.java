package org.example.carrent;

import java.util.List;

public interface IUserRepository {
    User getUser(String login);
    List<User> getUsers();
    boolean update(User user);
    boolean addUser(String login, String password);
    boolean removeUser(String login);
}

