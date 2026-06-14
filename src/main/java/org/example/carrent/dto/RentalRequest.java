package org.example.carrent.dto;

public class RentalRequest {

    private String vehicleId;

    public RentalRequest() {
    }

    public RentalRequest(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }
}