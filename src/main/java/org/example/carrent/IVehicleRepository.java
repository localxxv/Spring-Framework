package org.example.carrent;

import java.util.List;

public interface IVehicleRepository {
    boolean rentVehicle(String id);
    boolean returnVehicle(String id);
    List<Vehicle> getVehicles();
    boolean add(Vehicle vehicle);
    boolean remove(String id);
    Vehicle getVehicle(String id);
}