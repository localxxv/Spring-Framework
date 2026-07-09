package org.example.carrent.services;

import org.example.carrent.models.Vehicle;
import org.example.carrent.repositories.IVehicleRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class VehicleLocationScheduler {

    private final IVehicleRepository vehicleRepository;

    private static final List<String> POSSIBLE_LOCATIONS = List.of(
            "Lublin",
            "Warszawa",
            "Kraków",
            "Rzeszów",
            "Zamość"
    );

    public VehicleLocationScheduler(IVehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void randomizeRentedVehiclesLocations() {
        List<Vehicle> vehicles = vehicleRepository.getVehicles();

        for (Vehicle vehicle : vehicles) {
            if (vehicle.isRented()) {
                String randomLocation = POSSIBLE_LOCATIONS.get(
                        ThreadLocalRandom.current().nextInt(POSSIBLE_LOCATIONS.size())
                );
                vehicle.setLocationName(randomLocation);
                vehicleRepository.update(vehicle);
            }
        }
    }
}