package org.example.carrent;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final IVehicleRepository vehicleRepository;
    private final IUserRepository userRepository;
    private final Authentication authentication;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleUI(IVehicleRepository vehicleRepository, IUserRepository userRepository) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.authentication = new Authentication(userRepository);
    }

    public void start() {
        while (true) {
            User loggedUser = login();

            if (loggedUser == null) {
                System.out.println("Niepoprawny login lub hasło.");
                continue;
            }

            if (loggedUser.getRole() == Role.ADMIN) {
                adminMenu();
            } else {
                userMenu(loggedUser);
            }
        }
    }

    private User login() {
        System.out.println("=== LOGOWANIE ===");
        System.out.print("Login: ");
        String login = scanner.nextLine();
        System.out.print("Hasło: ");
        String password = scanner.nextLine();

        return authentication.authenticate(login, password);
    }

    private void userMenu(User user) {
        int choice;
        do {
            System.out.println("\n=== MENU USER ===");
            System.out.println("1. Wypożycz pojazd");
            System.out.println("2. Zwróć pojazd");
            System.out.println("3. Pokaż moje dane");
            System.out.println("0. Wyloguj");
            System.out.print("Wybierz: ");
            choice = readInt();

            switch (choice) {
                case 1 -> rentVehicleForUser(user);
                case 2 -> returnVehicleForUser(user);
                case 3 -> showUserData(user);
                case 0 -> System.out.println("Wylogowano.");
                default -> System.out.println("Niepoprawna opcja.");
            }

            user = userRepository.getUser(user.getLogin());
        } while (choice != 0);
    }

    private void adminMenu() {
        int choice;
        do {
            System.out.println("\n=== MENU ADMIN ===");
            System.out.println("1. Dodaj pojazd");
            System.out.println("2. Usuń pojazd");
            System.out.println("3. Lista pojazdów");
            System.out.println("4. Lista użytkowników");
            System.out.println("0. Wyloguj");
            System.out.print("Wybierz: ");
            choice = readInt();

            switch (choice) {
                case 1 -> addVehicle();
                case 2 -> removeVehicle();
                case 3 -> showVehicles();
                case 4 -> showUsersWithVehicles();
                case 0 -> System.out.println("Wylogowano.");
                default -> System.out.println("Niepoprawna opcja.");
            }
        } while (choice != 0);
    }

    private void showVehicles() {
        List<Vehicle> vehicles = vehicleRepository.getVehicles();

        if (vehicles.isEmpty()) {
            System.out.println("Brak pojazdów.");
            return;
        }

        for (Vehicle vehicle : vehicles) {
            System.out.println(vehicle);
        }
    }

    private void rentVehicleForUser(User user) {
        if (user.getRentedVehicleId() != null) {
            System.out.println("Masz już wypożyczony pojazd.");
            return;
        }

        showVehicles();

        System.out.print("Podaj ID pojazdu do wypożyczenia: ");
        String id = scanner.nextLine();

        Vehicle vehicle = vehicleRepository.getVehicle(id);
        if (vehicle == null) {
            System.out.println("Pojazd nie istnieje.");
            return;
        }

        if (vehicle.isRented()) {
            System.out.println("Pojazd jest już wypożyczony.");
            return;
        }

        if (vehicleRepository.rentVehicle(id)) {
            user.setRentedVehicleId(id);
            userRepository.update(user);
            System.out.println("Pojazd został wypożyczony.");
        } else {
            System.out.println("Nie udało się wypożyczyć pojazdu.");
        }
    }

    private void returnVehicleForUser(User user) {
        if (user.getRentedVehicleId() == null) {
            System.out.println("Nie masz wypożyczonego pojazdu.");
            return;
        }

        if (vehicleRepository.returnVehicle(user.getRentedVehicleId())) {
            user.setRentedVehicleId(null);
            userRepository.update(user);
            System.out.println("Pojazd został zwrócony.");
        } else {
            System.out.println("Nie udało się zwrócić pojazdu.");
        }
    }

    private void showUserData(User user) {
        User current = userRepository.getUser(user.getLogin());
        System.out.println("Dane użytkownika: " + current);

        if (current.getRentedVehicleId() != null) {
            Vehicle vehicle = vehicleRepository.getVehicle(current.getRentedVehicleId());
            System.out.println("Wypożyczony pojazd: " + vehicle);
        } else {
            System.out.println("Brak wypożyczonego pojazdu.");
        }
    }

    private void addVehicle() {
        System.out.println("1. Car");
        System.out.println("2. Motorcycle");
        System.out.print("Wybierz typ pojazdu: ");
        int type = readInt();

        System.out.print("ID: ");
        String id = scanner.nextLine();
        System.out.print("Marka: ");
        String brand = scanner.nextLine();
        System.out.print("Model: ");
        String model = scanner.nextLine();
        System.out.print("Rok: ");
        int year = readInt();
        System.out.print("Cena za dzień: ");
        double price = readDouble();

        boolean added = false;

        if (type == 1) {
            added = vehicleRepository.add(new Car(id, brand, model, year, price, false));
        } else if (type == 2) {
            System.out.print("Kategoria prawa jazdy (A, A1, A2, AM): ");
            String cat = scanner.nextLine().toUpperCase();
            LicenseCategory category;
            try {
                category = LicenseCategory.valueOf(cat);
            } catch (IllegalArgumentException e) {
                System.out.println("Niepoprawna kategoria prawa jazdy.");
                return;
            }
            added = vehicleRepository.add(new Motorcycle(id, brand, model, year, price, false, category));
        } else {
            System.out.println("Niepoprawny typ pojazdu.");
            return;
        }

        if (added) {
            System.out.println("Dodano pojazd.");
        } else {
            System.out.println("Nie udało się dodać pojazdu. ID prawdopodobnie już istnieje.");
        }
    }

    private void removeVehicle() {
        System.out.print("Podaj ID pojazdu do usunięcia: ");
        String id = scanner.nextLine();

        if (vehicleRepository.remove(id)) {
            System.out.println("Usunięto pojazd.");
        } else {
            System.out.println("Nie udało się usunąć pojazdu.");
        }
    }

    private void showUsersWithVehicles() {
        List<User> users = userRepository.getUsers();

        if (users.isEmpty()) {
            System.out.println("Brak użytkowników.");
            return;
        }

        for (User user : users) {
            System.out.println(user);
            if (user.getRentedVehicleId() != null) {
                Vehicle vehicle = vehicleRepository.getVehicle(user.getRentedVehicleId());
                System.out.println("  -> " + vehicle);
            }
        }
    }

    private int readInt() {
        while (true) {
            try {
                String input = scanner.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Podaj poprawną liczbę całkowitą: ");
            }
        }
    }

    private double readDouble() {
        while (true) {
            try {
                String input = scanner.nextLine();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.print("Podaj poprawną liczbę: ");
            }
        }
    }
}