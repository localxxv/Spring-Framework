package org.example.carrent.repositories.impl;

import org.example.carrent.models.Role;
import org.example.carrent.models.User;
import org.example.carrent.repositories.IUserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("jdbc")
public class UserJdbcRepository implements IUserRepository {

    private final DataSource dataSource;

    public UserJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();

        String sql = """
                SELECT login, password_hash, role, address
                FROM users
                """;

        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Błąd getUsers: " + e.getMessage(), e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }

        return users;
    }

    @Override
    public Optional<User> findByLogin(String login) {
        String sql = """
                SELECT login, password_hash, role, address
                FROM users
                WHERE login = ?
                """;

        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, login);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Błąd findByLogin: " + e.getMessage(), e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }

        return Optional.empty();
    }

    @Override
    public void add(User user) {
        String sql = """
                INSERT INTO users (login, password_hash, role, address)
                VALUES (?, ?, ?, ?)
                ON CONFLICT (login) DO UPDATE SET
                    password_hash = EXCLUDED.password_hash,
                    role = EXCLUDED.role,
                    address = EXCLUDED.address
                """;

        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getRole().name());
            ps.setString(4, user.getAddress());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Błąd add user: " + e.getMessage(), e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public boolean update(User user) {
        String sql = """
                UPDATE users
                SET password_hash = ?,
                    role = ?,
                    address = ?
                WHERE login = ?
                """;

        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getPasswordHash());
            ps.setString(2, user.getRole().name());
            ps.setString(3, user.getAddress());
            ps.setString(4, user.getLogin());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Błąd update user: " + e.getMessage(), e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public boolean remove(String login) {
        String sql = """
                DELETE FROM users
                WHERE login = ?
                """;

        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, login);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Błąd remove user: " + e.getMessage(), e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getString("login"),
                rs.getString("password_hash"),
                Role.valueOf(rs.getString("role")),
                rs.getString("address")
        );
    }
}