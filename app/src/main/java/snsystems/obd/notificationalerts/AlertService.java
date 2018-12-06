package snsystems.obd.notificationalerts;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import snsystems.obd.classes.T;


/**
 * Created by shree on 17-Jan-17.
 */
public class AlertService extends Service {


    /*

     $device_id = $this->post('device_id');
   $email_id = $this->post('email_id');

     */
    /** indicates how to behave if the service is killed */
    int mStartMode = START_STICKY;

    /** interface for clients that bind */
    IBinder mBinder;

    /** indicates whether onRebind should be used */
    boolean mAllowRebind;

    /** Called when the service is being created. */
    @Override
    public void onCreate() {

    }

    /** The service is starting, due to a call to startService() */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //compile 'com.jakewharton:butterknife:7.0.1'
        // @Bind(R.id.textView1) TextView title;
        try
        {
            T.t(this,"Service started");


            //long INTERVAL_MSEC = 900000;

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {

                Log.e("ANDROID_SERVICE","Time"+T.getSystemTime());
                //Do something after 20 seconds
                handler.postDelayed(this, 10000);
            }
        }, 10000);

        }
        catch (Exception e)
        {

        }
        return mStartMode;
    }
    /** Called when The service is no longer used and is being destroyed */
    @Override
    public void onDestroy() {
        T.t(this,"Service stop");


    }
    /** Called when all clients have unbound with unbindService() */
    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    /** Called when a client is binding to the service with bindService()*/
    @Override
    public void onRebind(Intent intent) {

    }


    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}
