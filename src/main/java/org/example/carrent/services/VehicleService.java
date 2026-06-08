package org.example.carrent.services;

import org.example.carrent.models.Vehicle;
import org.example.carrent.repositories.IVehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    private final IVehicleRepository vehicleRepository;

    public VehicleService(IVehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public List<Vehicle> getVehicles() {
        return vehicleRepository.getVehicles();
    }

    public List<Vehicle> findAllVehicles() {
        return vehicleRepository.getVehicles();
    }

    public List<Vehicle> findAll() {
        return vehicleRepository.getVehicles();
    }

    public List<Vehicle> list(boolean available) {
        if (available) {
            return findAvailableVehicles();
        }
        return findAllVehicles();
    }

    public List<Vehicle> findAvailableVehicles() {
        return vehicleRepository.getVehicles()
                .stream()
                .filter(vehicle -> !vehicle.isRented())
                .collect(Collectors.toList());
    }

    public Vehicle findById(String id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono pojazdu: " + id));
    }

    public Vehicle get(String id) {
        return findById(id);
    }

    public Vehicle getVehicle(String id) {
        return findById(id);
    }

    public Vehicle addVehicle(Vehicle vehicle) {
        boolean added = vehicleRepository.add(vehicle);

        if (!added) {
            throw new RuntimeException("Nie udało się dodać pojazdu.");
        }

        return vehicle;
    }

    public Vehicle create(Vehicle vehicle) {
        return addVehicle(vehicle);
    }

    public boolean removeVehicle(String id) {
        return vehicleRepository.remove(id);
    }

    public boolean delete(String id) {
        return vehicleRepository.remove(id);
    }

    public boolean updateVehicle(Vehicle vehicle) {
        return vehicleRepository.update(vehicle);
    }
}