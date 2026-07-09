package org.example.carrent.dto;

public class RegisterRequest {

    private String login;
    private String password;
    private String address;

    public RegisterRequest() {
    }

    public RegisterRequest(String login, String password, String address) {
        this.login = login;
        this.password = password;
        this.address = address;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getAddress() {
        return address;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}