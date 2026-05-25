package org.example.carrent;

import org.example.carrent.hibernate.HibernateUtil;
import org.example.carrent.repositories.IRentalRepository;
import org.example.carrent.repositories.IUserRepository;
import org.example.carrent.repositories.IVehicleRepository;
import org.example.carrent.repositories.VehicleCategoryConfigRepository;
import org.example.carrent.repositories.impl.*;
import org.example.carrent.services.*;

public class Main {
    public static void main(String[] args) {
        String mode = args.length > 0 ? args[0].toLowerCase() : "hibernate";

        IVehicleRepository vehicleRepository;
        IUserRepository userRepository;
        IRentalRepository rentalRepository;

        if (mode.equals("hibernate")) {
            System.out.println("Używam repozytoriów Hibernate (PostgreSQL)");

            vehicleRepository = new VehicleHibernateRepository();
            userRepository = new UserHibernateRepository();
            rentalRepository = new RentalHibernateRepository();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                HibernateUtil.getSessionFactory().close();
            }));

        } else if (mode.equals("jdbc")) {
            System.out.println("Używam repozytoriów JDBC (PostgreSQL)");

            vehicleRepository = new VehicleJdbcRepository();
            userRepository = new UserJdbcRepository();
            rentalRepository = new RentalJdbcRepository();

        } else {
            System.out.println("Używam repozytoriów JSON");

            vehicleRepository = new VehicleRepositoryImpl("vehicles.json");
            userRepository = new UserRepository("users.json");
            rentalRepository = new RentalRepository("rentals.json");
        }

        VehicleCategoryConfigRepository categoryConfigRepository =
                new VehicleCategoryConfigJsonRepository("categories.json");

        VehicleCategoryConfigService categoryConfigService =
                new VehicleCategoryConfigService(categoryConfigRepository);

        VehicleValidator vehicleValidator = new VehicleValidator(categoryConfigService);

        AuthService authService = new AuthService(userRepository);
        RentalService rentalService = new RentalService(vehicleRepository, rentalRepository);
        VehicleService vehicleService = new VehicleService(vehicleRepository, rentalRepository, vehicleValidator);
        UserService userService = new UserService(userRepository, rentalService);

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