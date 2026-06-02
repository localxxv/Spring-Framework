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

    @GetMapping("/me")
    public List<Rental> myRentals(Authentication authentication) {
        return rentalService.findUserRentals(authentication.getName());
    }

    @PostMapping("/rent/{vehicleId}")
    public ResponseEntity<String> rent(@PathVariable String vehicleId, Authentication authentication) {
        boolean result = rentalService.rent(authentication.getName(), vehicleId);

        if (result) {
            return ResponseEntity.ok("Pojazd wypożyczony.");
        }

        return ResponseEntity.badRequest().body("Nie udało się wypożyczyć pojazdu.");
    }

    @PostMapping("/return")
    public ResponseEntity<String> returnVehicle(Authentication authentication) {
        boolean result = rentalService.returnVehicle(authentication.getName());

        if (result) {
            return ResponseEntity.ok("Pojazd zwrócony.");
        }

        return ResponseEntity.badRequest().body("Brak aktywnego wypożyczenia.");
    }
}