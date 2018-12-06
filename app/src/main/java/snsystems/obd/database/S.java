package snsystems.obd.database;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import snsystems.obd.classes.T;
import snsystems.obd.fueltrack.FuelInformation;
import snsystems.obd.interfaces.Constants;
import snsystems.obd.maintenance.MaintewnanceInformation;
import snsystems.obd.notificationalerts.NotificationInformation;
import snsystems.obd.tripsnew.TripInfoNew;

/**
 * Created by shree on 06-Jan-17.
 */
public class S
{

    public static boolean checkNewAlerts(DBHelper db,String alertName, String alertDate)
    {
        boolean status = false;

        try
        {

            Cursor cursor = null;

            cursor = db.checkNewAlerts(alertName,alertDate);

            if(cursor.getCount()>0)
            {
                status = true;
            }
            else
            {
                status = false;
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return status;
    }
    public static ArrayList<String> selectGeofenceData(DBHelper db,String device_id,String activeStatus)
    {

        ArrayList<String> GEOFENCE_DATA = new ArrayList<>();


        String data = null;

        try
        {

            Cursor cursor = null;

            cursor = db.selectGeofenceData(device_id,activeStatus);

            if(cursor.getCount()>0)
            {
                while(cursor.moveToNext())
                {
                    String DEVICE_ID = cursor.getString(cursor.getColumnIndex(db.DEVICE_ID));
                    String ADDRESS_LATLONG = cursor.getString(cursor.getColumnIndex(db.ADDRESS_LATLONG));
                    String RADIOUS = cursor.getString(cursor.getColumnIndex(db.RADIOUS));
                    String ACTIVE_STATUS = cursor.getString(cursor.getColumnIndex(db.ACTIVE_STATUS));

                    data = DEVICE_ID+"#"+ADDRESS_LATLONG+"#"+RADIOUS+"#"+ACTIVE_STATUS;

                    GEOFENCE_DATA.add(data);
                }
            }



        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return GEOFENCE_DATA;


    }

    public static ArrayList<TripInfoNew> getTripsDetails(DBHelper db,Context context)
    {




        ArrayList<TripInfoNew> SEVEN_DAY_TRIP = new ArrayList<>();

        try
        {

            Cursor cursor = null;

            cursor = db.getTripsDetails();

            if(cursor.getCount()>0)
            {
                while(cursor.moveToNext())
                {
                    String TRIP_LATLONG_ARRAY = cursor.getString(cursor.getColumnIndex(db.TRIP_LATLONG_ARRAY));
                    String HALT_LATLONG_ARRAY = cursor.getString(cursor.getColumnIndex(db.HALT_LATLONG_ARRAY));
                    String SOURCE_DATETIME = cursor.getString(cursor.getColumnIndex(db.SOURCE_DATETIME));
                    String DESTINATION_DATETIME = cursor.getString(cursor.getColumnIndex(db.DESTINATION_DATETIME));
                    String SOURE_ADDRESS = cursor.getString(cursor.getColumnIndex(db.SOURE_ADDRESS));
                    String DESTINATION_ADDRESS = cursor.getString(cursor.getColumnIndex(db.DESTINATION_ADDRESS));
                    String AVG_SPEED = cursor.getString(cursor.getColumnIndex(db.AVG_SPEED));
                    String ONTIME_OFFTIME = cursor.getString(cursor.getColumnIndex(db.ONTIME_OFFTIME));
                    String HALTS = cursor.getString(cursor.getColumnIndex(db.HALTS));

                    //Log.e("TRIP_LATLONG_ARRAY",""+TRIP_LATLONG_ARRAY);

                    ArrayList<String> tripLatlongArray = parsetriplatLongArray(TRIP_LATLONG_ARRAY);
                    ArrayList<String> haltLatlongArray = parseHaltLatLongArray(HALT_LATLONG_ARRAY);

                    TripInfoNew tripInfoNew = new TripInfoNew();

                    String [] sorceDateTime = SOURCE_DATETIME.split(" ");
                    String [] destinationDateTime = DESTINATION_DATETIME.split(" ");

                    tripInfoNew.setSourceTime(sorceDateTime[1]);
                    tripInfoNew.setSourceDate(sorceDateTime[0]);
                    tripInfoNew.setDestinationTime(destinationDateTime[1]);
                    tripInfoNew.setDestinationDate(destinationDateTime[0]);


                    String [] sourceAddressData = SOURE_ADDRESS.split("#");
                    String [] destAddressData = DESTINATION_ADDRESS.split("#");


                    tripInfoNew.setSourceAddress(T.getAddress(context,Double.valueOf(sourceAddressData[0]),Double.valueOf(sourceAddressData[1])));
                    tripInfoNew.setDestinationAddress(T.getAddress(context,Double.valueOf(destAddressData[0]),Double.valueOf(destAddressData[1])));

                    tripInfoNew.setDistance("0");
                    tripInfoNew.setEngineHalts(HALTS);
                    tripInfoNew.setAvgSpeed(AVG_SPEED);
                    tripInfoNew.setOnOffTime(ONTIME_OFFTIME);
                    tripInfoNew.setLatlongRouteArray(tripLatlongArray);
                    tripInfoNew.setLatlongHaltArray(haltLatlongArray);

                    SEVEN_DAY_TRIP.add(tripInfoNew);



                }
            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return SEVEN_DAY_TRIP;


    }

    public static ArrayList<NotificationInformation> getNotificationDashAlerts(DBHelper db,String device_id)
    {


        ArrayList<NotificationInformation> ALERTS_DATA = new ArrayList<>();

        try
        {

            Cursor cursor = null;

            cursor = db.selectDashboardData(device_id);

            if(cursor.getCount()>0)
            {
                while(cursor.moveToNext())
                {
                    String ALERT_NAME = cursor.getString(cursor.getColumnIndex(db.ALERT_NAME));
                    String ALERT_DATE_TIME = cursor.getString(cursor.getColumnIndex(db.ALERT_DATE_TIME));
                    String ALERT_TYPE = cursor.getString(cursor.getColumnIndex(db.ALERT_TYPE));


                    NotificationInformation tripInfoNew = new NotificationInformation();

                    tripInfoNew.setNotificationName(ALERT_NAME);
                    tripInfoNew.setNotificationDateTime(ALERT_DATE_TIME);
                    tripInfoNew.setNotificationLevel(ALERT_TYPE);

                    ALERTS_DATA.add(tripInfoNew);



                }
            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return ALERTS_DATA;


    }
    private static ArrayList<String> parseHaltLatLongArray(String haltarray) {


        ArrayList<String> latLong = new ArrayList<>();
        try
        {
            JSONArray jsonArray = new JSONArray(haltarray);


            for(int i =0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String latitude = jsonObject.getString("halt_latitude");
                String longitude = jsonObject.getString("halt_longitude");

                if(!(latitude.equals("0") && longitude.equals("0")))
                {
                    latLong.add(latitude+"#"+longitude);
                }



               // latLong.add(latitude+"#"+longitude);
            }


        }
        catch (Exception e)
        {

        }
        return latLong;
    }

    private static ArrayList<String> parsetriplatLongArray(String trip_latlong_array) {


        ArrayList<String> latLong = new ArrayList<>();
        try
        {
            JSONArray jsonArray = new JSONArray(trip_latlong_array);


            for(int i =0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String latitude = jsonObject.getString("latitude");
                String longitude = jsonObject.getString("longitude");

                if(!(latitude.equals("0") && longitude.equals("0")))
                {
                    latLong.add(latitude+"#"+longitude);
                }



            }


        }
        catch (Exception e)
        {

        }
        return latLong;
    }

    public static String getWeeklyMonthlyDetails(DBHelper db,String fromDate,String toDate)
    {


        String data = null;

        try
        {

            Cursor cursor = null;

            cursor = db.getWeeklyMonthlyDetails(fromDate,toDate);

            if(cursor.getCount()>0)
            {
                while(cursor.moveToNext())
                {
                    String AVG_SPEED = cursor.getString(cursor.getColumnIndex(db.AVG_SPEED));
                    String TOTAL_ALERT_COUNT = cursor.getString(cursor.getColumnIndex(db.TOTAL_ALERT_COUNT));
                    String HALT_COUNT = cursor.getString(cursor.getColumnIndex(db.HALT_COUNT));
                    String RPM_ALERT_COUNT = cursor.getString(cursor.getColumnIndex(db.RPM_ALERT_COUNT));
                    String SPEED_ALERT_COUNT = cursor.getString(cursor.getColumnIndex(db.SPEED_ALERT_COUNT));
                    String TROUBLE_ALERT_COUNT = cursor.getString(cursor.getColumnIndex(db.TROUBLE_ALERT_COUNT));
                    //String TODAYS_DATE = cursor.getString(cursor.getColumnIndex(db.TODAYS_DATE));

                    data = AVG_SPEED+"#"+TOTAL_ALERT_COUNT+"#"+HALT_COUNT+"#"+RPM_ALERT_COUNT+"#"+SPEED_ALERT_COUNT+"#"+TROUBLE_ALERT_COUNT;
                }
            }
            else
            {
                data = Constants.NA;
            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return data;


    }


    public static String getPerformanceDetails(DBHelper db)
    {


        String data = null;

        try
        {

            Cursor cursor = null;

            cursor = db.getPerformanceDetails();

            if(cursor.getCount()>0)
            {
                while(cursor.moveToNext())
                {
                    String AVG_SPEED = cursor.getString(cursor.getColumnIndex(db.AVG_SPEED));
                    String TOTAL_ALERT_COUNT = cursor.getString(cursor.getColumnIndex(db.TOTAL_ALERT_COUNT));
                    String HALT_COUNT = cursor.getString(cursor.getColumnIndex(db.HALT_COUNT));
                    String RPM_ALERT_COUNT = cursor.getString(cursor.getColumnIndex(db.RPM_ALERT_COUNT));
                    String SPEED_ALERT_COUNT = cursor.getString(cursor.getColumnIndex(db.SPEED_ALERT_COUNT));
                    String TROUBLE_ALERT_COUNT = cursor.getString(cursor.getColumnIndex(db.TROUBLE_ALERT_COUNT));
                    String TODAYS_DATE = cursor.getString(cursor.getColumnIndex(db.TODAYS_DATE));

                    data = AVG_SPEED+"#"+TOTAL_ALERT_COUNT+"#"+HALT_COUNT+"#"+RPM_ALERT_COUNT+"#"+SPEED_ALERT_COUNT+"#"+TROUBLE_ALERT_COUNT+"#"+TODAYS_DATE;
                }
            }
            else
            {
                data = Constants.NA;
            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return data;


    }
    public static String  checkOdoAvailable(DBHelper db,String vname)
    {



        String data = null;

        try
        {

            Cursor cursor = null;

            cursor = db.getOdoReading(vname);

            if(cursor.getCount() > 0)
            {
                while(cursor.moveToNext())
                {
                    String odo_reading = cursor.getString(cursor.getColumnIndex(db.ODO_READING));
                    String quantity = cursor.getString(cursor.getColumnIndex(db.QUANTITY));
                    String fuel_cost = cursor.getString(cursor.getColumnIndex(db.FFUEL_COST));

                    data = odo_reading+"#"+quantity+"#"+fuel_cost;

                }

            }
            else
            {
                data = Constants.NA;
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return data;


    }

    public static String getMaxMinFuelEconimyPrice(DBHelper db, String vName)
    {


        String data = null;

        try
        {

            Cursor cursor = null;

            cursor = db.getMaxMinFuelEconimyPrice(vName);

            if(cursor.getCount() > 0)
            {
                while(cursor.moveToNext())
                {
                    String fuel_economy_best = cursor.getString(cursor.getColumnIndex("fuel_economy_best"));
                    String fuel_economy_worst = cursor.getString(cursor.getColumnIndex("fuel_economy_worst"));
                    String fuel_price_max = cursor.getString(cursor.getColumnIndex("fuel_price_max"));
                    String fuel_price_min = cursor.getString(cursor.getColumnIndex("fuel_price_min"));

                    data = fuel_economy_best+"#"+fuel_economy_worst+"#"+fuel_price_max+"#"+fuel_price_min;
                }
            }
            else
            {
                data = Constants.NA;
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return data;
    }

    public static String getFuelEconomyFuelPriceValues(DBHelper db, String vName)
    {


        String data = null;

        try
        {

            Cursor cursor = null;

            cursor = db.getFuelEconomyFuelPriceValues(vName);

            if(cursor.getCount() > 0)
            {
                while(cursor.moveToNext())
                {
                    String oreading = cursor.getString(cursor.getColumnIndex("oreading"));
                    String fuel_price_avg = cursor.getString(cursor.getColumnIndex("fuel_price_avg"));
                    String total_fuel_qty = cursor.getString(cursor.getColumnIndex("total_fuel_qty"));

                    data = oreading+"#"+fuel_price_avg+"#"+total_fuel_qty;
                }
            }
            else
            {
                data = Constants.NA;
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return data;
    }


    public static String getTotalsData(DBHelper db,String vName)
    {

        String data = null;

        try
        {

            Cursor cursor = null;

            cursor = db.selectTotalsForFuelData(vName);

            if(cursor.getCount() > 0)
            {
                while(cursor.moveToNext())
                {
                    String total_distance = cursor.getString(cursor.getColumnIndex("total_distance"));
                    String total_fuel = cursor.getString(cursor.getColumnIndex("total_fuel"));
                    String total_fuel_cost = cursor.getString(cursor.getColumnIndex("total_fuel_cost"));
                    String total_fillups = cursor.getString(cursor.getColumnIndex("total_fillups"));

                    data = total_fuel+"#"+total_fuel_cost+"#"+total_fillups+"#"+total_distance;
                }
            }
            else
            {
                data = Constants.NA;
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return data;
    }

    public static String getVnameLastFillDateQty(DBHelper db,String vName)
    {

       String data = null;

        try
        {

            Cursor cursor = null;

            cursor = db.getVnameLastFillDateQty(vName);

            if(cursor.getCount() > 0)
            {
                while(cursor.moveToNext())
                {
                    String last_fill_date = cursor.getString(cursor.getColumnIndex(db.FFUEL_DATE));
                    String fuel_qty = cursor.getString(cursor.getColumnIndex(db.QUANTITY));

                    data = last_fill_date+"#"+fuel_qty;
                }
            }
            else
            {
                data = Constants.NA;
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return data;
    }

    //check already emergency contacts

//    public static String getDeviceIdUserName(DBHelper db)
//    {
//
//
//        String deviceIduser_email = null;
//
//        try
//        {
//
//            Cursor cursor = null;
//
//            cursor = db.getDeviceId();
//
//            if(cursor.getCount()>0)
//            {
//                while(cursor.moveToNext())
//                {
//                    String deviceId = cursor.getString(cursor.getColumnIndex("device_id"));
//                    String user_email = cursor.getString(cursor.getColumnIndex("user_email"));
//
//                    deviceIduser_email = deviceId+"#"+user_email;
//                }
//            }
//
//
//
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//
//        return deviceIduser_email;
//
//
//    }

    public static boolean alreadyEmergencyContacts(DBHelper db,String id)
    {
        boolean status = false;

        try
        {

            Cursor cursor = null;

            cursor = db.getAlreadyEmergency(id);

            if(cursor.getCount()>0)
            {
                status = true;
            }
            else
            {
                status = false;
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return status;
    }

    public static boolean checkSosAvailable(DBHelper db)
    {
        boolean status = false;

        try
        {

            Cursor cursor = null;

            cursor = db.checkSosAvailable();

            if(cursor.getCount()>0)
            {
                status = true;
            }
            else
            {
                status = false;
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return status;
    }

    //get contact details
    public static HashMap<String,ArrayList<String>> getCotactDetails(Context context)
    {
        HashMap<String,ArrayList<String>> CONTACTS = new HashMap<>();

        ArrayList<String> NAMES = new ArrayList<>();
        ArrayList<String> MOBILE = new ArrayList<>();

        try
        {

            Cursor cursor = null;

            cursor = new DBHelper(context).getCotacts();

            if(cursor.getCount() > 0)
            {
                while(cursor.moveToNext())
                {
                    String first_name = cursor.getString(cursor.getColumnIndex("first_name"));
                    String last_name = cursor.getString(cursor.getColumnIndex("last_name"));
                    String mobile = cursor.getString(cursor.getColumnIndex("mobile"));

                    NAMES.add(first_name+" "+last_name);
                    MOBILE.add(mobile);
                }

                CONTACTS.put("names",NAMES);
                CONTACTS.put("contacts",MOBILE);
            }



        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return CONTACTS;
    }

    public static HashMap<String,ArrayList<String>> getCotactDetailsIndividual(Context context,String mobiles)
    {
        HashMap<String,ArrayList<String>> CONTACTS = new HashMap<>();

        ArrayList<String> NAMES = new ArrayList<>();
        ArrayList<String> MOBILE = new ArrayList<>();

        try
        {

            Cursor cursor = null;

            cursor = new DBHelper(context).getCotactsIndividual(mobiles);

            if(cursor.getCount() > 0)
            {
                while(cursor.moveToNext())
                {
                    String first_name = cursor.getString(cursor.getColumnIndex("first_name"));
                    String last_name = cursor.getString(cursor.getColumnIndex("last_name"));
                    String mobile = cursor.getString(cursor.getColumnIndex("mobile"));

                    NAMES.add(first_name+" "+last_name);
                    MOBILE.add(mobile);
                }

                CONTACTS.put("names",NAMES);
                CONTACTS.put("contacts",MOBILE);
            }



        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return CONTACTS;
    }

    public static ArrayList<String> selectAlerts(DBHelper db)
    {

        ArrayList<String> ALERTS = new ArrayList<>();

        try
        {

            Cursor cursor = null;

            cursor = db.selectAlerts();


                while(cursor.moveToNext())
                {
                    String alert_name = cursor.getString(cursor.getColumnIndex("engine_data"));
                    String alert_status = cursor.getString(cursor.getColumnIndex("onoff_status"));


                    ALERTS.add(alert_name+"#"+alert_status);
                }







        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return ALERTS;
    }

    public static boolean checkAlerts(DBHelper db)
    {
        boolean status = false;

        try
        {

            Cursor cursor = null;

            cursor = db.selectAlerts();

            if(cursor.getCount()>0)
            {
                status = true;
            }
            else
            {
                status = false;
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return status;
    }

    public static String getDeviceIdUserName(DBHelper db)
    {


        String deviceIduser_email = null;

        try
        {

            Cursor cursor = null;

            cursor = db.getDeviceId();

            if(cursor.getCount()>0)
            {
                while(cursor.moveToNext())
                {
                    String deviceId = cursor.getString(cursor.getColumnIndex("device_id"));
                    String user_email = cursor.getString(cursor.getColumnIndex("user_email"));
                    String user_name = cursor.getString(cursor.getColumnIndex("user_name"));
                    String user_mobile_no = cursor.getString(cursor.getColumnIndex("user_mobile_no"));

                    deviceIduser_email = deviceId+"#"+user_email+"#"+user_name+"#"+user_mobile_no;
                }
            }



        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return deviceIduser_email;


    }
    public static String getDeviceId(DBHelper db)
    {


        String deviceId = null;

        try
        {

            Cursor cursor = null;

            cursor = db.getDeviceIddd();

            if(cursor.getCount()>0)
            {
                while(cursor.moveToNext())
                {
                    deviceId = cursor.getString(cursor.getColumnIndex("device_id"));

                    Log.e("DEVICXE_ID",""+deviceId);

                }
            }



        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return deviceId;


    }
    public static boolean checkDeviceIdAvail(DBHelper db)
    {


        boolean data = false;

        try
        {

            Cursor cursor = null;

            cursor = db.getDeviceIddd();

            if(cursor.getCount()>0)
            {
                data = true;
            }
            else
            {
                data = false;
            }



        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return data;


    }
    public static String getNameByDEviceId(DBHelper db,String dev_id)
    {


        String deviceId = null;

        try
        {

            Cursor cursor = null;

            cursor = db.getNameByDEviceId(dev_id);

            if(cursor.getCount()>0)
            {
                while(cursor.moveToNext())
                {
                    deviceId = cursor.getString(cursor.getColumnIndex("vname"));

                   // Log.e("DEVICXE_ID",""+deviceId);

                }
            }



        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return deviceId;


    }

    public static boolean checkSOSContactsAvailable(DBHelper db)
    {
        boolean status = false;

        try
        {

            Cursor cursor = null;

            cursor = db.checkDeviceAvail();

            if(cursor.getCount()>0)
            {
                status = true;
            }
            else
            {
                status = false;
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return status;
    }

    public static ArrayList<String> getSosContact(DBHelper db)
    {

        ArrayList<String> SOS_CONTACT = new ArrayList<>();

        try
        {

            Cursor cursor = null;

            cursor = db.checkDeviceAvail();

            if(cursor.getCount()>0)
            {
                while(cursor.moveToNext())
                {
                    String first_name = cursor.getString(cursor.getColumnIndex("first_name"));
                    String mobile = cursor.getString(cursor.getColumnIndex("mobile"));

                    SOS_CONTACT.add(first_name);
                    SOS_CONTACT.add(mobile);
                }
            }



        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return SOS_CONTACT;


    }

    public static boolean checkUserAlertsAvailablere(DBHelper db)
    {
        boolean status = false;

        try
        {

            Cursor cursor = null;

            cursor = db.checkUserAlertsAvail();

            if(cursor.getCount()>0)
            {
                status = true;
            }
            else
            {
                status = false;
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return status;
    }

    public static ArrayList<String> checkUserAlertsAvailable(DBHelper db)
    {
        ArrayList<String> ALERTS = new ArrayList<>();

        try
        {

            Cursor cursor = null;

            cursor = db.checkUserAlertsAvail();

            while(cursor.moveToNext())
            {
                String alert_name = cursor.getString(cursor.getColumnIndex("alert_name"));
                String on_off_statu = cursor.getString(cursor.getColumnIndex("on_off_status"));
                String selected_data = cursor.getString(cursor.getColumnIndex("selected_data"));

                ALERTS.add(alert_name+"#"+on_off_statu+"#"+selected_data);
                //Log.e("DARRRR",""+alert_name+"#"+on_off_statu+"#"+selected_data);
            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return ALERTS;

    }

    public static ArrayList<String> getVehicleName(DBHelper db)
    {
        /*
        static final String VNAME = "vname";
    static final String VDEVICE_ID = "vdevice_id";
         */
        ArrayList<String> VEHICLE = new ArrayList<>();

        try
        {
            Cursor cursor = null;
            cursor = db.getVehicleName();

            while(cursor.moveToNext())
            {

                String vname = cursor.getString(cursor.getColumnIndex("vname"));

                VEHICLE.add(vname);

            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return VEHICLE;

    }


    public static String returnDeviceIdByVehicleName(DBHelper db,String vehicleName)
    {


        String deviceId = null;

        try
        {

            Cursor cursor = null;

            cursor = db.getDeviceIdByName(vehicleName);

            if(cursor.getCount()>0)
            {
                while(cursor.moveToNext())
                {
                    deviceId = cursor.getString(cursor.getColumnIndex("vdevice_id"));

                    Log.e("DEVICXE_ID",""+deviceId);

                }
            }



        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return deviceId;


    }
    //maintenace data
    public static ArrayList<MaintewnanceInformation> getMaintenanceVehicleWiseData(DBHelper db,String vehicle_name)
    {

        ArrayList<MaintewnanceInformation> VEHICLE = new ArrayList<>();

        try
        {
            Cursor cursor = null;
            cursor = db.getMaintenceVehicleWise(vehicle_name);

            while(cursor.moveToNext())
            {

                String vname = cursor.getString(cursor.getColumnIndex("vname"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String mtype = cursor.getString(cursor.getColumnIndex("mtype"));
                String odo = cursor.getString(cursor.getColumnIndex("odo"));
                String cost = cursor.getString(cursor.getColumnIndex("cost"));
                String alerts = cursor.getString(cursor.getColumnIndex("alerts"));

                MaintewnanceInformation maintewnanceInformation = new MaintewnanceInformation();

                maintewnanceInformation.setVehicleName(vname);
                maintewnanceInformation.setmDate(date);
                maintewnanceInformation.setMaintenanceDetails(mtype);
                maintewnanceInformation.setOdoReading(odo);
                maintewnanceInformation.setmCost(cost);
                maintewnanceInformation.setmAlerts(alerts);


                VEHICLE.add(maintewnanceInformation);

            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return VEHICLE;

    }
    //maintenace data
    public static ArrayList<FuelInformation> getFuelData(DBHelper db,String vehicle_name)
    {

        ArrayList<FuelInformation> VEHICLE = new ArrayList<>();

        try
        {
            Cursor cursor = null;
            cursor = db.getFuelData(vehicle_name);

            while(cursor.moveToNext())
            {



                String vname = cursor.getString(cursor.getColumnIndex("vname"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String vtype = cursor.getString(cursor.getColumnIndex("vtype"));
                String fltrs = cursor.getString(cursor.getColumnIndex("fltrs"));
                String cost = cursor.getString(cursor.getColumnIndex("cost"));
                String oldkm = cursor.getString(cursor.getColumnIndex("oldkm"));
                String newkm = cursor.getString(cursor.getColumnIndex("newkm"));

                FuelInformation maintewnanceInformation = new FuelInformation();

                maintewnanceInformation.setVehicleName(vname);
                maintewnanceInformation.setDate(date);
                maintewnanceInformation.setFuelType(vtype);
                maintewnanceInformation.setFuelLtrs(fltrs);
                maintewnanceInformation.setCost(cost);
                maintewnanceInformation.setOldKm(oldkm);
                maintewnanceInformation.setNewKm(newkm);

                VEHICLE.add(maintewnanceInformation);

            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return VEHICLE;

    }

    public static String getCalculationDetails(DBHelper db,String vehicleName)
    {

        String costLtrs = null;

        ArrayList<Integer> COST = new ArrayList<>();
        ArrayList<Integer> LTRS = new ArrayList<>();

        try
        {
            Cursor cursor = null;
            cursor = db.getFuelCalculations(vehicleName);

            while(cursor.moveToNext())
            {

                String fltrs = cursor.getString(cursor.getColumnIndex("fltrs"));
                String cost = cursor.getString(cursor.getColumnIndex("cost"));

                COST.add(Integer.valueOf(cost));
                LTRS.add(Integer.valueOf(fltrs));

            }

            int sumCost = 0;
            //Advanced for loop
            for( int numCost : COST)
            {
                sumCost = sumCost+numCost;
            }

            int sumLtrs = 0;
            //Advanced for loop
            for( int numLtrs : LTRS)
            {
                sumLtrs = sumLtrs+numLtrs;
            }

            costLtrs = String.valueOf(sumCost)+"#"+String.valueOf(sumLtrs);


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return costLtrs;

    }

    public static ArrayList<String> selectAllSos(DBHelper db)
    {

        ArrayList<String> SOS_CONTACTS = new ArrayList<>();

        try
        {

            Cursor cursor = null;

            cursor = db.selectAllsos();


            while(cursor.moveToNext())
            {
                String mobile = cursor.getString(cursor.getColumnIndex("mobile"));



                SOS_CONTACTS.add(mobile);
            }







        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return SOS_CONTACTS;
    }

    public static String getDeviceIdFromVehicleMaster(DBHelper db)
    {


        String deviceId = null;

        try
        {

            Cursor cursor = null;

            cursor = db.getDeviceIdfromVehicleMaster();

            if(cursor.getCount()>0)
            {
                while(cursor.moveToNext())
                {
                    deviceId = cursor.getString(cursor.getColumnIndex("vdevice_id"));

                    Log.e("DEVICXE_ID",""+deviceId);

                }
            }



        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return deviceId;


    }


    public static boolean checkLogin(DBHelper db)
    {
        boolean status = false;

        try
        {

            Cursor cursor = null;

            cursor = db.checkLogin();

            if(cursor.getCount()>0)
            {
                status = true;
            }
            else
            {
                status = false;
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return status;
    }



}
