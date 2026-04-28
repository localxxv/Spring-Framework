package org.example.carrent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VehicleCategoryConfigJsonRepository implements VehicleCategoryConfigRepository {
    private final List<VehicleCategoryConfig> configs = new ArrayList<>();

    public VehicleCategoryConfigJsonRepository() {
        this("categories.json");
    }

    public VehicleCategoryConfigJsonRepository(String fileName) {
        load(fileName);
    }

    private void load(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            return;
        }

        try (Reader reader = new FileReader(file)) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<VehicleCategoryConfig>>() {}.getType(); //parsowania
            List<VehicleCategoryConfig> loaded = gson.fromJson(reader, type);  // parsowania

            if (loaded != null) {
                configs.addAll(loaded);
            }
        } catch (Exception e) {
            throw new RuntimeException("Błąd odczytu categories.json: " + e.getMessage());
        }
    }

    @Override
    public List<VehicleCategoryConfig> findAll() {
        List<VehicleCategoryConfig> copy = new ArrayList<>();
        for (VehicleCategoryConfig config : configs) {
            copy.add(config.copy());
        }
        return copy;
    }

    @Override
    public Optional<VehicleCategoryConfig> findByCategory(String category) {
        return configs.stream()
                .filter(c -> c.getCategory().equalsIgnoreCase(category))
                .findFirst()
                .map(VehicleCategoryConfig::copy);
    }
}