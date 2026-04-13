package org.example.carrent;

public class Main {
    public static void main(String[] args) {
        IVehicleRepository vehicleRepository = new VehicleRepositoryImpl("vehicles.json");
        IUserRepository userRepository = new UserRepository("users.json");
        IRentalRepository rentalRepository = new RentalRepository("rentals.json");

        if (vehicleRepository.getVehicles().isEmpty()) {
            vehicleRepository.add(new Car("C1", "Toyota", "Corolla", 2020, 150.0, false));
            vehicleRepository.add(new Car("C2", "Skoda", "Octavia", 2021, 180.0, false));
            vehicleRepository.add(new Motorcycle("M1", "Yamaha", "MT-07", 2022, 120.0, false, LicenseCategory.A));
        }

        ConsoleUI ui = new ConsoleUI(vehicleRepository, userRepository, rentalRepository);
        ui.start();
    }
}