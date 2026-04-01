package org.example.carrent;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository implements IUserRepository {
    private final List<User> users = new ArrayList<>();
    private final String fileName;

    public UserRepository() {
        this("users.csv");
    }

    public UserRepository(String fileName) {
        this.fileName = fileName;
        load();
    }

    @Override
    public User getUser(String login) {
        for (User user : users) {
            if (user.getLogin().equals(login)) {
                return user.copy();
            }
        }
        return null;
    }

    @Override
    public List<User> getUsers() {
        List<User> copy = new ArrayList<>();
        for (User user : users) {
            copy.add(user.copy());
        }
        return copy;
    }


    public void save() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (User user : users) {
                writer.println(user.toCSV());
            }
        } catch (IOException e) {
            System.out.println("Błąd zapisu użytkowników: " + e.getMessage());
        }
    }


    public void load() {
        users.clear();

        File file = new File(fileName);
        if (!file.exists()) {
            createDefaultUsers();
            save();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split(";");
                String login = parts[0];
                String passwordHash = parts[1];
                Role role = Role.valueOf(parts[2]);
                String rentedVehicleId = (parts.length > 3 && !parts[3].isEmpty()) ? parts[3] : null;

                users.add(new User(login, passwordHash, role, rentedVehicleId));
            }
        } catch (IOException e) {
            System.out.println("Błąd odczytu użytkowników: " + e.getMessage());
        }
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
        users.add(new User("user", Authentication.hashPassword("user123"), Role.USER, null));
        users.add(new User("admin", Authentication.hashPassword("admin123"), Role.ADMIN, null));
    }

    @Override
    public boolean addUser(String login, String password) {
        if (getUser(login) != null) {
            return false;
        }
        users.add(new User(login, Authentication.hashPassword(password), Role.USER, null));
        save();
        return true;
    }

    @Override
    public boolean removeUser(String login) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getLogin().equals(login)) {
                if (users.get(i).getRentedVehicleId() != null) {
                    return false; // має випожичаний транспорт
                }
                users.remove(i);
                save();
                return true;
            }
        }
        return false;
    }
}