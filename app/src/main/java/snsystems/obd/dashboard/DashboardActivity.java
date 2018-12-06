package snsystems.obd.dashboard;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;


import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;
import snsystems.obd.activity.CustomViewsActivity;
import snsystems.obd.activity.FeedbackActivity;
import snsystems.obd.advertise.DisplayAdvertiseActivity;
import snsystems.obd.advertise.DisplayAdvertiseAdapter;
import snsystems.obd.classes.MyApplication;
import snsystems.obd.classes.T;
import snsystems.obd.classes.VolleyResponseClass;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.devicemgt.ActivityAddDevice;
import snsystems.obd.drawer.NavigationDrawerFragment;
import snsystems.obd.drawer.NavigationDrawerFragmentUpdate;
import snsystems.obd.geofencesnehal.GeofenceActivitySnehal;
import snsystems.obd.geofencing.CheckEntryExitService;
import snsystems.obd.geofencing.GeoFencingActivity;
import snsystems.obd.health.HealthNewActivity;
import snsystems.obd.health.VehicleHealthActivity;
import snsystems.obd.interfaces.Constants;
import snsystems.obd.interfaces.VolleyCallback;
import snsystems.obd.interfaces.VolleyErrorCallback;
import snsystems.obd.locatecar.LocateCarActivity;
import snsystems.obd.locatecar.LocateCarNewActivity;
import snsystems.obd.notificationalerts.DisplayNotificationDetailsActivity;
import snsystems.obd.notificationalerts.MakeAlertActivity;
import snsystems.obd.notificationalerts.NotificationInformation;
import snsystems.obd.services.FloatingFaceBubbleService;
import snsystems.obd.sos.MessageActivity;
import snsystems.obd.sos.SendSosActivity;
import snsystems.obd.sos.SubmitSosContactActivity;
import snsystems.obd.trips.CreateTripActivity;
import snsystems.obd.trips.TripMapsActivity;
import snsystems.obd.trips.TripsTabActivity;
import snsystems.obd.tripsnew.TripsSmartTabsTripsActivity;


public class DashboardActivity extends AnimationActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback,
        LocationListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener
{

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    static int rpmCount =  0, fuelCount = 0, sppedCount = 0, subscriptionCount = 0, serviceDueCount = 0;

    @Bind(R.id.fb_locate_my_car)
    FloatingActionButton fb_locate;

    @Bind(R.id.fb_sos)
    FloatingActionButton fb_sos;

    @Bind(R.id.fb_trips)
    FloatingActionButton fb_trips;

    @Bind(R.id.fb_health)
    FloatingActionButton fb_health;

    @Bind(R.id.fb_geo_fencing)
    FloatingActionButton fb_geo_fencing;


//    @Bind(R.id.viewSendSOS)
//    ImageView viewSendSOS;


//    @Bind(R.id.fb_geo_fencing)
//    FloatingActionButton fb_geo;


    private SupportMapFragment mapFragment;
    private GoogleMap map;

    private final int UPDATE_INTERVAL =  1 * 60 * 1000; // 1 minutes
    private final int FASTEST_INTERVAL = 30 * 1000;  // 30 secs

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.advertiseRelativeLayout)
    RelativeLayout advertiseRelativeLayout;

    Marker carMarker;

    private GoogleApiClient googleApiClient;



    private String [] alertsName = {

            "Fuel Level",
            "Speed",
            "RPM",
            "Maintenance",
            "Service Due",
            "Subscription"
    };

    private LocationRequest locationRequest;
    //advertise
    @Bind(R.id.expandableListviewForDisplayAdvertise)
    ExpandableListView expandableListviewForDisplayAdvertise;

//    @Bind(R.id.toolbar)
//    Toolbar toolbar;

    private DisplayAdvertiseAdapter
            productCategoryAdapter;

    ArrayList<String> PARRENT = new ArrayList<>();
    HashMap<String, List<String>> CHILD = new HashMap<String, List<String>>();


    @Bind(R.id.speedTextView)
    TextView speedTextView;

    @Bind(R.id.multiple_actions)
    FloatingActionsMenu multiple_actions;


    @Bind(R.id.fuelLevelTextView)
    TextView fuelLevelTextView;

    @Bind(R.id.rpmLevelTextView)
    TextView rpmLevelTextView;

    @Bind(R.id.fuelRpmRelativeLayout)
    RelativeLayout fuelRpmRelativeLayout;


    ArrayList<String> vehicleNAmes;

    private String device_id_get;

    private Location lastLocation;

    private SharedPreferences smstextPreferences;

//2E:4F:0C:7E:15:CA:59:4A:31:1F:DE:BE:E0:02:66:50:A3:74:7D:79;

    //alerts

    public int count = 0;
    String fuel_alertname,
            fuel_status,
            fuel_data,
            speed_alertname,
            speed_status,
            speed_data,
            rpm_alertname,
            rpm_status,
            rpm_data,
            maintenance_alertname,
            maintenance_status,
            maintenance_data,
//            service_due_alertname,
//            service_due_status,
//            service_due_data,
            subscription_alertname,
            subscription_status,
            subscription_data;
   // private ArrayList<String> NOTIFICATION_DATA = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_dashboard);
        setContentView(R.layout.activity_dashboard_new);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);



        startService(new Intent(getApplicationContext(),CheckEntryExitService.class));

        expandableListviewForDisplayAdvertise.setGroupIndicator(null);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();


        //double sdf =  distanceBetween(18.501038, 73.858016, 18.499730, 73.864421);
//4945.393724119637
        //double sdf =  distanceBetween(new LatLng(18.501832, 73.863591), new LatLng(18.457532, 73.867746));

        //18.501832,73.863591
        //Log.e("TOATAL_DISTANCE",""+(sdf/1000));

        getVehicleNames();
        initGMaps();
        setupNavigationDrawer();

        setListner();
        //startServices();
        createGoogleApi();

        //getDashBoardDetails();

        boolean c = T.checkConnection(DashboardActivity.this);
        if(c)
        {
            getAdvertise();
        }
        else
        {
            T.t(DashboardActivity.this,"Network connection off");
        }


//        new Timer().scheduleAtFixedRate(new TimerTask()
//        {
//            @Override
//            public void run()
//            {
//                boolean dd = S.checkDeviceIdAvail(new DBHelper(DashboardActivity.this));
//
//                if(dd)
//                {
//                    getDashBoardDetails();
//                }
//
//
//            }
//        }, 0, 10000);//put here time 1000 milliseconds=1 second

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {

                boolean dd = S.checkDeviceIdAvail(MyApplication.db);

                if(dd)
                {
                    getDashBoardDetails();
                }
                //Do something after 10 seconds
                handler.postDelayed(this, 10000);
            }
        }, 10000);




    }






    private double getDistance(double lat1, double lat2, double lng1, double lng2)
    {


        //Calculating distance
        double earthRadius = 3958.75;

        double dLat = Math.toRadians(lat1-lat2);
        double dLng = Math.toRadians(lng1-lng2);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(lat1)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        return dist;
    }

    private void getDashBoardDetails()
    {

        boolean c = T.checkConnection(DashboardActivity.this);
        if(c)
        {
            String device_id_get = S.getDeviceId(MyApplication.db);
            getDashBoardDetails(device_id_get);
            getNotification();

        }
        else
        {

            toastInUi("Network connection off");

        }

    }

    private void toastInUi(final String message)
    {
        DashboardActivity.this.runOnUiThread(new Runnable()
        {
            public void run()
            {
                T.t(DashboardActivity.this,""+message);
            }
        });
    }

    private void getNotification() {

        ArrayList<String> alerts_information = S.checkUserAlertsAvailable(MyApplication.db);

        if(!alerts_information.isEmpty())
        {
            /*
            03-01 16:39:49.901 7257-7257/snsystems.obd E/ALERTS_DATA: Fuel Level#false#NA
03-01 16:39:49.901 7257-7257/snsystems.obd E/ALERTS_DATA: Speed#false#NA
03-01 16:39:49.901 7257-7257/snsystems.obd E/ALERTS_DATA: RPM#false#NA
03-01 16:39:49.901 7257-7257/snsystems.obd E/ALERTS_DATA: Maintenance#false#NA
03-01 16:39:49.901 7257-7257/snsystems.obd E/ALERTS_DATA: Service Due#false#NA
03-01 16:39:49.901 7257-7257/snsystems.obd E/ALERTS_DATA: Subscription#false#NA
             {"success":"1","dashboard_alert_data":[{"fuel_level":"120","rpm":"63","vehicle_speed":"14","service_date":"2017-02-25 00:01:00","service_due_date":"2018-02-25 00:01:00","activation_end_date":"2017-11-24 17:45:11","service_days":"-4","service_due_days":"361","activation_end_days":"268"}]}*/

            String fuelLevel = alerts_information.get(0);
            String speed = alerts_information.get(1);
            String rpm = alerts_information.get(2);
            String maintenance = alerts_information.get(3);
            //String service_due = alerts_information.get(4);
            String subscription = alerts_information.get(5);

            String [] fuelData = fuelLevel.split("#");
            String [] speedData = speed.split("#");
            String [] rpmData = rpm.split("#");
            String [] maintenanceData = maintenance.split("#");
          //  String [] service_dueData = service_due.split("#");
            String [] subscriptionData = subscription.split("#");


            fuel_alertname = fuelData[0];
            fuel_status = fuelData[1];
            fuel_data = fuelData[2];

            speed_alertname = speedData[0];
            speed_status = speedData[1];
            speed_data = speedData[2];

            rpm_alertname = rpmData[0];
            rpm_status = rpmData[1];
            rpm_data = rpmData[2];

            maintenance_alertname = maintenanceData[0];
            maintenance_status = maintenanceData[1];
            maintenance_data = maintenanceData[2];

//            service_due_alertname = service_dueData[0];
//            service_due_status = service_dueData[1];
//            service_due_data = service_dueData[2];

            subscription_alertname = subscriptionData[0];
            subscription_status = subscriptionData[1];
            subscription_data = subscriptionData[2];

//            Log.e("ALERTS_DATA",""+fuel_alertname);
//            Log.e("ALERTS_DATA",""+fuel_status);
//            Log.e("ALERTS_DATA",""+fuel_data);

            getAlertsInformation();
        }
    }
    private void getAlertsInformation() {

        String device_id_mail = S.getDeviceIdUserName(MyApplication.db);

        String [] data = device_id_mail.split("#");

        String [] parameters =
                {
                        "device_id"+"#"+data[0],
                        "email_id"+"#"+data[1]

                };
        VolleyResponseClass.getResponseAlertsVollyError(
                new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {

                        //Log.e("DATASSS", "" + result);
                        parseResponseAlerts(result);
                        //toastInUi("Network connection off");

                    }
                },
                new VolleyErrorCallback() {
                    @Override
                    public void onError(VolleyError result) {

                        toastInUi(""+result);

                    }
                },
                DashboardActivity.this,
                getResources().getString(R.string.webUrl) + "" + getResources().getString(R.string.alertApp),
                new EditText(DashboardActivity.this),
                parameters);
    }
    private void parseResponseAlerts(String result)
    {

        String  fuel_level = Constants.NA;
        String  rpm = Constants.NA;
        String  vehicle_speed = Constants.NA;
        String  service_days = Constants.NA;
        //String  service_due_days = Constants.NA;
        String  activation_end_days = Constants.NA;
        String  today_date = Constants.NA;

        try
        {
            if(result != null || result.length() > 0)
            {
                Object json = new JSONTokener(result).nextValue();
                if (json instanceof JSONObject)
                {
                    JSONObject tripsJsonObject = new JSONObject(result);
                    String status = tripsJsonObject.getString("success");

                    if(status.equals("1"))
                    {

//                        NOTIFICATION_DATA.clear();
//                        count = 0;
//                        invalidateOptionsMenu();


                        JSONArray alertJsonArray = tripsJsonObject.getJSONArray("dashboard_alert_data");

                        JSONObject aalertsJsonObject = alertJsonArray.getJSONObject(0);

                        if(aalertsJsonObject.has("fuel_level") && !aalertsJsonObject.isNull("fuel_level"));
                        {
                            fuel_level = aalertsJsonObject.getString("fuel_level");
                        }
                        if(aalertsJsonObject.has("rpm") && !aalertsJsonObject.isNull("rpm"));
                        {
                            rpm = aalertsJsonObject.getString("rpm");
                        }
                        if(aalertsJsonObject.has("vehicle_speed") && !aalertsJsonObject.isNull("vehicle_speed"));
                        {
                            vehicle_speed = aalertsJsonObject.getString("vehicle_speed");
                        }
                        if(aalertsJsonObject.has("service_days") && !aalertsJsonObject.isNull("service_days"));
                        {
                            service_days = aalertsJsonObject.getString("service_days");
                        }
//                        if(aalertsJsonObject.has("service_due_days") && !aalertsJsonObject.isNull("service_due_days"));
//                        {
//                            service_due_days = aalertsJsonObject.getString("service_due_days");
//                        }
                        if(aalertsJsonObject.has("activation_end_days") && !aalertsJsonObject.isNull("activation_end_days"));
                        {
                            activation_end_days = aalertsJsonObject.getString("activation_end_days");
                        }
                        if(aalertsJsonObject.has("today_date") && !aalertsJsonObject.isNull("today_date"));
                        {
                            today_date = aalertsJsonObject.getString("today_date");
                        }

                        //static int rpmCount =  0, fuelCount = 0, sppedCount = 0, subscriptionCount = 0, serviceDueCount = 0;

                        if(Boolean.valueOf(fuel_status))
                        {
//                            if(!(Integer.valueOf(fuel_level) == 0))
//                            {

                                if(fuelCount == 0)
                                {
                                    if(Integer.valueOf(fuel_level) < Integer.valueOf(fuel_data))
                                    //if(Integer.valueOf(fuel_level) == 0)
                                    {
                                        //check sqlite table alert date time with server date time
                                        //if same then this is not new alert (no increment)
                                        //else new alert (increment count)
                                        if(!S.checkNewAlerts(MyApplication.db,"Fuel Level",today_date))
                                        {
                                            sendAlertToServer("Fuel Level","fuel",fuel_level,today_date);
                                            //NOTIFICATION_DATA.add(fuel_alertname+"#"+fuel_level+" %#"+fuel_data);
                                            count++;
                                            invalidateOptionsMenu();
                                            //generateNotification(MakeAlertActivity.this, "Fuel Level", "Your current fuel level "+fuel_level);
                                        }

                                    }
//                                    fuelCount++;
//                                }

                            }

                        }
                        if(Boolean.valueOf(speed_status))
                        {
                            if(!(Integer.valueOf(vehicle_speed) == 0))
                            {
//                                if(sppedCount == 0)
//                                {
                                    if(Integer.valueOf(vehicle_speed) > Integer.valueOf(speed_data))
                                    {
                                        if(!S.checkNewAlerts(MyApplication.db,"Speed",today_date))
                                        {
                                            sendAlertToServer("Speed","speed",vehicle_speed,today_date);
                                            //NOTIFICATION_DATA.add(speed_alertname+"#"+vehicle_speed+" Kmph#"+speed_data);
                                            count++;
                                            invalidateOptionsMenu();
                                        }

                                        //generateNotification(MakeAlertActivity.this, "Fuel Level", "Your current fuel level "+vehicle_speed);
                                    }
//                                    sppedCount++;
//                                }

                            }

                        }
                        if(Boolean.valueOf(rpm_status))
                        {
                            if(!(Integer.valueOf(rpm) == 0))
                            {
//                                if(rpmCount == 0)
//                                {
                                    if(Integer.valueOf(rpm) > Integer.valueOf(rpm_data))
                                    {
                                        if(!S.checkNewAlerts(MyApplication.db,"RPM",today_date))
                                        {
                                            sendAlertToServer("RPM","rpm",rpm,today_date);
                                            //NOTIFICATION_DATA.add(rpm_alertname+"#"+rpm+" Rpm#"+rpm_data);
                                            count++;
                                            invalidateOptionsMenu();
                                        }
                                        // generateNotification(MakeAlertActivity.this, "Fuel Level", "Your current fuel level "+rpm);
                                    }
//                                    rpmCount++;
//                                }
                            }

                        }
                        //activation subscription
                        if(Boolean.valueOf(subscription_status))
                        {
                            //if(!(Integer.valueOf(activation_end_days) == 0))
                                if(!(Integer.valueOf(activation_end_days) == 0))
                            {
//                                if(subscriptionCount == 0)
//                                {
                                    // if(Integer.valueOf(activation_end_days) < 267)
                                    if(Integer.valueOf(activation_end_days) < Integer.valueOf(subscription_data))
                                    {
                                        if(Integer.valueOf(activation_end_days) < 0)
                                        {
                                            if(!S.checkNewAlerts(MyApplication.db,"Subscrption",today_date))
                                            {
                                                sendAlertToServer("Subscrption","subscrption",activation_end_days,today_date);
                                                //NOTIFICATION_DATA.add("Days left"+"#0 Days#"+subscription_data);
                                                count++;
                                                invalidateOptionsMenu();
                                            }
                                        }
                                        else
                                        {
                                            if(!S.checkNewAlerts(MyApplication.db,"Subscrption",today_date))
                                            {
                                                sendAlertToServer("Subscrption","subscrption",activation_end_days,today_date);
                                                //NOTIFICATION_DATA.add("Days left"+"#"+activation_end_days+" Days#"+subscription_data);
                                                count++;
                                                invalidateOptionsMenu();
                                            }
                                            //generateNotification(MakeAlertActivity.this, "Fuel Level", "Your current fuel level "+fuel_level);
                                        }

                                    }
//                                    subscriptionCount++;
//                                }

                            }

                        }

                        //service due
                        if(Boolean.valueOf(maintenance_status))
                        {
                            if(!(Integer.valueOf(service_days) == 0))
                            {
                                //serviceDueCount
//                                if(serviceDueCount == 0)
//                                {
                                    if(Integer.valueOf(service_days) < Integer.valueOf(maintenance_data))
                                    //if(Integer.valueOf(service_days) < 10)
                                    {
                                        if(Integer.valueOf(service_days) < 0)
                                        {
                                            if(!S.checkNewAlerts(MyApplication.db,"Service",today_date))
                                            {
                                                //NOTIFICATION_DATA.add("Days left"+"#0 Days#"+service_due_data);
                                                sendAlertToServer("Service","service",service_days+" Days",today_date);
                                                count++;
                                                invalidateOptionsMenu();
                                            }

                                            // generateNotification(MakeAlertActivity.this, "Fuel Level", "Your current fuel level "+fuel_level);
                                        }
                                        else
                                        {
                                            if(!S.checkNewAlerts(MyApplication.db,"Service",today_date))
                                            {
                                                //NOTIFICATION_DATA.add("Days left"+"#"+service_days+" Days#"+service_due_data);
                                                sendAlertToServer("Service","service",service_days+" Days",today_date);
                                                count++;
                                                invalidateOptionsMenu();
                                            }

                                            // generateNotification(MakeAlertActivity.this, "Fuel Level", "Your current fuel level "+fuel_level);
                                        }



                                    }
//                                    serviceDueCount++;
//                                }

                            }

                        }

                    }

                }
                else
                {
                    T.t(DashboardActivity.this, "incorect json");
                }
            }
            else
            {
                T.t(DashboardActivity.this,"0 or null json");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
    }

    private void sendAlertToServer(String alertName,String alertType,String alertData,String today_date)
    {

        String device_id_mail = S.getDeviceIdUserName(MyApplication.db);

        String [] data = device_id_mail.split("#");

        //db.execSQL("ALTER TABLE "+TABLE_DASHBOARD_ALERTS+" ADD COLUMN "+DEVICE_ID+" TEXT");

        MyApplication.db.insertDashboardAlerts(alertName,today_date,alertData,data[0]);



        String [] parameters =
                {
                        "device_id"+"#"+data[0],
                        "alert_name"+"#"+alertName,
                        //"alert_date"+"#"+data_time,
                        "alert_type"+"#"+alertType



                };
        VolleyResponseClass.getResponseWithoutProgress(
                new VolleyCallback() {
                    @Override
                    public void onSuccess(String result)
                    {

                       // parseFeedback(result);
                       // T.t(DashboardActivity.this,""+result);

                    }
                },
                DashboardActivity.this,
                getResources().getString(R.string.webUrl)+""+getResources().getString(R.string.addAlerts),
                speedTextView,
                parameters);


    }

    private void createGoogleApi()
    {
        //T.t(DashboardActivity.this, "createGoogleApi()");
        if ( googleApiClient == null ) {
            googleApiClient = new GoogleApiClient.Builder( this )
                    .addConnectionCallbacks( this )
                    .addOnConnectionFailedListener( this )
                    .addApi( LocationServices.API )
                    .build();
        }
    }
    private void startServices()
    {

        Intent intent = new Intent(DashboardActivity.this, FloatingFaceBubbleService.class);
        startService(intent);
        //Intent i = new Intent(MainActivity.this, AlertService.class);
        // startService(i);
    }

    private void getVehicleNames()
    {
        vehicleNAmes = S.getVehicleName(MyApplication.db);

        if(vehicleNAmes.isEmpty())
        {
            vehicleNAmes.add(getString(R.string.no_vehicle_found));
        }
    }

    private void getDashBoardDetails(String device_id) {

        /*
        http://192.168.1.47:81/obd/api/MobileAPI/getDashboard

para:  $device_id = $this->post('device_id');
         */

        //String devIDD = S.getDeviceId(MyApplication.db;


        String [] parameters =
                {
                        "device_id"+"#"+device_id
                        //"device_id"+"#"+"40000003"

                };
        VolleyResponseClass.getResponseProgressDialogErrorWithoutProgress(
                new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        //Log.e("VolleyResponse", "" + result);

                        //T.t(DisplayAdvertiseActivity.this,""+result);
                        // {"success":"1","dashboard_data":[{"fuel_level":"4","rpm":"200","vehicle_speed":"85","latitude":"18.5018","longitude":"73.8636","vehicle_name":"honda city","vehicle_make":"honda","vehicle_model":"honda city","vehicle_make_year":"2016","vehicle_gear_type":"asa","vehicle_fuel_type":"dsf"}]}

                        parseDashboardResponse(result);


                    }
                },
                new VolleyErrorCallback() {
                    @Override
                    public void onError(VolleyError result) {


                        toastInUi(""+result);
                        //T.t(DashboardActivity.this,""+result);

                    }
                },
                DashboardActivity.this,
                getResources().getString(R.string.webUrl) + "" + getResources().getString(R.string.displayDashboard),
                expandableListviewForDisplayAdvertise,
                parameters);


    }
    private void parseDashboardResponse(String result) {

        String fuel_level = Constants.NA;
        String rpm = Constants.NA;
        String vehicle_speed = Constants.NA;
        String latitude = Constants.NA;
        String longitude = Constants.NA;
        String vehicle_number = Constants.NA;//add again
        String vehicle_name = Constants.NA;
        String vehicle_make = Constants.NA;
        String vehicle_model = Constants.NA;
        String vehicle_make_year = Constants.NA;
        String vehicle_gear_type = Constants.NA;
        String vehicle_fuel_type = Constants.NA;

        try
        {
            if(result != null || result.length() > 0)
            {
                Object json = new JSONTokener(result).nextValue();
                if (json instanceof JSONObject)
                {
                    JSONObject tripsJsonObject = new JSONObject(result);
                    String status = tripsJsonObject.getString("success");

                    if(status.equals("1"))
                    {
                        fuelRpmRelativeLayout.setVisibility(View.VISIBLE);

                        JSONArray dashboardJsonArray = tripsJsonObject.getJSONArray("dashboard_data");

                        JSONObject ddasJsonObject = dashboardJsonArray.getJSONObject(0);

                        if(ddasJsonObject.has("fuel_level") && !ddasJsonObject.isNull("fuel_level"))
                        {
                            fuel_level = ddasJsonObject.getString("fuel_level");
                        }
                        if(ddasJsonObject.has("rpm") && !ddasJsonObject.isNull("rpm"))
                        {
                            rpm = ddasJsonObject.getString("rpm");
                        }
                        if(ddasJsonObject.has("vehicle_speed") && !ddasJsonObject.isNull("vehicle_speed"))
                        {
                            vehicle_speed = ddasJsonObject.getString("vehicle_speed");
                        }
                        if(ddasJsonObject.has("latitude") && !ddasJsonObject.isNull("latitude"))
                        {
                            latitude = ddasJsonObject.getString("latitude");
                        }
                        if(ddasJsonObject.has("longitude") && !ddasJsonObject.isNull("longitude"))
                        {
                            longitude = ddasJsonObject.getString("longitude");
                        }
                        if(ddasJsonObject.has("vehicle_name") && !ddasJsonObject.isNull("vehicle_name"))
                        {
                            vehicle_name = ddasJsonObject.getString("vehicle_name");
                        }
                        if(ddasJsonObject.has("vehicle_no") && !ddasJsonObject.isNull("vehicle_no"))
                        {
                            vehicle_number = ddasJsonObject.getString("vehicle_no");
                        }
                        if(ddasJsonObject.has("vehicle_make") && !ddasJsonObject.isNull("vehicle_make"))
                        {
                            vehicle_make = ddasJsonObject.getString("vehicle_make");
                        }
                        if(ddasJsonObject.has("vehicle_model") && !ddasJsonObject.isNull("vehicle_model"))
                        {
                            vehicle_model = ddasJsonObject.getString("vehicle_model");
                        }
                        if(ddasJsonObject.has("vehicle_make_year") && !ddasJsonObject.isNull("vehicle_make_year"))
                        {
                            vehicle_make_year = ddasJsonObject.getString("vehicle_make_year");
                        }
                        if(ddasJsonObject.has("vehicle_gear_type") && !ddasJsonObject.isNull("vehicle_gear_type"))
                        {
                            vehicle_gear_type = ddasJsonObject.getString("vehicle_gear_type");
                        }
                        if(ddasJsonObject.has("vehicle_fuel_type") && !ddasJsonObject.isNull("vehicle_fuel_type"))
                        {
                            vehicle_fuel_type = ddasJsonObject.getString("vehicle_fuel_type");
                        }

                        fuelLevelTextView.setText(fuel_level+" %");
                        rpmLevelTextView.setText(rpm);
                        speedTextView.setText(vehicle_speed+" Km/Hr");






                        editor.putString("device_lat", latitude).commit();
                        editor.putString("device_lang", longitude).commit();

                        setBirdEyeView(
                                Double.valueOf(latitude),
                                Double.valueOf(longitude),
                                vehicle_number,
                                vehicle_name,
                                vehicle_make,
                                vehicle_model,
                                vehicle_make_year,
                                vehicle_gear_type,
                                vehicle_fuel_type);

                        setSmsText(vehicle_number,latitude,longitude);


                    }
                    else if(status.equals("0"))
                    {
                        fuelRpmRelativeLayout.setVisibility(View.GONE);

                        carMarker.remove();

                        LatLng india = new LatLng(28.704059, 77.102490);
                        // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(india,4));

                        T.t(DashboardActivity.this,"Vehicle details not available at server side, when data available of this current device then we will get back to you.");

//                        new SweetAlertDialog(DashboardActivity.this, SweetAlertDialog.ERROR_TYPE)
//                                .setTitleText("Oops...")
//                                .setConfirmText("OK")
//                                .setContentText("Vehicle details not available at server side, when data available of this current device then we will get back to you.")
//                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                    @Override
//                                    public void onClick(SweetAlertDialog sDialog)
//                                    {
//                                        sDialog.dismissWithAnimation();
//
//                                    }
//                                })
//                                .show();

                    }
                    else if(status.equals("2"))//for vehicle details not available in vehicle master
                    {
                        fuelRpmRelativeLayout.setVisibility(View.GONE);

                        new SweetAlertDialog(DashboardActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setConfirmText("OK")
                                .setCancelText("Cancel")
                                .setContentText("Vehicle details not available, Click Ok to fillup add device form.")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog)
                                    {
                                        sDialog.dismissWithAnimation();

                                        Intent i = new Intent(DashboardActivity.this, ActivityAddDevice.class);
                                        i.putExtra("status","from_dashboard_vehicle_datanotfoundfirsttime");
                                        startActivityForResult(i,1);


                                    }
                                })
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                    }
                                })
                                .show();
                        // T.displayErrorMessage(,"","OK","");


                    }
                    else if(status.equals("3"))//for vehicle details not available in vehicle master
                    {

                        String [] activation_start_date = tripsJsonObject.getString("activation_start_date").split(" ");
                        String [] activation_end_date = tripsJsonObject.getString("activation_end_date").split(" ");

                        new SweetAlertDialog(DashboardActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setConfirmText("OK")
                                .setContentText("This Device is not activated from admin side, Please contact to admin for activate device. \nActivation Start Date : "+activation_start_date[0]+" Activation End Date : "+activation_end_date[0])
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();

                                    }
                                })
                                .show();

                    }
                }
                else
                {
                    T.t(DashboardActivity.this, "incorect json");
                }
            }
            else
            {
                T.t(DashboardActivity.this,"0 or null json");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
    }

    private void setSmsText(String vehicle_number, String latitude, String longitude)
    {

        String address = getAddress(Double.valueOf(latitude),Double.valueOf(longitude));

        String userData = S.getDeviceIdUserName(MyApplication.db);
        String [] data = userData.split("#");
        //sms text preferences
        smstextPreferences = getSharedPreferences(Constants.SMS_TEXT, 0);
        SharedPreferences.Editor smsEditor = smstextPreferences.edit();

        smsEditor.putString("vehicle_number", vehicle_number);
        smsEditor.putString("latitude", latitude);
        smsEditor.putString("longitude", longitude);
        smsEditor.putString("address", address);
        smsEditor.putString("name", data[2]);
        smsEditor.putString("mobile", data[3]);


        smsEditor.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try
        {


            if(requestCode == 5)
            {
                count = 0;
                invalidateOptionsMenu();
                //NOTIFICATION_DATA.clear();
            }
            //for device info not avail first time
            if(requestCode==1)
            {
                String device_id_get = S.getDeviceId(MyApplication.db);
                getDashBoardDetails(device_id_get);
            }
            else
            {
                invalidateOptionsMenu();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setBirdEyeView(
            final Double latitude,
            final Double longitude,
            final String vehicle_number,
            final String vehicle_name,
            final String vehicle_make,
            final String vehicle_model,
            String vehicle_make_year,
            String vehicle_gear_type,
            final String vehicle_fuel_type)
    {

        if (carMarker != null) {
            carMarker.remove();
        }

        carMarker = map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_launcher))
                .position(new LatLng(latitude, longitude))
                .title("PUNE"));


        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 14));

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            public View getInfoWindow(Marker arg0) {
                View v = getLayoutInflater().inflate(R.layout.activity_show_info, null);

                TextView vehicleAddressTextView = (TextView) v.findViewById(R.id.vehicleAddressTextView);
                TextView vehicleNumberTextView = (TextView) v.findViewById(R.id.vehicleNumberTextView);
                TextView vehicleNameAddressTextView = (TextView) v.findViewById(R.id.vehicleNameAddressTextView);
                TextView vehicleMakeTextView = (TextView) v.findViewById(R.id.vehicleMakeTextView);
                TextView vehicleModelTextView = (TextView) v.findViewById(R.id.vehicleModelTextView);
                TextView vehicleFuelTypeTextView = (TextView) v.findViewById(R.id.vehicleFuelTypeTextViewdfdg);

                String addressV = getAddress(latitude,longitude);

                vehicleAddressTextView.setText(Html.fromHtml("<b>Address: </b>"+addressV));
                vehicleNumberTextView.setText(Html.fromHtml("<b>Vehicle No: </b>"+vehicle_number));
                vehicleNameAddressTextView.setText(Html.fromHtml("<b>Name: </b>"+vehicle_name));
                vehicleMakeTextView.setText(Html.fromHtml("<b>Make: </b>"+vehicle_make));
                vehicleModelTextView.setText(Html.fromHtml("<b>Model: </b>"+vehicle_model));
                vehicleFuelTypeTextView.setText(Html.fromHtml("<b>Fuel Type: </b>"+vehicle_fuel_type));
                return v;
            }

            public View getInfoContents(Marker arg0) {
                return null;
            }
        });

        //       getSupportActionBar().setTitle(Html.fromHtml("<small> <font color='#ffffff'>GENSET</font></small"));
    }

    private String getAddress(Double lat, Double landi) {


        String locationAddress = null;
        try
        {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            addresses = geocoder.getFromLocation(lat, landi, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            locationAddress = address + ", " + city + ", " + state + ", " + country + ","+postalCode+".";
        } catch (Exception e) {

        }

        return locationAddress;
    }

    private void getAdvertise() {


        String device_id_mail = S.getDeviceIdUserName(MyApplication.db);

        String [] data = device_id_mail.split("#");


        String [] parameters =
                {
                        "username"+"#"+data[1]

                };
        VolleyResponseClass.getResponseProgressDialogErrorWithoutProgress(
                new VolleyCallback()
                {
                    @Override
                    public void onSuccess(String result) {
                        //Log.e("VolleyResponse", "" + result);

                        //T.t(DisplayAdvertiseActivity.this,""+result);
                        //{"status":1,"advertise":[{"advertise_title":"ddd","advertise_sub_title":"sss","advertise_description":"eeee","start_date":"2017-02-01","end_date":"2017-03-18","file_link":0}]}

                        parseResponse(result);


                    }
                },
                new VolleyErrorCallback()
                {
                    @Override
                    public void onError(VolleyError result)
                    {
                        T.t(DashboardActivity.this,""+result);
                    }
                },
                DashboardActivity.this,
                getResources().getString(R.string.webUrl) + "" + getResources().getString(R.string.displayAdvertise),
                expandableListviewForDisplayAdvertise,
                parameters);

    }
    private void parseResponse(String result) {


        ArrayList<String> MENU_PARRENT = new ArrayList<>();


        String advertise_title = Constants.NA;
        String advertise_sub_title = Constants.NA;
        String advertise_description = Constants.NA;
        String file_link = Constants.NA;

        try
        {
            if(result != null || result.length() > 0)
            {
                Object json = new JSONTokener(result).nextValue();
                if (json instanceof JSONObject)
                {
                    JSONObject adJsonObject = new JSONObject(result);
                    String status = adJsonObject.getString("status");

                    if(status.equals("1"))
                    {
                        advertiseRelativeLayout.setVisibility(View.VISIBLE);
                        JSONArray adJsonArray = adJsonObject.getJSONArray("advertise");

                        for(int i = 0; i < adJsonArray.length(); i++)
                        {
                            List<String> su_items = new ArrayList<>();

                            JSONObject addJsonObject = adJsonArray.getJSONObject(i);

                            if(addJsonObject.has("advertise_title") && !addJsonObject.isNull("advertise_title"))
                            {
                                advertise_title = addJsonObject.getString("advertise_title");
                            }

                            if(addJsonObject.has("advertise_sub_title") && !addJsonObject.isNull("advertise_sub_title"))
                            {
                                advertise_sub_title = addJsonObject.getString("advertise_sub_title");
                            }
                            if(addJsonObject.has("advertise_description") && !addJsonObject.isNull("advertise_description"))
                            {
                                advertise_description = addJsonObject.getString("advertise_description");
                            }
                            if(addJsonObject.has("file_link") && !addJsonObject.isNull("file_link"))
                            {
                                file_link = addJsonObject.getString("file_link");
                            }

                            MENU_PARRENT.add(advertise_title+"#"+advertise_sub_title);
                            su_items.add(advertise_description+"#"+file_link);

                            PARRENT.add(MENU_PARRENT.get(i));
                            CHILD.put(PARRENT.get(i), su_items);

                        }

                        productCategoryAdapter = new DisplayAdvertiseAdapter(DashboardActivity.this, PARRENT, CHILD);
                        expandableListviewForDisplayAdvertise.setAdapter(productCategoryAdapter);


                    }
                    else
                    {
                        //T.t(DashboardActivity.this,"No advertise found");

                    }
                }
                else
                {
                    T.t(DashboardActivity.this, "incorect json");
                }
            }
            else
            {
                T.t(DashboardActivity.this,"0 or null json");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
    }
    private void insertUserDefinedAlerts()
    {

        boolean alerts = S.checkUserAlertsAvailablere(MyApplication.db);

        if(!alerts)
        {

            for(int i = 0; i < alertsName.length; i++)
            {
                MyApplication.db.insertUserAlerts(alertsName[i]);
            }
        }

    }

    private void sendLinkMap()
    {

        String strUri = "http://maps.google.com/maps?q=loc:" + 18.520430 + "," + 73.856744 + " (" + "Pune,India" + ")";
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));

        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

        startActivity(intent);
    }

    private void setListner() {




        fb_geo_fencing.setSize(FloatingActionButton.SIZE_MINI);
        fb_geo_fencing.setColorNormalResId(R.color.fb_button_normal);
        fb_geo_fencing.setColorPressedResId(R.color.fb_button_pressed);
        fb_geo_fencing.setIcon(R.drawable.ic_album_black_24dp);
        fb_geo_fencing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {


                Intent intent_device_id = new Intent(getApplicationContext(), GeofenceActivitySnehal.class);
                startActivity(intent_device_id);
            }
        });
        fb_locate.setSize(FloatingActionButton.SIZE_MINI);
        fb_locate.setColorNormalResId(R.color.fb_button_normal);
        fb_locate.setColorPressedResId(R.color.fb_button_pressed);
        fb_locate.setIcon(R.drawable.ic_room_white_24dp);
        fb_locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent_device_id = new Intent(getApplicationContext(), LocateCarNewActivity.class);
                startActivity(intent_device_id);
            }
        });

        fb_sos.setSize(FloatingActionButton.SIZE_MINI);
        fb_sos.setColorNormalResId(R.color.fb_button_normal);
        fb_sos.setColorPressedResId(R.color.fb_button_pressed);
        fb_sos.setIcon(R.drawable.ic_contact_mail_white_24dp);
        fb_sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent_device_id = new Intent(getApplicationContext(), SubmitSosContactActivity.class);
//                startActivity(intent_device_id);

                Intent intent_device_id = new Intent(getApplicationContext(), SendSosActivity.class);
                startActivity(intent_device_id);
            }
        });

        fb_health.setSize(FloatingActionButton.SIZE_MINI);
        fb_health.setColorNormalResId(R.color.fb_button_normal);
        fb_health.setColorPressedResId(R.color.fb_button_pressed);
        fb_health.setIcon(R.drawable.ic_engine_white_24dp);
        fb_health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_device_id = new Intent(getApplicationContext(), HealthNewActivity.class);
                startActivity(intent_device_id);
            }
        });
        fb_trips.setSize(FloatingActionButton.SIZE_MINI);
        fb_trips.setColorNormalResId(R.color.fb_button_normal);
        fb_trips.setColorPressedResId(R.color.fb_button_pressed);
        fb_trips.setIcon(R.drawable.ic_trending_up_white_24dp);
        fb_trips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_device_id = new Intent(getApplicationContext(), TripsSmartTabsTripsActivity.class);
                //Intent intent_device_id = new Intent(getApplicationContext(), CreateTripActivity.class);
                startActivity(intent_device_id);
            }
        });

//        fb_geo.setSize(FloatingActionButton.SIZE_MINI);
//        fb_geo.setColorNormalResId(R.color.fb_button_normal);
//        fb_geo.setColorPressedResId(R.color.fb_button_pressed);
//        fb_geo.setIcon(R.drawable.ic_my_location_white_24dp);
//        fb_geo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "For Geo-fencing", Toast.LENGTH_LONG).show();
//            }
//        });
    }



    private void setupNavigationDrawer() {

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

    }

    // Initialize GoogleMaps
    private void initGMaps()
    {

        if (map == null)
        {
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }


    // Callback called when Map is ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Log.d(TAG, "onMapReady()");
        map = googleMap;
        map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(this);

        //set google map camera position by default on india
        map = googleMap;
        LatLng india = new LatLng(28.704059, 77.102490);
        // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(india,4));
    }

    // Callback called when Map is touched
    @Override
    public void onMapClick(LatLng latLng) {
        // Log.d(TAG, "onMapClick(" + latLng + ")");
    }

    // Callback called when Marker is touched
    @Override
    public boolean onMarkerClick(Marker marker) {
        // Log.d(TAG, "onMarkerClickListener: " + marker.getPosition());
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);

        MenuItem menuItem = menu.findItem(R.id.testAction);
        menuItem.setIcon(buildCounterDrawable(count, R.drawable.ic_notifications_none_black_24dp));

        MenuItem item = menu.findItem(R.id.spinner);

        final Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        final ArrayList<String> vehicleNAmes = S.getVehicleName(MyApplication.db);
       // vehicleNAmes.add("gokul");

        String device_id_get = S.getDeviceId(MyApplication.db);
        String nameByDeviceId = S.getNameByDEviceId(MyApplication.db,device_id_get);
        int position = vehicleNAmes.indexOf(nameByDeviceId);

//        Log.e("AMOL_DATA","device_id_get :"+device_id_get);
//        Log.e("AMOL_DATA","nameByDeviceId :"+nameByDeviceId);
//        Log.e("AMOL_DATA","position :"+position);
        if(!vehicleNAmes.isEmpty())
        {

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, vehicleNAmes);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerArrayAdapter);
            spinner.setSelection(position);
//            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, vehicleNAmes, android.R.layout.simple_spinner_item);
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            spinner.setAdapter(adapter);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                       long arg3) {

                //TextView tt = (TextView) arg1;

                int item = spinner.getSelectedItemPosition();
                String vehicleName = vehicleNAmes.get(item);

               // T.t(DashboardActivity.this,""+y);

                boolean c = T.checkConnection(DashboardActivity.this);

                                        if(c)
                                        {
                                          //  button.setText(text.toString());
                                            String device_id = S.returnDeviceIdByVehicleName(MyApplication.db,vehicleName);
                                            String user_email1 = S.getDeviceIdUserName(MyApplication.db);
                                            String [] user_email = user_email1.split("#");
                                            MyApplication.db.updateDeviceID(device_id,user_email[1]);
                                            getDashBoardDetails(device_id);
                                        }
                                        else
                                        {
                                            T.t(DashboardActivity.this,"Network connection off");
                                        }


            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        //setVehicleNames(menu);

        return true;
    }
    private Drawable buildCounterDrawable(int count, int backgroundImageId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.counter_menuitem_layout, null);
        view.setBackgroundResource(backgroundImageId);

        if (count == 0)
        {
            View counterTextPanel = view.findViewById(R.id.counterValuePanel);
            counterTextPanel.setVisibility(View.GONE);
        }
        else
        {
            TextView textView = (TextView) view.findViewById(R.id.count);
            textView.setText("" + count);

        }

        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(getResources(), bitmap);
    }

    private void setVehicleNames(Menu menu) {

        //No vehicle name available please fill vehicle master

        MenuItem item = menu.findItem(R.id.spinner);
        //final Spinner button = (Spinner) MenuItemCompat.getActionView(item);

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
//
//                new MaterialDialog.Builder(DashboardActivity.this)
//                        .title("Select Vehicle")
//                        .items(vehicleNAmes)
//                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice()
//                        {
//                            @Override
//                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
//                                try
//                                {
//
//                                    String vehicleName = text.toString();
//
//                                    if(!vehicleName.equals(getString(R.string.no_vehicle_found)))
//                                    {
//                                        boolean c = T.checkConnection(DashboardActivity.this);
//
//                                        if(c)
//                                        {
//                                            button.setText(text.toString());
//                                            String device_id = S.returnDeviceIdByVehicleName(new DBHelper(DashboardActivity.this),text.toString());
//                                            new DBHelper(DashboardActivity.this).updateDeviceID(device_id,"1");
//                                            getDashBoardDetails(device_id);
//                                        }
//                                        else
//                                        {
//                                            T.t(DashboardActivity.this,"Network connection off");
//                                        }
//
//                                    }
//                                    else
//                                    {
//                                        T.t(DashboardActivity.this,""+vehicleName);
//                                    }
//
//
//
//                                }
//                                catch (Exception e)
//                                {
//                                    e.printStackTrace();
//                                }
//
//                                return true;
//                            }
//                        })
//                        .show();
//
//            }
//        });


        final Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        ArrayList<String> vehicleNAmes = S.getVehicleName(MyApplication.db);

        if(!vehicleNAmes.isEmpty())
        {

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, vehicleNAmes);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerArrayAdapter);
//            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, vehicleNAmes, android.R.layout.simple_spinner_item);
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            spinner.setAdapter(adapter);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                       long arg3) {

                //TextView tt = (TextView) arg1;

                T.t(DashboardActivity.this,""+spinner.getSelectedItem().toString());


            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.testAction)
        {
            String device_id_mail = S.getDeviceIdUserName(MyApplication.db);

            String [] data = device_id_mail.split("#");

            ArrayList<NotificationInformation> alertss = S.getNotificationDashAlerts(MyApplication.db,data[0]);
            if(alertss.isEmpty())
            {

                T.displayErrorMessage(DashboardActivity.this,"Oops...","Cancel","No alerts found to display");
                //T.tTop(MakeAlertActivity.this,);
            }
            else
            {

                Intent i = new Intent(DashboardActivity.this,DisplayNotificationDetailsActivity.class);
                startActivityForResult(i,5);
            }


        }

        return super.onOptionsItemSelected(item);
    }

    public void colseAdvertise(View view) {

        advertiseRelativeLayout.setVisibility(View.GONE);
        PARRENT.clear();
        CHILD.clear();
    }

    @Override
    public void onLocationChanged(Location location) {

        lastLocation = location;

        //writeActualLocation(location);

        // Setting Current Longitude
        Log.e("LAT_LONG", "Longitude:" + location.getLongitude());
        // Setting Current Latitude
        Log.e("LAT_LONG", "Latitude:" + location.getLatitude());

        editor.putString("Longitude", "" + location.getLongitude()).commit();
        editor.putString("Latitude", "" + location.getLatitude()).commit();
    }

    @Override
    public void onConnected(Bundle bundle) {

        // T.t(MapsActivity.this, "onConnected()");

        //check m device for permissions
         getLastKnownLocation(); //for display current location when nw on

    }

    // Get last known location
    private void getLastKnownLocation()
    {
        Log.d("TAG", "getLastKnownLocation()");
        if ( checkPermission() )
        {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if ( lastLocation != null )
            {
                Log.i("TAG", "LasKnown location. " + "Long: " + lastLocation.getLongitude() + " | Lat: " + lastLocation.getLatitude());
                //writeLastLocation();
                startLocationUpdates();
            }
            else
            {
                Log.w("TAG", "No location retrieved yet");
                startLocationUpdates();
            }
        }
        else askPermission();
    }
    //Start location Updates
    private void startLocationUpdates()
    {
        Log.i("TAG", "startLocationUpdates()");
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        if ( checkPermission() )
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }
    @Override
    protected void onStart() {
        super.onStart();

        // Call GoogleApiClient connection when starting the Activity
        googleApiClient.connect();
    }
    @Override
    protected void onStop() {
        super.onStop();

        // Disconnect GoogleApiClient when stopping Activity
        googleApiClient.disconnect();
    }
    private void askPermission()
    {
        Log.d("TAG", "askPermission()");
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                12
        );
    }
    // Check for permission to access Location
    private boolean checkPermission() {
        Log.d("TAG", "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED );
    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void sendSosCustomer()
    {
        boolean status  = S.checkLogin(MyApplication.db);

        if(status)
        {

            checkPermissionforsms();

            boolean c = T.checkConnection(DashboardActivity.this);
            if(c)
            {
                sendSignalToWebApp();
            }
            else
            {
                T.t(DashboardActivity.this,"Network connection off. Message will send from device wait");
            }

        }
        else
        {
            displayDialog(
                    "Oops...",
                    "Ok",
                    "Close",
                    "No Sos contacts found");
            //T.displaySuccessMessage(MessageActivity.this, "Success", "Close", "SMS successfully send");
        }
    }


    private void checkPermissionforsms()
    {

        ArrayList<String> sosContacts = S.getSosContact(MyApplication.db);

        smstextPreferences = getSharedPreferences(Constants.SMS_TEXT, 0);
        SharedPreferences.Editor editor = smstextPreferences.edit();
        editor.commit();


        String vehicle_number = smstextPreferences.getString("vehicle_number","");
        String latitude = smstextPreferences.getString("latitude","");
        String longitude = smstextPreferences.getString("longitude","");
        String address = smstextPreferences.getString("address","");
        String name = smstextPreferences.getString("name","");
        String mobile = smstextPreferences.getString("mobile","");

        String latLongUrl = "http://maps.google.com/maps?q=loc:"+latitude+","+longitude+"("+address.replace(" ","%20")+")";

        String smsText = "Vehicle Number : "+vehicle_number+",\nMobile:"+mobile+",\nPerson Name: "+name+",\nLocation: "+latLongUrl+", \nAddress:"+address+"";

        Log.e("smsText",""+smsText);

        if ( T.checkPermission(DashboardActivity.this, Manifest.permission.SEND_SMS) )
        {


            //Toast.makeText(getApplicationContext(), "Message Sent successfully!", Toast.LENGTH_LONG).show();

            for(int i = 0; i < sosContacts.size(); i++)
            {

                //Log.e("smsText","Contacts :"+sosContacts.get(i));
                if(!(sosContacts.get(i).equals("") || sosContacts.get(i).equals(null) || sosContacts.get(i) == null))
                {
                    Intent intent =new Intent();
                    PendingIntent pi=PendingIntent.getActivity(getApplicationContext(), 0, intent,0);

                    SmsManager sms= SmsManager.getDefault();
                    sms.sendTextMessage(sosContacts.get(i), null, smsText, pi,null);

                    Toast.makeText(getApplicationContext(), "Message Sent successfully!", Toast.LENGTH_LONG).show();
                }


            }
        }

        else T.askPermission(DashboardActivity.this,Manifest.permission.SEND_SMS);
    }
    private void displayDialog(String title,String confirmText,String cancelText,String contentText)
    {
        //
        new SweetAlertDialog(DashboardActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setConfirmText(confirmText)
                .setCancelText(cancelText)
                .setContentText(contentText)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();

                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        finish();

                    }
                })
                .show();

    }
    private void sendSignalToWebApp()
    {


        String device_id_mail = S.getDeviceIdUserName(MyApplication.db);

        String [] data = device_id_mail.split("#");

        boolean c = T.checkConnection(DashboardActivity.this);

        if(c)
        {
            String [] parameters =
                    {
                            "device_id"+"#"+data[0],
                            "username"+"#"+data[1]
                    };
            VolleyResponseClass.getResponseProgressDialog(
                    new VolleyCallback() {
                        @Override
                        public void onSuccess(String result)
                        {
                            // Log.e("VolleyResponse", "" + result);
                            //{"status":1,"success":"Success! Alert added successfully."}

                            parseResponseSms(result);


                        }
                    },
                    DashboardActivity.this,
                    getResources().getString(R.string.webUrl)+""+getResources().getString(R.string.sendSosSignalTowebapp),
                    new EditText(DashboardActivity.this),
                    parameters,
                    "Sending sos...");
        }

    }
    private void parseResponseSms(String result) {

        try
        {
            if(result != null || result.length() > 0)
            {
                Object json = new JSONTokener(result).nextValue();
                if (json instanceof JSONObject)
                {
                    JSONObject tripsJsonObject = new JSONObject(result);
                    String status = tripsJsonObject.getString("status");

                    if(status.equals("1"))
                    {


                    }
                }
                else
                {
                    T.t(DashboardActivity.this,"incorect json");
                }
            }
            else
            {
                T.t(DashboardActivity.this,"0 or null json");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
    }
}
