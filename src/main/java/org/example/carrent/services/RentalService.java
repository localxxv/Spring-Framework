package org.example.carrent.services;

import org.example.carrent.models.Rental;
import org.example.carrent.models.Vehicle;
import org.example.carrent.repositories.IRentalRepository;
import org.example.carrent.repositories.IVehicleRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RentalService {
    private final IVehicleRepository vehicleRepository;
    private final IRentalRepository rentalRepository;

    public RentalService(IVehicleRepository vehicleRepository, IRentalRepository rentalRepository) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
    }

    public List<Rental> getAllRentals() {
        return rentalRepository.getAll();
    }

    public List<Rental> getRentalsByUser(String userLogin) {
        return rentalRepository.getAll()
                .stream()
                .filter(r -> r.getUserLogin().equals(userLogin))
                .toList();
    }

    public Rental rentVehicle(String userLogin, String vehicleId) {
        if (rentalRepository.findActiveByUserLogin(userLogin).isPresent()) {
            throw new IllegalStateException("Użytkownik ma już aktywne wypożyczenie.");
        }

        Optional<Vehicle> vehicle = vehicleRepository.findById(vehicleId);

        if (vehicle.isEmpty()) {
            throw new IllegalArgumentException("Nie znaleziono pojazdu.");
        }

        if (vehicle.get().isRented()) {
            throw new IllegalStateException("Pojazd jest już wypożyczony.");
        }

        Vehicle rentedVehicle = vehicle.get();
        rentedVehicle.setRented(true);
        vehicleRepository.update(rentedVehicle);

        Rental rental = new Rental(
                UUID.randomUUID().toString(),
                userLogin,
                vehicleId,
                LocalDate.now().toString()
        );

        rentalRepository.add(rental);

        return rental;
    }

    public Rental returnVehicleWithInfo(String userLogin) {
        Optional<Rental> rental = rentalRepository.findActiveByUserLogin(userLogin);

        if (rental.isEmpty()) {
            throw new IllegalStateException("Użytkownik nie ma aktywnego wypożyczenia.");
        }

        Rental activeRental = rental.get();
        activeRental.setEndDate(LocalDate.now().toString());
        rentalRepository.update(activeRental);

        vehicleRepository.findById(activeRental.getVehicleId())
                .ifPresent(v -> {
                    v.setRented(false);
                    vehicleRepository.update(v);
                });

        return activeRental;
    }

    public boolean rent(String userLogin, String vehicleId) {
        try {
            rentVehicle(userLogin, vehicleId);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }


    public List<Rental> findAllRentals() {
        return rentalRepository.getAll();
    }

    public List<Rental> findUserRentals(String userLogin) {
        return rentalRepository.getAll()
                .stream()
                .filter(r -> r.getUserLogin().equals(userLogin))
                .toList();
    }

    public boolean returnVehicle(String userLogin) {
        try {
            returnVehicleWithInfo(userLogin);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public boolean hasActiveRental(String userLogin) {
        return rentalRepository.findActiveByUserLogin(userLogin).isPresent();
    }

    public boolean vehicleHasActiveRental(String vehicleId) {
        return rentalRepository.getAll()
                .stream()
                .anyMatch(r -> r.getVehicleId().equals(vehicleId) && r.isActive());
    }
}