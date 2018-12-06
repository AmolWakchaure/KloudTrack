package snsystems.obd.alerts;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shree on 26-Dec-16.
 */
public class CarLogInformation implements Parcelable
{

    private String imageStatus;
    private String engineStatus;
    private String carlogTime;
    private String carlogDate;

    public CarLogInformation()
    {

    }

    public CarLogInformation(Parcel in)
    {
        imageStatus = in.readString();
        engineStatus = in.readString();
        carlogTime = in.readString();
        carlogDate = in.readString();

    }
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(imageStatus);
        dest.writeString(engineStatus);
        dest.writeString(carlogTime);
        dest.writeString(carlogDate);




    }
    public static final Creator<CarLogInformation> CREATOR


            = new Creator<CarLogInformation>() {
        public CarLogInformation createFromParcel(Parcel in) {


            return new CarLogInformation(in);
        }

        public CarLogInformation[] newArray(int size)
        {
            return new CarLogInformation[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }


    public String getImageStatus() {
        return imageStatus;
    }

    public void setImageStatus(String imageStatus) {
        this.imageStatus = imageStatus;
    }

    public String getEngineStatus() {
        return engineStatus;
    }

    public void setEngineStatus(String engineStatus) {
        this.engineStatus = engineStatus;
    }

    public String getCarlogTime() {
        return carlogTime;
    }

    public void setCarlogTime(String carlogTime) {
        this.carlogTime = carlogTime;
    }

    public String getCarlogDate() {
        return carlogDate;
    }

    public void setCarlogDate(String carlogDate) {
        this.carlogDate = carlogDate;
    }
}
