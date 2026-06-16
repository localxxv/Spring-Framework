package org.example.carrent.services;

import org.example.carrent.models.Vehicle;
import org.example.carrent.repositories.IRentalRepository;
import org.example.carrent.repositories.IVehicleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    private final IVehicleRepository vehicleRepository;
    private final IRentalRepository rentalRepository;

    public VehicleService(IVehicleRepository vehicleRepository,
                          IRentalRepository rentalRepository) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
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
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Nie znaleziono pojazdu: " + id
                ));
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
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Nie udało się dodać pojazdu."
            );
        }

        return vehicle;
    }

    public Vehicle create(Vehicle vehicle) {
        return addVehicle(vehicle);
    }

    public boolean removeVehicle(String id) {
        checkCanDeleteVehicle(id);
        return vehicleRepository.remove(id);
    }

    public boolean delete(String id) {
        checkCanDeleteVehicle(id);
        return vehicleRepository.remove(id);
    }

    public boolean updateVehicle(Vehicle vehicle) {
        return vehicleRepository.update(vehicle);
    }

    private void checkCanDeleteVehicle(String id) {
        Vehicle vehicle = findById(id);

        if (vehicle.isRented()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Nie można usunąć pojazdu, który jest aktualnie wypożyczony."
            );
        }

        boolean hasActiveRental = rentalRepository.getAll()
                .stream()
                .anyMatch(rental ->
                        rental.getVehicleId().equals(id)
                                && (rental.getEndDate() == null || rental.getEndDate().isBlank())
                );

        if (hasActiveRental) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Nie można usunąć pojazdu, ponieważ istnieje aktywne wypożyczenie."
            );
        }
    }
}