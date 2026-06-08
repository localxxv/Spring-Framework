package org.example.carrent.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.carrent.models.VehicleCategoryConfig;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class VehicleCategoryConfigService {

    private final List<VehicleCategoryConfig> categories = new ArrayList<>();
    private final Gson gson = new Gson();

    public VehicleCategoryConfigService() {
        loadCategories();
    }

    private void loadCategories() {
        Type listType = new TypeToken<List<VehicleCategoryConfig>>() {}.getType();

        try {
            File file = new File("categories.json");

            if (file.exists()) {
                try (Reader reader = new FileReader(file)) {
                    List<VehicleCategoryConfig> loaded = gson.fromJson(reader, listType);

                    if (loaded != null) {
                        categories.clear();
                        categories.addAll(loaded);
                    }
                }

                return;
            }

            try (Reader reader = new InputStreamReader(
                    getClass().getClassLoader().getResourceAsStream("categories.json"),
                    StandardCharsets.UTF_8
            )) {
                List<VehicleCategoryConfig> loaded = gson.fromJson(reader, listType);

                if (loaded != null) {
                    categories.clear();
                    categories.addAll(loaded);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Nie udało się wczytać categories.json", e);
        }
    }

    public List<VehicleCategoryConfig> findAllCategories() {
        return categories;
    }

    public List<VehicleCategoryConfig> list() {
        return categories;
    }

    public List<VehicleCategoryConfig> getAll() {
        return categories;
    }

    public List<VehicleCategoryConfig> getCategories() {
        return categories;
    }

    public VehicleCategoryConfig findByCategory(String category) {
        return categories.stream()
                .filter(config -> config.getCategory().equalsIgnoreCase(category))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nie znaleziono kategorii: " + category));
    }

    public VehicleCategoryConfig get(String category) {
        return findByCategory(category);
    }

    public VehicleCategoryConfig getByCategory(String category) {
        return findByCategory(category);
    }
}