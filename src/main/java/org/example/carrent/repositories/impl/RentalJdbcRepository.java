package org.example.carrent.repositories.impl;

import org.example.carrent.models.Rental;
import org.example.carrent.repositories.IRentalRepository;
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
public class RentalJdbcRepository implements IRentalRepository {

    private final DataSource dataSource;

    public RentalJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void add(Rental rental) {
        String sql = """
                INSERT INTO rental (id, vehicle_id, user_id, rent_date, return_date)
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT (id) DO UPDATE SET
                    vehicle_id = EXCLUDED.vehicle_id,
                    user_id = EXCLUDED.user_id,
                    rent_date = EXCLUDED.rent_date,
                    return_date = EXCLUDED.return_date
                """;

        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, rental.getId());
            ps.setString(2, rental.getVehicleId());
            ps.setString(3, rental.getUserLogin());
            ps.setString(4, rental.getStartDate());
            ps.setString(5, rental.getEndDate());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Błąd add rental: " + e.getMessage(), e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public Optional<Rental> findActiveByUserLogin(String userLogin) {
        String sql = """
                SELECT id, user_id, vehicle_id, rent_date, return_date
                FROM rental
                WHERE user_id = ?
                  AND return_date IS NULL
                LIMIT 1
                """;

        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userLogin);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd findActiveByUserLogin: " + e.getMessage(), e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }

        return Optional.empty();
    }

    @Override
    public List<Rental> getAll() {
        List<Rental> rentals = new ArrayList<>();

        String sql = """
                SELECT id, user_id, vehicle_id, rent_date, return_date
                FROM rental
                ORDER BY rent_date DESC
                """;

        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rentals.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Błąd getAll rentals: " + e.getMessage(), e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }

        return rentals;
    }

    @Override
    public boolean update(Rental rental) {
        String sql = """
                UPDATE rental
                SET return_date = ?
                WHERE id = ?
                """;

        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, rental.getEndDate());
            ps.setString(2, rental.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Błąd update rental: " + e.getMessage(), e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    private Rental mapRow(ResultSet rs) throws SQLException {
        Rental rental = new Rental(
                rs.getString("id"),
                rs.getString("user_id"),
                rs.getString("vehicle_id"),
                rs.getString("rent_date")
        );

        rental.setEndDate(rs.getString("return_date"));

        return rental;
    }
}