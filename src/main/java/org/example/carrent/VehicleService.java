package org.example.carrent;

import java.util.List;

public class VehicleService {
    private final IVehicleRepository vehicleRepository;
    private final IRentalRepository rentalRepository;
    private final VehicleValidator validator;

    public VehicleService(IVehicleRepository vehicleRepository,
                          IRentalRepository rentalRepository,
                          VehicleValidator validator) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
        this.validator = validator;
    }

    public Vehicle addVehicle(Vehicle vehicle) {
        validator.validate(vehicle);

        if (!vehicleRepository.add(vehicle)) {
            throw new IllegalArgumentException("Pojazd o takim ID już istnieje.");
        }

        return vehicle;
    }

    public void removeVehicle(String id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pojazdu."));

        if (vehicle.isRented() || vehicleHasActiveRental(id)) {
            throw new IllegalStateException("Nie można usunąć pojazdu, bo jest wypożyczony.");
        }

        vehicleRepository.remove(id);
    }

    public List<Vehicle> findAllVehicles() {
        return vehicleRepository.getVehicles();
    }

    public List<Vehicle> findAvailableVehicles() {
        return vehicleRepository.getVehicles()
                .stream()
                .filter(v -> !v.isRented())
                .toList();
    }

    public Vehicle findById(String id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pojazdu."));
    }

    public boolean isVehicleRented(String id) {
        return vehicleRepository.findById(id)
                .map(Vehicle::isRented)
                .orElse(false);
    }

    public boolean vehicleHasActiveRental(String vehicleId) {
        return rentalRepository.getAll()
                .stream()
                .anyMatch(r -> r.getVehicleId().equals(vehicleId) && r.isActive());
    }
}