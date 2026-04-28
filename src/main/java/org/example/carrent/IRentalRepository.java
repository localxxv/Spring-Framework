package org.example.carrent;

import java.util.List;
import java.util.Optional;

public interface IRentalRepository {
    void add(Rental rental);
    Optional<Rental> findActiveByUserLogin(String userLogin);
    List<Rental> getAll();
    boolean update(Rental rental);
}
