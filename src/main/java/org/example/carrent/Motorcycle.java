package org.example.carrent;
public class Motorcycle extends Vehicle {
    private LicenseCategory category;

    public Motorcycle(String id, String brand, String model, int year, double price, boolean rented, LicenseCategory category) {
        super(id, brand, model, year, price, rented);
        this.category = category;
    }

    public Motorcycle(Motorcycle other) {
        super(other);
        this.category = other.category;
    }

    public LicenseCategory getCategory() {
        return category;
    }

    public void setCategory(LicenseCategory category) {
        this.category = category;
    }

    @Override
    public String toCSV() {
        return "MOTORCYCLE;" + commonCSV() + ";" + category;
    }

    @Override
    public Vehicle copy() {
        return new Motorcycle(this);
    }

    @Override
    public String toString() {
        return "Motorcycle{" + super.toString() + ", category=" + category + "}";
    }
}