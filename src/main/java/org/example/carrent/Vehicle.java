package org.example.carrent;
public abstract class Vehicle {
    private String id;
    private String brand;
    private String model;
    private int year;
    private double price;
    private boolean rented;

    public Vehicle(String id, String brand, String model, int year, double price, boolean rented) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.price = price;
        this.rented = rented;
    }


    public Vehicle(Vehicle other) {
        this.id = other.id;
        this.brand = other.brand;
        this.model = other.model;
        this.year = other.year;
        this.price = other.price;
        this.rented = other.rented;
    }

    public String getId() {
        return id;
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

    public double getPrice() {
        return price;
    }

    public boolean isRented() {
        return rented;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setRented(boolean rented) {
        this.rented = rented;
    }

    protected String commonCSV() {
        return id + ";" + brand + ";" + model + ";" + year + ";" + price + ";" + rented;
    }

    public abstract String toCSV();

    public abstract Vehicle copy();

    @Override
    public String toString() {
        return "id='" + id + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", price=" + price +
                ", rented=" + rented;
    }
}