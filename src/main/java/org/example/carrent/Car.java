package org.example.carrent;
public class Car extends Vehicle {

    public Car(String id, String brand, String model, int year, double price, boolean rented) {
        super(id, brand, model, year, price, rented);
    }

    public Car(Car other) {
        super(other);
    }

    @Override
    public String toCSV() {
        return "CAR;" + commonCSV();
    }

    @Override
    public Vehicle copy() {
        return new Car(this);
    }

    @Override
    public String toString() {
        return "Car{" + super.toString() + "}";
    }
}