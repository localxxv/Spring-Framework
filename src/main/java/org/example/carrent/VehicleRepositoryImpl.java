package org.example.carrent;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class VehicleRepositoryImpl implements IVehicleRepository {
    private List<Vehicle> vehicles = new ArrayList<>();
    private final String fileName;
    private final Gson gson;

    public VehicleRepositoryImpl(String fileName) {
        this.fileName = fileName;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Vehicle.class, new VehicleDeserializer())
                .create();
        load();
    }

    public VehicleRepositoryImpl() {
        this("test-vehicles.json");
    }

    private void save() {
        try (Writer writer = new FileWriter(fileName)) {
            gson.toJson(vehicles, writer);
        } catch (IOException e) {
            System.out.println("Błąd zapisu pojazdów: " + e.getMessage());
        }
    }

    private void load() {
        File file = new File(fileName);
        if (!file.exists()) return;
        try (Reader reader = new FileReader(fileName)) {
            Type type = new TypeToken<List<Vehicle>>(){}.getType();
            List<Vehicle> loaded = gson.fromJson(reader, type);
            if (loaded != null) vehicles = loaded;
        } catch (IOException e) {
            System.out.println("Błąd odczytu pojazdów: " + e.getMessage());
        }
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        return vehicles.stream().filter(v -> v.getId().equals(id)).findFirst();
    }

    @Override
    public List<Vehicle> getVehicles() {
        List<Vehicle> copy = new ArrayList<>();
        for (Vehicle v : vehicles) copy.add(v.copy());
        return copy;
    }

    @Override
    public boolean add(Vehicle vehicle) {
        if (findById(vehicle.getId()).isPresent()) return false;
        vehicles.add(vehicle);
        save();
        return true;
    }

    @Override
    public boolean remove(String id) {
        Optional<Vehicle> v = findById(id);
        if (v.isEmpty()) return false;
        if (v.get().isRented()) return false;
        vehicles.removeIf(vehicle -> vehicle.getId().equals(id));
        save();
        return true;
    }

    @Override
    public boolean update(Vehicle updated) {
        for (int i = 0; i < vehicles.size(); i++) {
            if (vehicles.get(i).getId().equals(updated.getId())) {
                vehicles.set(i, updated);
                save();
                return true;
            }
        }
        return false;
    }

    // Gson не знає чи це Car чи Motorcycle — цей клас допомагає
    private static class VehicleDeserializer implements JsonDeserializer<Vehicle> {
        @Override
        public Vehicle deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx)
                throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            if (obj.has("category")) {
                return ctx.deserialize(json, Motorcycle.class);
            } else {
                return ctx.deserialize(json, Car.class);
            }
        }
    }
}