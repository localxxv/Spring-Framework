package org.example.carrent.repositories.impl;

import org.example.carrent.models.Role;
import org.example.carrent.models.User;
import org.example.carrent.repositories.IUserRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserJdbcRepository implements IUserRepository {

    private Connection conn() {
        return DatabaseConnection.get();
    }

    @Override
    public Optional<User> findByLogin(String login) {
        String sql = "SELECT * FROM users WHERE login = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("Błąd findByLogin: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) users.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("Błąd getUsers: " + e.getMessage());
        }
        return users;
    }

    @Override
    public void add(User user) {
        String sql = "INSERT INTO users (login, password_hash, role) VALUES (?, ?, ?) " +
                "ON CONFLICT (login) DO UPDATE SET password_hash = EXCLUDED.password_hash, role = EXCLUDED.role";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getRole().name());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Błąd add user: " + e.getMessage());
        }
    }

    @Override
    public boolean remove(String login) {
        String sql = "DELETE FROM users WHERE login = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, login);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Błąd remove user: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean update(User user) {
        String sql = "UPDATE users SET password_hash = ?, role = ? WHERE login = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, user.getPasswordHash());
            ps.setString(2, user.getRole().name());
            ps.setString(3, user.getLogin());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Błąd update user: " + e.getMessage());
        }
        return false;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getString("login"),
                rs.getString("password_hash"),
                Role.valueOf(rs.getString("role"))
        );
    }
}