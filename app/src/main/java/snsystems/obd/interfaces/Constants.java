package snsystems.obd.interfaces;

/**
 * Created by shree on 30-Jan-17.
 */
public interface Constants
{

    String NA = "NA";
    String ZERO = "0";
    String DATEE = "0000-00-00";
    //input parameters
    String DEVICE_ID = "device_id";
    String DATE = "date";
    String PARAARRAY = "paraarray";
    String START_DATE = "startdate";
    String END_DATE = "enddate";

    String SOURCE_LATLONG = "source_latlong";
    String DESTINATION_LATLONG = "destination_latlong";

    String SOURCE_ADDRESS = "source_address";
    String DESTINATION_ADDRESS = "destination_address";

    String SOURCE_DATE_TIME = "source_date_time";
    String DESTINATION_DATE_TIME = "destination_date_time";

    String TRIP_PREFERENCE = "trip_preference";
    String NOTIFICATION_PREFERENCE = "notification_preference";
    String SMS_TEXT = "sms_text";
   // String TRIP_PREFERENCE = "trip_preference";
    String GEOFENCE_PREFERENCE = "geofence_preference";


    String NAVIGATION = "navigation";

    String FUEL_FILLUP = "fillup";

    //json key


    public static final String API_NOT_CONNECTED = "Google API not connected";
    public static final String SOMETHING_WENT_WRONG = "OOPs!!! Something went wrong...";
    public static String PlacesTag = "Google Places Auto Complete";

    public static String activity_register_url = "http://1.22.124.222:81/obd/api/MobileAPI/GetUserData";
    public static String activity_login_url = "http://1.22.124.222:81/obd/api/MobileAPI/ValidLogin";
    public static String activity_change_password_url = "http://1.22.124.222:81/obd/api/MobileAPI/ChangePassword";
    public static String activity_forgot_password_url = "http://1.22.124.222:81/obd/api/MobileAPI/ForgetPassword";
    public static String check_device_id_url ="http://1.22.124.222:81/obd/api/MobileAPI/CheckExistingDeviceID";
    public static  String user_logout = "http://1.22.124.222:81/obd/api/MobileAPI/Logout";

    public static String activity_user_email_id= "http://1.22.124.222:81/obd/api/MobileAPI/UserProfile";
    public static String activity_insert_user_info= "http://1.22.124.222:81/obd/api/MobileAPI/InsertUserProfile";

    public static String activity_manage_account = "http://1.22.124.222:81/obd/api/MobileAPI/ManageAccount";
    public static String activity_insert_device = "http://1.22.124.222:81/obd/api/MobileAPI/InsertDevice";
    public static String activity_update_device = "http://1.22.124.222:81/obd/api/MobileAPI/UpdateDevice";
    public static String activity_device_lat_lang = "http://1.22.124.222:81/obd/api/MobileAPI/GetDeviceLatLong";

    public String OOPS = "Oops...";
    public String SUCCESS = "Success";
    public String NETWORK_CONNECTION_OFF = "Network connection off,Check your network connection.";
    public String OKK = "OK";
    public String CANCEL = "CANCEL";
    public String INCORRECT_JSON = "Invalid Json format...";

    public String NULL_JSON = "null Json format...";


}
