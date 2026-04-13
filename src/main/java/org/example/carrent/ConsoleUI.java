package org.example.carrent;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleUI {
    private final IVehicleRepository vehicleRepository;
    private final IUserRepository userRepository;
    private final IRentalRepository rentalRepository;
    private final AuthService authService;
    private final RentalService rentalService;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleUI(IVehicleRepository vehicleRepository, IUserRepository userRepository, IRentalRepository rentalRepository) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
        this.authService = new AuthService(userRepository);
        this.rentalService = new RentalService(vehicleRepository, rentalRepository);
    }

    public void start() {
        while (true) {
            System.out.println("\n=== SYSTEM WYPOŻYCZALNI ===");
            System.out.println("1. Zaloguj się");
            System.out.println("2. Zarejestruj się");
            System.out.print("Wybierz: ");
            int choice = readInt();

            if (choice == 2) {
                registerUser();
                continue;
            }

            User loggedUser = loginUser();
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
            System.out.println("6. Lista wypożyczeń");
            System.out.println("0. Wyloguj");
            System.out.print("Wybierz: ");
            choice = readInt();

            switch (choice) {
                case 1 -> addVehicle();
                case 2 -> removeVehicle();
                case 3 -> showAllVehicles();
                case 4 -> showUsers();
                case 5 -> removeUser();
                case 6 -> showAllRentals();
                case 0 -> System.out.println("Wylogowano.");
                default -> System.out.println("Niepoprawna opcja.");
            }
        } while (choice != 0);
    }

    private void rentVehicle(User user) {
        List<Vehicle> available = vehicleRepository.getVehicles()
                .stream()
                .filter(v -> !v.isRented())
                .toList();

        if (available.isEmpty()) {
            System.out.println("Brak dostępnych pojazdów.");
            return;
        }

        System.out.println("=== DOSTĘPNE POJAZDY ===");
        for (Vehicle v : available) {
            System.out.println(v);
        }

        System.out.print("Podaj ID pojazdu: ");
        String id = scanner.nextLine();

        if (rentalService.rent(user.getLogin(), id)) {
            System.out.println("Pojazd wypożyczony!");
        } else {
            System.out.println("Nie udało się wypożyczyć.");
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

        Optional<Rental> rental = rentalRepository.findActiveByUserLogin(user.getLogin());
        if (rental.isPresent()) {
            vehicleRepository.findById(rental.get().getVehicleId())
                    .ifPresent(v -> System.out.println("Wypożyczony pojazd: " + v));
        } else {
            System.out.println("Brak wypożyczonego pojazdu.");
        }
    }

    private void showAvailableVehicles() {
        List<Vehicle> available = vehicleRepository.getVehicles()
                .stream()
                .filter(v -> !v.isRented())
                .toList();

        if (available.isEmpty()) {
            System.out.println("Brak dostępnych pojazdów.");
            return;
        }

        for (Vehicle v : available) {
            System.out.println(v);
        }
    }

    private void showAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.getVehicles();
        if (vehicles.isEmpty()) {
            System.out.println("Brak pojazdów.");
            return;
        }

        for (Vehicle v : vehicles) {
            System.out.println(v);
        }
    }

    private void showUsers() {
        List<User> users = userRepository.getUsers();
        if (users.isEmpty()) {
            System.out.println("Brak użytkowników.");
            return;
        }

        for (User u : users) {
            System.out.println(u);
            rentalRepository.findActiveByUserLogin(u.getLogin())
                    .ifPresent(r -> vehicleRepository.findById(r.getVehicleId())
                            .ifPresent(v -> System.out.println("  -> " + v)));
        }
    }

    private void showAllRentals() {
        List<Rental> rentals = rentalRepository.getAll();
        if (rentals.isEmpty()) {
            System.out.println("Brak wypożyczeń.");
            return;
        }

        for (Rental rental : rentals) {
            System.out.println(rental);
        }
    }

    private void removeUser() {
        System.out.print("Podaj login użytkownika do usunięcia: ");
        String login = scanner.nextLine();

        if (rentalService.hasActiveRental(login)) {
            System.out.println("Nie można usunąć: użytkownik ma wypożyczony pojazd.");
            return;
        }

        if (userRepository.remove(login)) {
            System.out.println("Użytkownik usunięty.");
        } else {
            System.out.println("Użytkownik nie istnieje.");
        }
    }

    private void addVehicle() {
        System.out.println("1. Car  2. Motorcycle");
        System.out.print("Wybierz typ: ");
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
            System.out.print("Kategoria (A, A1, A2, AM): ");
            String cat = scanner.nextLine().toUpperCase();

            try {
                added = vehicleRepository.add(
                        new Motorcycle(id, brand, model, year, price, false, LicenseCategory.valueOf(cat))
                );
            } catch (IllegalArgumentException e) {
                System.out.println("Niepoprawna kategoria.");
                return;
            }
        } else {
            System.out.println("Niepoprawny typ pojazdu.");
            return;
        }

        System.out.println(added ? "Dodano pojazd." : "Nie udało się dodać. ID już istnieje.");
    }

    private void removeVehicle() {
        System.out.print("Podaj ID pojazdu: ");
        String id = scanner.nextLine();
        System.out.println(vehicleRepository.remove(id) ? "Usunięto." : "Nie udało się usunąć.");
    }

    private int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Podaj liczbę: ");
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