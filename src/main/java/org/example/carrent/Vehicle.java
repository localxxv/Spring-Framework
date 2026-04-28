package org.example.carrent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Vehicle {
    private String id;
    private String category;
    private String brand;
    private String model;
    private int year;
    private String plate;
    private double price;
    private boolean rented;
    private Map<String, Object> attributes = new HashMap<>();

    public Vehicle(String id, String category, String brand, String model,
                   int year, String plate, double price, boolean rented) {
        this.id = id;
        this.category = category;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.plate = plate;
        this.price = price;
        this.rented = rented;
        this.attributes = new HashMap<>();
    }

    public Vehicle(Vehicle other) {
        this.id = other.id;
        this.category = other.category;
        this.brand = other.brand;
        this.model = other.model;
        this.year = other.year;
        this.plate = other.plate;
        this.price = other.price;
        this.rented = other.rented;
        this.attributes = other.attributes == null
                ? new HashMap<>()
                : new HashMap<>(other.attributes);
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public String getPlate() {
        return plate;
    }

    public double getPrice() {
        return price;
    }

    public boolean isRented() {
        return rented;
    }

    public Map<String, Object> getAttributes() {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        return Collections.unmodifiableMap(attributes);
    }

    public void setRented(boolean rented) {
        this.rented = rented;
    }

    public void addAttribute(String name, Object value) {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        attributes.put(name, value);
    }

    public Vehicle copy() {
        return new Vehicle(this);
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id='" + id + '\'' +
                ", category='" + category + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", plate='" + plate + '\'' +
                ", price=" + price +
                ", rented=" + rented +
                ", attributes=" + getAttributes() +
                '}';
    }
}