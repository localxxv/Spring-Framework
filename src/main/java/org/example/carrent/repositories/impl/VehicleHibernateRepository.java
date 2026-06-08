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
        entityManager.merge(toEntity(vehicle));
        return true;
    }

    @Override
    public boolean remove(String id) {
        VehicleEntity entity = entityManager.find(VehicleEntity.class, id);

        if (entity == null) {
            return false;
        }

        entityManager.remove(entity);
        return true;
    }

    @Override
    public boolean update(Vehicle vehicle) {
        VehicleEntity existing = entityManager.find(VehicleEntity.class, vehicle.getId());

        if (existing == null) {
            return false;
        }

        entityManager.merge(toEntity(vehicle));
        return true;
    }

    private VehicleEntity toEntity(Vehicle vehicle) {
        return new VehicleEntity(
                vehicle.getId(),
                vehicle.getCategory(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getYear(),
                vehicle.getPlate(),
                vehicle.getPrice(),
                vehicle.isRented(),
                gson.toJson(vehicle.getAttributes())
        );
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