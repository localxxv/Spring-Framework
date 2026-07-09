package org.example.carrent.models;

public class User {

    private String login;
    private String passwordHash;
    private Role role;
    private String address;

    public User() {
    }

    public User(String login, String passwordHash, Role role) {
        this.login = login;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public User(String login, String passwordHash, Role role, String address) {
        this.login = login;
        this.passwordHash = passwordHash;
        this.role = role;
        this.address = address;
    }

    public User copy() {
        return new User(
                this.login,
                this.passwordHash,
                this.role,
                this.address
        );
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

    public String getAddress() {
        return address;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", role=" + role +
                ", address='" + address + '\'' +
                '}';
    }
}