package snsystems.obd.trips;

/**
 * Created by shree on 13-Jan-17.
 */
public class TripsInformation
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
    private String engineIdleTime;
    private String latitude;
    private String landitude;

    public TripsInformation()
    {

    }

//    public TripsInformation(Parcel in)
//    {
//        sourceAddress = in.readString();
//        sourceDate = in.readString();
//        sourceTime = in.readString();
//        destinationAddress = in.readString();
//        destinationDate = in.readString();
//        destinationTime = in.readString();
//        distance = in.readString();
//        avgSpeed = in.readString();
//        engineIdleTime = in.readString();
//        latitude = in.readString();
//        landitude = in.readString();
//
//    }

//    @Override
//    public void writeToParcel(Parcel dest, int flags)
//    {
//        dest.writeString(sourceAddress);
//        dest.writeString(sourceDate);
//        dest.writeString(sourceTime);
//        dest.writeString(destinationAddress);
//        dest.writeString(destinationDate);
//        dest.writeString(destinationTime);
//        dest.writeString(distance);
//        dest.writeString(avgSpeed);
//        dest.writeString(engineIdleTime);
//        dest.writeString(latitude);
//        dest.writeString(landitude);
//
//
//    }
//
//    public static final Creator<TripsInformation> CREATOR
//
//
//            = new Creator<TripsInformation>() {
//        public TripsInformation createFromParcel(Parcel in) {
//
//
//            return new TripsInformation(in);
//        }
//
//        public TripsInformation[] newArray(int size)
//        {
//            return new TripsInformation[size];
//        }
//    };
//
//    @Override
//    public int describeContents()
//    {
//        return 0;
//    }

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

    public String getEngineIdleTime() {
        return engineIdleTime;
    }

    public void setEngineIdleTime(String engineIdleTime) {
        this.engineIdleTime = engineIdleTime;
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
}
