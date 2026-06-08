package org.example.carrent.controllers;

import org.example.carrent.models.Rental;
import org.example.carrent.services.RentalService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
        return rentalService.findAllRentals();
    }

    @GetMapping("/users/{userId}")
    public List<Rental> userRentals(@PathVariable String userId) {
        return rentalService.findUserRentals(userId);
    }

    @GetMapping("/me")
    public List<Rental> myRentals(Authentication authentication) {
        return rentalService.findUserRentals(authentication.getName());
    }

    @PostMapping("/users/{userId}/rent/{vehicleId}")
    public Rental rentOldEndpoint(@PathVariable String userId,
                                  @PathVariable String vehicleId) {
        return rentalService.rentVehicle(userId, vehicleId);
    }

    @PostMapping("/users/{userId}/return")
    public Rental returnVehicleOldEndpoint(@PathVariable String userId) {
        return rentalService.returnVehicle(userId);
    }

    @PostMapping("/rent/{vehicleId}")
    public ResponseEntity<?> rent(@PathVariable String vehicleId,
                                  Authentication authentication) {
        Rental rental = rentalService.rentVehicle(authentication.getName(), vehicleId);
        return ResponseEntity.ok(rental);
    }

    @PostMapping("/return")
    public ResponseEntity<?> returnVehicle(Authentication authentication) {
        Rental rental = rentalService.returnVehicle(authentication.getName());
        return ResponseEntity.ok(rental);
    }
}