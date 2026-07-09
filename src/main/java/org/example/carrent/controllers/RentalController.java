package org.example.carrent.controllers;

import org.example.carrent.dto.RentalRequest;
import org.example.carrent.models.Rental;
import org.example.carrent.services.RentalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/rentals", "/api/rentals"})
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Rental> list() {
        return rentalService.findAllRentals();
    }

    @GetMapping("/me")
    public List<Rental> myRentals(@AuthenticationPrincipal UserDetails userDetails) {
        return rentalService.findUserRentals(userDetails.getUsername());
    }

    @PostMapping("/rent")
    public ResponseEntity<Rental> rent(@RequestBody RentalRequest rentalRequest,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        Rental rental = rentalService.rentVehicle(
                userDetails.getUsername(),
                rentalRequest.getVehicleId()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(rental);
    }

    @PostMapping("/return")
    public ResponseEntity<Rental> returnVehicle(@AuthenticationPrincipal UserDetails userDetails) {
        Rental rental = rentalService.returnVehicle(userDetails.getUsername());
        return ResponseEntity.ok(rental);
    }
}