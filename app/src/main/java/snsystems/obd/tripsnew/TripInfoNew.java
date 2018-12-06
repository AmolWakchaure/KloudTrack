package snsystems.obd.tripsnew;

import java.util.ArrayList;

/**
 * Created by snsystem_amol on 3/17/2017.
 */

public class TripInfoNew
{
    private String id;
    private String sourceAddress;
    private String sourceDate;
    private String sourceTime;
    private String destinationAddress;
    private String destinationDate;
    private String destinationTime;
    private String distance;
    private String avgSpeed;
    private String engineHalts;
    private String latitude;
    private String landitude;

    private String onOffTime;

    private ArrayList<String> latlongRouteArray;
    private ArrayList<String> latlongHaltArray;

    public TripInfoNew()
    {

    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public String getSourceDate() {
        return sourceDate;
    }

    public void setSourceDate(String sourceDate) {
        this.sourceDate = sourceDate;
    }

    public String getSourceTime() {
        return sourceTime;
    }

    public void setSourceTime(String sourceTime) {
        this.sourceTime = sourceTime;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getDestinationDate() {
        return destinationDate;
    }

    public void setDestinationDate(String destinationDate) {
        this.destinationDate = destinationDate;
    }

    public String getDestinationTime() {
        return destinationTime;
    }

    public void setDestinationTime(String destinationTime) {
        this.destinationTime = destinationTime;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(String avgSpeed) {
        this.avgSpeed = avgSpeed;
    }




    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLanditude() {
        return landitude;
    }

    public void setLanditude(String landitude) {
        this.landitude = landitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEngineHalts() {
        return engineHalts;
    }

    public void setEngineHalts(String engineHalts) {
        this.engineHalts = engineHalts;
    }


    public ArrayList<String> getLatlongRouteArray() {
        return latlongRouteArray;
    }

    public void setLatlongRouteArray(ArrayList<String> latlongRouteArray) {
        this.latlongRouteArray = latlongRouteArray;
    }

    public ArrayList<String> getLatlongHaltArray() {
        return latlongHaltArray;
    }

    public void setLatlongHaltArray(ArrayList<String> latlongHaltArray) {
        this.latlongHaltArray = latlongHaltArray;
    }

    public String getOnOffTime() {
        return onOffTime;
    }

    public void setOnOffTime(String onOffTime) {
        this.onOffTime = onOffTime;
    }
}
