package org.example.carrent;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public class RentalService {
    private final IVehicleRepository vehicleRepository;
    private final IRentalRepository rentalRepository;

    public RentalService(IVehicleRepository vehicleRepository, IRentalRepository rentalRepository) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
    }

    public boolean rent(String userLogin, String vehicleId) {
        if (rentalRepository.findActiveByUserLogin(userLogin).isPresent()) {
            return false;
        }

        Optional<Vehicle> vehicle = vehicleRepository.findById(vehicleId);
        if (vehicle.isEmpty() || vehicle.get().isRented()) {
            return false;
        }

        Vehicle rentedVehicle = vehicle.get();
        rentedVehicle.setRented(true);
        vehicleRepository.update(rentedVehicle);

        rentalRepository.add(new Rental(
                UUID.randomUUID().toString(),
                userLogin,
                vehicleId,
                LocalDate.now().toString()
        ));

        return true;
    }

    public boolean returnVehicle(String userLogin) {
        Optional<Rental> rental = rentalRepository.findActiveByUserLogin(userLogin);
        if (rental.isEmpty()) {
            return false;
        }

        Rental activeRental = rental.get();
        activeRental.setEndDate(LocalDate.now().toString());
        rentalRepository.update(activeRental);

        vehicleRepository.findById(activeRental.getVehicleId())
                .ifPresent(v -> {
                    v.setRented(false);
                    vehicleRepository.update(v);
                });

        return true;
    }

    public boolean hasActiveRental(String userLogin) {
        return rentalRepository.findActiveByUserLogin(userLogin).isPresent();
    }
}