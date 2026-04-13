package org.example.carrent;

import java.util.List;
import java.util.Optional;

public interface IVehicleRepository {
    Optional<Vehicle> findById(String id);
    List<Vehicle> getVehicles();
    boolean add(Vehicle vehicle);
    boolean remove(String id);
    boolean update(Vehicle vehicle);
}