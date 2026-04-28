package org.example.carrent;

public class Main {
    public static void main(String[] args) {
        IVehicleRepository vehicleRepository = new VehicleRepositoryImpl("vehicles.json");
        IUserRepository userRepository = new UserRepository("users.json");
        IRentalRepository rentalRepository = new RentalRepository("rentals.json");

        VehicleCategoryConfigRepository categoryConfigRepository =
                new VehicleCategoryConfigJsonRepository("categories.json");

        VehicleCategoryConfigService categoryConfigService =
                new VehicleCategoryConfigService(categoryConfigRepository);

        VehicleValidator vehicleValidator =
                new VehicleValidator(categoryConfigService);

        AuthService authService =
                new AuthService(userRepository);

        RentalService rentalService =
                new RentalService(vehicleRepository, rentalRepository);

        VehicleService vehicleService =
                new VehicleService(vehicleRepository, rentalRepository, vehicleValidator);

        UserService userService =
                new UserService(userRepository, rentalService);

        ConsoleUI ui = new ConsoleUI(
                authService,
                vehicleService,
                rentalService,
                userService,
                categoryConfigService
        );

        ui.start();
    }
}