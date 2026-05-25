package org.example.carrent.controllers;

import org.example.carrent.models.Rental;
import org.example.carrent.services.RentalService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rentals")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping
    public List<Rental> list() {
        return rentalService.getAllRentals();
    }

    @GetMapping("/users/{userId}")
    public List<Rental> userRentals(@PathVariable String userId) {
        return rentalService.getRentalsByUser(userId);
    }

    @PostMapping("/users/{userId}/rent/{vehicleId}")
    public Rental rent(
            @PathVariable String userId,
            @PathVariable String vehicleId
    ) {
        return rentalService.rentVehicle(userId, vehicleId);
    }

    @PostMapping("/users/{userId}/return")
    public Rental returnVehicle(@PathVariable String userId) {
        return rentalService.returnVehicleWithInfo(userId);
    }
}