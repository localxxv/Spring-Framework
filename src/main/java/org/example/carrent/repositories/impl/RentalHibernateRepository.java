package org.example.carrent.repositories.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.carrent.entities.RentalEntity;
import org.example.carrent.entities.UserEntity;
import org.example.carrent.entities.VehicleEntity;
import org.example.carrent.models.Rental;
import org.example.carrent.repositories.IRentalRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Repository
@Profile("jpa")
@Transactional
public class RentalHibernateRepository implements IRentalRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void add(Rental rental) {
        UserEntity user = entityManager.find(UserEntity.class, rental.getUserLogin());
        VehicleEntity vehicle = entityManager.find(VehicleEntity.class, rental.getVehicleId());

        if (user == null)
            throw new IllegalArgumentException("Nie znaleziono użytkownika: " + rental.getUserLogin());
        if (vehicle == null)
            throw new IllegalArgumentException("Nie znaleziono pojazdu: " + rental.getVehicleId());

        // Sprawdź czy już istnieje (na wypadek duplikatu)
        RentalEntity existing = entityManager.find(RentalEntity.class, rental.getId());
        if (existing != null) {
            existing.setReturnDate(rental.getEndDate());
            entityManager.merge(existing);
            return;
        }

        RentalEntity entity = new RentalEntity(
                rental.getId(),
                user,
                vehicle,
                rental.getStartDate(),
                rental.getEndDate()
        );
        entityManager.persist(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Rental> findActiveByUserLogin(String userLogin) {
        return entityManager
                .createQuery(
                        "FROM RentalEntity WHERE user.login = :login AND returnDate IS NULL",
                        RentalEntity.class)
                .setParameter("login", userLogin)
                .getResultStream()
                .findFirst()
                .map(this::toModel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rental> getAll() {
        return entityManager
                .createQuery("FROM RentalEntity", RentalEntity.class)
                .getResultList()
                .stream()
                .map(this::toModel)
                .toList();
    }

    @Override
    public boolean update(Rental rental) {
        RentalEntity entity = entityManager.find(RentalEntity.class, rental.getId());
        if (entity == null) return false;
        entity.setReturnDate(rental.getEndDate());
        entityManager.merge(entity);
        return true;
    }

    private Rental toModel(RentalEntity entity) {
        Rental rental = new Rental(
                entity.getId(),
                entity.getUser().getLogin(),
                entity.getVehicle().getId(),
                entity.getRentDate()
        );
        rental.setEndDate(entity.getReturnDate());
        return rental;
    }
}