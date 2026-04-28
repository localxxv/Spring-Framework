package org.example.carrent;

import java.util.Map;

public class VehicleValidator {
    private final VehicleCategoryConfigService configService;

    public VehicleValidator(VehicleCategoryConfigService configService) {
        this.configService = configService;
    }

    public void validate(Vehicle vehicle) { //validator
        if (vehicle == null) {
            throw new IllegalArgumentException("Pojazd nie może być null.");
        }

        requireNonBlank(vehicle.getId(), "ID jest wymagane.");
        requireNonBlank(vehicle.getCategory(), "Kategoria jest wymagana.");
        requireNonBlank(vehicle.getBrand(), "Marka jest wymagana.");
        requireNonBlank(vehicle.getModel(), "Model jest wymagany.");
        requireNonBlank(vehicle.getPlate(), "Numer rejestracyjny jest wymagany.");

        if (vehicle.getYear() <= 0) {
            throw new IllegalArgumentException("Rok musi być dodatni.");
        }

        if (vehicle.getPrice() < 0) {
            throw new IllegalArgumentException("Cena nie może być ujemna.");
        }

        VehicleCategoryConfig config = configService.getByCategory(vehicle.getCategory());
        validateAttributes(vehicle.getAttributes(), config);
    }

    private void validateAttributes(Map<String, Object> actualAttributes, VehicleCategoryConfig config) {
        Map<String, String> expectedAttributes = config.getAttributes();

        for (String actualName : actualAttributes.keySet()) {
            if (!expectedAttributes.containsKey(actualName)) {
                throw new IllegalArgumentException(
                        "Niedozwolony atrybut dla kategorii " + config.getCategory() + ": " + actualName
                );
            }
        }

        for (Map.Entry<String, String> entry : expectedAttributes.entrySet()) {
            String attrName = entry.getKey();
            String expectedType = entry.getValue();

            Object value = actualAttributes.get(attrName);

            if (value == null) {
                throw new IllegalArgumentException("Brak wymaganego atrybutu: " + attrName);
            }

            if (expectedType.equalsIgnoreCase("string") && value instanceof String s && s.isBlank()) {
                throw new IllegalArgumentException("Atrybut " + attrName + " nie może być pusty.");
            }

            boolean validType = switch (expectedType.toLowerCase()) {
                case "string" -> value instanceof String;
                case "number" -> value instanceof Number;
                case "boolean" -> value instanceof Boolean;
                case "integer" -> value instanceof Number n && n.doubleValue() % 1 == 0;
                default -> throw new IllegalArgumentException("Nieobsługiwany typ w configu: " + expectedType);
            };

            if (!validType) {
                throw new IllegalArgumentException(
                        "Atrybut " + attrName + " musi być typu " + expectedType
                );
            }
        }
    }

    private void requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}