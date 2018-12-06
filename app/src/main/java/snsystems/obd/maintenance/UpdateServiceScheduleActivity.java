package snsystems.obd.maintenance;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;
import snsystems.obd.classes.T;
import snsystems.obd.classes.Validations;
import snsystems.obd.classes.VolleyResponseClass;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.interfaces.VolleyCallback;
import snsystems.obd.interfaces.VolleyErrorCallback;

public class UpdateServiceScheduleActivity extends AnimationActivity
{



    @Bind(R.id.serviceTypeRelativeLayout)
    RelativeLayout serviceTypeRelativeLayout;

    @Bind(R.id.serviceStatusRelativeLayout)
    RelativeLayout serviceStatusRelativeLayout;

    @Bind(R.id.otherLovRelativeLayout)
    RelativeLayout otherLovRelativeLayout;

    @Bind(R.id.vehicleNameSpinner)
    TextView vehicleNameSpinner;

    @Bind(R.id.maintenanceTypeSpinner)
    TextView maintenanceTypeSpinner;

    @Bind(R.id.lovServiceStatus)
    TextView lovServiceStatus;

    @Bind(R.id.lovAlertsSpinner)
    TextView lovAlertsSpinner;

    @Bind(R.id.maintenanceDateTextView)
    TextView maintenanceDateTextView;




    @Bind(R.id.submitFillupMaintFloatingActionButton)
    FloatingActionButton submitFillupMaintFloatingActionButton;


    @Bind(R.id.hideRelativeLayout)
    RelativeLayout hideRelativeLayout;

    @Bind(R.id.serviceChargesEditText)
    EditText serviceChargesEditText;


    @Bind(R.id.remarkEditextgdfg)
    EditText remarkEditextgdfg;

    @Bind(R.id.toolbar)
    Toolbar toolbar;



    String id,deviceid,maintenanceDate,maintenanceType,serviceRemark,serviceStatus,serviceOtherLov,serviceCost,update_status;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_service_schedule);

        ButterKnife.bind(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setClickListner();



        getUpdateData();
    }

    private void setClickListner() {


        serviceStatusRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new MaterialDialog.Builder(UpdateServiceScheduleActivity.this)
                        .title("Select Service Status")
                        .items(R.array.service_status)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice()
                        {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                try
                                {

                                    lovServiceStatus.setText(text.toString());

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
        });

        otherLovRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(UpdateServiceScheduleActivity.this)
                        .title("Select Other Lov Type")
                        .items(R.array.service_other_break_down)
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
        });


        serviceTypeRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                new MaterialDialog.Builder(UpdateServiceScheduleActivity.this)
                        .title("Select Service Type")
                        .items(R.array.service_type)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice()
                        {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                try
                                {

                                    String service_type = text.toString();

                                    maintenanceTypeSpinner.setText(service_type);

                                    if(service_type.equals("Break down") || service_type.equals("Other"))
                                    {
                                        hideRelativeLayout.setVisibility(View.VISIBLE);
                                    }
                                    else
                                    {
                                        hideRelativeLayout.setVisibility(View.GONE);
                                    }



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
        });

    }

    private void getUpdateData()
    {

        Bundle bundle = getIntent().getExtras();

        if(bundle != null)
        {



             id = bundle.getString("id");
             deviceid = bundle.getString("deviceid");
             maintenanceDate = bundle.getString("maintenanceDate");
             maintenanceType = bundle.getString("maintenanceType");
             serviceRemark = bundle.getString("serviceRemark");
             serviceStatus = bundle.getString("serviceStatus");
             serviceOtherLov = bundle.getString("serviceOtherLov");
             serviceCost = bundle.getString("serviceCost");

            vehicleNameSpinner.setText(S.getNameByDEviceId(new DBHelper(UpdateServiceScheduleActivity.this),deviceid));

            String [] data = maintenanceDate.split(" ");
            maintenanceDateTextView.setText(data[0]);

            if(serviceRemark.equals("NA"))
            {
                remarkEditextgdfg.setText("");
            }
            else
            {
                remarkEditextgdfg.setText(serviceRemark);
            }
            if(serviceCost.equals("0"))
            {
                serviceChargesEditText.setText("");
            }
            else
            {
                serviceChargesEditText.setText(serviceCost);
            }

            lovServiceStatus.setText(serviceStatus);

            if(serviceOtherLov.equals("Lov"))
            {
                lovAlertsSpinner.setVisibility(View.VISIBLE);
                lovAlertsSpinner.setText(serviceOtherLov);
            }
            else
            {
                lovAlertsSpinner.setVisibility(View.VISIBLE);
                lovAlertsSpinner.setText(serviceOtherLov);
            }

            if(maintenanceType.equals("Break down") || maintenanceType.equals("Other"))
            {
                hideRelativeLayout.setVisibility(View.VISIBLE);
                maintenanceTypeSpinner.setText(maintenanceType);

            }
            else
            {
                hideRelativeLayout.setVisibility(View.VISIBLE);
                maintenanceTypeSpinner.setText(maintenanceType);
            }

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

            update_status = "false";
            Intent intent = new Intent();
            intent.putExtra("status",update_status);
            setResult(6, intent);
            finish();
            //finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        update_status = "false";
        Intent intent = new Intent();
        intent.putExtra("status",update_status);
        setResult(6, intent);
        finish();
    }

    public void updateServiceDetails(View view)
    {



        boolean c = T.checkConnection(UpdateServiceScheduleActivity.this);

        if(c)
        {
            if(serviceChargesEditText.getText().toString().trim().isEmpty())
            {
                serviceCost = "0";
            }
            else
            {
                serviceCost = serviceChargesEditText.getText().toString();
            }

            if(remarkEditextgdfg.getText().toString().trim().isEmpty())
            {
                serviceRemark = "NA";
            }
            else
            {
                serviceRemark = remarkEditextgdfg.getText().toString();
            }


            updateServiceDetails();
        }
        else
        {
            T.t(UpdateServiceScheduleActivity.this,"Network connection off");
        }

    }

    private void updateServiceDetails()
    {



        //String id,deviceid,maintenanceDate,maintenanceType,serviceRemark,serviceStatus,serviceOtherLov,serviceCost;


        String [] parameters =
                {
                        "service_schedule_master_id"+"#"+id,
                        "device_id"+"#"+deviceid,
                        "service_date"+"#"+maintenanceDate,
                        "service_charges"+"#"+serviceCost,
                        "remark"+"#"+serviceRemark,
                        "service_status"+"#"+lovServiceStatus.getText().toString(),
                        "service_type"+"#"+maintenanceTypeSpinner.getText().toString(),
                        "service_other_lov"+"#"+serviceOtherLov


                };
        VolleyResponseClass.getResponseProgressDialogError(
                new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {

                        parseResponse(result);

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
                UpdateServiceScheduleActivity.this,
                getResources().getString(R.string.webUrl) + "" + getResources().getString(R.string.updateService),
                remarkEditextgdfg,
                parameters,
                "Updating...");
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
    private void displayError(String title,
                              String error)
    {

        new SweetAlertDialog(UpdateServiceScheduleActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setConfirmText("Try again")
                .setCancelText("Cancel")
                .setContentText(error)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        sweetAlertDialog.dismissWithAnimation();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                {
                    @Override
                    public void onClick(SweetAlertDialog sDialog)
                    {
                        sDialog.dismissWithAnimation();
                        updateServiceDetails();
                    }
                })
                .show();

    }

    private void parseResponse(String result) {

        //{"status":1,"success":"Success! Record added successfully."}
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
                        new SweetAlertDialog(UpdateServiceScheduleActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Success")
                                .setConfirmText("OK")
                                .setContentText("Service details successfully updated.")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();

                                        update_status = "true";
                                        Intent intent = new Intent();
                                        intent.putExtra("status",update_status);
                                        setResult(6, intent);
                                        finish();
                                    }
                                })
                                .show();


                    }
                    else
                    {
                        T.displayErrorMessage(UpdateServiceScheduleActivity.this, "Fail", "Cancel", "Fail to update Service details. Try again...");
                    }
                }
                else
                {
                    T.t(UpdateServiceScheduleActivity.this,"incorrect json");
                }
            }
            else
            {
                T.t(UpdateServiceScheduleActivity.this,"0 or null json");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
    }
}
