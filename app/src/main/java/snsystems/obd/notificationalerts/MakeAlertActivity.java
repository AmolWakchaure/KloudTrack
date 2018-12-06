package snsystems.obd.notificationalerts;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import snsystems.obd.R;
import snsystems.obd.classes.T;
import snsystems.obd.classes.VolleyResponseClass;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.interfaces.Constants;
import snsystems.obd.interfaces.VolleyCallback;

public class MakeAlertActivity extends AppCompatActivity {


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
            service_due_alertname,
            service_due_status,
            service_due_data,
            subscription_alertname,
            subscription_status,
            subscription_data;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private ArrayList<String> NOTIFICATION_DATA = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_alert);

        ButterKnife.bind(this);

        initialize();
    }

    private void initialize() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



    }

    public void getData(View view)
    {



        ArrayList<String> alerts_information = S.checkUserAlertsAvailable(new DBHelper(MakeAlertActivity.this));

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
            String service_due = alerts_information.get(4);
            String subscription = alerts_information.get(5);

            String [] fuelData = fuelLevel.split("#");
            String [] speedData = speed.split("#");
            String [] rpmData = rpm.split("#");
            String [] maintenanceData = maintenance.split("#");
            String [] service_dueData = service_due.split("#");
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

                    service_due_alertname = service_dueData[0];
                    service_due_status = service_dueData[1];
                    service_due_data = service_dueData[2];

                    subscription_alertname = subscriptionData[0];
                    subscription_status = subscriptionData[1];
                    subscription_data = subscriptionData[2];

            Log.e("ALERTS_DATA",""+fuel_alertname);
            Log.e("ALERTS_DATA",""+fuel_status);
            Log.e("ALERTS_DATA",""+fuel_data);

            getAlertsInformation();
        }
    }

    private void generateNotification(Context context,
                                      String notification_title,
                                      String notification_message)
    {



        int icon = R.drawable.ic_car_engine_on_24dp;

        Intent intent = new Intent(context, MakeAlertActivity.class);
        intent.putExtra("response", "");
        int mNotificationId = 001;

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context);
        Notification notification = mBuilder.setSmallIcon(icon).setTicker(notification_title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(notification_title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notification_message))
                .setContentIntent(resultPendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_cake_black_48dp))
                .setContentText(notification_message).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(mNotificationId, notification);



    }


    private void getAlertsInformation() {

        String device_id_mail = S.getDeviceIdUserName(new DBHelper(MakeAlertActivity.this));

        String [] data = device_id_mail.split("#");

        String [] parameters =
                {
//                        "device_id"+"#"+data[0],
//                        "email_id"+"#"+data[1]
                        "device_id"+"#"+"9000",
                        "email_id"+"#"+"a@gmail.com"


                };
        VolleyResponseClass.getResponseAlerts(
                new VolleyCallback() {
                    @Override
                    public void onSuccess(String result)
                    {
                        parseResponse(result);

                    }
                },
                MakeAlertActivity.this,
                getResources().getString(R.string.alertApp),
                new EditText(MakeAlertActivity.this),
                parameters);
    }
    private void parseResponse(String result)
    {

        String  fuel_level = Constants.NA;
        String  rpm = Constants.NA;
        String  vehicle_speed = Constants.NA;
        String  service_days = Constants.NA;
        String  service_due_days = Constants.NA;
        String  activation_end_days = Constants.NA;

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
                        if(aalertsJsonObject.has("service_due_days") && !aalertsJsonObject.isNull("service_due_days"));
                        {
                            service_due_days = aalertsJsonObject.getString("service_due_days");
                        }
                        if(aalertsJsonObject.has("activation_end_days") && !aalertsJsonObject.isNull("activation_end_days"));
                        {
                            activation_end_days = aalertsJsonObject.getString("activation_end_days");
                        }


                        if(Boolean.valueOf(fuel_status))
                        {
                            if(Integer.valueOf(fuel_level) < Integer.valueOf(fuel_data))
                            {

                                NOTIFICATION_DATA.add(fuel_alertname+"#"+fuel_level+" %#"+fuel_data);
                                count++;
                                invalidateOptionsMenu();

                                //generateNotification(MakeAlertActivity.this, "Fuel Level", "Your current fuel level "+fuel_level);
                            }
                        }
                        if(Boolean.valueOf(speed_status))
                        {
                            if(Integer.valueOf(vehicle_speed) > Integer.valueOf(speed_data))
                            {
                                NOTIFICATION_DATA.add(speed_alertname+"#"+vehicle_speed+" Kmph#"+speed_data);
                                count++;
                                invalidateOptionsMenu();
                                //generateNotification(MakeAlertActivity.this, "Fuel Level", "Your current fuel level "+vehicle_speed);
                            }
                        }

                        if(Boolean.valueOf(rpm_status))
                        {
                            if(Integer.valueOf(rpm) < Integer.valueOf(rpm_data))
                            {
                                NOTIFICATION_DATA.add(rpm_alertname+"#"+rpm+" Rpm#"+rpm_data);
                                count++;
                                invalidateOptionsMenu();
                                // generateNotification(MakeAlertActivity.this, "Fuel Level", "Your current fuel level "+rpm);
                            }
                        }
                        //activation subscription
                        if(Boolean.valueOf(subscription_status))
                        {
                            // if(Integer.valueOf(activation_end_days) < 267)
                            if(Integer.valueOf(activation_end_days) < Integer.valueOf(subscription_data))
                            {
                                if(Integer.valueOf(activation_end_days) < 0)
                                {
                                    NOTIFICATION_DATA.add("Days left"+"#0 Days#"+subscription_data);
                                    count++;
                                    invalidateOptionsMenu();
                                }
                                else
                                {
                                    NOTIFICATION_DATA.add("Days left"+"#"+activation_end_days+" Days#"+subscription_data);
                                    count++;
                                    invalidateOptionsMenu();
                                    //generateNotification(MakeAlertActivity.this, "Fuel Level", "Your current fuel level "+fuel_level);
                                }

                            }
                        }

                        //service due
                        if(Boolean.valueOf(service_due_status))
                        {
                            if(Integer.valueOf(service_days) < Integer.valueOf(service_due_data))
                            //if(Integer.valueOf(service_days) < 10)
                            {
                                if(Integer.valueOf(service_days) < 0)
                                {
                                    NOTIFICATION_DATA.add("Days left"+"#0 Days#"+service_due_data);
                                    count++;
                                    invalidateOptionsMenu();
                                    // generateNotification(MakeAlertActivity.this, "Fuel Level", "Your current fuel level "+fuel_level);
                                }
                                else
                                {
                                    NOTIFICATION_DATA.add("Days left"+"#"+service_days+" Days#"+service_due_data);
                                    count++;
                                    invalidateOptionsMenu();
                                    // generateNotification(MakeAlertActivity.this, "Fuel Level", "Your current fuel level "+fuel_level);
                                }


                            }
                        }

                    }


//
//

//





                }
                else
                {
                    T.t(MakeAlertActivity.this, "incorect json");
                }
            }
            else
            {
                T.t(MakeAlertActivity.this,"0 or null json");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
    }

    private void generateNotification() {
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alerts, menu);

        MenuItem menuItem = menu.findItem(R.id.testAction);
        menuItem.setIcon(buildCounterDrawable(count, R.drawable.ic_notifications_none_black_24dp));



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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {

            finish();
        }
        if (id == R.id.testAction)
        {

            if(NOTIFICATION_DATA.isEmpty())
            {

                T.displayErrorMessage(MakeAlertActivity.this,"Oops...","Cancel","No alerts found to display");
                //T.tTop(MakeAlertActivity.this,);
            }
            else
            {
                Intent i = new Intent(MakeAlertActivity.this,DisplayNotificationDetailsActivity.class);
                i.putStringArrayListExtra("notification_list", NOTIFICATION_DATA);
                startActivity(i);
            }


        }

        return super.onOptionsItemSelected(item);
    }


}
