package org.example.carrent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RentalRepository implements IRentalRepository {
    private List<Rental> rentals = new ArrayList<>();
    private final String fileName;
    private final Gson gson = new Gson();

    public RentalRepository(String fileName) {
        this.fileName = fileName;
        load();
    }

    public RentalRepository() {
        this("test-rentals.json");
    }

    private void save() {
        try (Writer writer = new FileWriter(fileName)) {
            gson.toJson(rentals, writer);
        } catch (IOException e) {
            System.out.println("Błąd zapisu wypożyczeń: " + e.getMessage());
        }
    }

    private void load() {
        File file = new File(fileName);
        if (!file.exists()) return;

        try (Reader reader = new FileReader(fileName)) {
            Type type = new TypeToken<List<Rental>>() {}.getType();
            List<Rental> loaded = gson.fromJson(reader, type);
            if (loaded != null) rentals = loaded;
        } catch (IOException e) {
            System.out.println("Błąd odczytu wypożyczeń: " + e.getMessage());
        }
    }

    @Override
    public void add(Rental rental) {
        rentals.add(rental);
        save();
    }

    @Override
    public Optional<Rental> findActiveByUserLogin(String userLogin) {
        return rentals.stream()
                .filter(r -> r.getUserLogin().equals(userLogin) && r.isActive())
                .findFirst();
    }

    @Override
    public List<Rental> getAll() {
        return new ArrayList<>(rentals);
    }

    @Override
    public boolean update(Rental updated) {
        for (int i = 0; i < rentals.size(); i++) {
            if (rentals.get(i).getId().equals(updated.getId())) {
                rentals.set(i, updated);
                save();
                return true;
            }
        }
        return false;
    }
}