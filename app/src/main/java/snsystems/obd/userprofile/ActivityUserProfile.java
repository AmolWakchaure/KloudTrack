package snsystems.obd.userprofile;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

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
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;


import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;
import snsystems.obd.activity.FeedbackActivity;
import snsystems.obd.carlog.CarLogTabsActivity;
import snsystems.obd.classes.ConnectionStatus;
import snsystems.obd.classes.DisplayErrorMessage;
import snsystems.obd.classes.T;
import snsystems.obd.classes.Validations;
import snsystems.obd.classes.VolleyResponseClass;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.devicemgt.ActivityAddDevice;
import snsystems.obd.interfaces.Constants;
import snsystems.obd.interfaces.VolleyCallback;
import snsystems.obd.interfaces.VolleyErrorCallback;
import snsystems.obd.sos.SendSosActivity;

/**
 * Created by Admin on 12/28/2016.
 */
public class ActivityUserProfile extends AnimationActivity implements DatePickerDialog.OnDateSetListener{


    private Toolbar toolbar;
    Calendar now;
    DatePickerDialog dpd;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    String updatedDateFormat;

    EditText et_name, et_email_id, et_add_1, et_city, et_pin, et_state, et_phone,input_birth_date;
    TextInputLayout txt_name, txt_email_id, txt_add_1, txt_city, txt_state, txt_phone_no, txt_pin,txt_birth_date;

    String name, email_id, add_1,city, pin, state, phone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        Initialize();


        if (ConnectionStatus.isConnectingToInternet(getApplicationContext())) {
            senduserEmail();

        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    private void Initialize() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //  getSupportActionBar().setTitle(Html.fromHtml("<large> <font color='#ffffff'>MY PROFILE</font></large>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        now = Calendar.getInstance();

        dpd = DatePickerDialog.newInstance(
                ActivityUserProfile.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );


        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();

        et_name = (EditText) findViewById(R.id.input_full_name);
        et_email_id = (EditText) findViewById(R.id.input_email_id);
        et_add_1 = (EditText) findViewById(R.id.input_add_1);
        et_city = (EditText) findViewById(R.id.input_city);
        et_pin = (EditText) findViewById(R.id.input_pin);
        et_state = (EditText) findViewById(R.id.input_state);
        et_phone = (EditText) findViewById(R.id.input_phone_no);
        input_birth_date = (EditText) findViewById(R.id.input_birth_date);

        txt_name = (TextInputLayout) findViewById(R.id.txt_full_name);
        txt_email_id = (TextInputLayout) findViewById(R.id.txt_full_name);
        txt_add_1 = (TextInputLayout) findViewById(R.id.txt_add_1);
        txt_city = (TextInputLayout) findViewById(R.id.txt_city);
        txt_state = (TextInputLayout) findViewById(R.id.txt_state);
        txt_pin = (TextInputLayout) findViewById(R.id.txt_pin);
        txt_phone_no = (TextInputLayout) findViewById(R.id.txt_phone_no);
        txt_birth_date = (TextInputLayout) findViewById(R.id.txt_birth_date);

    }

    public void submit_profile(View view) {



        if(!Validations.validateEmptyField(et_name,"enter name",txt_name))
        {
            return;
        }
        if(!Validations.validateEmptyField(input_birth_date,"enter birth date",txt_birth_date))
        {
            return;
        }
        if(!Validations.validateEmptyField(et_add_1,"enter address",txt_add_1))
        {
            return;
        }
        if(!Validations.validateEmptyField(et_city,"enter city",txt_city))
        {
            return;
        }
        if(!Validations.validateEmptyField(et_state,"enter state",txt_state))
        {
            return;
        }
        if(!Validations.validateEmptyField(et_pin,"enter pin code",txt_pin))
        {
            return;
        }
        if(!Validations.validatePincodelessthan(et_pin,"invalid pin code",txt_pin))
        {
            return;
        }

        if (ConnectionStatus.isConnectingToInternet(getApplicationContext()))
        {

           senduserInfo();

        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }


    private void senduserInfo()
    {

        name = et_name.getText().toString();
        email_id = et_email_id.getText().toString();
        phone = et_phone.getText().toString();
        add_1 = et_add_1.getText().toString();
        city = et_city.getText().toString();
        state = et_state.getText().toString();
        pin = et_pin.getText().toString();

        String device_id_mail = S.getDeviceIdUserName(new DBHelper(ActivityUserProfile.this));

        String [] data = device_id_mail.split("#");

        String [] parameters =
                {
                        "name"+"#"+name,
                        "email_id"+"#"+data[1],
                        "address"+"#"+add_1,
                        "city"+"#"+city,
                        "state"+"#"+state,
                        "pin"+"#"+pin,
                        "birthdate"+"#"+T.changeDateFormatMonth(input_birth_date.getText().toString())

                };

        VolleyResponseClass.getResponseProgressDialogError(
                new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {

                        parseInsert(result);

                    }
                },
                new VolleyErrorCallback() {
                    @Override
                    public void onError(VolleyError result) {


                        handleError(result);

                    }
                },
                ActivityUserProfile.this,
                getResources().getString(R.string.webUrl) + "" + getResources().getString(R.string.activity_insert_user_info),
                et_name,
                parameters,
                "Submiting...");


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

        new SweetAlertDialog(ActivityUserProfile.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setConfirmText("Try again")
                .setContentText(error)

                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                {
                    @Override
                    public void onClick(SweetAlertDialog sDialog)
                    {
                        sDialog.dismissWithAnimation();
                        senduserInfo();
                    }
                })
                .show();

    }

    private void parseInsert(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            String successString = jsonResponse.getString("status");

            if (successString.equals("1"))
            {
                new SweetAlertDialog(ActivityUserProfile.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Success")
                        .setContentText("Successfully submited")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override

                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                sweetAlertDialog.dismissWithAnimation();
                                finish();
                            }
                        })
                        .show();



            }
            else {
                DisplayErrorMessage.displayErrorMessage(ActivityUserProfile.this, "Oops...",
                        "Something went wrong!\nPlease Try Again..");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void senduserEmail()
    {
        String device_id_mail = S.getDeviceIdUserName(new DBHelper(ActivityUserProfile.this));

        final String [] data = device_id_mail.split("#");

        final KProgressHUD progressDialog = KProgressHUD.create(ActivityUserProfile.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait...")
                //.setDetailsLabel("Downloading data")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();



        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.webUrl)+""+getResources().getString(R.string.activity_user_email_id),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response_email)
                    {
                        progressDialog.dismiss();
                        //Log.e("RESPONSEEE",""+response_email);
                        parseEmailResponse(response_email);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressDialog.dismiss();
                        //handleVolleyerrorProgressNew(error);
                        T.t(ActivityUserProfile.this,""+error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();

                params.put("email_id", data[1]);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void parseEmailResponse(String result) {

        String customer_name = "";
        String customer_contact_no = "";
        String email_id = "";
        String customer_dob = "";
        String customer_address = "";
        String customer_city = "";
        String customer_state = "";
        String customer_pincode = "";

        try
        {
            if(result != null || result.length() > 0)
            {
                Object json = new JSONTokener(result).nextValue();
                if (json instanceof JSONObject)
                {
                    JSONObject profilJsonObject = new JSONObject(result);
                    JSONArray profilJsonArray = profilJsonObject.getJSONArray("userinfo");

                    JSONObject pprofilJsonObject = profilJsonArray.getJSONObject(0);

                    if(pprofilJsonObject.has("customer_name") && !pprofilJsonObject.isNull("customer_name"))
                    {
                        customer_name = pprofilJsonObject.getString("customer_name");
                    }

                    if(pprofilJsonObject.has("customer_contact_no") && !pprofilJsonObject.isNull("customer_contact_no"))
                    {
                        customer_contact_no = pprofilJsonObject.getString("customer_contact_no");
                    }
                    if(pprofilJsonObject.has("email_id") && !pprofilJsonObject.isNull("email_id"))
                    {
                        email_id = pprofilJsonObject.getString("email_id");
                    }

                    if(pprofilJsonObject.has("customer_dob") && !pprofilJsonObject.isNull("customer_dob"))
                    {
                        customer_dob = pprofilJsonObject.getString("customer_dob");
                    }
                    if(pprofilJsonObject.has("customer_address") && !pprofilJsonObject.isNull("customer_address"))
                    {
                        customer_address = pprofilJsonObject.getString("customer_address");
                    }

                    if(pprofilJsonObject.has("customer_city") && !pprofilJsonObject.isNull("customer_city"))
                    {
                        customer_city = pprofilJsonObject.getString("customer_city");
                    }

                    if(pprofilJsonObject.has("customer_state") && !pprofilJsonObject.isNull("customer_state"))
                    {
                        customer_state = pprofilJsonObject.getString("customer_state");
                    }

                    if(pprofilJsonObject.has("customer_pincode") && !pprofilJsonObject.isNull("customer_pincode"))
                    {
                        customer_pincode = pprofilJsonObject.getString("customer_pincode");
                    }

                    try
                    {
                        String [] dobdata = customer_dob.split(" ");

                        et_name.setText(customer_name);
                        et_email_id.setText(email_id);
                        et_add_1.setText(customer_address);
                        et_city.setText(customer_city);
                        et_pin.setText(customer_pincode);
                        et_state.setText(customer_state);
                        et_phone.setText(customer_contact_no);

                        Log.e("customer_dob",""+customer_dob);

                        String [] data = dobdata[0].split("-");
                        input_birth_date.setText(T.changeDateFormatDdMmYyyy(dobdata[0]));
                        dpd.initialize(null,Integer.valueOf(data[0]),Integer.valueOf(data[1])-1,Integer.valueOf(data[2]));
                        //dpd.initialize(null,1990,10,5-1);
                    }
                    catch (Exception e)
                    {

                    }



                }
                else
                {
                    T.t(ActivityUserProfile.this, "incorect json");
                }
            }
            else
            {
                T.t(ActivityUserProfile.this,"0 or null json");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
    }

//    private void parseEmailResponse(String response_email) {
//        try
//        {
//            JSONArray jsonArray = new JSONArray(response_email);
//            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
//
//            String customer_name = jsonObject.getString("customer_name");
//            String customer_contact_no = jsonObject.getString("customer_contact_no");
//            String customer_email_id = jsonObject.getString("customer_email_id");
//
//            et_name.setText(customer_name);
//            et_phone.setText(customer_contact_no);
//            et_email_id.setText(customer_email_id);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void handleVolleyerrorProgressNew(VolleyError error) {
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {

            Toast.makeText(ActivityUserProfile.this, "Oops!Server Timeout Please refresh", Toast.LENGTH_LONG).show();

        } else if (error instanceof AuthFailureError) {

            Toast.makeText(ActivityUserProfile.this, "AuthFailureError", Toast.LENGTH_LONG).show();

        } else if (error instanceof ServerError) {

            Toast.makeText(ActivityUserProfile.this, "ServerError", Toast.LENGTH_LONG).show();

        } else if (error instanceof NetworkError) {

            Toast.makeText(ActivityUserProfile.this, "NetworkError", Toast.LENGTH_LONG).show();

        } else if (error instanceof ParseError) {

            Toast.makeText(ActivityUserProfile.this, "ParseError", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_app_tutorial, menu);
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

    public void selectBirthDate(View view)
    {


        dpd.setMaxDate(Calendar.getInstance());

        dpd.setAccentColor(Color.parseColor("#0066B3"));
        dpd.show(getFragmentManager(), "Pick Date");

        //dpd.initialize(null,2015,11,5);



        dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int month, int date)
            {
                //String dateSet = "" + date + "/" + (++month) + "/" + year;
                String dateSet = "" + year + "-" + (++month) + "-" + date;

                String dateFormat = T.parseDate(dateSet);

                updatedDateFormat = dateFormat;


                input_birth_date.setText(T.changeDateFormatDdMmYyyy(dateFormat));



            }
        });


    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {


    }
}
