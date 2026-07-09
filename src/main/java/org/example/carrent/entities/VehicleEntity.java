package org.example.carrent.entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicle")
public class VehicleEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "brand", nullable = false)
    private String brand;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "year", nullable = false)
    private int year;

    @Column(name = "plate", nullable = false)
    private String plate;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "rented", nullable = false)
    private boolean rented;

    @Column(name = "location_name")
    private String locationName;

    @Column(name = "attributes", columnDefinition = "TEXT")
    private String attributes;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RentalEntity> rentals = new ArrayList<>();

    public VehicleEntity() {}

    public VehicleEntity(String id, String category, String brand, String model,
                         int year, String plate, double price, boolean rented, String attributes) {
        this.id = id;
        this.category = category;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.plate = plate;
        this.price = price;
        this.rented = rented;
        this.attributes = attributes;
    }

    public String getId() { return id; }
    public String getCategory() { return category; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public String getPlate() { return plate; }
    public double getPrice() { return price; }
    public boolean isRented() { return rented; }
    public String getLocationName() { return locationName; }
    public String getAttributes() { return attributes; }
    public void setRented(boolean rented) { this.rented = rented; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    public void setAttributes(String attributes) { this.attributes = attributes; }
}