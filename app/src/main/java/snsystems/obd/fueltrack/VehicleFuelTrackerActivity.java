package snsystems.obd.fueltrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;
import snsystems.obd.classes.T;
import snsystems.obd.classes.Validations;
import snsystems.obd.classes.VolleyResponseClass;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.devicemgt.ActivityUpdateDevice;
import snsystems.obd.interfaces.VolleyCallback;
import snsystems.obd.maintenance.MaintenanceReportsActivity;
import snsystems.obd.maintenance.VehicleMaintenanceActivity;

public class VehicleFuelTrackerActivity extends AnimationActivity implements DatePickerDialog.OnDateSetListener {


    @Bind(R.id.toolbar)
    Toolbar toolbar;


    @Bind(R.id.maintenanceDateTextView)
    TextView maintenanceDateTextView;

    @Bind(R.id.vehicleNameSpinner)
    TextView vehicleNameSpinner;

    @Bind(R.id.petrolRadioButton)
    RadioButton petrolRadioButton;

    @Bind(R.id.dieselRadioButton)
    RadioButton dieselRadioButton;

    @Bind(R.id.otherRadioButton)
    RadioButton otherRadioButton;

    @Bind(R.id.fuelInLtrsEditext)
    EditText fuelInLtrsEditext;

    @Bind(R.id.costEditext)
    EditText costEditext;

    @Bind(R.id.oldReadingEditText)
    EditText oldReadingEditText;


    @Bind(R.id.currentReadingEditext)
    EditText currentReadingEditext;

//    @Bind(R.id.oldReadingEditText)
//    EditText oldReadingEditText;
//
//    @Bind(R.id.currentReadingEditext)
//    EditText currentReadingEditext;




    private String dateSet;

    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_fuel_tracker);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initialize();

        setReading();
        getFormsData();
    }

    private void setReading()
    {


        try
        {
            preferences = getSharedPreferences("reading", 0);
            SharedPreferences.Editor editor = preferences.edit();
            //editor.putString("status", "000");
            editor.commit();
            String readingOld = preferences.getString("status","");

            if(readingOld.equals(null))
            {
                oldReadingEditText.setText("000");
            }
            else
            {
                oldReadingEditText.setText(readingOld);
               // oldReadingEditText.setEnabled(false);
            }
        }
        catch (Exception e)
        {

        }

    }

    private void initialize() {

        ButterKnife.bind(this);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }



    public void saveFuelRecord(View view)

    {
        if(!Validations.validateTextViewEmpty(vehicleNameSpinner,"Select Vehicle",VehicleFuelTrackerActivity.this,"Select Vehicle"))
        {
            return;
        }
        if(!Validations.validateTextViewEmpty(maintenanceDateTextView,"Select Date",VehicleFuelTrackerActivity.this,"yyyy-mm-dd"))
        {
            return;
        }

        if(!Validations.validateEmptyEditext(VehicleFuelTrackerActivity.this,fuelInLtrsEditext,"enter fuel"))
        {
            return;
        }
        if(!Validations.validateEmptyEditext(VehicleFuelTrackerActivity.this,oldReadingEditText,"enter old reading"))
        {
            return;
        }
        if(!Validations.validateEmptyEditext(VehicleFuelTrackerActivity.this,currentReadingEditext,"enter current reading"))
        {
            return;
        }
        if(!Validations.validateEmptyEditext(VehicleFuelTrackerActivity.this,costEditext,"enter cost"))
        {
            return;
        }

        try
        {
            String fuel_type = null;

            if(petrolRadioButton.isChecked())
            {
                fuel_type = "Petrol";
            }
            if(dieselRadioButton.isChecked())
            {
                fuel_type = "Diesel";
            }
            if(otherRadioButton.isChecked())
            {
                fuel_type = "Other";
            }

            new DBHelper(VehicleFuelTrackerActivity.this).insertFuel(
                    vehicleNameSpinner.getText().toString(),
                    maintenanceDateTextView.getText().toString(),
                    fuel_type,
                    fuelInLtrsEditext.getText().toString(),
                    costEditext.getText().toString(),
                    oldReadingEditText.getText().toString(),
                    currentReadingEditext.getText().toString());

            preferences = getSharedPreferences("reading", 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("status", currentReadingEditext.getText().toString());
            editor.commit();


            maintenanceDateTextView.setText("yyyy-mm-dd");
            fuelInLtrsEditext.setText("");
            costEditext.setText("");

            T.displaySuccessMessage(VehicleFuelTrackerActivity.this,"Success","OK","Fuel details successfully submited.");
        }
        catch (Exception e)
        {
            T.t(VehicleFuelTrackerActivity.this,""+e);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fuel, menu);
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
        if (id == R.id.view_reports) {

            Intent i = new Intent(VehicleFuelTrackerActivity.this,FuelReportsActivity.class);
            i.putExtra("vehicle_name",vehicleNameSpinner.getText().toString());
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    private void getFormsData()
    {

        String device_id_get = S.getDeviceId(new DBHelper(VehicleFuelTrackerActivity.this));
        String nameByDeviceId = S.getNameByDEviceId(new DBHelper(VehicleFuelTrackerActivity.this),device_id_get);

        vehicleNameSpinner.setText(nameByDeviceId);

        boolean c = T.checkConnection(VehicleFuelTrackerActivity.this);

        if(c)
        {
            getVehicleName(device_id_get);

        }
        else
        {
            T.t(VehicleFuelTrackerActivity.this,"Network connection off");
        }



    }
    private void getVehicleName(String device_id) {


        String [] parameters =
                {
                        "device_id"+"#"+device_id,

                };
        VolleyResponseClass.getResponseProgressDialog(
                new VolleyCallback() {
                    @Override
                    public void onSuccess(String result)
                    {

                        //Log.e("RESPONSE",""+result);
                        parseResponse(result);

                    }
                },
                VehicleFuelTrackerActivity.this,
                getResources().getString(R.string.webUrl)+getResources().getString(R.string.getDeviceUpdateDetails),
                toolbar,
                parameters,
                "Please wait...");


    }
    private void parseResponse(String result) {


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

                        JSONArray vJsonArray = tripsJsonObject.getJSONArray("vehicleinfo");

                        JSONObject vJsonObject = vJsonArray.getJSONObject(0);


                        String vehicle_fuel_type = vJsonObject.getString("vehicle_fuel_type");


                        if(vehicle_fuel_type.equals("Petrol"))
                        {
                            petrolRadioButton.setChecked(true);
                        }
                        else if(vehicle_fuel_type.equals("Diesel"))
                        {
                            dieselRadioButton.setChecked(true);
                        }
                        else if(vehicle_fuel_type.equals("Other"))
                        {
                            otherRadioButton.setChecked(true);
                        }
                        else
                        {
                            otherRadioButton.setChecked(true);
                        }


                    }
                    else
                    {
                        T.t(VehicleFuelTrackerActivity.this,"No data found");

                    }
                }
                else
                {
                    T.t(VehicleFuelTrackerActivity.this, "incorect json");
                }
            }
            else
            {
                T.t(VehicleFuelTrackerActivity.this,"0 or null json");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
    }
    public void selectDate(View view) {

        Calendar now = Calendar.getInstance();

        DatePickerDialog dpd = DatePickerDialog.newInstance(
                VehicleFuelTrackerActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpd.setMinDate(Calendar.getInstance());
        dpd.setAccentColor(Color.parseColor("#0066B3"));
        dpd.show(getFragmentManager(), "Pick Date");

        dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int month, int date)
            {
                //String dateSet = "" + date + "-" + (++month) + "-" + year;
                dateSet = "" + year + "-" + (++month) + "-" + date;
                maintenanceDateTextView.setText(T.parseDate(dateSet));


            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

    }


    public void selectVehicle(View view)
    {


        ArrayList<String> vehicleNames = S.getVehicleName(new DBHelper(VehicleFuelTrackerActivity.this));

        new MaterialDialog.Builder(VehicleFuelTrackerActivity.this)
                .title("Select Vehicle")
                .items(vehicleNames)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice()
                {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        try
                        {

                            vehicleNameSpinner.setText(text.toString());

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        return true;
                    }
                })
                .show();


    }
}
