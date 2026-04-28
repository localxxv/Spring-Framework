package org.example.carrent;

import java.util.List;

public class VehicleCategoryConfigService {
    private final VehicleCategoryConfigRepository repository;

    public VehicleCategoryConfigService(VehicleCategoryConfigRepository repository) {
        this.repository = repository;
    }

    public List<VehicleCategoryConfig> findAllCategories() {
        return repository.findAll();
    }

    public VehicleCategoryConfig getByCategory(String category) {
        return repository.findByCategory(category)
                .orElseThrow(() -> new IllegalArgumentException("Nieznana kategoria pojazdu: " + category));
    }

    public boolean categoryExists(String category) {
        return repository.findByCategory(category).isPresent();
    }
}