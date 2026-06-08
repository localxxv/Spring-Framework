package org.example.carrent.repositories.impl;

import org.example.carrent.entities.RentalEntity;
import org.example.carrent.entities.UserEntity;
import org.example.carrent.entities.VehicleEntity;
import org.example.carrent.hibernate.HibernateUtil;
import org.example.carrent.models.Rental;
import org.example.carrent.repositories.IRentalRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("jpa")
public class RentalHibernateRepository implements IRentalRepository {

    public RentalHibernateRepository() {
    }

    @Override
    public void add(Rental rental) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            RentalEntity existing = session.get(RentalEntity.class, rental.getId());

            if (existing != null) {
                existing.setReturnDate(rental.getEndDate());
                session.merge(existing);
                tx.commit();
                return;
            }

            UserEntity user = session.get(UserEntity.class, rental.getUserLogin());
            VehicleEntity vehicle = session.get(VehicleEntity.class, rental.getVehicleId());

            if (user == null) {
                throw new IllegalArgumentException("Nie znaleziono użytkownika: " + rental.getUserLogin());
            }

            if (vehicle == null) {
                throw new IllegalArgumentException("Nie znaleziono pojazdu: " + rental.getVehicleId());
            }

            RentalEntity entity = new RentalEntity(
                    rental.getId(),
                    user,
                    vehicle,
                    rental.getStartDate(),
                    rental.getEndDate()
            );

            session.persist(entity);

            tx.commit();

        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    @Override
    public Optional<Rental> findActiveByUserLogin(String userLogin) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM RentalEntity WHERE user.login = :login AND returnDate IS NULL",
                            RentalEntity.class
                    )
                    .setParameter("login", userLogin)
                    .uniqueResultOptional()
                    .map(this::toModel);
        }
    }

    @Override
    public List<Rental> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM RentalEntity", RentalEntity.class)
                    .list()
                    .stream()
                    .map(this::toModel)
                    .toList();
        }
    }

    @Override
    public boolean update(Rental rental) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            RentalEntity entity = session.get(RentalEntity.class, rental.getId());

            if (entity == null) {
                tx.rollback();
                return false;
            }

            entity.setReturnDate(rental.getEndDate());

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