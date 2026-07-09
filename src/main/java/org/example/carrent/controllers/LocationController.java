package org.example.carrent.controllers;

import org.example.carrent.models.Vehicle;
import org.example.carrent.services.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/vehicles/{id}/location", "/api/vehicles/{id}/location"})
public class LocationController {

    private final VehicleService vehicleService;

    public LocationController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    public record LocationUpdateRequest(String locationName) {}

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Vehicle> setLocation(@PathVariable String id,
                                               @RequestBody LocationUpdateRequest request) {
        Vehicle updated = vehicleService.updateLocation(id, request.locationName());
        return ResponseEntity.ok(updated);
    }
}