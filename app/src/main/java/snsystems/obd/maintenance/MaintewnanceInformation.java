package snsystems.obd.maintenance;

/**
 * Created by snsystem_amol on 2/27/2017.
 */

public class MaintewnanceInformation
{
    private String vehicleName;
    private String maintenanceDetails;
    private String odoReading;
    private String mCost;
    private String mDate;
    private String mAlerts;

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getMaintenanceDetails() {
        return maintenanceDetails;
    }

    public void setMaintenanceDetails(String maintenanceDetails) {
        this.maintenanceDetails = maintenanceDetails;
    }

    public String getOdoReading() {
        return odoReading;
    }

    public void setOdoReading(String odoReading) {
        this.odoReading = odoReading;
    }

    public String getmCost() {
        return mCost;
    }

    public void setmCost(String mCost) {
        this.mCost = mCost;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public String getmAlerts() {
        return mAlerts;
    }

    public void setmAlerts(String mAlerts) {
        this.mAlerts = mAlerts;
    }
}
