package org.example.carrent;

public class Rental {
    private String id;
    private String userLogin;
    private String vehicleId;
    private String startDate;
    private String endDate;

    public Rental(String id, String userLogin, String vehicleId, String startDate) {
        this.id = id;
        this.userLogin = userLogin;
        this.vehicleId = vehicleId;
        this.startDate = startDate;
        this.endDate = null;
    }

    public String getId() { return id; }
    public String getUserLogin() { return userLogin; }
    public String getVehicleId() { return vehicleId; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public boolean isActive() { return endDate == null; }

    @Override
    public String toString() {
        return "Rental{id='" + id + "', userLogin='" + userLogin +
                "', vehicleId='" + vehicleId + "', startDate='" + startDate +
                "', endDate='" + endDate + "'}";
    }
}