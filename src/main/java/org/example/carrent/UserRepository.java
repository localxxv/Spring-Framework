package org.example.carrent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository implements IUserRepository {
    private List<User> users = new ArrayList<>();
    private String fileName;
    private final Gson gson = new Gson();

    public UserRepository() {
        this.fileName = "test-users.json";
    }

    public UserRepository(String fileName) {
        this.fileName = fileName;
        load();
        if (users.isEmpty()) {
            createDefaultUsers();
            save();
        }
    }

    private void save() {
        try (Writer writer = new FileWriter(fileName)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            System.out.println("Błąd zapisu użytkowników: " + e.getMessage());
        }
    }

    private void load() {
        File file = new File(fileName);
        if (!file.exists()) return;

        try (Reader reader = new FileReader(fileName)) {
            Type type = new TypeToken<List<User>>() {}.getType();
            List<User> loaded = gson.fromJson(reader, type);
            if (loaded != null) users = loaded;
        } catch (IOException e) {
            System.out.println("Błąd odczytu użytkowników: " + e.getMessage());
        }
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return users.stream()
                .filter(u -> u.getLogin().equals(login))
                .findFirst();
    }

    @Override
    public List<User> getUsers() {
        List<User> copy = new ArrayList<>();
        for (User u : users) {
            copy.add(u.copy());
        }
        return copy;
    }

    @Override
    public void add(User user) {
        users.add(user.copy());
        save();
    }

    @Override
    public boolean remove(String login) {
        boolean removed = users.removeIf(u -> u.getLogin().equals(login));
        if (removed) {
            save();
        }
        return removed;
    }

    @Override
    public boolean update(User updatedUser) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getLogin().equals(updatedUser.getLogin())) {
                users.set(i, updatedUser.copy());
                save();
                return true;
            }
        }
        return false;
    }

    private void createDefaultUsers() {
        users.add(new User("user", AuthService.hashPassword("user123"), Role.USER));
        users.add(new User("admin", AuthService.hashPassword("admin123"), Role.ADMIN));
    }
}