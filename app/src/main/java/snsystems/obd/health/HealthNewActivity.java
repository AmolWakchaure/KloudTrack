package snsystems.obd.health;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;
import snsystems.obd.classes.T;
import snsystems.obd.classes.VolleyResponseClass;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.interfaces.Constants;
import snsystems.obd.interfaces.VolleyCallback;
import snsystems.obd.interfaces.VolleyErrorCallback;

public class HealthNewActivity extends AnimationActivity
{

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.hideLAyout)
    LinearLayout hideLAyout;

    @Bind(R.id.imageArrow)
    ImageView imageArrow;

    @Bind(R.id.batteryVoltageButton)
    TextView batteryVoltageButton;

    @Bind(R.id.coolanttempretureButton)
    TextView coolanttempretureButton;

    @Bind(R.id.dateTextView)
    TextView dateTextView;


    @Bind(R.id.batteryImageArrao)
    ImageView batteryImageArrao;

    @Bind(R.id.coolanrImageArrao)
    ImageView coolanrImageArrao;

    @Bind(R.id.coolantLinearLayout)
    LinearLayout coolantLinearLayout;

    @Bind(R.id.batteryLinearLayout)
    LinearLayout batteryLinearLayout;




    int countBattery = 1;

    int countCoolant = 1;

    int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_new);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        boolean c = T.checkConnection(HealthNewActivity.this);

        if(c)
        {
            getHealthData();
        }
        else
        {

           T.t(HealthNewActivity.this,"Network connection off");
//            notificationHideRelativeLayout.setVisibility(View.VISIBLE);
//            imgHideLayout.setImageResource(R.drawable.ic_cloud_off_black_48dp);
//            textViewHideLayout.setText("No Connection");
        }
    }
    private void getHealthData()
    {


        String device_id_mail = S.getDeviceIdUserName(new DBHelper(HealthNewActivity.this));

        String [] data = device_id_mail.split("#");

        String [] parameters =
                {
                        "device_id"+"#"+data[0],

                };
        VolleyResponseClass.getResponseProgressDialogError(
                new VolleyCallback()
                {
                    @Override
                    public void onSuccess(String result)
                    {

                        parseHealth(result);

                    }
                },
                new VolleyErrorCallback()
                {
                    @Override
                    public void onError(VolleyError result)
                    {
                        handleError(result);
                    }
                },
                HealthNewActivity.this,
                getResources().getString(R.string.webUrl)+""+getResources().getString(R.string.getHealth),
                toolbar,
                parameters,
                "Getting health...");

    }

    private void handleError(VolleyError error)
    {
        try
        {

            if(error instanceof TimeoutError || error instanceof NoConnectionError)
            {

                displayError("TimeoutError/NoConnectionError","Server not responding or no connection.");


            }
            else if(error instanceof AuthFailureError)
            {

                displayError("AuthFailureError","Remote server returns (401) Unauthorized?.");

            }
            else if(error instanceof ServerError)
            {


                displayError("ServerError","Wrong webservice call or wrong webservice url.");

            }
            else if (error instanceof NetworkError)
            {
                displayError("NetworkError","you doesn't have a data connection and wi-fi Connection.");

            }
            else if(error instanceof ParseError)
            {

                displayError("NetworkError","Incorrect json response.");
            }



        }
        catch (Exception e)
        {

        }

    }

    private void displayError(String title,String error)
    {

        new SweetAlertDialog(HealthNewActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setConfirmText("Try again")
                .setContentText(error)

                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                {
                    @Override
                    public void onClick(SweetAlertDialog sDialog)
                    {
                        sDialog.dismissWithAnimation();
                        getHealthData();
                    }
                })
                .show();

    }

    private void parseHealth(String result) {


        String battery_voltage = Constants.NA;
        String created_date = Constants.NA;
        String engine_coolent_temperature = Constants.NA;
        try
        {
            if(result != null || result.length() > 0)
            {
                Object json = new JSONTokener(result).nextValue();
                if (json instanceof JSONObject)
                {
                    JSONObject healthJsonObject = new JSONObject(result);
                    String status = healthJsonObject.getString("status");

                    if(status.equals("1"))
                    {
//{"status":1,"vehicle_health":[{"device_id":"10000001","created_date":"2017-02-25 12:56:37","battery_voltage":"0","fuel_level":"0"}]}



                        JSONArray healthJsonArray = healthJsonObject.getJSONArray("vehicle_health");

                        JSONObject hhealthJsonObject  = healthJsonArray.getJSONObject(0);

                        if(hhealthJsonObject.has("battery_voltage") && !hhealthJsonObject.isNull("battery_voltage"))
                        {
                            battery_voltage = hhealthJsonObject.getString("battery_voltage");
                        }

                        if(hhealthJsonObject.has("engine_coolent_temperature") && !hhealthJsonObject.isNull("engine_coolent_temperature"))
                        {
                            engine_coolent_temperature = hhealthJsonObject.getString("engine_coolent_temperature");
                        }

                        if(hhealthJsonObject.has("created_date") && !hhealthJsonObject.isNull("created_date"))
                        {
                            created_date = hhealthJsonObject.getString("created_date");
                        }

                        batteryVoltageButton.setText("Battery Voltage: "+battery_voltage+" V");
                        coolanttempretureButton.setText("Coolant Temprature: "+engine_coolent_temperature+"°C");
                        dateTextView.setText("Last Update: "+created_date);

                    }
                    else
                    {
                        T.t(HealthNewActivity.this,"No health found for this vehicle");


                    }
                }
                else
                {
                    T.t(HealthNewActivity.this, "incorect json");
                }
            }
            else
            {
                T.t(HealthNewActivity.this,"0 or null json");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
    }

    public void displayTroubleCode(View view)
    {


        if(count == 0)
        {
            hideLAyout.setVisibility(View.VISIBLE);
            imageArrow.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
            count = 1;
        }
        else
        {
            hideLAyout.setVisibility(View.GONE);
            imageArrow.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
            count = 0;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feedback, menu);
        return true;
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

        return super.onOptionsItemSelected(item);
    }

    public void batteryInfoDialog(View view)
    {
        T.displayInfoDialogg(HealthNewActivity.this,"Battery","Close",getString(R.string.batterrry));

    }

    public void coolantTempretureDetails(View view) {

        T.displayInfoDialogg(HealthNewActivity.this,"Coolant Temperature","Close",getString(R.string.coolant_tempreture));
    }

    public void airFuelControl(View view) {


        T.displayInfoDialogg(HealthNewActivity.this,"Air Fuel Control","Close",getString(R.string.air_fuel_control));
    }

    public void fuelControl(View view) {

        T.displayInfoDialogg(HealthNewActivity.this,"Fuel Control","Close",getString(R.string.fuel_control));
    }

    public void ignitionMisfire(View view) {

        T.displayInfoDialogg(HealthNewActivity.this,"Ignition/Misfire","Close",getString(R.string.ignitionMisfire));
    }

    public void auxillaryEmission(View view) {

        T.displayInfoDialogg(HealthNewActivity.this,"Auxillary Emission","Close",getString(R.string.auxillaryEmission));
    }

    public void speedIdelControl(View view) {

        T.displayInfoDialogg(HealthNewActivity.this,"Speed/Idle Control","Close",getString(R.string.speedIdelControl));
    }

    public void computerSystem(View view) {

        T.displayInfoDialogg(HealthNewActivity.this,"Computer System","Close",getString(R.string.computerSystem));
    }

    public void transsmissionTransaxle(View view) {

        T.displayInfoDialogg(HealthNewActivity.this,"Transmission/Transaxle","Close",getString(R.string.transsmissionTransaxle));
    }

    public void setOthers(View view) {

        T.displayInfoDialogg(HealthNewActivity.this,"Others","Close",getString(R.string.setOthers));
    }

    public void batteryHideShow(View view)
    {
        if(countBattery == 0)
        {
            batteryLinearLayout.setVisibility(View.VISIBLE);
            batteryImageArrao.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
            countBattery = 1;
        }
        else
        {
            batteryLinearLayout.setVisibility(View.GONE);
            batteryImageArrao.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
            countBattery = 0;
        }
    }

    public void coolantTempretureyHideShow(View view) {

        if(countCoolant == 0)
        {
            coolantLinearLayout.setVisibility(View.VISIBLE);
            coolanrImageArrao.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
            countCoolant = 1;
        }
        else
        {
            coolantLinearLayout.setVisibility(View.GONE);
            coolanrImageArrao.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
            countCoolant = 0;
        }

    }
}
