package snsystems.obd.devicemgt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;
import snsystems.obd.activity.CustomViewsActivity;
import snsystems.obd.classes.ConnectionStatus;
import snsystems.obd.classes.DisplayErrorMessage;
import snsystems.obd.classes.T;
import snsystems.obd.classes.Validations;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.sos.SendSosActivity;

public class ActivityAddDevice extends AnimationActivity {

    Toolbar toolbar;
    TextInputLayout txt_device_name, txt_Device_id,txt_vehicle_number;
    EditText eT_device_id, et_device_name,input_vehicle_number;
    DBHelper dbHelper;

    private TextView
            et_device_make,
            et_device_model,
            gearTypeTextView;
    String id, name, make, model, year, gear, fuel_type;
    RadioButton radioButton1, radioButton2, radioButton3;
    private RadioGroup radioGrp;

    private static final String[] GEAR = new String[]{
            "Manual", "Auto"};



    private String commingStatus,statusActivity;

    String [] modelsData;

    //String car_makes = "Toyota,Holden,Ford,Nissan,BMW,Mazda,Mercedes Benz,Volkswagen,Audi,Kia,Peugeot,Hundai";

    String car_makes = "Bajaj,Toyota,Ford,Hundai,Honda,Mahindra,Nissan,Volkswagen";


    String totyota_models = "86,Aurion,Camry,Corolla,HiAce,HiLux,Kluger,LandCruiser,Prado,Prius,RAV4,Rukus,Tarago,Yaris";
    String Holden_models = "Astra,Barina,Barina Spark,Caprice,Captiva,Cascada,Colorado,Colorado 7,Commodore,Cruze,Insignia,Malibu,Spark,Trax,Ute";
    String Ford_models = "EcoSport,Everest,Falcon,Falcon Ute,Fiesta,Focus,Kuga,Mondeo,Mustang,Ranger,Territory,Transit,Transit Custom";
    String Nissan_models = "370Z,Altima,GT-R,Juke,Leaf,Micra,Navara,Pathfinder,Patrol,Pulsar,Qashqai,X-Trail";
    String BMW_models = "1 Series,2 Series,3 Series,4 Series,5 Series,6 Series,7 Series,i3,i8,M2,M3,M4,M5,M6,X1,X3,X4,X5,X6,Z4";
    String Mazda_models = "Mazda2,Mazda3,Mazda6,BT-50,CX-3,CX-5,CX-9,MX-5";
    String Mercedes_models = "A-Class,AMG GT,B-Class,C-Class,CLA-Class,E-Class,G-Class,GL-Class,GLA-Class,GLC-Class,GLE-Class,S-Class,SL-Class,SLK-Class,V-Class";
    String Volkswagen_models = "Amarok,Beetle,Caddy,Caravelle,Golf,Jetta,Multivan,Passat,Polo,Scirocco,Tiguan,Touareg,Transporter";
    String Audi_models = "A1,A3,A4,A5,A6,A7,A8,Q3,Q5,Q7,R8,RS Q3,RS4,RS5,RS6,RS7,S1,S3,S4,S5,S6,S8,SQ5,TT";
    String Kia_models = "Carnival,Cerato,Optima,Picanto,Pro_cee'd GT,Rio,Rondo,Sorento,Soul,Sportage";
    String Peugeot_models = "2008,208,3008,308,4008,508";
    String Hundai_models = "Accent,Elantra,Genesis,i20,i30,i40,iLoad,iMax,Santa Fe,Sonata,Tucson,Veloster";

    String Bajaj_models = "Bajaj RE60";
    String honda_models = "Honda City,Honda WRV,Honda Amaze,Honda BRV,Honda Jazz,Honda Brio,Honda Mobilio,Honda CR-V,Honda Accord,Honda Vezel,Honda Civic";
    String mahindra_models = "Mahindra Scorpio,Mahindra XUV500,Mahindra Thar,Mahindra Bolero,Mahindra NuvoSport,Mahindra Xylo,Mahindra TUV 300,Mahindra Verito,Mahindra e2o";


    @Bind(R.id.vehicleMakeYear)
    TextView vehicleMakeYear;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_device_new);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle(Html.fromHtml("<large> <font color='#ffffff'>ADD DEVICE</font></large>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intialize();

        createMakeModels();
//        ArrayAdapter<String> gear_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, GEAR);
//        txt_gear = (BetterSpinner) findViewById(R.id.material_spinner_gear);
//        txt_gear.setAdapter(gear_adapter);


        getBundleData();

    }

    private void createMakeModels() {


    }

    public void selectGearType(View view) {

        new MaterialDialog.Builder(ActivityAddDevice.this)
                .title("Gear Type")
                .items(R.array.gear_type)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice()
                {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        try
                        {

                            gearTypeTextView.setText(text.toString());

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

    private void getBundleData() {


        Bundle bundle = getIntent().getExtras();

        if(bundle != null)
        {

            statusActivity = bundle.getString("status");

            if(statusActivity.equals("from_dashboard_vehicle_datanotfoundfirsttime"))
            {
                String device_id = S.getDeviceId(new DBHelper(ActivityAddDevice.this));

                if(device_id.equals("NA"))
                {

                }
                else
                {
                    eT_device_id.setText(device_id);
                }

            }
        }

    }

    public void Intialize() {

        dbHelper = new DBHelper(this);

        txt_Device_id = (TextInputLayout) findViewById(R.id.txt_device_id);
        txt_device_name = (TextInputLayout) findViewById(R.id.txt_device_name);


        txt_vehicle_number = (TextInputLayout) findViewById(R.id.txt_vehicle_number);

        eT_device_id = (EditText) findViewById(R.id.input_device_id);
        et_device_name = (EditText) findViewById(R.id.input_device_name);
        et_device_model = (TextView) findViewById(R.id.modelTextView);
        gearTypeTextView = (TextView) findViewById(R.id.gearTypeTextView);
        et_device_make = (TextView) findViewById(R.id.vehicleMake);

        input_vehicle_number = (EditText) findViewById(R.id.input_vehicle_number);

        radioGrp = (RadioGroup) findViewById(R.id.rg_fuel);
        radioButton1 = (RadioButton) findViewById(R.id.rb_add_petrol);
        radioButton2 = (RadioButton) findViewById(R.id.rb_add_diesel);
        radioButton3 = (RadioButton) findViewById(R.id.rb_add_cylinder);

    }


    public void display_id(View view)
    {

//        Intent intent_device_id = new Intent(getApplicationContext(), ActivityDeviceId.class);
//        startActivity(intent_device_id);
    }

    private void add_Device()
    {




        if (!Validations.validateEmptyField(eT_device_id, "Enter Device Id", txt_Device_id)) {
            return;
        }
        if (!Validations.validateEmptyField(et_device_name, "Enter Device Name", txt_device_name)) {
            return;
        }
        if (!Validations.validateEmptyField(input_vehicle_number, "Enter vehicle number", txt_vehicle_number)) {
            return;
        }
        if (!Validations.validateVehicleNumber(input_vehicle_number.getText().toString(),ActivityAddDevice.this))
        {
            return;
        }
        if (!Validations.validateTextViewEmpty(et_device_make, "select Vehicle Make", ActivityAddDevice.this,"Make")) {
            return;
        }
        if (!Validations.validateTextViewEmpty(et_device_model, "select Vehicle Model", ActivityAddDevice.this,"Model")) {
            return;
        }
        if (!Validations.validateTextViewEmpty(vehicleMakeYear, "select Vehicle Make Year", ActivityAddDevice.this,"Year")) {
            return;
        }

        if (!Validations.validateTextViewEmpty(gearTypeTextView, "select gear type", ActivityAddDevice.this,"Gear Type")) {
            return;
        }
        if (!Validations.validateRadioEmpty(radioGrp,ActivityAddDevice.this,"Select fuel type"))
        {
            return;
        }

        int radioButtonID = radioGrp.getCheckedRadioButtonId();
        View radioButton = radioGrp.findViewById(radioButtonID);
        int idx = radioGrp.indexOfChild(radioButton);
        if (idx == 0) {
            fuel_type = "Petrol";
            // radioButton1.setChecked(false);
        } else if (idx == 1) {
            fuel_type = "Diesel";
            // radioButton2.setChecked(false);
        } else if (idx == 2) {
            fuel_type = "Other";
            // radioButton2.setChecked(false);
        }
        radioGrp.clearCheck();

        Log.d(">>", "fuel_type>>" + fuel_type);
        Log.d(">>", "gear>>" + gear);

        if (ConnectionStatus.isConnectingToInternet(getApplicationContext()))
        {


            id = eT_device_id.getText().toString();
            name = et_device_name.getText().toString();
            make = et_device_make.getText().toString();
            model = et_device_model.getText().toString();
            year = vehicleMakeYear.getText().toString();
            gear = gearTypeTextView.getText().toString();

            sendAddDevice();

        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    public void add_close(View view) {
        finish();
    }


    private void sendAddDevice() {
        try {

            String device_id_mail = S.getDeviceIdUserName(new DBHelper(ActivityAddDevice.this));

            final String [] data = device_id_mail.split("#");

            final KProgressHUD progressDialog = KProgressHUD.create(ActivityAddDevice.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Adding device...")
                    //.setDetailsLabel("Downloading data")
                    .setCancellable(false)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.webUrl)+""+getResources().getString(R.string.activity_insert_device),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            Log.e("RESPONSE_RESPONSE",""+response);
                            parseAddDevice(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                           // Log.d(">>", "ERROR :" + error.toString());

                            progressDialog.dismiss();
                            handleError(error);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email_id", data[1]);
                    params.put("device_id", id);
                    params.put("device_name", name);
                    params.put("vehicle_no", input_vehicle_number.getText().toString());
                    params.put("make", make);
                    params.put("model", model);
                    params.put("year", year);
                    params.put("gear_type", gear);
                    params.put("fuel_type", fuel_type);
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

        new SweetAlertDialog(ActivityAddDevice.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setConfirmText("Try again")
                .setContentText(error)

                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                {
                    @Override
                    public void onClick(SweetAlertDialog sDialog)
                    {
                        sDialog.dismissWithAnimation();
                        sendAddDevice();
                    }
                })
                .show();

    }

    private void parseAddDevice(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            String successString = jsonResponse.getString("status");

            if (successString.equals("1"))
            {

                ///eT_device_id, et_device_name
                new DBHelper(ActivityAddDevice.this).insertVehicleMaster(et_device_name.getText().toString(),eT_device_id.getText().toString());

                String [] activation_start_date = jsonResponse.getString("activation_start_date").split(" ");
                String [] activation_end_date = jsonResponse.getString("activation_end_date").split(" ");

                new SweetAlertDialog(ActivityAddDevice.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Success")
                        .setConfirmText("OK")
                        .setContentText("Device Info Added successfully. \nActivation Start Date : "+activation_start_date[0]+" Activation End Date : "+activation_end_date[0])
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();

                                finish();


                            }
                        })
                        .show();

//                if(statusActivity.equals("from_dashboard_vehicle_datanotfoundfirsttime"))
//                {
//
//                    new DBHelper(ActivityAddDevice.this).updateDeviceID(eT_device_id.getText().toString(),"1");
//
//                    et_device_name.setText("");
//                    eT_device_id.setText("");
//                    et_device_make.setText("");
//                    et_device_model.setText("");
//                    et_device_year.setText("");
//                    // txt_gear.setSelection(0);
//
//
//                    new SweetAlertDialog(ActivityAddDevice.this, SweetAlertDialog.SUCCESS_TYPE)
//                            .setTitleText("Success...")
//                            .setConfirmText("OK")
//                            .setContentText("Device Info Added successfully")
//                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                @Override
//                                public void onClick(SweetAlertDialog sDialog) {
//                                    sDialog.dismissWithAnimation();
//
//                                    Intent intent = new Intent();
//                                    setResult(1, intent);
//                                    finish();
//
//                                }
//                            })
//                            .show();
//
//
//                }
//                else
//                {
//                    //{"status":1,"activation_start_date":"2017-03-14 15:31:10","activation_end_date":"2018-03-13 15:31:10"}
//
//                    String [] activation_start_date = jsonResponse.getString("activation_start_date").split(" ");
//                    String [] activation_end_date = jsonResponse.getString("activation_end_date").split(" ");
//                    T.displaySuccessMessage(ActivityAddDevice.this,"Success","OK","Device Info Added successfully. \nActivation Start Date : "+activation_start_date[0]+" Activation End Date : "+activation_end_date[0]);
//
//                }






            }
            else if(successString.equals("0"))
            {
                T.displayErrorMessage(ActivityAddDevice.this, "Oops...", "Ok","Device already exist");
            }
            else if(successString.equals("2"))
            {
                T.displayErrorMessage(ActivityAddDevice.this, "Oops...", "Ok","Device not alloted to any SD or MR, when it allot then try to add this device again.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_device, menu);
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

    public void vehicleMake(View view)
    {
        String  [] makeData = car_makes.split(",");

        new MaterialDialog.Builder(ActivityAddDevice.this)
                .title("Vehicle Make")
                .items(makeData)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice()
                {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        try
                        {

                            et_device_make.setText(text.toString());
                            et_device_model.setText("Model");

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

    public void vehicleModel(View view)
    {



        if(!Validations.validateTextViewEmpty(et_device_make,"Select make first",ActivityAddDevice.this,"Make"))
        {
            return;
        }



        String makeName = et_device_make.getText().toString();


        if(makeName.equals("Toyota"))
        {
            modelsData = totyota_models.split(",");
        }
        else if(makeName.equals("Holden"))
        {
            modelsData = Holden_models.split(",");
        }
        else if(makeName.equals("Ford"))
        {
            modelsData = Ford_models.split(",");
        }
        else if(makeName.equals("Nissan"))
        {
            modelsData = Nissan_models.split(",");
        }
        else if(makeName.equals("BMW"))
        {
            modelsData = BMW_models.split(",");
        }
        else if(makeName.equals("Mazda"))
        {
            modelsData = Mazda_models.split(",");
        }
        else if(makeName.equals("Mercedes Benz"))
        {
            modelsData = Mercedes_models.split(",");
        }
        else if(makeName.equals("Volkswagen"))
        {
            modelsData = Volkswagen_models.split(",");
        }
        else if(makeName.equals("Audi"))
        {
            modelsData = Audi_models.split(",");
        }
        else if(makeName.equals("Kia"))
        {
            modelsData = Kia_models.split(",");
        }
        else if(makeName.equals("Peugeot"))
        {
            modelsData = Peugeot_models.split(",");
        }
        else if(makeName.equals("Hundai"))
        {
            modelsData = Hundai_models.split(",");
        }

        else if(makeName.equals("Bajaj"))
        {
            modelsData = Bajaj_models.split(",");
        }
        else if(makeName.equals("Honda"))
        {
            modelsData = honda_models.split(",");
        }
        else if(makeName.equals("Mahindra"))
        {
            modelsData = mahindra_models.split(",");
        }


        new MaterialDialog.Builder(ActivityAddDevice.this)
                .title("Vehicle Make")
                .items(modelsData)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice()
                {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        try
                        {

                            et_device_model.setText(text.toString());

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

    public void submitDetails(View view) {

        add_Device();
    }

    public void vehicleMakeYear(View view) {

        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1990; i <= thisYear; i++)
        {
            years.add(Integer.toString(i));
        }

        new MaterialDialog.Builder(ActivityAddDevice.this)
                .title("Vehicle Make Year")
                .items(years)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice()
                {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        try
                        {

                            vehicleMakeYear.setText(text.toString());

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

