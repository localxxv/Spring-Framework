package org.example.carrent.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    private String login;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String role;

    @Column
    private String address;

    public UserEntity() {
    }

    public UserEntity(String login, String passwordHash, String role) {
        this.login = login;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public UserEntity(String login, String passwordHash, String role, String address) {
        this.login = login;
        this.passwordHash = passwordHash;
        this.role = role;
        this.address = address;
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getRole() {
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

    public void setRole(String role) {
        this.role = role;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}