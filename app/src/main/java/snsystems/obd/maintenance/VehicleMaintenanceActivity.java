package snsystems.obd.maintenance;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;
import snsystems.obd.activity.FeedbackActivity;
import snsystems.obd.classes.T;
import snsystems.obd.classes.Validations;
import snsystems.obd.dashboard.DashboardActivity;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.docupload.DocumentUploadActivity;

public class VehicleMaintenanceActivity extends AnimationActivity implements DatePickerDialog.OnDateSetListener {


    @Bind(R.id.vehicleNameSpinner)
    TextView vehicleNameSpinner;

    @Bind(R.id.maintenanceTypeSpinner)
    TextView maintenanceTypeSpinner;

    @Bind(R.id.lovAlertsSpinner)
    TextView lovAlertsSpinner;

    @Bind(R.id.maintenanceDateTextView)
    TextView maintenanceDateTextView;

    @Bind(R.id.odometerReadingEditext)
    EditText odometerReadingEditext;

    @Bind(R.id.costEditext)
    EditText costEditext;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private String dateSet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_maintenance);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getFormsData();

        //getDeviceWiseMaintenanceData();


    }

//    private void getDeviceWiseMaintenanceData()
//    {
//
//        ArrayList<String> maintenanceData = S.getMaintenanceVehicleWiseData(new DBHelper(VehicleMaintenanceActivity.this),vehicleNameSpinner.getText().toString());
//
//                if(!maintenanceData.isEmpty())
//                {
//                    maintenanceDateTextView.setText(maintenanceData.get(1));
//                    maintenanceTypeSpinner.setText(maintenanceData.get(2));
//                    odometerReadingEditext.setText(maintenanceData.get(3));
//                    costEditext.setText(maintenanceData.get(4));
//                    lovAlertsSpinner.setText(maintenanceData.get(5));
//                }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_maintenance, menu);
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

            Intent i = new Intent(VehicleMaintenanceActivity.this,MaintenanceReportsActivity.class);
            i.putExtra("vehicle_name",vehicleNameSpinner.getText().toString());
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
    private void getFormsData()
    {

        String device_id_get = S.getDeviceId(new DBHelper(VehicleMaintenanceActivity.this));
        String nameByDeviceId = S.getNameByDEviceId(new DBHelper(VehicleMaintenanceActivity.this),device_id_get);

        vehicleNameSpinner.setText(nameByDeviceId);



    }

    public void saveMaintenanceRecord(View view)
    {


        if(!Validations.validateTextViewEmpty(vehicleNameSpinner,"Select Vehicle",VehicleMaintenanceActivity.this,"Select Vehicle"))
        {
            return;
        }
        if(!Validations.validateTextViewEmpty(maintenanceDateTextView,"Select Date",VehicleMaintenanceActivity.this,"yyyy-mm-dd"))
        {
            return;
        }
        if(!Validations.validateTextViewEmpty(maintenanceTypeSpinner,"Select Maintenance Type",VehicleMaintenanceActivity.this,"Select Maintenance Type"))
        {
            return;
        }
        if(!Validations.validateEmptyEditext(VehicleMaintenanceActivity.this,odometerReadingEditext,"enter odometer reading"))
        {
            return;
        }
        if(!Validations.validateEmptyEditext(VehicleMaintenanceActivity.this,costEditext,"enter cost"))
        {
            return;
        }
        if(!Validations.validateTextViewEmpty(lovAlertsSpinner,"Select Lov Alerts",VehicleMaintenanceActivity.this,"Select Lov Alert"))
        {
            return;
        }

        try
        {
            new DBHelper(VehicleMaintenanceActivity.this).insertMaintenance(
                    vehicleNameSpinner.getText().toString(),
                    maintenanceDateTextView.getText().toString(),
                    maintenanceTypeSpinner.getText().toString(),
                    odometerReadingEditext.getText().toString(),
                    costEditext.getText().toString(),
                    lovAlertsSpinner.getText().toString());


            maintenanceDateTextView.setText("yyyy-mm-dd");
            maintenanceTypeSpinner.setText("Select Maintenance Type");
            odometerReadingEditext.setText("");
            costEditext.setText("");
            lovAlertsSpinner.setText("Select Lov Alert");

            T.displaySuccessMessage(VehicleMaintenanceActivity.this,"Success","OK","Maintenance details successfully submited.");
        }
        catch (Exception e)
        {
            T.t(VehicleMaintenanceActivity.this,""+e);
        }




    }

    public void selectVehicle(View view)
    {

        ArrayList<String> vehicleNames = S.getVehicleName(new DBHelper(VehicleMaintenanceActivity.this));

        new MaterialDialog.Builder(VehicleMaintenanceActivity.this)
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

    public void selectMaintenaneType(View view)
    {

        new MaterialDialog.Builder(VehicleMaintenanceActivity.this)
                .title("Maintenace Type")
                .items(R.array.maintenace_type)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice()
                {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        try
                        {

                            maintenanceTypeSpinner.setText(text.toString());

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

    public void selectLovAlerts(View view)
    {

        new MaterialDialog.Builder(VehicleMaintenanceActivity.this)
                .title("Lov Alerts")
                .items(R.array.feedback_problem)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice()
                {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        try
                        {

                            lovAlertsSpinner.setText(text.toString());

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

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {


    }

    public void selectDate(View view) {

        Calendar now = Calendar.getInstance();

        DatePickerDialog dpd = DatePickerDialog.newInstance(
                VehicleMaintenanceActivity.this,
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
}
