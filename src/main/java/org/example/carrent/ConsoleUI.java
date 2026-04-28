package org.example.carrent;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleUI {
    private final AuthService authService;
    private final VehicleService vehicleService;
    private final RentalService rentalService;
    private final UserService userService;
    private final VehicleCategoryConfigService categoryConfigService;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleUI(AuthService authService,
                     VehicleService vehicleService,
                     RentalService rentalService,
                     UserService userService,
                     VehicleCategoryConfigService categoryConfigService) {
        this.authService = authService;
        this.vehicleService = vehicleService;
        this.rentalService = rentalService;
        this.userService = userService;
        this.categoryConfigService = categoryConfigService;
    }

    public void start() {
        while (true) {
            System.out.println("\n=== SYSTEM WYPOŻYCZALNI ===");
            System.out.println("1. Zaloguj się");
            System.out.println("2. Zarejestruj się");
            System.out.println("0. Koniec");
            System.out.print("Wybierz: ");

            int choice = readInt();

            switch (choice) {
                case 1 -> {
                    User loggedUser = loginUser();
                    if (loggedUser == null) {
                        System.out.println("Niepoprawny login lub hasło.");
                    } else if (loggedUser.getRole() == Role.ADMIN) {
                        adminMenu();
                    } else {
                        userMenu(loggedUser);
                    }
                }
                case 2 -> registerUser();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Niepoprawna opcja.");
            }
        }
    }

    private void registerUser() {
        System.out.println("=== REJESTRACJA ===");
        System.out.print("Login: ");
        String login = scanner.nextLine();
        System.out.print("Hasło: ");
        String password = scanner.nextLine();

        RegisterResult result = authService.register(login, password);

        switch (result) {
            case SUCCESS -> System.out.println("Zarejestrowano pomyślnie!");
            case EMPTY_LOGIN -> System.out.println("Login nie może być pusty.");
            case WEAK_PASSWORD -> System.out.println("Hasło jest za słabe. Minimum 4 znaki.");
            case LOGIN_ALREADY_EXISTS -> System.out.println("Login już istnieje.");
        }
    }

    private User loginUser() {
        System.out.println("=== LOGOWANIE ===");
        System.out.print("Login: ");
        String login = scanner.nextLine();
        System.out.print("Hasło: ");
        String password = scanner.nextLine();

        return authService.login(login, password).orElse(null);
    }

    private void userMenu(User user) {
        int choice;

        do {
            System.out.println("\n=== MENU USER ===");
            System.out.println("1. Wypożycz pojazd");
            System.out.println("2. Zwróć pojazd");
            System.out.println("3. Pokaż moje dane");
            System.out.println("4. Lista dostępnych pojazdów");
            System.out.println("0. Wyloguj");
            System.out.print("Wybierz: ");

            choice = readInt();

            switch (choice) {
                case 1 -> rentVehicle(user);
                case 2 -> returnVehicle(user);
                case 3 -> showUserData(user);
                case 4 -> showAvailableVehicles();
                case 0 -> System.out.println("Wylogowano.");
                default -> System.out.println("Niepoprawna opcja.");
            }
        } while (choice != 0);
    }

    private void adminMenu() {
        int choice;

        do {
            System.out.println("\n=== MENU ADMIN ===");
            System.out.println("1. Dodaj pojazd");
            System.out.println("2. Usuń pojazd");
            System.out.println("3. Lista wszystkich pojazdów");
            System.out.println("4. Lista użytkowników");
            System.out.println("5. Usuń użytkownika");
            System.out.println("0. Wyloguj");
            System.out.print("Wybierz: ");

            choice = readInt();

            switch (choice) {
                case 1 -> addVehicle();
                case 2 -> removeVehicle();
                case 3 -> showAllVehicles();
                case 4 -> showUsers();
                case 5 -> removeUser();
                case 0 -> System.out.println("Wylogowano.");
                default -> System.out.println("Niepoprawna opcja.");
            }
        } while (choice != 0);
    }

    private void addVehicle() {
        try {
            System.out.println("=== DODAWANIE POJAZDU ===");

            List<VehicleCategoryConfig> categories = categoryConfigService.findAllCategories();

            if (categories.isEmpty()) {
                System.out.println("Brak kategorii w categories.json.");
                return;
            }

            System.out.println("Dostępne kategorie:");
            for (VehicleCategoryConfig config : categories) {
                System.out.println("- " + config.getCategory());
            }

            System.out.print("ID: ");
            String id = scanner.nextLine();

            System.out.print("Kategoria: ");
            String category = scanner.nextLine();

            VehicleCategoryConfig config = categoryConfigService.getByCategory(category);

            System.out.print("Marka: ");
            String brand = scanner.nextLine();

            System.out.print("Model: ");
            String model = scanner.nextLine();

            System.out.print("Rok: ");
            int year = readInt();

            System.out.print("Numer rejestracyjny: ");
            String plate = scanner.nextLine();

            System.out.print("Cena za dzień: ");
            double price = readDouble();

            Vehicle vehicle = new Vehicle(
                    id,
                    config.getCategory(),
                    brand,
                    model,
                    year,
                    plate,
                    price,
                    false
            );

            for (Map.Entry<String, String> entry : config.getAttributes().entrySet()) {
                vehicle.addAttribute(
                        entry.getKey(),
                        readAttributeValue(entry.getKey(), entry.getValue())
                );
            }

            vehicleService.addVehicle(vehicle);
            System.out.println("Dodano pojazd.");

        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    private void removeVehicle() {
        try {
            System.out.print("Podaj ID pojazdu: ");
            String id = scanner.nextLine();

            vehicleService.removeVehicle(id);
            System.out.println("Usunięto pojazd.");

        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    private void showAllVehicles() {
        List<Vehicle> vehicles = vehicleService.findAllVehicles();

        if (vehicles.isEmpty()) {
            System.out.println("Brak pojazdów.");
            return;
        }

        for (Vehicle v : vehicles) {
            System.out.println(v);
        }
    }

    private void showAvailableVehicles() {
        List<Vehicle> vehicles = vehicleService.findAvailableVehicles();

        if (vehicles.isEmpty()) {
            System.out.println("Brak dostępnych pojazdów.");
            return;
        }

        for (Vehicle v : vehicles) {
            System.out.println(v);
        }
    }

    private void rentVehicle(User user) {
        try {
            showAvailableVehicles();

            System.out.print("Podaj ID pojazdu: ");
            String id = scanner.nextLine();

            if (rentalService.rent(user.getLogin(), id)) {
                System.out.println("Pojazd wypożyczony!");
            } else {
                System.out.println("Nie udało się wypożyczyć.");
            }

        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    private void returnVehicle(User user) {
        if (rentalService.returnVehicle(user.getLogin())) {
            System.out.println("Pojazd zwrócony!");
        } else {
            System.out.println("Nie masz wypożyczonego pojazdu.");
        }
    }

    private void showUserData(User user) {
        System.out.println("Dane: " + user);

        if (rentalService.hasActiveRental(user.getLogin())) {
            System.out.println("Użytkownik ma aktywne wypożyczenie.");
        } else {
            System.out.println("Brak aktywnego wypożyczenia.");
        }
    }

    private void showUsers() {
        List<User> users = userService.findAllUsers();

        if (users.isEmpty()) {
            System.out.println("Brak użytkowników.");
            return;
        }

        for (User u : users) {
            System.out.println(u);
        }
    }

    private void removeUser() {
        try {
            System.out.print("Podaj login użytkownika do usunięcia: ");
            String login = scanner.nextLine();

            userService.removeUser(login);
            System.out.println("Użytkownik usunięty.");

        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    private Object readAttributeValue(String name, String type) {
        while (true) {
            try {
                System.out.print(name + " (" + type + "): ");
                String value = scanner.nextLine();

                return switch (type.toLowerCase()) {
                    case "string" -> value;
                    case "integer" -> Integer.parseInt(value);
                    case "number" -> Double.parseDouble(value);
                    case "boolean" -> {
                        if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
                            throw new IllegalArgumentException();
                        }
                        yield Boolean.parseBoolean(value);
                    }
                    default -> throw new IllegalArgumentException("Nieznany typ: " + type);
                };

            } catch (Exception e) {
                System.out.println("Niepoprawna wartość. Spróbuj ponownie.");
            }
        }
    }

    private int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Podaj liczbę całkowitą: ");
            }
        }
    }

    private double readDouble() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Podaj liczbę: ");
            }
        }
    }
}