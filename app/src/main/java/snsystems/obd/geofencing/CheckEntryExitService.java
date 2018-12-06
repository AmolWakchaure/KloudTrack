package snsystems.obd.geofencing;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import snsystems.obd.R;
import snsystems.obd.classes.T;
import snsystems.obd.classes.VolleyErrorClass;
import snsystems.obd.dashboard.DashboardActivity;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.geofencesnehal.GeoFenceObjectClass;
import snsystems.obd.geofencesnehal.GeofenceActivitySnehal;
import snsystems.obd.interfaces.Constants;

/**
 * Created by snsystem_amol on 4/1/2017.
 */

public class CheckEntryExitService extends Service {

    int i = 0;

    final static String MY_ACTION = "MY_ACTION";

    Context context;

    /*

     $device_id = $this->post('device_id');
   $email_id = $this->post('email_id');

     */
    /**
     * indicates how to behave if the service is killed
     */
    int mStartMode = START_STICKY;

    /**
     * interface for clients that bind
     */
    IBinder mBinder;

    /**
     * indicates whether onRebind should be used
     */

    String[] data;

    boolean mAllowRebind;

    /**
     * Called when the service is being created.
     */

    public CheckEntryExitService() {

    }

    @Override
    public void onCreate() {

    }

    /**
     * The service is starting, due to a call to startService()
     */
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        //compile 'com.jakewharton:butterknife:7.0.1'
        // @Bind(R.id.textView1) TextView title;
        try {
//            T.t(this, "Service started");

            context = this;

            //long INTERVAL_MSEC = 900000;

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    String device_id_mail = S.getDeviceIdUserName(new DBHelper(CheckEntryExitService.this));
                    data = device_id_mail.split("#");

                    if(data != null)
                    {
                        boolean c = T.checkConnection(CheckEntryExitService.this);
                        if (c)
                        {
                            getLastLocation_Lat_Long(data[0]);
                        }
                    }



//                    Log.e("ANDROID_SERVICE", "Time" + T.getSystemTime());
//
//                    Intent intent = new Intent();
//                    intent.setAction(MY_ACTION);
//
//                    intent.putExtra("DATAPASSED", "" + T.getSystemTime());
//
//                    sendBroadcast(intent);



                    //Do something after 10 seconds
                    handler.postDelayed(this, 30000);

                    /// i++;

                }
            }, 10000);

        } catch (Exception e) {

        }
        return mStartMode;
    }

    @Override
    public void onDestroy() {
        T.t(this, "Service stop");
    }

    /**
     * Called when all clients have unbound with unbindService()
     */
    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    /**
     * Called when a client is binding to the service with bindService()
     */
    @Override
    public void onRebind(Intent intent) {

    }


    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    private void generateNotification(Context context,
                                      String notification_title,
                                      String notification_message, int id) {

        //for notification icon
        int icon = R.drawable.ic_launcher;
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

//        Intent notificationIntent = new Intent(CheckEntryExitService.this);
//        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        Notification.Builder builder = new Notification.Builder(context);

        builder.setSmallIcon(icon)
                .setContentTitle(notification_title)
                .setAutoCancel(true)
                //.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/raw/amol"))
                .setContentText(notification_message);
//              .setContentIntent(intent);


        Notification notification1 = builder.getNotification();
        notificationManager.notify(id, notification1);
    }

    private void getLastLocation_Lat_Long(final String device_id) {
        try {

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    getResources().getString(R.string.webUrl) + "" + getResources().getString(R.string.displayDashboard),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            parseLastLocation_Lat_Long(response, device_id);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(">>", "ERROR :" + error.toString());
                            VolleyErrorClass.handleVolleyerrorProgressNew(getApplicationContext(), error);

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("device_id", device_id);
                    Log.d(">>", "device_id :" + device_id);
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.getCache().clear();
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseLastLocation_Lat_Long(String result, String device_id) {
        try {

            String latitude = Constants.NA;
            String longitude = Constants.NA;

            JSONObject tripsJsonObject = new JSONObject(result);
            String status = tripsJsonObject.getString("success");

            if (status.equals("1")) {

                JSONArray dashboardJsonArray = tripsJsonObject.getJSONArray("dashboard_data");

                JSONObject ddasJsonObject = dashboardJsonArray.getJSONObject(0);

                if (ddasJsonObject.has("latitude") && !ddasJsonObject.isNull("latitude")) {
                    latitude = ddasJsonObject.getString("latitude");
                }
                if (ddasJsonObject.has("longitude") && !ddasJsonObject.isNull("longitude")) {
                    longitude = ddasJsonObject.getString("longitude");
                }

                if (!latitude.equals("") && !longitude.equals("")) {

                    ArrayList<GeoFenceObjectClass> GEOFENCE_LIST =
                            new DBHelper(CheckEntryExitService.this).selectAllGeofence(device_id);

                    ArrayList<LatLng> lat_long_list = new ArrayList<>();


                    double distance;

                    java.text.DateFormat df = new SimpleDateFormat("h:mm a");
                    String date = df.format(Calendar.getInstance().getTime());

                    Log.d(">>", "lat_long_list>>" + GEOFENCE_LIST.size() + date);

                    if (GEOFENCE_LIST.size() > 0) {
                        for (int i = 0; i < GEOFENCE_LIST.size(); i++) {

                            lat_long_list.clear();

                            String[] a = GEOFENCE_LIST.get(i).getGetGeo_fence_lat_long().toString().split(",");
                            String lat_value = a[0];
                            String long_value = a[1];

                            lat_long_list.add(new LatLng(Double.valueOf(latitude), Double.valueOf(longitude)));
                            lat_long_list.add(new LatLng(Double.valueOf(lat_value), Double.valueOf(long_value)));

                            distance = calculateDistanceUsingLatLong(lat_long_list);

                            Log.d(">>", "distance>>"+i+">> "+ distance);
                            Log.d(">>", "radius>>"+i+">> "+ GEOFENCE_LIST.get(i).getGeo_fence_radius());

                            if (distance > (Double.parseDouble(GEOFENCE_LIST.get(i).getGeo_fence_radius())) * 1000)
                            {

                                if (GEOFENCE_LIST.get(i).getGetGeo_fence_depart_alert().toString().equals("true"))
                                {

                                    Log.d(">>", "depart>>"+i+">> "+ GEOFENCE_LIST.get(i).getGetGeo_fence_depart_alert().toString());
                                    generateNotification(CheckEntryExitService.this,
                                            "Departure : " + GEOFENCE_LIST.get(i).getGeo_fence_name(),
                                            "OutSide of Geofence at " + date, i);
                                }
                            }
                            else
                            {
                                if (GEOFENCE_LIST.get(i).getGetGeo_fence_arrive_alert().toString().equals("true")) {

                                    Log.d(">>", "arrive>>" +i+">> "+ GEOFENCE_LIST.get(i).getGetGeo_fence_arrive_alert().toString());

                                    generateNotification(CheckEntryExitService.this,
                                            "Arrive : " + GEOFENCE_LIST.get(i).getGeo_fence_name(),
                                            "Inside of Geofence at " + date, i);
                                }
                            }

                            Log.d(">>", "*****************************************");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static double calculateDistanceUsingLatLong(ArrayList<LatLng> ROUTE_MARKERS)

    {


        double total_distance = 0;
        try {

            for (int i = 0; i < ROUTE_MARKERS.size() - 1; i++) {

                double total_distance1 = distanceBetween(ROUTE_MARKERS.get(i), ROUTE_MARKERS.get(i + 1));

                total_distance = total_distance + total_distance1;
            }
        } catch (Exception e) {

        }

        return total_distance;
    }

    public static Double distanceBetween(LatLng point1, LatLng point2) {
        if (point1 == null || point2 == null) {
            return null;
        }

        return SphericalUtil.computeDistanceBetween(point1, point2);
    }

}
