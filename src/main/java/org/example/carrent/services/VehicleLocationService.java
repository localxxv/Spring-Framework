package org.example.carrent.services;

import org.example.carrent.models.Vehicle;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class VehicleLocationService {

    public static final Set<String> ALLOWED_LOCATIONS = Set.of(
            "Lublin"
    );

    public boolean isAtAllowedLocation(Vehicle vehicle) {
        return vehicle.getLocationName() != null
                && ALLOWED_LOCATIONS.contains(vehicle.getLocationName());
    }
}