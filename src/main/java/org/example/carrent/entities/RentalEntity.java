package org.example.carrent.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "rental")
public class RentalEntity {

    @Id
    @Column(name = "id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private VehicleEntity vehicle;

    @Column(name = "rent_date", nullable = false)
    private String rentDate;

    @Column(name = "return_date")
    private String returnDate;

    public RentalEntity() {}

    public RentalEntity(String id, UserEntity user, VehicleEntity vehicle,
                        String rentDate, String returnDate) {
        this.id = id;
        this.user = user;
        this.vehicle = vehicle;
        this.rentDate = rentDate;
        this.returnDate = returnDate;
    }

    public String getId() { return id; }
    public UserEntity getUser() { return user; }
    public VehicleEntity getVehicle() { return vehicle; }
    public String getRentDate() { return rentDate; }
    public String getReturnDate() { return returnDate; }
    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }
}