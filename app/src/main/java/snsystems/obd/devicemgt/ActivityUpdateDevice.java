package snsystems.obd.devicemgt;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;
import snsystems.obd.activity.FeedbackActivity;
import snsystems.obd.classes.ConnectionStatus;
import snsystems.obd.classes.DisplayErrorMessage;
import snsystems.obd.classes.T;
import snsystems.obd.classes.Validations;
import snsystems.obd.classes.VolleyResponseClass;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.interfaces.Constants;
import snsystems.obd.interfaces.VolleyCallback;
import snsystems.obd.sos.SendSosActivity;

public class ActivityUpdateDevice extends AnimationActivity {

    Toolbar toolbar;

    TextInputLayout txt_device_name,txt_vehicle_number;
    EditText  et_device_name,input_update_device_id,input_vehicle_number;
    DBHelper dbHelper;

    TextView eT_device_id;

    private TextView
            et_device_make,
            et_device_model;
    String id, name, make, model, year, gear, fuel_type;
    RadioButton radioButton1, radioButton2, radioButton3;
    private RadioGroup radioGrp;
    //BetterSpinner txt_gear;

    TextView gearTypeTextView;

    private static final String[] GEAR = new String[]{
            "Manual", "Auto"};
    Bundle bundle;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_device_new);

        ButterKnife.bind(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       // getSupportActionBar().setTitle(Html.fromHtml("<large> <font color='#ffffff'>UPDATE DEVICE</font></large>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Initialise();
//        ArrayAdapter<String> gear_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, GEAR);
//        txt_gear = (BetterSpinner) findViewById(R.id.material_update_spinner_gear);
//        txt_gear.setAdapter(gear_adapter);


         bundle = getIntent().getExtras();

        if(bundle != null)
        {
            boolean c = T.checkConnection(ActivityUpdateDevice.this);

            if(c)
            {
                getVehicleName(bundle.getString("device_id"));
                eT_device_id.setText(bundle.getString("device_id"));
            }
            else
            {
                T.t(ActivityUpdateDevice.this,"Network connection off");
            }
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
                ActivityUpdateDevice.this,
                getResources().getString(R.string.webUrl)+""+getResources().getString(R.string.getDeviceUpdateDetails),
                toolbar,
                parameters,
                "Please wait...");


    }
    private void parseResponse(String result) {




        try
        {
            String vehicle_name = "";
            String vehicle_no = "";
            String vehicle_make = "Make";
            String vehicle_model = "Model";
            String vehicle_make_year = "";
            String vehicle_gear_type = "Gear Type";
            String vehicle_fuel_type = "";

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

                        if(vJsonObject.has("vehicle_name") && !vJsonObject.isNull("vehicle_name"))
                        {
                            vehicle_name = vJsonObject.getString("vehicle_name");
                        }

                        if(vJsonObject.has("vehicle_no") && !vJsonObject.isNull("vehicle_no"))
                        {
                            vehicle_no = vJsonObject.getString("vehicle_no");
                        }

                        if(vJsonObject.has("vehicle_make") && !vJsonObject.isNull("vehicle_make"))
                        {
                            vehicle_make = vJsonObject.getString("vehicle_make");
                        }
                        if(vJsonObject.has("vehicle_model") && !vJsonObject.isNull("vehicle_model"))
                        {
                            vehicle_model = vJsonObject.getString("vehicle_model");
                        }
                        if(vJsonObject.has("vehicle_make_year") && !vJsonObject.isNull("vehicle_make_year"))
                        {
                            vehicle_make_year = vJsonObject.getString("vehicle_make_year");
                        }
                        if(vJsonObject.has("vehicle_gear_type") && !vJsonObject.isNull("vehicle_gear_type"))
                        {
                            vehicle_gear_type = vJsonObject.getString("vehicle_gear_type");
                        }

                        if(vJsonObject.has("vehicle_fuel_type") && !vJsonObject.isNull("vehicle_fuel_type"))
                        {
                            vehicle_fuel_type = vJsonObject.getString("vehicle_fuel_type");
                        }

                            et_device_name.setText(vehicle_name);
                            input_vehicle_number.setText(vehicle_no);
                            vehicleMakeYear.setText(vehicle_make_year);
                            et_device_model.setText(vehicle_model);
                            et_device_make.setText(vehicle_make);
                            gearTypeTextView.setText(vehicle_gear_type);
                           eT_device_id.setText(bundle.getString("device_id"));


                        if(vehicle_fuel_type.equals("Petrol"))
                        {
                            radioButton1.setChecked(true);
                        }
                        else if(vehicle_fuel_type.equals("Diesel"))
                        {
                            radioButton2.setChecked(true);
                        }
                        else if(vehicle_fuel_type.equals("Other"))
                        {
                            radioButton2.setChecked(true);
                        }
//                        else
//                        {
//                            radioButton2.setChecked(true);
//                        }




                    }
                    else
                    {
                        T.t(ActivityUpdateDevice.this,"No data found");

                    }
                }
                else
                {
                    T.t(ActivityUpdateDevice.this, "incorect json");
                }
            }
            else
            {
                T.t(ActivityUpdateDevice.this,"0 or null json");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
    }

    private void Initialise() {

        dbHelper = new DBHelper(this);


        txt_device_name = (TextInputLayout) findViewById(R.id.txt_update_device_name);
        txt_vehicle_number = (TextInputLayout) findViewById(R.id.txt_vehicle_number);


       // txt_year = (TextInputLayout) findViewById(R.id.txt_device_year);

        gearTypeTextView = (TextView) findViewById(R.id.gearTypeTextView);

        eT_device_id = (TextView) findViewById(R.id.input_update_device_id);
        et_device_name = (EditText) findViewById(R.id.input_update_device_name);
        et_device_model = (TextView) findViewById(R.id.modelTextView);
        et_device_make = (TextView) findViewById(R.id.vehicleMake);
        //et_device_year = (EditText) findViewById(R.id.input_device_year);


        input_vehicle_number = (EditText) findViewById(R.id.input_vehicle_number);



        radioGrp = (RadioGroup) findViewById(R.id.rg_fuel);
        radioButton1 = (RadioButton) findViewById(R.id.rb_add_petrol);
        radioButton2 = (RadioButton) findViewById(R.id.rb_add_diesel);
        radioButton3 = (RadioButton) findViewById(R.id.rb_add_cylinder);
    }

    private void update_Device() {


//        if (!Validations.validateEmptyField(eT_device_id, "Enter Device Id", txt_Device_id)) {
//            return;
//        }
        if (!Validations.validateEmptyField(et_device_name, "Enter Device Name", txt_device_name)) {
            return;
        }
        if (!Validations.validateEmptyField(input_vehicle_number, "Enter vehicle number", txt_vehicle_number)) {
            return;
        }
        if (!Validations.validateVehicleNumber(input_vehicle_number.getText().toString(),ActivityUpdateDevice.this))
        {
            return;
        }
        if (!Validations.validateTextViewEmpty(et_device_make, "select Vehicle Make", ActivityUpdateDevice.this,"Make")) {
            return;
        }
        if (!Validations.validateTextViewEmpty(et_device_model, "select Vehicle Model", ActivityUpdateDevice.this,"Model")) {
            return;
        }
        if (!Validations.validateTextViewEmpty(vehicleMakeYear, "select Vehicle Make Year", ActivityUpdateDevice.this,"Year")) {
            return;
        }
//        if (!Validations.validateYear(et_device_year, "Enter Less Year than Current Year", txt_year)) {
//            return;
//        }
        if (!Validations.validateTextViewEmpty(gearTypeTextView, "select gear type", ActivityUpdateDevice.this,"Gear Type")) {
            return;
        }

        if (!Validations.validateRadioEmpty(radioGrp,ActivityUpdateDevice.this,"Select fuel type"))
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


       // T.t(ActivityUpdateDevice.this,"Success");


        if (ConnectionStatus.isConnectingToInternet(getApplicationContext()))
        {


            id = eT_device_id.getText().toString();
            name = et_device_name.getText().toString();
            make = et_device_make.getText().toString();
            model = et_device_model.getText().toString();
            year = vehicleMakeYear.getText().toString();
            gear = gearTypeTextView.getText().toString();



            sendUpdateDeviceInfo();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    public void update_close(View view) {
        finish();
    }

    private void sendUpdateDeviceInfo() {


        try {

            String device_id_mail = S.getDeviceIdUserName(new DBHelper(ActivityUpdateDevice.this));

            final String [] data = device_id_mail.split("#");


            final KProgressHUD progressDialog = KProgressHUD.create(ActivityUpdateDevice.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Updating device...")
                    //.setDetailsLabel("Downloading data")
                    .setCancellable(false)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();


            StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.webUrl)+""+getResources().getString(R.string.activity_update_device),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            parseUpdateDeviceInfo(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Log.d(">>", "ERROR :" + error.toString());

                            progressDialog.dismiss();
                            handleError(error);



                            //T.handleVolleyerror(error, toolbar);
                          //  VolleyErrorClass.handleVolleyerrorProgressNew(getApplicationContext(), error);

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

        new SweetAlertDialog(ActivityUpdateDevice.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setConfirmText("Try again")
                .setContentText(error)

                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                {
                    @Override
                    public void onClick(SweetAlertDialog sDialog)
                    {
                        sDialog.dismissWithAnimation();
                        sendUpdateDeviceInfo();
                    }
                })
                .show();

    }

    private void parseUpdateDeviceInfo(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            String successString = jsonResponse.getString("status");

            if (successString.equals("1"))
            {
                //T.displaySuccessMessage(ActivityUpdateDevice.this,"Success","OK","Device Info Updated Successfully");

                new DBHelper(ActivityUpdateDevice.this).updateVehicleNameFromVehicleMaster(eT_device_id.getText().toString(),et_device_name.getText().toString());

                new SweetAlertDialog(ActivityUpdateDevice.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Success")
                        .setConfirmText("OK")
                        .setContentText("Device Info Updated Successfully")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();

                                finish();


                            }
                        })
                        .show();

//                et_device_name.setText("");
//                eT_device_id.setText("");
//                et_device_make.setText("");
//                et_device_model.setText("");
//                et_device_year.setText("");
//                //txt_gear.setSelection(0);

            }
            else
            {
                DisplayErrorMessage.displayErrorMessage(ActivityUpdateDevice.this, "Oops...", "Email Id Not Present...!\nPlease Try Again..");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectGearType(View view) {

        new MaterialDialog.Builder(ActivityUpdateDevice.this)
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

        new MaterialDialog.Builder(ActivityUpdateDevice.this)
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



        if(!Validations.validateTextViewEmpty(et_device_make,"Select make first",ActivityUpdateDevice.this,"Make"))
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
        //String car_makes = "Bajaj,Toyota,Ford,Hundai,Honda,Mahindra,Nissan,Volkswagen";

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


        new MaterialDialog.Builder(ActivityUpdateDevice.this)
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

    public void submitDetails(View view)
    {

        update_Device();
    }

    public void vehicleMakeYear(View view)
    {


        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1990; i <= thisYear; i++)
        {
            years.add(Integer.toString(i));
        }

        new MaterialDialog.Builder(ActivityUpdateDevice.this)
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
