package org.example.carrent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class VehicleCategoryConfig {
    private String category;
    private Map<String, String> attributes = new HashMap<>();

    public VehicleCategoryConfig() {}

    public VehicleCategoryConfig(String category, Map<String, String> attributes) {
        this.category = category;
        this.attributes = attributes == null ? new HashMap<>() : new HashMap<>(attributes);
    }

    public String getCategory() {
        return category;
    }

    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public VehicleCategoryConfig copy() {
        return new VehicleCategoryConfig(category, attributes);
    }
}