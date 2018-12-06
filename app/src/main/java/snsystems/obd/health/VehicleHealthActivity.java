package snsystems.obd.health;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import snsystems.obd.activity.FeedbackActivity;
import snsystems.obd.classes.T;
import snsystems.obd.classes.VolleyResponseClass;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.interfaces.Constants;
import snsystems.obd.interfaces.VolleyCallback;
import snsystems.obd.interfaces.VolleyErrorCallback;

public class VehicleHealthActivity extends AnimationActivity
{

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.textViewBatteryVoltage)
    TextView textViewBatteryVoltage;

    @Bind(R.id.textViewFuelLevel)
    TextView textViewFuelLevel;

    @Bind(R.id.hidelayout)
    RelativeLayout hidelayout;

    @Bind(R.id.notificationHideRelativeLayout)
    RelativeLayout notificationHideRelativeLayout;

    @Bind(R.id.imgHideLayout)
    ImageView imgHideLayout;

    @Bind(R.id.textViewHideLayout)
    TextView textViewHideLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_health);

        initialise();

        boolean c = T.checkConnection(VehicleHealthActivity.this);

                if(c)
                {
                    getHealthData();
                }
        else
                {
                    notificationHideRelativeLayout.setVisibility(View.VISIBLE);
                    imgHideLayout.setImageResource(R.drawable.ic_cloud_off_black_48dp);
                    textViewHideLayout.setText("No Connection");
                }
    }

    private void getHealthData()
    {


        String device_id_mail = S.getDeviceIdUserName(new DBHelper(VehicleHealthActivity.this));

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
                VehicleHealthActivity.this,
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

        new SweetAlertDialog(VehicleHealthActivity.this, SweetAlertDialog.ERROR_TYPE)
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
        String fuel_level = Constants.NA;
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

                        hidelayout.setVisibility(View.VISIBLE);

                        JSONArray healthJsonArray = healthJsonObject.getJSONArray("vehicle_health");

                        JSONObject hhealthJsonObject  = healthJsonArray.getJSONObject(0);

                        if(hhealthJsonObject.has("battery_voltage") && !hhealthJsonObject.isNull("battery_voltage"))
                        {
                            battery_voltage = hhealthJsonObject.getString("battery_voltage");
                        }

                        if(hhealthJsonObject.has("fuel_level") && !hhealthJsonObject.isNull("fuel_level"))
                        {
                            fuel_level = hhealthJsonObject.getString("fuel_level");
                        }

                        textViewBatteryVoltage.setText(""+battery_voltage+" V");
                        textViewFuelLevel.setText(""+fuel_level+" %");




                    }
                    else
                    {
                        //T.t(VehicleHealthActivity.this,"No health found for this vehicle");

                        notificationHideRelativeLayout.setVisibility(View.VISIBLE);
                        imgHideLayout.setImageResource(R.drawable.ic_battery_unknown_black_48dp);
                        textViewHideLayout.setText("Health not found");

                    }
                }
                else
                {
                    T.t(VehicleHealthActivity.this, "incorect json");
                }
            }
            else
            {
                T.t(VehicleHealthActivity.this,"0 or null json");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
    }


    private void initialise()
    {

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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
}
