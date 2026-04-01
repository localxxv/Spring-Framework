package org.example.carrent;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleRepositoryImpl implements IVehicleRepository {
    private final List<Vehicle> vehicles = new ArrayList<>();
    private final String fileName;

    public VehicleRepositoryImpl() {
        this("vehicles.csv");
    }

    public VehicleRepositoryImpl(String fileName) {
        this.fileName = fileName;
        load();
    }

    @Override
    public boolean rentVehicle(String id) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getId().equals(id) && !vehicle.isRented()) {
                vehicle.setRented(true);
                save();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean returnVehicle(String id) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getId().equals(id) && vehicle.isRented()) {
                vehicle.setRented(false);
                save();
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Vehicle> getVehicles() {
        List<Vehicle> copy = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            copy.add(vehicle.copy());
        }
        return copy;
    }

    @Override
    public Vehicle getVehicle(String id) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getId().equals(id)) {
                return vehicle.copy();
            }
        }
        return null;
    }

    @Override
    public boolean add(Vehicle vehicle) {
        for (Vehicle v : vehicles) {
            if (v.getId().equals(vehicle.getId())) {
                return false;
            }
        }
        vehicles.add(vehicle);
        save();
        return true;
    }

    @Override
    public boolean remove(String id) {
        for (int i = 0; i < vehicles.size(); i++) {
            Vehicle vehicle = vehicles.get(i);
            if (vehicle.getId().equals(id)) {
                if (vehicle.isRented()) {
                    return false;
                }
                vehicles.remove(i);
                save();
                return true;
            }
        }
        return false;
    }

    private void addIfUniqueId(Vehicle vehicle) {
        for (Vehicle v : vehicles) {
            if (v.getId().equals(vehicle.getId())) {
                System.out.println("Pominięto pojazd z duplikatem ID: " + vehicle.getId());
                return;
            }
        }
        vehicles.add(vehicle);
    }

    @Override
    public void save() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (Vehicle vehicle : vehicles) {
                writer.println(vehicle.toCSV());
            }
        } catch (IOException e) {
            System.out.println("Błąd podczas zapisu do pliku: " + e.getMessage());
        }
    }

    @Override
    public void load() {
        vehicles.clear();

        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("Plik " + fileName + " nie istnieje. Tworzę pustą bazę.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split(";");

                String type = parts[0];
                String id = parts[1];
                String brand = parts[2];
                String model = parts[3];
                int year = Integer.parseInt(parts[4]);
                double price = Double.parseDouble(parts[5]);
                boolean rented = Boolean.parseBoolean(parts[6]);

                switch (type) {
                    case "CAR":
                        addIfUniqueId(new Car(id, brand, model, year, price, rented));
                        break;

                    case "MOTORCYCLE":
                        LicenseCategory category = LicenseCategory.valueOf(parts[7]);
                        addIfUniqueId(new Motorcycle(id, brand, model, year, price, rented, category));
                        break;

                    default:
                        System.out.println("Nieznany typ pojazdu w pliku: " + type);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Błąd podczas wczytywania pliku: " + e.getMessage());
        }
    }
}