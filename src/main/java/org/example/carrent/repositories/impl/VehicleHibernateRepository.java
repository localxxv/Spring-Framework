package org.example.carrent.repositories.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.carrent.entities.VehicleEntity;
import org.example.carrent.hibernate.HibernateUtil;
import org.example.carrent.models.Vehicle;
import org.example.carrent.repositories.IVehicleRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VehicleHibernateRepository implements IVehicleRepository {

    private final Gson gson = new Gson();

    public VehicleHibernateRepository() {
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            VehicleEntity entity = session.get(VehicleEntity.class, id);
            return Optional.ofNullable(entity).map(this::toModel);
        }
    }

    @Override
    public List<Vehicle> getVehicles() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM VehicleEntity", VehicleEntity.class)
                    .list()
                    .stream()
                    .map(this::toModel)
                    .toList();
        }
    }

    @Override
    public boolean add(Vehicle vehicle) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            VehicleEntity existing = session.get(VehicleEntity.class, vehicle.getId());

            if (existing != null) {
                merge(existing, vehicle);
                session.merge(existing);
            } else {
                session.persist(toEntity(vehicle));
            }

            tx.commit();
            return true;

        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    @Override
    public boolean remove(String id) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            VehicleEntity entity = session.get(VehicleEntity.class, id);

            if (entity == null) {
                tx.rollback();
                return false;
            }

            session.remove(entity);

            tx.commit();
            return true;

        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    @Override
    public boolean update(Vehicle vehicle) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            VehicleEntity entity = session.get(VehicleEntity.class, vehicle.getId());

            if (entity == null) {
                tx.rollback();
                return false;
            }

            merge(entity, vehicle);
            session.merge(entity);

            tx.commit();
            return true;

        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    private void merge(VehicleEntity entity, Vehicle vehicle) {
        entity.setRented(vehicle.isRented());
        entity.setAttributes(gson.toJson(vehicle.getAttributes()));
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