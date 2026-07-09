package org.example.carrent.repositories.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.carrent.models.Vehicle;
import org.example.carrent.repositories.IVehicleRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Profile("jdbc")
public class VehicleJdbcRepository implements IVehicleRepository {

    private final DataSource dataSource;
    private final Gson gson = new Gson();

    public VehicleJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        String sql = "SELECT * FROM vehicle WHERE id = ?";

        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Błąd findById vehicle", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }

        return Optional.empty();
    }

    @Override
    public List<Vehicle> getVehicles() {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT * FROM vehicle";

        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Błąd getVehicles", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }

        return list;
    }

    @Override
    public boolean add(Vehicle vehicle) {
        String sql = """
                INSERT INTO vehicle (id, category, brand, model, year, plate, price, rented, attributes, location_name)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (id) DO UPDATE SET
                    category = EXCLUDED.category,
                    brand = EXCLUDED.brand,
                    model = EXCLUDED.model,
                    year = EXCLUDED.year,
                    plate = EXCLUDED.plate,
                    price = EXCLUDED.price,
                    rented = EXCLUDED.rented,
                    attributes = EXCLUDED.attributes,
                    location_name = EXCLUDED.location_name
                """;

        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, vehicle.getId());
            ps.setString(2, vehicle.getCategory());
            ps.setString(3, vehicle.getBrand());
            ps.setString(4, vehicle.getModel());
            ps.setInt(5, vehicle.getYear());
            ps.setString(6, vehicle.getPlate());
            ps.setDouble(7, vehicle.getPrice());
            ps.setBoolean(8, vehicle.isRented());
            ps.setString(9, gson.toJson(vehicle.getAttributes()));
            ps.setString(10, vehicle.getLocationName());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Błąd add vehicle", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public boolean remove(String id) {
        String sql = "DELETE FROM vehicle WHERE id = ?";

        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Błąd remove vehicle", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public boolean update(Vehicle vehicle) {
        String sql = """
                UPDATE vehicle
                SET category = ?,
                    brand = ?,
                    model = ?,
                    year = ?,
                    plate = ?,
                    price = ?,
                    rented = ?,
                    attributes = ?,
                    location_name = ?
                WHERE id = ?
                """;

        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, vehicle.getCategory());
            ps.setString(2, vehicle.getBrand());
            ps.setString(3, vehicle.getModel());
            ps.setInt(4, vehicle.getYear());
            ps.setString(5, vehicle.getPlate());
            ps.setDouble(6, vehicle.getPrice());
            ps.setBoolean(7, vehicle.isRented());
            ps.setString(8, gson.toJson(vehicle.getAttributes()));
            ps.setString(9, vehicle.getLocationName());
            ps.setString(10, vehicle.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Błąd update vehicle", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    private Vehicle mapRow(ResultSet rs) throws SQLException {
        Vehicle vehicle = new Vehicle(
                rs.getString("id"),
                rs.getString("category"),
                rs.getString("brand"),
                rs.getString("model"),
                rs.getInt("year"),
                rs.getString("plate"),
                rs.getDouble("price"),
                rs.getBoolean("rented")
        );

        vehicle.setLocationName(rs.getString("location_name"));

        String attrJson = rs.getString("attributes");

        if (attrJson != null && !attrJson.isBlank()) {
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> attrs = gson.fromJson(attrJson, type);

            if (attrs != null) {
                attrs.forEach(vehicle::addAttribute);
            }
        }

        return vehicle;
    }
}