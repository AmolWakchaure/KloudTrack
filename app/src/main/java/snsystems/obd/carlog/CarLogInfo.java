package snsystems.obd.carlog;

/**
 * Created by snsystem_amol on 2/24/2017.
 */

public class CarLogInfo
{
    private  String avgSpeed;
    private  String max_speed;
    private  String no_of_halts;
    private  String total_km;
    private  String vehicle_on_time;
    private  String vehicle_off_time;

    public String getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(String avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public String getMax_speed() {
        return max_speed;
    }

    public void setMax_speed(String max_speed) {
        this.max_speed = max_speed;
    }

    public String getNo_of_halts() {
        return no_of_halts;
    }

    public void setNo_of_halts(String no_of_halts) {
        this.no_of_halts = no_of_halts;
    }

    public String getTotal_km() {
        return total_km;
    }

    public void setTotal_km(String total_km) {
        this.total_km = total_km;
    }

    public String getVehicle_on_time() {
        return vehicle_on_time;
    }

    public void setVehicle_on_time(String vehicle_on_time) {
        this.vehicle_on_time = vehicle_on_time;
    }

    public String getVehicle_off_time() {
        return vehicle_off_time;
    }

    public void setVehicle_off_time(String vehicle_off_time) {
        this.vehicle_off_time = vehicle_off_time;
    }
}
