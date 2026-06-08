package org.example.carrent.repositories.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.carrent.models.Role;
import org.example.carrent.models.User;
import org.example.carrent.repositories.IUserRepository;
import org.example.carrent.services.AuthService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("json")
public class UserRepository implements IUserRepository {

    private List<User> users = new ArrayList<>();
    private String fileName;
    private final Gson gson = new Gson();

    public UserRepository() {
        this("users.json");
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
            throw new RuntimeException("Błąd zapisu użytkowników: " + e.getMessage(), e);
        }
    }

    private void load() {
        File file = new File(fileName);

        if (!file.exists()) {
            return;
        }

        try (Reader reader = new FileReader(fileName)) {
            Type type = new TypeToken<List<User>>() {}.getType();
            List<User> loaded = gson.fromJson(reader, type);

            if (loaded != null) {
                users = loaded;
            }
        } catch (IOException e) {
            throw new RuntimeException("Błąd odczytu użytkowników: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return users.stream()
                .filter(user -> user.getLogin().equals(login))
                .findFirst()
                .map(User::copy);
    }

    @Override
    public List<User> getUsers() {
        List<User> copy = new ArrayList<>();

        for (User user : users) {
            copy.add(user.copy());
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
        boolean removed = users.removeIf(user -> user.getLogin().equals(login));

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