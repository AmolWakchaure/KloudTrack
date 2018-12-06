package snsystems.obd.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import snsystems.obd.R;
import snsystems.obd.classes.T;
import snsystems.obd.classes.VolleyResponseClass;
import snsystems.obd.dashboard.DashboardActivity;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.interfaces.VolleyCallback;
import snsystems.obd.interfaces.VolleyErrorCallback;


/**
 * Created by snsystem_amol on 13-Apr-17.
 */

public class LoadDailyData extends Service
{
    int i = 0;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public final static String MY_ACTION = "MY_ACTION";

    Context context;
    /*

     $device_id = $this->post('device_id');
   $email_id = $this->post('email_id');

     */
    /** indicates how to behave if the service is killed */
    int mStartMode = START_STICKY;

    /** interface for clients that bind */
    IBinder mBinder;

    /*indicates whether onRebind should be used */
    boolean mAllowRebind;

    /** Called when the service is being created. */

    public LoadDailyData()
    {

    }
    @Override
    public void onCreate() {

    }

    /** The service is starting, due to a call to startService() */
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        //compile 'com.jakewharton:butterknife:7.0.1'
        // @Bind(R.id.textView1) TextView title;
        try
        {
            //T.t(this,"Service started");



            context = this;
            //long INTERVAL_MSEC = 900000;



            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run()
                {


                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run()
                        {

                            boolean dd = S.checkDeviceIdAvail(new DBHelper(context));

                            if(dd)
                            {
                                getDashBoardDetails();
                            }
                            //Do something after 10 seconds
                            handler.postDelayed(this, 10000);
                        }
                    }, 10000);

                    //Do 1minutes
                    handler.postDelayed(this, 100000);

                }
            }, 100000);

        }
        catch (Exception e)
        {
            Log.e("systemTime","systemTime :"+e);
        }
        return mStartMode;
    }

    private void getDashBoardDetails()
    {

        boolean c = T.checkConnection(context);
        if(c)
        {
            String device_id_get = S.getDeviceId(new DBHelper(context));
            getDashBoardDetails(device_id_get);
            //getNotification();

        }
        else
        {

            T.t(context,"Network connection off");

        }

    }
    private void getDashBoardDetails(String device_id) {

        /*
        http://192.168.1.47:81/obd/api/MobileAPI/getDashboard

para:  $device_id = $this->post('device_id');
         */

        //String devIDD = S.getDeviceId(new DBHelper(DashboardActivity.this));


        String [] parameters =
                {
                        "device_id"+"#"+device_id
                        //"device_id"+"#"+"40000003"

                };
        VolleyResponseClass.getResponseProgressDialogErrorWithoutProgress(
                new VolleyCallback() {
                    @Override
                    public void onSuccess(String result)
                    {
                        //Log.e("VolleyResponse", "" + result);

                        //T.t(DisplayAdvertiseActivity.this,""+result);
                        // {"success":"1","dashboard_data":[{"fuel_level":"4","rpm":"200","vehicle_speed":"85","latitude":"18.5018","longitude":"73.8636","vehicle_name":"honda city","vehicle_make":"honda","vehicle_model":"honda city","vehicle_make_year":"2016","vehicle_gear_type":"asa","vehicle_fuel_type":"dsf"}]}

                       // parseDashboardResponse(result);

                        //send broad cast when data successfully inserted
                        Intent intent = new Intent();
                        intent.setAction(MY_ACTION);

                        intent.putExtra("DATAPASSED", result);

                        sendBroadcast(intent);



                    }
                },
                new VolleyErrorCallback() {
                    @Override
                    public void onError(VolleyError result) {


                        //toastInUi(""+result);
                        T.t(context,""+result);

                    }
                },
                context,
                getResources().getString(R.string.webUrl) + "" + getResources().getString(R.string.displayDashboard),
                new EditText(context),
                parameters);


    }







    /** Called when The service is no longer used and is being destroyed */
    @Override
    public void onDestroy()
    {
       // T.t(this,"Service stop");


    }
    /** Called when all clients have unbound with unbindService() */
    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    /** Called when a client is binding to the service with bindService()*/
    @Override
    public void onRebind(Intent intent)
    {

    }


    @Override
    public IBinder onBind(Intent intent)
    {

        return null;
    }


}
