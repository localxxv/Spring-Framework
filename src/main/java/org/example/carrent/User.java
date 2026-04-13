package org.example.carrent;

public class User {
    private String login;
    private String passwordHash;
    private Role role;

    public User(String login, String passwordHash, Role role) {
        this.login = login;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public User(User other) {
        this.login = other.login;
        this.passwordHash = other.passwordHash;
        this.role = other.role;
    }

    public String getLogin() { return login; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }

    public User copy() { return new User(this); }

    @Override
    public String toString() {
        return "User{login='" + login + "', role=" + role + "}";
    }
}