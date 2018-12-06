package snsystems.obd.maintenance;

/**
 * Created by snsystem_amol on 3/18/2017.
 */

public class ServiceHistoryInformationn
{
    private String id;
    private String deviceId;
    private String vehicleName;
    private String maintenanceDate;
    private String maintenanceType;
    private String serviceDueDate;
    private String serviceRemark;
    private String serviceStatus;
    private String serviceOtherLov;
    private String serviceCost;

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getMaintenanceDate() {
        return maintenanceDate;
    }

    public void setMaintenanceDate(String maintenanceDate) {
        this.maintenanceDate = maintenanceDate;
    }

    public String getMaintenanceType() {
        return maintenanceType;
    }

    public void setMaintenanceType(String maintenanceType) {
        this.maintenanceType = maintenanceType;
    }

    public String getServiceDueDate() {
        return serviceDueDate;
    }

    public void setServiceDueDate(String serviceDueDate) {
        this.serviceDueDate = serviceDueDate;
    }

    public String getServiceRemark() {
        return serviceRemark;
    }

    public void setServiceRemark(String serviceRemark) {
        this.serviceRemark = serviceRemark;
    }

    public String getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public String getServiceOtherLov() {
        return serviceOtherLov;
    }

    public void setServiceOtherLov(String serviceOtherLov) {
        this.serviceOtherLov = serviceOtherLov;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceCost() {
        return serviceCost;
    }

    public void setServiceCost(String serviceCost) {
        this.serviceCost = serviceCost;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
