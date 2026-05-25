package org.example.carrent;

import org.example.carrent.hibernate.HibernateUtil;
import org.example.carrent.repositories.IRentalRepository;
import org.example.carrent.repositories.IUserRepository;
import org.example.carrent.repositories.IVehicleRepository;
import org.example.carrent.repositories.VehicleCategoryConfigRepository;
import org.example.carrent.repositories.impl.*;
import org.example.carrent.services.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public IVehicleRepository vehicleRepository() {
        return new VehicleHibernateRepository();
    }

    @Bean
    public IUserRepository userRepository() {
        return new UserHibernateRepository();
    }

    @Bean
    public IRentalRepository rentalRepository() {
        return new RentalHibernateRepository();
    }

    @Bean
    public VehicleCategoryConfigRepository categoryConfigRepository() {
        return new VehicleCategoryConfigJsonRepository("categories.json");
    }

    @Bean
    public VehicleCategoryConfigService categoryConfigService(
            VehicleCategoryConfigRepository categoryConfigRepository
    ) {
        return new VehicleCategoryConfigService(categoryConfigRepository);
    }

    @Bean
    public VehicleValidator vehicleValidator(
            VehicleCategoryConfigService categoryConfigService
    ) {
        return new VehicleValidator(categoryConfigService);
    }

    @Bean
    public AuthService authService(IUserRepository userRepository) {
        return new AuthService(userRepository);
    }

    @Bean
    public RentalService rentalService(
            IVehicleRepository vehicleRepository,
            IRentalRepository rentalRepository
    ) {
        return new RentalService(vehicleRepository, rentalRepository);
    }

    @Bean
    public VehicleService vehicleService(
            IVehicleRepository vehicleRepository,
            IRentalRepository rentalRepository,
            VehicleValidator vehicleValidator
    ) {
        return new VehicleService(vehicleRepository, rentalRepository, vehicleValidator);
    }

    @Bean
    public UserService userService(
            IUserRepository userRepository,
            RentalService rentalService
    ) {
        return new UserService(userRepository, rentalService);
    }
}