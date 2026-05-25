package org.example.carrent.repositories.impl;

import org.example.carrent.models.Rental;
import org.example.carrent.repositories.IRentalRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RentalJdbcRepository implements IRentalRepository {

    private Connection conn() {
        return DatabaseConnection.get();
    }

    @Override
    public void add(Rental rental) {
        String sql = "INSERT INTO rental (id, vehicle_id, user_id, rent_date, return_date) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET vehicle_id = EXCLUDED.vehicle_id, " +
                "user_id = EXCLUDED.user_id, rent_date = EXCLUDED.rent_date, " +
                "return_date = EXCLUDED.return_date";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, rental.getId());
            ps.setString(2, rental.getVehicleId());
            ps.setString(3, rental.getUserLogin());
            ps.setString(4, rental.getStartDate());
            ps.setString(5, rental.getEndDate());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Błąd add rental: " + e.getMessage());
        }
    }

    @Override
    public Optional<Rental> findActiveByUserLogin(String userLogin) {
        String sql = "SELECT * FROM rental WHERE user_id = ? AND return_date IS NULL";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, userLogin);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("Błąd findActiveByUserLogin: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Rental> getAll() {
        List<Rental> list = new ArrayList<>();
        String sql = "SELECT * FROM rental";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("Błąd getAll rentals: " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean update(Rental rental) {
        String sql = "UPDATE rental SET return_date = ? WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, rental.getEndDate());
            ps.setString(2, rental.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Błąd update rental: " + e.getMessage());
        }
        return false;
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