package snsystems.obd.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;

import snsystems.obd.R;
import snsystems.obd.geofencesnehal.GeoFenceObjectClass;


/**
 * Created by shree on 06-Jan-17.
 */
public class DBHelper extends SQLiteOpenHelper
{
    private static final String DATABSE_NAME = "obd";
    private static final int DATABSE_VERSION = 2;


    // Tables
    static final String TABLE_EMERGENCY_CONTACTS = "emergency_contacts";
    static final String TABLE_ENGINE_ALERTS = "engine_alerts";


    //columns of emergency_contacts
    static final String ID = "id";
    static final String FIRST_NAME = "first_name";
    //static final String LAST_NAME = "last_name";
    static final String MOBILE = "mobile";


    //columns of engine_alerts
    static final String EID = "eid";
    static final String ENGINE_ALARM = "engine_data";
    static final String ON_OFF_STATUS = "onoff_status";



    //-----------------------------------------------swati---------------------------------------------------

    //Tables
    static final String TABLE_REGISTER = "register_table";
    static final String TABLE_LOGIN = "login_table";
    static final String USER_DEFINED_ALERTS = "user_alerts";

    //Register_table Columns
    public static final String R_ID = "r_id";
    public static final String USER_NAME = "user_name";
    public static final String USER_EMAIL = "user_email";
    public static final String USER_DEVICE_ID = "device_id";
    public static final String USER_PASSWORD = "user_password";
    public static final String USER_MOBILE_NO = "user_mobile_no";

    //Login table Columns
    public static final String L_ID = "l_id";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";

    //columns of user_alerts
    static final String UDID = "aid";
    static final String ALERT_NAME = "alert_name";
    static final String UDON_OFF_STATUS = "on_off_status";
    static final String UDON_SELECTED_DATA = "selected_data";

    public Context context;
    //-------------------------------------------------------------------------------------------------------
    String CREATE_USER_ALERTS = "CREATE TABLE " + USER_DEFINED_ALERTS + "("
            + UDID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ALERT_NAME +
            " TEXT , " + UDON_OFF_STATUS + " TEXT , "  + UDON_SELECTED_DATA + " TEXT)";

    private static final String EME_CONTACTS_TABLE = "create table " + TABLE_EMERGENCY_CONTACTS
            + "(" + ID + " Integer Primary Key AutoIncrement," + FIRST_NAME
            + "  TEXT," + MOBILE + " TEXT)";

    private static final String ENG_ALERTS_TABLE = "create table " + TABLE_ENGINE_ALERTS
            + "(" + EID + " Integer Primary Key AutoIncrement," + ENGINE_ALARM
            + "  TEXT," + ON_OFF_STATUS + " TEXT)";

    String CREATE_LOGIN = "CREATE TABLE " + TABLE_LOGIN + "("
            + L_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + EMAIL
            + " TEXT, " + PASSWORD + " TEXT )";

    String CREATE_REGISTER = "CREATE TABLE " + TABLE_REGISTER + "("
            + R_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + USER_NAME +
            " TEXT , " + USER_EMAIL + " TEXT , "  + USER_DEVICE_ID + " TEXT , "
            + USER_PASSWORD + " TEXT , " +
            USER_MOBILE_NO + " TEXT )";


    //table
    static final String TABLE_VEHICLE_MASTER = "vehicle";

    //fields
    static final String VID = "vid";
    static final String VNAME = "vname";
    static final String VDEVICE_ID = "vdevice_id";


    String CREATE_VEHICEL_MASTER = "CREATE TABLE " + TABLE_VEHICLE_MASTER + "("
            + VID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + VNAME +
            " TEXT , " + VDEVICE_ID + " TEXT)";

    //---------------------------------------------------maintenance---------------------------------------------------

    static final String TABLE_MAINTENANCE_MASTER = "maintenance";

    //fields
    static final String MID = "id";
    static final String MVEHICEL_VNAME = "vname";
    static final String MMAINTENANCE_DATE = "date";
    static final String MMAINTENANCE_TYPE = "mtype";
    static final String MMAINTENANCE_ODO = "odo";
    static final String MMAINTENANCE_COST = "cost";
    static final String MMAINTENANCE_LALERTS = "alerts";


    String CREATE_MAINTENANCE_MASTER = "CREATE TABLE " + TABLE_MAINTENANCE_MASTER + "("
            + MID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + MVEHICEL_VNAME +
            " TEXT , " + MMAINTENANCE_DATE + " TEXT," + MMAINTENANCE_TYPE + " TEXT," + MMAINTENANCE_ODO + " TEXT," + MMAINTENANCE_COST + " TEXT," + MMAINTENANCE_LALERTS + " TEXT)";

    //---------------------------------------------------fuel tracker---------------------------------------------------

    static final String TABLE_FUEL_MASTER = "fuel";

    //fields
    static final String FID = "id";
    static final String FVEHICEL_VNAME = "vname";
    static final String FFUEL_DATE = "date";
    static final String FVEHICLE_TYPE = "vtype";
    static final String FFUEL_LTRS = "fltrs";
    static final String FFUEL_COST = "cost";
    static final String OLD_KM = "oldkm";
    static final String NEW_KM = "newkm";

    String CREATE_FUEL_MASTER = "CREATE TABLE " + TABLE_FUEL_MASTER + "("
            + FID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FVEHICEL_VNAME +
            " TEXT , " + FFUEL_DATE + " TEXT," + FVEHICLE_TYPE + " TEXT," + FFUEL_LTRS + " TEXT," + FFUEL_COST + " TEXT," + OLD_KM + " TEXT," + NEW_KM + " TEXT)";

    //---------------------------------------------------notification details---------------------------------------------------

    static final String NOTIFICATION_MASTER = "notification";

    //fields
    static final String NID = "id";
    static final String NNAME = "name";
    static final String NDATE_TIME = "datetime";

    String CREATE_NOTIFICATION_MASTER = "CREATE TABLE " + NOTIFICATION_MASTER + "("
            + NID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NNAME +
            " TEXT , " + NDATE_TIME + " TEXT)";



    //---------------------------------------------------fuel tracker new---------------------------------------------------

    static final String TABLE_FUEL_MASTER_NEW = "fuelnew";

    //fields
    //static final String ID = "id";
    //static final String FVEHICEL_VNAME = "vname";
    //static final String FFUEL_DATE = "date";
    static final String ODO_READING = "oreading";
    static final String QUANTITY = "qty";
    //static final String FFUEL_COST = "cost";
    static final String NOTES = "notes";
    static final String LAST_FILL_DATE = "ldate";


    String CREATE_FUEL_MASTER_NEW = "CREATE TABLE " + TABLE_FUEL_MASTER_NEW + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FVEHICEL_VNAME +
            " TEXT , " + FFUEL_DATE + " TEXT," + ODO_READING + " TEXT," + QUANTITY + " TEXT," + FFUEL_COST + " TEXT," + NOTES + " TEXT," + LAST_FILL_DATE + " TEXT)";

    //---------------------------------------------------vehicle performance---------------------------------------------------

    static final String TABLE_VEHICLE_PERFORMANCE= "performance";

    //fields
    //static final String ID = "id";
    static final String AVG_SPEED = "avg_speed";
    static final String TOTAL_ALERT_COUNT = "total_alert_count";
    static final String HALT_COUNT = "halt_count";
    static final String RPM_ALERT_COUNT = "rpm_alert_count";
    static final String SPEED_ALERT_COUNT = "apeed_alert_count";
    static final String TROUBLE_ALERT_COUNT = "t_alert_count";
    static final String TODAYS_DATE = "date";



    String CREATE_PERFORMANCE_MASTER_NEW = "CREATE TABLE " + TABLE_VEHICLE_PERFORMANCE + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + AVG_SPEED + " TEXT , "
            + TOTAL_ALERT_COUNT + " TEXT,"
            + HALT_COUNT + " TEXT,"
            + RPM_ALERT_COUNT + " TEXT,"
            + SPEED_ALERT_COUNT + " TEXT,"
            + TROUBLE_ALERT_COUNT + " TEXT,"
            + TODAYS_DATE + " TEXT,UNIQUE ("+TODAYS_DATE+") ON CONFLICT REPLACE)";


    //---------------------------------------------------trips---------------------------------------------------

    static final String TABLE_VEHICLE_TRIPS= "tripss";

    //fields
    //static final String ID = "id";
    static final String TRIP_LATLONG_ARRAY = "trip_latlong_array";
    static final String HALT_LATLONG_ARRAY = "halt_latlong_array";
    static final String SOURCE_DATETIME = "source_datetime";
    static final String DESTINATION_DATETIME = "destination_datetime";
    static final String SOURE_ADDRESS = "source_address";
    static final String DESTINATION_ADDRESS = "destination_address";
    //static final String AVG_SPEED = "avg_speed";
    static final String ONTIME_OFFTIME = "ontime_offtime";
    static final String HALTS = "halt";
    //static final String TODAYS_DATE = "date";



    String CREATE_TRIPS_MASTER_NEW = "CREATE TABLE " + TABLE_VEHICLE_TRIPS + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TRIP_LATLONG_ARRAY + " TEXT , "
            + HALT_LATLONG_ARRAY + " TEXT,"
            + SOURCE_DATETIME + " TEXT,"
            + DESTINATION_DATETIME + " TEXT,"
            + SOURE_ADDRESS + " TEXT,"
            + DESTINATION_ADDRESS + " TEXT,"
            + AVG_SPEED + " TEXT,"
            + ONTIME_OFFTIME + " TEXT,"
            + HALTS + " TEXT,"
            + TODAYS_DATE + " TEXT,UNIQUE ("+TODAYS_DATE+") ON CONFLICT REPLACE)";



    //---------------------------------------------------dash board alerts---------------------------------------------------

    static final String TABLE_DASHBOARD_ALERTS= "dash_alerts";

    //fields
    //static final String ID = "id";
    //static final String ALERT_NAME = "alert_name";
    static final String ALERT_DATE_TIME = "alert_date_time";
    static final String ALERT_TYPE = "alert_type";

    static final String VEHICLE_NAME = "vname";



    String CREATE_DASHBOARD_ALERTS_NEW = "CREATE TABLE " + TABLE_DASHBOARD_ALERTS + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ALERT_NAME + " TEXT , "
            + ALERT_DATE_TIME + " TEXT,"
            + ALERT_TYPE + " TEXT,"
            + DEVICE_ID + " TEXT)";



    //---------------------------------------------------store geofence---------------------------------------------------

    static final String TABLE_STORE_GEOFENCE = "store_geofence";

    //fields
    //static final String ID = "id";
    static final String DEVICE_ID = "d_id";
    static final String ADDRESS_LATLONG = "latlong";
    static final String RADIOUS = "radious";

    static final String ACTIVE_STATUS = "status";
    static final String GEOFENCE_NAME = "geofence_name";
    static final String ARRIVED = "arrive";
    static final String DEPARTURE = "departure";


    String CREATE_STORE_GEOFENCE = "CREATE TABLE " + TABLE_STORE_GEOFENCE + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DEVICE_ID + " TEXT , "
            + ADDRESS_LATLONG + " TEXT,"
            + RADIOUS + " TEXT,"
            + GEOFENCE_NAME + " TEXT,"
            + ACTIVE_STATUS + " TEXT,"
            + ARRIVED + " TEXT  DEFAULT 'false' ,"
            + DEPARTURE + " TEXT DEFAULT 'false' )";

    public DBHelper(Context context)
    {
        super(context, DATABSE_NAME, null, DATABSE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {

        db.execSQL(EME_CONTACTS_TABLE);
        db.execSQL(ENG_ALERTS_TABLE);
        db.execSQL(CREATE_LOGIN);
        db.execSQL(CREATE_REGISTER);
        db.execSQL(CREATE_USER_ALERTS);
        db.execSQL(CREATE_VEHICEL_MASTER);
        db.execSQL(CREATE_MAINTENANCE_MASTER);
        db.execSQL(CREATE_FUEL_MASTER);
        db.execSQL(CREATE_NOTIFICATION_MASTER);
        db.execSQL(CREATE_FUEL_MASTER_NEW);
        db.execSQL(CREATE_PERFORMANCE_MASTER_NEW);
        db.execSQL(CREATE_TRIPS_MASTER_NEW);
        db.execSQL(CREATE_DASHBOARD_ALERTS_NEW);
        db.execSQL(CREATE_STORE_GEOFENCE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        /*
         static final String TABLE_STORE_GEOFENCE = "store_geofence";

    //fields
    //static final String ID = "id";
    static final String DEVICE_ID = "d_id";
    static final String ADDRESS_LATLONG = "latlong";
    static final String RADIOUS = "radious";
    static final String ACTIVE_STATUS = "status";

    static final String GEOFENCE_NAME = "geofence_name";
    static final String ARRIVED = "arrive";
    static final String DEPARTURE = "departure";

         */

        if(oldVersion < newVersion)
        {
           // Log.e("dbVersion",""+oldVersion);
            db.execSQL("ALTER TABLE "+TABLE_DASHBOARD_ALERTS+" ADD COLUMN "+DEVICE_ID+" TEXT");
            //db.execSQL("ALTER TABLE "+TABLE_STORE_GEOFENCE+" ADD COLUMN "+GEOFENCE_NAME+" TEXT, "+ARRIVED+" TEXT, "+DEPARTURE+" TEXT");
        }



    }

    public Cursor checkNewAlerts(String alertName, String alertDate)
    {
        /*
         values.put(ALERT_NAME, alert_name);
            values.put(ALERT_DATE_TIME, alert_date_time);
            values.put(ALERT_TYPE, alert_data);

            db.insert(TABLE_DASHBOARD_ALERTS, null, values);
         */
        SQLiteDatabase dbb = this.getReadableDatabase();

        Cursor cursor;
        cursor = dbb.rawQuery("SELECT "+ALERT_DATE_TIME+" from "+TABLE_DASHBOARD_ALERTS+" WHERE "+ALERT_NAME+" ='"+alertName+"' AND "+ALERT_DATE_TIME+" ='"+alertDate+"'",null);
        return cursor;
    }

    //insert fuel new data
    public void storeGeofence(String d_id,
                              String latlong,
                              String radious,
                              String status,
                              String geofence_name) {

        /*
        static final String TABLE_STORE_GEOFENCE= "store_geofence";

    //fields
    //static final String ID = "id";
    static final String DEVICE_ID = "d_id";
    static final String ADDRESS_LATLONG = "latlong";
    static final String RADIOUS = "radious";
    static final String ACTIVE_STATUS = "status";

         */

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(DEVICE_ID, d_id);
            values.put(ADDRESS_LATLONG, latlong);
            values.put(RADIOUS, radious);
            values.put(ACTIVE_STATUS, status);
            values.put(GEOFENCE_NAME, geofence_name);

            db.insert(TABLE_STORE_GEOFENCE, null, values);

        } catch (Exception e) {
            // TODO Auto-generated catch block
        }

    }
    public ArrayList<GeoFenceObjectClass> selectGeofence(String device_id, String activeStatus) {

        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * from " + TABLE_STORE_GEOFENCE + " WHERE " + DEVICE_ID + " ='" + device_id + "' AND "
                + ACTIVE_STATUS + " = '" + activeStatus + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        ArrayList<GeoFenceObjectClass> tax_list = new ArrayList<GeoFenceObjectClass>();

        if (cursor.moveToFirst()) {
            do {

                tax_list.add(new GeoFenceObjectClass(cursor.getString(cursor.getColumnIndex(ID)),
                        cursor.getString(cursor.getColumnIndex(GEOFENCE_NAME)),
                        cursor.getString(cursor.getColumnIndex(RADIOUS)),
                        cursor.getString(cursor.getColumnIndex(ADDRESS_LATLONG)),
                        cursor.getString(cursor.getColumnIndex(ARRIVED)),
                        cursor.getString(cursor.getColumnIndex(DEPARTURE))));

            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
        return tax_list;
    }

    public Cursor selectGeofenceData(String device_id,String activeStatus)
    {
        SQLiteDatabase dbb = this.getReadableDatabase();

        Cursor cursor;
        cursor = dbb.rawQuery("SELECT * from "+TABLE_STORE_GEOFENCE+" WHERE "+DEVICE_ID+" ='"+device_id+"' AND "+ACTIVE_STATUS+" = '"+activeStatus+"'",null);
        return cursor;
    }

    //insert fuel new data
    public void insertDashboardAlerts(String alert_name,
                                  String alert_date_time,
                                  String alert_data, String device_id)
    {

        /*
        //fields
    //static final String ID = "id";
    //static final String ALERT_NAME = "alert_name";
    static final String ALERT_DATE_TIME = "alert_date_time";
    static final String ALERT_TYPE = "alert_type";
         */

        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(ALERT_NAME, alert_name);
            values.put(ALERT_DATE_TIME, alert_date_time);
            values.put(ALERT_TYPE, alert_data);
            values.put(DEVICE_ID, device_id);

            db.insert(TABLE_DASHBOARD_ALERTS, null, values);

        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
        }

    }

    public Cursor selectDashboardData(String device_id)
    {
        SQLiteDatabase dbb = this.getReadableDatabase();

        Cursor cursor;
        cursor = dbb.rawQuery("SELECT * from "+TABLE_DASHBOARD_ALERTS+ " WHERE "+DEVICE_ID+" = '"+device_id+"'",null);
        return cursor;
    }


    //insert fuel new data
    public void insertTrips(String trip_latlong_array,
                            String halt_latlong_array,
                            String source_datetime,
                            String destination_datetime,
                            String source_address,
                            String destination_address,
                            String avg_speed,
                            String ontime_offtime,
                            String halt,
                            String date)
    {

        /*
        //fields
    //static final String ID = "id";
    static final String TRIP_LATLONG_ARRAY = "trip_latlong_array";
    static final String HALT_LATLONG_ARRAY = "halt_latlong_array";
    static final String SOURCE_DATETIME = "source_datetime";
    static final String DESTINATION_DATETIME = "destination_datetime";
    static final String SOURE_ADDRESS = "source_address";
    static final String DESTINATION_ADDRESS = "destination_address";
    //static final String AVG_SPEED = "avg_speed";
    static final String ONTIME_OFFTIME = "ontime_offtime";
    static final String HALTS = "halt";
    //static final String TODAYS_DATE = "date";
         */

        SQLiteDatabase dbb = this.getReadableDatabase();

        dbb.execSQL("INSERT INTO "+TABLE_VEHICLE_TRIPS+"("
                +TRIP_LATLONG_ARRAY+","
                +HALT_LATLONG_ARRAY+","
                +SOURCE_DATETIME+","
                +DESTINATION_DATETIME+","
                +SOURE_ADDRESS+","
                +DESTINATION_ADDRESS+","
                +AVG_SPEED+","
                +ONTIME_OFFTIME+","
                +HALTS+","
                +TODAYS_DATE+") VALUES (" +
                "'"+trip_latlong_array+"'," +
                "'"+halt_latlong_array+"'," +
                "'"+source_datetime+"'," +
                "'"+destination_datetime+"'," +
                "'"+source_address+"'," +
                "'"+destination_address+"'," +
                "'"+avg_speed+"'," +
                "'"+ontime_offtime+"'," +
                "'"+halt+"'," +
                "'"+date+"')");

    }
    public Cursor getTripsDetails()
    {
        SQLiteDatabase dbb = this.getReadableDatabase();

        Cursor cursor;
        cursor = dbb.rawQuery("SELECT * from "+TABLE_VEHICLE_TRIPS,null);
        return cursor;
    }


    //insert fuel new data
    public void insertPerformanceDetails(String avg_speed,
                                         String total_alert_count,
                                         String halt_count,
                                         String rpm_alert_count,
                                         String apeed_alert_count,
                                         String t_alert_count,
                                         String date)
    {


        SQLiteDatabase dbb = this.getReadableDatabase();

        dbb.execSQL("INSERT INTO "+TABLE_VEHICLE_PERFORMANCE+"("
                +AVG_SPEED+","
                +TOTAL_ALERT_COUNT+","
                +HALT_COUNT+","
                +RPM_ALERT_COUNT+","
                +SPEED_ALERT_COUNT+","
                +TROUBLE_ALERT_COUNT+","
                +TODAYS_DATE+") VALUES ('"+avg_speed+"','"+total_alert_count+"','"+halt_count+"','"+rpm_alert_count+"','"+apeed_alert_count+"','"+t_alert_count+"','"+date+"')");

    }

    public Cursor getWeeklyMonthlyDetails(String fromDate,String toDate)
    {
        SQLiteDatabase dbb = this.getReadableDatabase();

        Cursor cursor;
        cursor = dbb.rawQuery("SELECT AVG(avg_speed) as avg_speed,SUM(total_alert_count) as  total_alert_count,SUM(halt_count) as  halt_count,SUM(rpm_alert_count) as  rpm_alert_count," +
                "SUM(apeed_alert_count) as  apeed_alert_count,SUM(t_alert_count) as  t_alert_count from "+TABLE_VEHICLE_PERFORMANCE+" WHERE "+TODAYS_DATE+" BETWEEN '"+fromDate+"' AND '"+toDate+"'",null);
        return cursor;
    }

    public Cursor getPerformanceDetails()
    {
        SQLiteDatabase dbb = this.getReadableDatabase();

        Cursor cursor;
        cursor = dbb.rawQuery("SELECT * from "+TABLE_VEHICLE_PERFORMANCE,null);
        return cursor;
    }

    public Cursor getOdoReading(String vehicleNAme)
    {
        /*
         //fields
    //static final String ID = "id";
    //static final String FVEHICEL_VNAME = "vname";
    //static final String FFUEL_DATE = "date";
    static final String ODO_READING = "oreading";
    static final String QUANTITY = "qty";
    //static final String FFUEL_COST = "cost";
    static final String NOTES = "notes";
    static final String LAST_FILL_DATE = "ldate";
         */
        SQLiteDatabase dbb = this.getReadableDatabase();

        Cursor cursor;
        cursor = dbb.rawQuery("SELECT "+ODO_READING+","+QUANTITY+","+FFUEL_COST+" from "+TABLE_FUEL_MASTER_NEW+" WHERE "+FVEHICEL_VNAME+"='"+vehicleNAme+"' ORDER BY id DESC LIMIT 1",null);
        return cursor;
    }


    public Cursor getVnameLastFillDateQty(String vehicleNAme)
    {
        SQLiteDatabase dbb = this.getReadableDatabase();

        Cursor cursor;
        cursor = dbb.rawQuery("SELECT * from "+TABLE_FUEL_MASTER_NEW+" WHERE "+FVEHICEL_VNAME+"='"+vehicleNAme+"'",null);
        return cursor;
    }



    //insert fuel new data
    public void insertFuelNewData(String vehicel_name,
                                  String fdate,
                                  String oresding,
                                  String qty,
                                  String fcost,
                                  String notes,
                                  String lastFillDate)
    {

        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(FVEHICEL_VNAME, vehicel_name);
            values.put(FFUEL_DATE, fdate);
            values.put(ODO_READING, oresding);
            values.put(QUANTITY, qty);
            values.put(FFUEL_COST, fcost);
            values.put(NOTES, notes);
            values.put(LAST_FILL_DATE, lastFillDate);

            db.insert(TABLE_FUEL_MASTER_NEW, null, values);

        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
        }

    }

    public Cursor getMaxMinFuelEconimyPrice(String vName)
    {

        /*
         /*
        fields
    //static final String ID = "id";
    //static final String FVEHICEL_VNAME = "vname";
    //static final String FFUEL_DATE = "date";
    static final String ODO_READING = "oreading";
    static final String QUANTITY = "qty";
    //static final String FFUEL_COST = "cost";
    static final String NOTES = "notes";
    static final String LAST_FILL_DATE = "ldate";

         */

        SQLiteDatabase dbb = this.getReadableDatabase();

        Cursor cursor;
        cursor = dbb.rawQuery("SELECT MAX(qty) as fuel_economy_best,MIN(qty) as fuel_economy_worst,MAX(cost) as fuel_price_max,MIN(cost) as fuel_price_min from "+TABLE_FUEL_MASTER_NEW+" WHERE vname='"+vName+"'",null);
        return cursor;
    }


    public Cursor getFuelEconomyFuelPriceValues(String vName)
    {

        SQLiteDatabase dbb = this.getReadableDatabase();

        Cursor cursor;
        //cursor = dbb.rawQuery("SELECT AVG(qty) as fuel_economy_avg,AVG(cost) as fuel_price_avg from "+TABLE_FUEL_MASTER_NEW+" WHERE vname='"+vName+"'",null);
        cursor = dbb.rawQuery("SELECT oreading,AVG(cost) as fuel_price_avg,SUM(qty) as total_fuel_qty from "+TABLE_FUEL_MASTER_NEW+" WHERE vname='"+vName+"'",null);
        return cursor;
    }

    public Cursor selectTotalsForFuelData(String vName)
    {

        SQLiteDatabase dbb = this.getReadableDatabase();

        Cursor cursor;
        cursor = dbb.rawQuery("SELECT SUM(oreading) as total_distance,SUM(qty) as total_fuel,SUM(cost) as total_fuel_cost,COUNT(vname) as total_fillups from "+TABLE_FUEL_MASTER_NEW+" WHERE vname='"+vName+"'",null);
        return cursor;
    }



    public Cursor checkLogin()
    {
        SQLiteDatabase dbb = this.getReadableDatabase();

        Cursor cursor;
        cursor = dbb.rawQuery("SELECT * from "+TABLE_REGISTER,null);
        return cursor;
    }


    public Cursor getDeviceIdfromVehicleMaster()
    {

        SQLiteDatabase dbb = this.getReadableDatabase();

        Cursor cursor;
        cursor = dbb.rawQuery("SELECT vdevice_id FROM "+TABLE_VEHICLE_MASTER,null);
        return cursor;
    }



    public Cursor selectAllsos()
    {

        SQLiteDatabase dbb = this.getReadableDatabase();

        Cursor cursor;
        cursor = dbb.rawQuery("SELECT mobile from "+TABLE_EMERGENCY_CONTACTS,null);
        return cursor;
    }

    public Cursor getFuelCalculations(String vehicleName)
    {

        /*
        "fltrs";
    static final String FFUEL_COST = "cost";
         */
        //select Subject, Semester, Count(*) from Subject_Selection group by Subject, Semester
        SQLiteDatabase dbb = this.getReadableDatabase();

        Cursor cursor;
        //cursor = dbb.rawQuery("SELECT fltrs,cost from "+TABLE_FUEL_MASTER+" WHERE vname='"+vehicleName+"'",null);
        cursor = dbb.rawQuery("SELECT fltrs,cost from "+TABLE_FUEL_MASTER+" WHERE vname='"+vehicleName+"'",null);
        return cursor;
    }


    public Cursor getFuelData(String vehicle_name)
    {

        SQLiteDatabase dbb = this.getReadableDatabase();

        Cursor cursor;
        cursor = dbb.rawQuery("SELECT * from "+TABLE_FUEL_MASTER+" WHERE vname='"+vehicle_name+"' ORDER BY id DESC",null);
        return cursor;
    }

    public void insertFuel(String vname,
                                  String date,
                                  String vtype,
                                  String fltrs,
                                  String cost,
                           String old_km,
                           String new_km)
    {


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(FVEHICEL_VNAME, vname);
        values.put(FFUEL_DATE, date);
        values.put(FVEHICLE_TYPE, vtype);
        values.put(FFUEL_LTRS, fltrs);
        values.put(FFUEL_COST, cost);
        values.put(OLD_KM, old_km);
        values.put(NEW_KM, new_km);
        db.insert(TABLE_FUEL_MASTER, null, values);

    }

    public void insertMaintenance(String vname,
                                  String date,
                                  String mtype,
                                  String odo,
                                  String cost,
                                  String alerts)
    {


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MVEHICEL_VNAME, vname);
        values.put(MMAINTENANCE_DATE, date);
        values.put(MMAINTENANCE_TYPE, mtype);
        values.put(MMAINTENANCE_ODO, odo);
        values.put(MMAINTENANCE_COST, cost);
        values.put(MMAINTENANCE_LALERTS, alerts);

        db.insert(TABLE_MAINTENANCE_MASTER, null, values);

    }

    public Cursor getMaintenceVehicleWise(String vehicle_name)
    {

        SQLiteDatabase dbb = this.getReadableDatabase();

        Cursor cursor;
        cursor = dbb.rawQuery("SELECT * from "+TABLE_MAINTENANCE_MASTER+" WHERE vname='"+vehicle_name+"' ORDER BY id DESC",null);
        return cursor;
    }

    public Cursor getVehicleName()
    {

        SQLiteDatabase dbb = this.getReadableDatabase();

        Cursor cursor;
        cursor = dbb.rawQuery("SELECT vname from "+TABLE_VEHICLE_MASTER,null);
        return cursor;
    }

    public void clearDbTableLogout()
    {
        SQLiteDatabase dbb = this.getReadableDatabase();
        dbb.execSQL("delete from "+TABLE_EMERGENCY_CONTACTS);
        dbb.execSQL("delete from "+TABLE_LOGIN);
        dbb.execSQL("delete from "+TABLE_REGISTER);
        dbb.execSQL("delete from "+TABLE_VEHICLE_MASTER);
    }

    public void clearSosContacts()
    {
        SQLiteDatabase dbb = this.getReadableDatabase();
        dbb.execSQL("delete from "+TABLE_EMERGENCY_CONTACTS);

    }

    public void updateDeviceID(String deviceID,String email)
    {
        SQLiteDatabase dbb = this.getReadableDatabase();
        dbb.execSQL("UPDATE "+TABLE_REGISTER+" SET "+USER_DEVICE_ID+"='"+deviceID+"' WHERE "+USER_EMAIL+"='"+email+"'");
    }

    public Cursor getDeviceIdByName(String vehicleName)
    {

        SQLiteDatabase dbb = this.getReadableDatabase();

        Cursor cursor;
        cursor = dbb.rawQuery("SELECT vdevice_id FROM "+TABLE_VEHICLE_MASTER+" WHERE vname='"+vehicleName+"'",null);
        return cursor;
    }

    public Cursor getNameByDEviceId(String dev_id)
    {

        SQLiteDatabase dbb = this.getReadableDatabase();

        Cursor cursor;
        cursor = dbb.rawQuery("SELECT vname FROM "+TABLE_VEHICLE_MASTER+" WHERE vdevice_id='"+dev_id+"'",null);
        return cursor;
    }

    public void insertVehicleMaster(String vname,String vdevice_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(VNAME, vname);
        values.put(VDEVICE_ID, vdevice_id);
        db.insert(TABLE_VEHICLE_MASTER, null, values);

    }

    /*
    //fields
    static final String VID = "vid";
    static final String VNAME = "vname";
    static final String VDEVICE_ID = "vdevice_id";
     */

    public void updateVehicleNameFromVehicleMaster(String deviceID,String vname)
    {
        SQLiteDatabase dbb = this.getReadableDatabase();
        dbb.execSQL("UPDATE "+TABLE_VEHICLE_MASTER+" SET "+VNAME+"='"+vname+"' WHERE "+VDEVICE_ID+"='"+deviceID+"'");
    }

    public Cursor checkUserAlertsAvail()
    {

        SQLiteDatabase dbb = this.getReadableDatabase();

        Cursor cursor;
        cursor = dbb.rawQuery("SELECT * from "+USER_DEFINED_ALERTS,null);
        return cursor;
    }

    public void insertUserAlerts(String alertData)
    {
        /*
        static final String ALERT_NAME = "alert_name";
    static final String UDON_OFF_STATUS = "on_off_status";
    static final String UDON_SELECTED_DATA = "selected_data";

         */
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(ALERT_NAME, alertData);
            values.put(UDON_OFF_STATUS, context.getString(R.string.alert_off));
            values.put(UDON_SELECTED_DATA, "NA");

            db.insert(USER_DEFINED_ALERTS, null, values);

        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
        }

    }
    public void updateUserDefinedAlertsOnOff(String alarmName,
                                             String status)
    {
        SQLiteDatabase dbb = this.getReadableDatabase();
        dbb.execSQL("UPDATE "+USER_DEFINED_ALERTS+" SET "+UDON_OFF_STATUS+"='"+status+"' WHERE "+ALERT_NAME+"='"+alarmName+"'");
    }
    public void updateUserDefinedAlertsSetData(String alarmName,
                                               String data)
    {
        SQLiteDatabase dbb = this.getReadableDatabase();
        dbb.execSQL("UPDATE "+USER_DEFINED_ALERTS+" SET "+UDON_SELECTED_DATA+"='"+data+"' WHERE "+ALERT_NAME+"='"+alarmName+"'");
    }

    public Cursor checkDeviceAvail()
    {

        SQLiteDatabase dbb = this.getReadableDatabase();

        Cursor cursor;
        cursor = dbb.rawQuery("SELECT * from "+TABLE_EMERGENCY_CONTACTS,null);
        return cursor;
    }
    public Cursor getDeviceId()
    {

        SQLiteDatabase dbb = this.getReadableDatabase();

        Cursor cursor;
        cursor = dbb.rawQuery("SELECT device_id,user_email,user_name,user_mobile_no from "+TABLE_REGISTER,null);
        return cursor;
    }
    public Cursor getDeviceIddd()
    {

        SQLiteDatabase dbb = this.getReadableDatabase();

        Cursor cursor;
        cursor = dbb.rawQuery("SELECT device_id FROM "+TABLE_REGISTER,null);
        return cursor;
    }

    public void registerUser(String name,
                             String mail_id,
                             String device_id,
                             String password,
                             String mob_no)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(USER_NAME, name);
        values.put(USER_EMAIL, mail_id);
        values.put(USER_DEVICE_ID,device_id);
        values.put(USER_PASSWORD,password);
        values.put(USER_MOBILE_NO,mob_no);

        db.insert(TABLE_REGISTER, null, values);

    }
    //add emergency contacts
    public void addEmergencyCotacts(String firstName,
                                    String mobile)
    {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(FIRST_NAME, firstName);
            //values.put(LAST_NAME, lastName);
            values.put(MOBILE, mobile);

            db.insert(TABLE_EMERGENCY_CONTACTS, null, values);

        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
        }

    }

    public Cursor checkSosAvailable()
    {
        SQLiteDatabase dbb = this.getReadableDatabase();

        Cursor cursor;
        cursor = dbb.rawQuery("SELECT * from "+TABLE_EMERGENCY_CONTACTS,null);
        return cursor;
    }

    public Cursor getAlreadyEmergency(String id)
    {
        SQLiteDatabase dbb = this.getReadableDatabase();

        Cursor cursor;
        cursor = dbb.rawQuery("SELECT "+MOBILE+" from "+TABLE_EMERGENCY_CONTACTS+" WHERE "+ID+" ='"+id+"'",null);
        return cursor;
    }

    public void updateContact(String firstName,
                              String mobileNumber,
                              String id)
    {
        SQLiteDatabase dbb = this.getReadableDatabase();
        dbb.execSQL("UPDATE "+TABLE_EMERGENCY_CONTACTS+" SET "+FIRST_NAME+"='"+firstName+"',"+MOBILE+"='"+mobileNumber+"' WHERE "+ID+"='"+id+"'");
    }


    public Cursor getCotacts()
    {

        SQLiteDatabase dbb = this.getReadableDatabase();

        Cursor cursor;
        cursor = dbb.rawQuery("SELECT * from "+TABLE_EMERGENCY_CONTACTS,null);
        return cursor;
    }
    public Cursor getCotactsIndividual(String mobileNumber)
    {

        SQLiteDatabase dbb = this.getReadableDatabase();

        Cursor cursor;
        cursor = dbb.rawQuery("SELECT * from "+TABLE_EMERGENCY_CONTACTS+"' WHERE "+MOBILE+"='"+mobileNumber+"'",null);
        return cursor;
    }



    //insert alerts data
    public void insertAlerts(String alertData)
    {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(ENGINE_ALARM, alertData);
            values.put(ON_OFF_STATUS, context.getString(R.string.alert_off));

            db.insert(TABLE_ENGINE_ALERTS, null, values);

        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
        }

    }

    public Cursor selectAlerts()
    {

        SQLiteDatabase dbb = this.getReadableDatabase();

        Cursor cursor;
        cursor = dbb.rawQuery("SELECT * from "+TABLE_ENGINE_ALERTS,null);
        return cursor;
    }

    public void updateAlerts(String alarmName,String status)
    {
        SQLiteDatabase dbb = this.getReadableDatabase();
        dbb.execSQL("UPDATE "+TABLE_ENGINE_ALERTS+" SET "+ON_OFF_STATUS+"='"+status+"' WHERE "+ENGINE_ALARM+"='"+alarmName+"'");
    }

    //send car log json
    public String carLogJson(String alarmStatus)
    {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * from "+TABLE_ENGINE_ALERTS+" WHERE "+ON_OFF_STATUS+"='"+alarmStatus+"'";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst())
        {
            do
            {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("parameter", cursor.getString(cursor.getColumnIndex(ENGINE_ALARM)));
                wordList.add(map);
            } while (cursor.moveToNext());
        }

        database.close();
        Gson gson = new GsonBuilder().create();
        //Use GSON to serialize Array List to JSON
        return gson.toJson(wordList);

    }
    public void updateGeofenceArriveAlert(String id, String arrive) {
        SQLiteDatabase dbb = this.getReadableDatabase();
        dbb.execSQL("UPDATE " + TABLE_STORE_GEOFENCE + " SET " + ARRIVED + "='" + arrive + "' WHERE " + ID + "='" + id + "'");

    }


    public void updateGeofenceDepartAlert(String id, String depart) {
        SQLiteDatabase dbb = this.getReadableDatabase();
        dbb.execSQL("UPDATE " + TABLE_STORE_GEOFENCE + " SET " + DEPARTURE + "='" + depart + "' WHERE " + ID + "='" + id + "'");
    }
    public void deleteGeofence(String id) {

        try {
            SQLiteDatabase dbb = this.getReadableDatabase();
            dbb.execSQL(" delete from " + TABLE_STORE_GEOFENCE + " where " + ID + "='" + id + "'");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(">>", "error>>" + e.getMessage());
        }
    }
    public void updateGeofence(String id, int radius) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(RADIOUS, radius);

            db.update(TABLE_STORE_GEOFENCE, values, ID + "='" + id + "'", null);

            // db.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public ArrayList<GeoFenceObjectClass> selectAllGeofence(String device_id) {

        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_STORE_GEOFENCE + " WHERE " + DEVICE_ID + " ='" + device_id + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        ArrayList<GeoFenceObjectClass> geo_fence_list = new ArrayList<GeoFenceObjectClass>();

        if (cursor.moveToFirst()) {
            do {

                GeoFenceObjectClass geoFenceObjectClass = new GeoFenceObjectClass();
                geoFenceObjectClass.setGeo_fence_id(cursor.getString(cursor.getColumnIndex(ID)));
                geoFenceObjectClass.setGeo_fence_radius(cursor.getString(cursor.getColumnIndex(RADIOUS)));
                geoFenceObjectClass.setGetGeo_fence_lat_long(cursor.getString(cursor.getColumnIndex(ADDRESS_LATLONG)));
                geoFenceObjectClass.setGeo_fence_name(cursor.getString(cursor.getColumnIndex(GEOFENCE_NAME)));
                geoFenceObjectClass.setGetGeo_fence_arrive_alert(cursor.getString(cursor.getColumnIndex(ARRIVED)));
                geoFenceObjectClass.setGetGeo_fence_depart_alert(cursor.getString(cursor.getColumnIndex(DEPARTURE)));

                geo_fence_list.add(geoFenceObjectClass);

            } while (cursor.moveToNext());
        }

        return geo_fence_list;
    }



}
