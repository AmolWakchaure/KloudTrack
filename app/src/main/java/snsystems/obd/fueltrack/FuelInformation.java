package snsystems.obd.fueltrack;

/**
 * Created by snsystem_amol on 2/27/2017.
 */

public class FuelInformation {

    private String vehicleName;
    private String date;
    private String fuelType;
    private String fuelLtrs;
    private String cost;
    private String oldKm;
    private String newKm;

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getFuelLtrs() {
        return fuelLtrs;
    }

    public void setFuelLtrs(String fuelLtrs) {
        this.fuelLtrs = fuelLtrs;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getOldKm() {
        return oldKm;
    }

    public void setOldKm(String oldKm) {
        this.oldKm = oldKm;
    }

    public String getNewKm() {
        return newKm;
    }

    public void setNewKm(String newKm) {
        this.newKm = newKm;
    }
}
