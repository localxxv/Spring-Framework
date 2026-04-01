package org.example.carrent;

public class User {
    private String login;
    private String passwordHash;
    private Role role;
    private String rentedVehicleId;

    public User(String login, String passwordHash, Role role, String rentedVehicleId) {
        this.login = login;
        this.passwordHash = passwordHash;
        this.role = role;
        this.rentedVehicleId = rentedVehicleId;
    }

    public User(User other) {
        this.login = other.login;
        this.passwordHash = other.passwordHash;
        this.role = other.role;
        this.rentedVehicleId = other.rentedVehicleId;
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public String getRentedVehicleId() {
        return rentedVehicleId;
    }

    public void setRentedVehicleId(String rentedVehicleId) {
        this.rentedVehicleId = rentedVehicleId;
    }

    public User copy() {
        return new User(this);
    }

    public String toCSV() {
        return login + ";" + passwordHash + ";" + role + ";" +
                (rentedVehicleId == null ? "" : rentedVehicleId);
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", role=" + role +
                ", rentedVehicleId='" + rentedVehicleId + '\'' +
                '}';
    }
}