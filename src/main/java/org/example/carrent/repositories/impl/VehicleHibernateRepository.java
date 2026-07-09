package org.example.carrent.repositories.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.carrent.entities.VehicleEntity;
import org.example.carrent.models.Vehicle;
import org.example.carrent.repositories.IVehicleRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Profile("jpa")
@Transactional
public class VehicleHibernateRepository implements IVehicleRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final Gson gson = new Gson();

    @Override
    @Transactional(readOnly = true)
    public Optional<Vehicle> findById(String id) {
        VehicleEntity entity = entityManager.find(VehicleEntity.class, id);
        return Optional.ofNullable(entity).map(this::toModel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> getVehicles() {
        return entityManager
                .createQuery("FROM VehicleEntity", VehicleEntity.class)
                .getResultList()
                .stream()
                .map(this::toModel)
                .toList();
    }

    @Override
    public boolean add(Vehicle vehicle) {
        int result = entityManager.createNativeQuery("""
                INSERT INTO vehicle (id, category, brand, model, year, plate, price, rented, attributes, location_name)
                VALUES (:id, :category, :brand, :model, :year, :plate, :price, :rented, :attributes, :locationName)
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
                """)
                .setParameter("id", vehicle.getId())
                .setParameter("category", vehicle.getCategory())
                .setParameter("brand", vehicle.getBrand())
                .setParameter("model", vehicle.getModel())
                .setParameter("year", vehicle.getYear())
                .setParameter("plate", vehicle.getPlate())
                .setParameter("price", vehicle.getPrice())
                .setParameter("rented", vehicle.isRented())
                .setParameter("attributes", gson.toJson(vehicle.getAttributes()))
                .setParameter("locationName", vehicle.getLocationName())
                .executeUpdate();

        return result > 0;
    }

    @Override
    public boolean remove(String id) {
        int result = entityManager.createNativeQuery("""
                DELETE FROM vehicle
                WHERE id = :id
                """)
                .setParameter("id", id)
                .executeUpdate();

        return result > 0;
    }

    @Override
    public boolean update(Vehicle vehicle) {
        int result = entityManager.createNativeQuery("""
                UPDATE vehicle
                SET category = :category,
                    brand = :brand,
                    model = :model,
                    year = :year,
                    plate = :plate,
                    price = :price,
                    rented = :rented,
                    attributes = :attributes,
                    location_name = :locationName
                WHERE id = :id
                """)
                .setParameter("category", vehicle.getCategory())
                .setParameter("brand", vehicle.getBrand())
                .setParameter("model", vehicle.getModel())
                .setParameter("year", vehicle.getYear())
                .setParameter("plate", vehicle.getPlate())
                .setParameter("price", vehicle.getPrice())
                .setParameter("rented", vehicle.isRented())
                .setParameter("attributes", gson.toJson(vehicle.getAttributes()))
                .setParameter("locationName", vehicle.getLocationName())
                .setParameter("id", vehicle.getId())
                .executeUpdate();

        return result > 0;
    }

    private Vehicle toModel(VehicleEntity entity) {
        Vehicle vehicle = new Vehicle(
                entity.getId(),
                entity.getCategory(),
                entity.getBrand(),
                entity.getModel(),
                entity.getYear(),
                entity.getPlate(),
                entity.getPrice(),
                entity.isRented()
        );

        vehicle.setLocationName(entity.getLocationName());

        if (entity.getAttributes() != null && !entity.getAttributes().isBlank()) {
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> attributes = gson.fromJson(entity.getAttributes(), type);

            if (attributes != null) {
                attributes.forEach(vehicle::addAttribute);
            }
        }

        return vehicle;
    }
}