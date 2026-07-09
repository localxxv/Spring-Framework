package org.example.carrent.dto;

public class PaymentResponse {

    private String userLogin;
    private String vehicleId;
    private double pricePerDay;
    private long days;
    private double amount;
    private boolean paid;
    private String message;

    public PaymentResponse() {
    }

    public PaymentResponse(String userLogin, String vehicleId, double pricePerDay,
                           long days, double amount, boolean paid, String message) {
        this.userLogin = userLogin;
        this.vehicleId = vehicleId;
        this.pricePerDay = pricePerDay;
        this.days = days;
        this.amount = amount;
        this.paid = paid;
        this.message = message;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public long getDays() {
        return days;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isPaid() {
        return paid;
    }

    public String getMessage() {
        return message;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public void setDays(long days) {
        this.days = days;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    private String paymentIntentId;

    public String getPaymentIntentId() { return paymentIntentId; }
    public void setPaymentIntentId(String paymentIntentId) { this.paymentIntentId = paymentIntentId; }
}