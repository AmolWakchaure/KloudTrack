package snsystems.obd.notificationalerts;

/**
 * Created by snsystem_amol on 3/3/2017.
 */

public class NotificationInformation
{
    private String notificationName;
    private String notificationLevel;
    private String notificationDateTime;

    public String getNotificationName() {
        return notificationName;
    }

    public void setNotificationName(String notificationName) {
        this.notificationName = notificationName;
    }

    public String getNotificationLevel() {
        return notificationLevel;
    }

    public void setNotificationLevel(String notificationLevel) {
        this.notificationLevel = notificationLevel;
    }


    public String getNotificationDateTime() {
        return notificationDateTime;
    }

    public void setNotificationDateTime(String notificationDateTime) {
        this.notificationDateTime = notificationDateTime;
    }
}
