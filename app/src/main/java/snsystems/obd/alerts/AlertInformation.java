package snsystems.obd.alerts;

/**
 * Created by shree on 28-Dec-16.
 */
public class AlertInformation
{

    private String alertData;
    private boolean alertStatus;


    public String getAlertData() {
        return alertData;
    }

    public void setAlertData(String alertData) {
        this.alertData = alertData;
    }


    public boolean isAlertStatus() {
        return alertStatus;
    }

    public void setAlertStatus(boolean alertStatus) {
        this.alertStatus = alertStatus;
    }
}
