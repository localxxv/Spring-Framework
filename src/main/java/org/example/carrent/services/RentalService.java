package org.example.carrent.services;

import org.example.carrent.models.Rental;
import org.example.carrent.models.Vehicle;
import org.example.carrent.repositories.IRentalRepository;
import org.example.carrent.repositories.IUserRepository;
import org.example.carrent.repositories.IVehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RentalService {

    private final IRentalRepository rentalRepository;
    private final IVehicleRepository vehicleRepository;
    private final IUserRepository userRepository;
    private final VehicleLocationService vehicleLocationService;

    public RentalService(IRentalRepository rentalRepository,
                         IVehicleRepository vehicleRepository,
                         IUserRepository userRepository,
                         VehicleLocationService vehicleLocationService) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.vehicleLocationService = vehicleLocationService;
    }

    public List<Rental> getAll() {
        return rentalRepository.getAll();
    }

    public List<Rental> list() {
        return rentalRepository.getAll();
    }

    public List<Rental> findAllRentals() {
        return rentalRepository.getAll();
    }

    public List<Rental> findUserRentals(String userLogin) {
        return rentalRepository.getAll()
                .stream()
                .filter(rental -> rental.getUserLogin().equals(userLogin))
                .collect(Collectors.toList());
    }

    public List<Rental> userRentals(String userLogin) {
        return findUserRentals(userLogin);
    }

    public boolean hasActiveRental(String userLogin) {
        return rentalRepository.findActiveByUserLogin(userLogin).isPresent();
    }

    @Transactional
    public Rental rentVehicle(String userLogin, String vehicleId) {
        if (userRepository.findByLogin(userLogin).isEmpty()) {
            throw new RuntimeException("Nie znaleziono użytkownika: " + userLogin);
        }

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono pojazdu: " + vehicleId));

        if (vehicle.isRented()) {
            throw new RuntimeException("Pojazd jest już wypożyczony.");
        }

        if (rentalRepository.findActiveByUserLogin(userLogin).isPresent()) {
            throw new RuntimeException("Użytkownik ma już aktywne wypożyczenie.");
        }

        Rental rental = new Rental(
                UUID.randomUUID().toString(),
                userLogin,
                vehicleId,
                LocalDate.now().toString()
        );

        rentalRepository.add(rental);

        vehicle.setRented(true);
        vehicleRepository.update(vehicle);

        return rental;
    }

    public boolean rent(String userLogin, String vehicleId) {
        rentVehicle(userLogin, vehicleId);
        return true;
    }

    @Transactional
    public Rental returnVehicle(String userLogin) {
        Rental rental = rentalRepository.findActiveByUserLogin(userLogin)
                .orElseThrow(() -> new RuntimeException("Brak aktywnego wypożyczenia."));

        Vehicle vehicle = vehicleRepository.findById(rental.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Nie znaleziono pojazdu: " + rental.getVehicleId()));

        if (!vehicleLocationService.isAtAllowedLocation(vehicle)) {
            throw new RuntimeException("Pojazd nie znajduje się w siedzibie firmy ani innym dozwolonym miejscu — nie można zwrócić.");
        }

        rental.setEndDate(LocalDate.now().toString());
        rentalRepository.update(rental);

        vehicle.setRented(false);
        vehicleRepository.update(vehicle);

        return rental;
    }

    public boolean returnVehicleBoolean(String userLogin) {
        returnVehicle(userLogin);
        return true;
    }
}