package org.example.carrent.repositories.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.carrent.models.Vehicle;
import org.example.carrent.repositories.IVehicleRepository;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VehicleJdbcRepository implements IVehicleRepository {

    private final Gson gson = new Gson();

    private Connection conn() {
        return DatabaseConnection.get();
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        String sql = "SELECT * FROM vehicle WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("Błąd findById: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Vehicle> getVehicles() {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT * FROM vehicle";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("Błąd getVehicles: " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean add(Vehicle vehicle) {
        String sql = "INSERT INTO vehicle (id, category, brand, model, year, plate, price, rented, attributes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET category = EXCLUDED.category, brand = EXCLUDED.brand, " +
                "model = EXCLUDED.model, year = EXCLUDED.year, plate = EXCLUDED.plate, " +
                "price = EXCLUDED.price, rented = EXCLUDED.rented, attributes = EXCLUDED.attributes";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, vehicle.getId());
            ps.setString(2, vehicle.getCategory());
            ps.setString(3, vehicle.getBrand());
            ps.setString(4, vehicle.getModel());
            ps.setInt(5, vehicle.getYear());
            ps.setString(6, vehicle.getPlate());
            ps.setDouble(7, vehicle.getPrice());
            ps.setBoolean(8, vehicle.isRented());
            ps.setString(9, gson.toJson(vehicle.getAttributes()));
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Błąd add vehicle: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean remove(String id) {
        String sql = "DELETE FROM vehicle WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Błąd remove vehicle: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean update(Vehicle vehicle) {
        String sql = "UPDATE vehicle SET category=?, brand=?, model=?, year=?, plate=?, price=?, rented=?, attributes=? WHERE id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, vehicle.getCategory());
            ps.setString(2, vehicle.getBrand());
            ps.setString(3, vehicle.getModel());
            ps.setInt(4, vehicle.getYear());
            ps.setString(5, vehicle.getPlate());
            ps.setDouble(6, vehicle.getPrice());
            ps.setBoolean(7, vehicle.isRented());
            ps.setString(8, gson.toJson(vehicle.getAttributes()));
            ps.setString(9, vehicle.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Błąd update vehicle: " + e.getMessage());
        }
        return false;
    }

    private Vehicle mapRow(ResultSet rs) throws SQLException {
        Vehicle v = new Vehicle(
                rs.getString("id"),
                rs.getString("category"),
                rs.getString("brand"),
                rs.getString("model"),
                rs.getInt("year"),
                rs.getString("plate"),
                rs.getDouble("price"),
                rs.getBoolean("rented")
        );
        String attrJson = rs.getString("attributes");
        if (attrJson != null && !attrJson.isBlank()) {
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> attrs = gson.fromJson(attrJson, type);
            if (attrs != null) attrs.forEach(v::addAttribute);
        }
        return v;
    }
}