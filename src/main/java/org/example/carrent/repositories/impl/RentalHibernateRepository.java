package org.example.carrent.repositories.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
        entityManager.createNativeQuery("""
                INSERT INTO rental (id, vehicle_id, user_id, rent_date, return_date)
                VALUES (:id, :vehicleId, :userId, :rentDate, :returnDate)
                ON CONFLICT (id) DO UPDATE SET
                    vehicle_id = EXCLUDED.vehicle_id,
                    user_id = EXCLUDED.user_id,
                    rent_date = EXCLUDED.rent_date,
                    return_date = EXCLUDED.return_date
                """)
                .setParameter("id", rental.getId())
                .setParameter("vehicleId", rental.getVehicleId())
                .setParameter("userId", rental.getUserLogin())
                .setParameter("rentDate", rental.getStartDate())
                .setParameter("returnDate", rental.getEndDate())
                .executeUpdate();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Rental> findActiveByUserLogin(String userLogin) {
        List<Object[]> rows = entityManager.createNativeQuery("""
                SELECT id, user_id, vehicle_id, rent_date, return_date
                FROM rental
                WHERE user_id = :userId
                  AND return_date IS NULL
                LIMIT 1
                """)
                .setParameter("userId", userLogin)
                .getResultList();

        if (rows.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(mapRow(rows.get(0)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rental> getAll() {
        List<Object[]> rows = entityManager.createNativeQuery("""
                SELECT id, user_id, vehicle_id, rent_date, return_date
                FROM rental
                ORDER BY rent_date DESC
                """)
                .getResultList();

        return rows.stream()
                .map(this::mapRow)
                .toList();
    }

    @Override
    public boolean update(Rental rental) {
        int updated = entityManager.createNativeQuery("""
                UPDATE rental
                SET return_date = :returnDate
                WHERE id = :id
                """)
                .setParameter("returnDate", rental.getEndDate())
                .setParameter("id", rental.getId())
                .executeUpdate();

        return updated > 0;
    }

    private Rental mapRow(Object[] row) {
        Rental rental = new Rental(
                toStr(row[0]),
                toStr(row[1]),
                toStr(row[2]),
                toStr(row[3])
        );

        rental.setEndDate(toStr(row[4]));
        return rental;
    }

    private String toStr(Object value) {
        return value == null ? null : value.toString();
    }
}