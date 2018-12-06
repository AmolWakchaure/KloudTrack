package snsystems.obd.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
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


import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import snsystems.obd.R;
import snsystems.obd.classes.ConnectionStatus;
import snsystems.obd.classes.T;
import snsystems.obd.classes.Validations;
import snsystems.obd.dashboard.DashboardActivity;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.health.VehicleHealthActivity;
import snsystems.obd.interfaces.Constants;
import snsystems.obd.sos.SubmitSosContactActivity;
import snsystems.obd.tutorial.AppTutorialActivity;

public class CustomViewsActivity extends AnimationActivity {

    TextView txt_forgot_pwsrd, txt_terms_condition;
    EditText input_email, input_password, reg_email, reg_name, reg_password,
            reg_confirm_password, reg_mobile, device_id;
    Button btn_login, btn_register,register_cancel;
    TextInputLayout inputLayoutEmail, inputLayoutPassword, inputLayoutEmail_reg,
            inputLayoutConfirm_pass_reg, inputLayoutName_reg, inputLayoutPassword_reg,
            inputLayoutMobile_reg, inputLayoutdevice_Id;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private DBHelper db;
    CheckBox checkbox_password;
    String password, input_device_id, user_name, reg_mail_id, mobile_no,
            email, login_password;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_form);

        db = new DBHelper(CustomViewsActivity.this);
        prefs = this.getSharedPreferences("com", Context.MODE_PRIVATE);
        editor = prefs.edit();

        hideSoftKeyboard();
        init();

        checkPermission();

       // managePreferences();

        btn_login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

               // Log.e("CLICKEDD","clicked...");
                submitForm();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                submitForm_register();
            }
        });
        register_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                reg_name.setText("");
                reg_email.setText("");
                startActivity(new Intent(CustomViewsActivity.this, CustomViewsActivity.class));
                finish();
            }
        });

        setClickListener();


    }

   private void checkPermission()
   {
       if ( T.checkPermission(CustomViewsActivity.this, Manifest.permission.READ_CONTACTS) )
       {

       }

       else T.askPermission(CustomViewsActivity.this,Manifest.permission.READ_CONTACTS);
   }

    private void managePreferences()
    {

        if (prefs.getBoolean("checkbox_password", false))
        {
            if (prefs.getBoolean("splash", false))
            {
                //Log.d("", ">>>>> in splash checkbox");
                Intent intent_splash = new Intent(CustomViewsActivity.this, DashboardActivity.class);
                startActivity(intent_splash);
                finish();
            }
            if (prefs.getBoolean("logout", false))
            {
                //Log.d("", ">>>>> in logout checkbox");
                editor.putBoolean("logout", false).commit();
                input_email.setText(prefs.getString("email_login", ""));
                input_password.setText(prefs.getString("password_login", ""));
            }
            checkbox_password.setChecked(true);
//            Log.d("", ">>>>> in if chk box");
//            Log.d("", "uname" + prefs.getString("email_login", ""));
//            Log.d("", "uname" + prefs.getString("password_login", ""));

        }
        else
        {
            checkbox_password.setChecked(false);
            // Log.d("", ">>>>> in else chk box");
        }
        editor.putBoolean("splash", false).commit();
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void loadPermissions() {
        // Check if the READ_PHONE_STATE permission is already available.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // READ_PHONE_STATE permission has not been granted.
            requestReadPhoneStatePermission();
        } else {
            // READ_PHONE_STATE permission is already been granted.
            doPermissionGrantedStuffs();
        }
    }

    /**
     * Requests the READ_PHONE_STATE permission.
     * If the permission has been denied previously, a dialog will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestReadPhoneStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            new AlertDialog.Builder(CustomViewsActivity.this)
                    .setTitle("Permission Request")
                    .setMessage("Read phoneState")
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //re-request
                            ActivityCompat.requestPermissions(CustomViewsActivity.this,
                                    new String[]{Manifest.permission.READ_PHONE_STATE},
                                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                        }
                    })
                    .setIcon(R.drawable.passwrd_icon)
                    .show();
        } else {
            // READ_PHONE_STATE permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            // Received permission result for READ_PHONE_STATE permission.est.");
            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // READ_PHONE_STATE permission has been granted, proceed with displaying IMEI Number
                //alertAlert(getString(R.string.permision_available_read_phone_state));
                doPermissionGrantedStuffs();
            } else {
                alertAlert("rrdeorie");
            }
        }
    }

    private void alertAlert(String msg) {
        new AlertDialog.Builder(CustomViewsActivity.this)
                .setTitle("Permission Request")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do somthing here
                    }
                })
                .setIcon(R.drawable.device_id)
                .show();
    }


    public void doPermissionGrantedStuffs() {
        //Have an  object of TelephonyManager
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //Get IMEI Number of Phone  //////////////// for this example i only need the IMEI
//        String IMEINumber=tm.getDeviceId();
        String android_Number = Settings.Secure.getString(getApplicationContext()
                .getContentResolver(), Settings.Secure.ANDROID_ID + "");

        editor.putString("Android_number", android_Number).commit();
        Toast.makeText(getApplicationContext(), "NO." + android_Number, Toast.LENGTH_LONG).show();
        Log.d(">>>", ">>>" + android_Number);
    }

    private void setClickListener() {
        device_id.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    input_device_id = device_id.getText().toString().trim();
                    if (!validateDevice()) {
                        return;
                    }
                    try {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.webUrl)+""+getResources().getString(R.string.check_device_id_url),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            if (response != null || response.length() > 0) {
                                                Object json = new JSONTokener(response).nextValue();
                                                if (json instanceof JSONObject) {
                                                    JSONObject jsonResponse = new JSONObject(response);
                                                    String successString = jsonResponse.getString("status");
                                                    Log.e(">>>>response_device", "successString" + successString);

                                                    if (successString.equals("0")) {
                                                        // device_id.setError("This Device ID is already registered.");
                                                        displayFailureMessage(CustomViewsActivity.this, "Oops...", "OK", "This Device ID is already registered...");
                                                    } else if (successString.equals("3")) {
                                                        displayFailureMessage(CustomViewsActivity.this, "Oops...", "OK", "This Device ID does not exits...");

                                                    }


                                                } else {
                                                    Toast.makeText(getApplicationContext(), "incorect json", Toast.LENGTH_LONG).show();

                                                }
                                            } else {
                                                Toast.makeText(CustomViewsActivity.this, "0 or null json", Toast.LENGTH_SHORT).show();
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(">>", "ERROR :" + error.toString());
                                        handleVolleyerrorProgressNew(error);

                                    }
                                })

                        {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("rg_device_id", input_device_id);
                                Log.d(">>>", ">>ondeviceID_editext " + input_device_id);
                                return params;
                            }

                        };
                        RequestQueue requestQueue = Volley.newRequestQueue(CustomViewsActivity.this);
                        requestQueue.getCache().clear();
                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        requestQueue.add(stringRequest);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }


    private void init() {
        //Login fields
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.TextInputLayout1);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.TextInputLayout2);
        txt_forgot_pwsrd = (TextView) findViewById(R.id.txt_forgot_pwsrd);
        txt_terms_condition = (TextView) findViewById(R.id.txt_terms_condition);
        input_email = (EditText) findViewById(R.id.input_email);
        input_password = (EditText) findViewById(R.id.input_password);

        checkbox_password = (CheckBox) findViewById(R.id.checkbox_password);
        //Register fields
        inputLayoutName_reg = (TextInputLayout) findViewById(R.id.inputLayoutName_reg);
        inputLayoutEmail_reg = (TextInputLayout) findViewById(R.id.inputLayoutEmail_reg);
        inputLayoutdevice_Id = (TextInputLayout) findViewById(R.id.inputLayoutdevice_Id);
        inputLayoutPassword_reg = (TextInputLayout) findViewById(R.id.inputLayoutPassword_reg);
        inputLayoutConfirm_pass_reg = (TextInputLayout) findViewById(R.id.inputLayoutConfirm_pass_reg);
        inputLayoutMobile_reg = (TextInputLayout) findViewById(R.id.inputLayoutMobile_reg);
        reg_name = (EditText) findViewById(R.id.input_firstname);
        reg_email = (EditText) findViewById(R.id.reg_email);
        device_id = (EditText) findViewById(R.id.device_id);
        reg_password = (EditText) findViewById(R.id.reg_password);
        reg_confirm_password = (EditText) findViewById(R.id.reg_confirm_password);
        reg_mobile = (EditText) findViewById(R.id.input_mobile);

        btn_login = (Button) findViewById(R.id.btn_login_login);
        btn_register = (Button) findViewById(R.id.btn_register);
        register_cancel = (Button)findViewById(R.id.register_cancel);

    }


    private void submitForm() {

        //Login
        email = input_email.getText().toString().trim();
        login_password = input_password.getText().toString().trim();

        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }
        if (ConnectionStatus.isConnectingToInternet(CustomViewsActivity.this))
        {
            loginUser();
        }
        else
        {
            displayFailureMessage(CustomViewsActivity.this, "Oops...", "OK", "Network connection off");
            //Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }

    }

    private void submitForm_register() {
        //Register
        user_name = reg_name.getText().toString().trim();
        reg_mail_id = reg_email.getText().toString().trim();
        input_device_id = device_id.getText().toString().trim();
        password = reg_password.getText().toString().trim();
        mobile_no = reg_mobile.getText().toString().trim();

        if (!validateName())
        {
            return;
        }
        if (!validateEmailReg())
        {
            return;
        }
        if (!validateDevice())
        {
            return;
        }
        if (!validatePasswordReg())
        {
            return;
        }
        if (!validatePasswordConfirm())
        {
            return;
        }
        if (!validateMobileNo()) {
            return;
        }
        if (!Validations.validateMobileLength(reg_mobile,"Invalid mobile number",inputLayoutMobile_reg,CustomViewsActivity.this))
        {
            return;
        }

        //T.t(CustomViewsActivity.this,"Success");

        if (ConnectionStatus.isConnectingToInternet(getApplicationContext())) {
            registerUser();

        }
        else
        {
            displayFailureMessage(CustomViewsActivity.this, "Oops...", "OK", "Network connection off");

        }
    }

    private boolean validateName() {

        if (user_name.isEmpty()) {
            inputLayoutName_reg.setError(getString(R.string.err_msg_name));
            requestFocus(reg_name);
            return false;
        } else {
            inputLayoutName_reg.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateEmail() {

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(input_email);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword() {
        if (login_password.isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(input_password);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateDevice() {
        if (input_device_id.isEmpty()) {
            inputLayoutdevice_Id.setError(getString(R.string.err_msg_device_id));
            //requestFocus(device_id);
            return false;
        } else {
            inputLayoutdevice_Id.setErrorEnabled(false);
        }
        return true;

    }

    private boolean validateEmailReg() {

        if (reg_mail_id.isEmpty() || !isValidEmail(reg_mail_id)) {
            inputLayoutEmail_reg.setError(getString(R.string.err_msg_email));
            requestFocus(reg_email);
            return false;
        } else {
            inputLayoutEmail_reg.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePasswordReg() {
        if (password.isEmpty()) {
            inputLayoutPassword_reg.setError(getString(R.string.err_msg_password));
            requestFocus(reg_password);
            return false;
        } else {
            inputLayoutPassword_reg.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePasswordConfirm() {
        String passRep = reg_confirm_password.getText().toString().trim();
        if (passRep.isEmpty() || !password.equals(passRep)) {
            // reg_confirm_password.setError("Passwords are different");
            inputLayoutConfirm_pass_reg.setError(getString(R.string.err_msg_confirm_pass));
            requestFocus(reg_confirm_password);
            return false;
        } else {
            inputLayoutConfirm_pass_reg.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateMobileNo() {
        if (mobile_no.isEmpty() || !isValidMobile(mobile_no)) {
            inputLayoutMobile_reg.setError(getString(R.string.err_msg_mobileNo));
            requestFocus(reg_mobile);
            return false;
        } else {
            inputLayoutMobile_reg.setErrorEnabled(false);
        }
        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    private void registerUser() {
        try {
            final KProgressHUD progressDialog = KProgressHUD.create(CustomViewsActivity.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Registering...")
                    //.setDetailsLabel("Downloading data")
                    .setCancellable(true)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.webUrl)+""+getResources().getString(R.string.activity_register_url),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            parseRegister(response);
//                            Log.e(">>>response", "response" + response);
                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                            Log.d(">>", "ERROR :" + error.toString());
                            progressDialog.dismiss();
                            handleError(error,"register");

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("rg_name", user_name);
                    params.put("rg_email", reg_mail_id);
                    params.put("rg_device_id", input_device_id);
                    params.put("rg_password", password);
                    params.put("rg_mobile_no", mobile_no);
                    //params.put("Android_number", prefs.getString("Android_number", ""));
                   // Log.d(">>", ">>>Send to server " + prefs.getString("Android_number", ""));
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

    private void parseRegister(String response) {
        try {
            if (response != null || response.length() > 0) {
                Object json = new JSONTokener(response).nextValue();
                Log.d(">>>response_json", "json" + json);
                if (json instanceof JSONObject) {
                    JSONObject jsonResponse = new JSONObject(response);
                    String successString = jsonResponse.getString("status");
                    Log.d(">>>response_sucess", "success" + successString);

                    if (successString.equals("1")) {



                        String date = jsonResponse.getString("activation_start_date");
                       // db.registerUser(user_name, reg_mail_id, input_device_id, password, mobile_no);
                        editor.putString("emailId", reg_mail_id).commit();


                        new SweetAlertDialog(CustomViewsActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Success")
                                .setConfirmText("ok")
                                .setContentText("Registered successfully.\nActivation Date :" + date)

                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();

                                        startActivity(new Intent(CustomViewsActivity.this, CustomViewsActivity.class));
                                        finish();


                                    }
                                })
                                .show();

                        reg_name.setText("");
                        reg_email.setText("");
                        device_id.setText("");
                        reg_password.setText("");
                        reg_confirm_password.setText("");
                        reg_mobile.setText("");

                    } else if (successString.equals("2")) {
                        displayFailureMessage(CustomViewsActivity.this, "Oops...", "OK", "The email address or the device Id you have entered is already registered !\n Please Try Again..");

                    } else {
                        displayFailureMessage(CustomViewsActivity.this, "Oops...", "OK", "Something went wrong!\nPlease Try Again..");
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Incorect json", Toast.LENGTH_LONG).show();

                }
            } else {
                Toast.makeText(CustomViewsActivity.this, "0 or Null json", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestFocus(View view)
    {
        if (view.requestFocus())
        {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void loginUser()
    {

        /*

         */
        final KProgressHUD progressDialog = KProgressHUD.create(CustomViewsActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Authenticating...")
                //.setDetailsLabel("Downloading data")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.webUrl)+""+getResources().getString(R.string.activity_login_url),
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response_login)
                    {

                        progressDialog.dismiss();
                       // Log.e("Login_response", "response_login" + response_login);
                        parseLogin(response_login);

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {

                        progressDialog.dismiss();
                        //handleVolleyerrorProgressNew(error);
                        handleError(error,"login");
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email_id", email);
                params.put("password", login_password);
                params.put("Android_number", prefs.getString("Android_number", ""));
                editor.putString("emailId", email).commit();
                //Log.d(">>", ">>>Send to server Android_number_login " + prefs.getString("Android_number", ""));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    private void handleError(VolleyError error,String status)
    {
        try
        {

            if(error instanceof TimeoutError || error instanceof NoConnectionError)
            {

                displayError("TimeoutError/NoConnectionError","Server not responding or no connection.",status);


            }
            else if(error instanceof AuthFailureError)
            {

                displayError("AuthFailureError","Remote server returns (401) Unauthorized?.",status);

            }
            else if(error instanceof ServerError)
            {


                displayError("ServerError","Wrong webservice call or wrong webservice url.",status);

            }
            else if (error instanceof NetworkError)
            {
                displayError("NetworkError","you doesn't have a data connection and wi-fi Connection.",status);

            }
            else if(error instanceof ParseError)
            {

                displayError("NetworkError","Incorrect json response.",status);
            }



        }
        catch (Exception e)
        {

        }

    }

    private void displayError(String title, String error, final String statusFlag)
    {

        new SweetAlertDialog(CustomViewsActivity.this, SweetAlertDialog.ERROR_TYPE)
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

                        if(statusFlag.equals("login"))
                        {
                            loginUser();
                        }
                        else if(statusFlag.equals("register"))
                        {
                            submitForm_register();
                        }
                        else if(statusFlag.equals("forgot"))
                        {
                            forgetPassword();
                        }

                    }
                })
                .show();

    }

    private void parseLogin(String response_login) {

        String email_id = Constants.NA;
        String password = Constants.NA;
        String customer_name = Constants.NA;
        String customer_contact_no = Constants.NA;

        String device_id = Constants.NA;
        String vehicle_name = Constants.NA;

        String full_name1 = Constants.NA;
        String contact_number1 = Constants.NA;
        String  full_name2 = Constants.NA;
        String  contact_number2 = Constants.NA;
        String  full_name3 = Constants.NA;
        String  contact_number3 = Constants.NA;
        String  full_name4 = Constants.NA;
        String  contact_number4 = Constants.NA;
        try {
            if (response_login != null || response_login.length() > 0) {
                Object json = new JSONTokener(response_login).nextValue();
                if (json instanceof JSONObject) {
                    JSONObject jsonResponse = new JSONObject(response_login);
                    String successString = jsonResponse.getString("status");

                    if (successString.equals("1"))
                    {

                        JSONArray userdataJsonArray = jsonResponse.getJSONArray("userdata");
                        JSONArray device_dataJsonArray = jsonResponse.getJSONArray("device_data");
                        JSONArray sosArray = jsonResponse.getJSONArray("sos_data");

                        JSONObject userdataJsonObject = userdataJsonArray.getJSONObject(0);

                        if(userdataJsonObject.has("email_id") && !userdataJsonObject.isNull("email_id"))
                        {
                            email_id = userdataJsonObject.getString("email_id");
                        }
                        if(userdataJsonObject.has("password") && !userdataJsonObject.isNull("password"))
                        {
                            password = userdataJsonObject.getString("password");
                        }
                        if(userdataJsonObject.has("customer_name") && !userdataJsonObject.isNull("customer_name"))
                        {
                            customer_name = userdataJsonObject.getString("customer_name");
                        }
                        if(userdataJsonObject.has("customer_contact_no") && !userdataJsonObject.isNull("customer_contact_no"))
                        {
                            customer_contact_no = userdataJsonObject.getString("customer_contact_no");
                        }

                        //check register table data if then delete it  same for vehicle master

                        boolean status = S.checkLogin(new DBHelper(CustomViewsActivity.this));

                        if(status)
                        {
                            new DBHelper(CustomViewsActivity.this).clearDbTableLogout();
                        }

                        new DBHelper(CustomViewsActivity.this).registerUser(customer_name, email_id, "NA", password, customer_contact_no);

                        for(int i = 0; i < device_dataJsonArray.length(); i++)
                        {
                            JSONObject device_dataJsonObject = device_dataJsonArray.getJSONObject(i);

                            if(device_dataJsonObject.has("device_id") && !device_dataJsonObject.isNull("device_id"))
                            {
                                device_id = device_dataJsonObject.getString("device_id");
                            }
                            if(device_dataJsonObject.has("vehicle_name") && !device_dataJsonObject.isNull("vehicle_name"))
                            {
                                vehicle_name = device_dataJsonObject.getString("vehicle_name");
                            }
                            
                            new DBHelper(CustomViewsActivity.this).insertVehicleMaster(vehicle_name,device_id);
                            new DBHelper(CustomViewsActivity.this).updateDeviceID(device_id,email_id);
//
//                            if(i == 0)
//                            {

                            //}
                        }

//                        if (checkbox_password.isChecked())
//                        {
//
//                            editor.putBoolean("checkbox_password", true).commit();
//                            editor.putString("password_login", input_password.getText().toString()).commit();
//                        }
//                        else
//                        {
//                            editor.putBoolean("checkbox_password", false).commit();
//                            editor.remove("password_login").commit();
//                        }


                        String checkedStatus,staus;
                        if (checkbox_password.isChecked())
                        {
                            checkedStatus = "checked";
                            staus = "login";

                        }
                        else
                        {
                            checkedStatus = "unchecked";
                            staus = "0";
                        }


                        SharedPreferences preferences = getSharedPreferences(Constants.NAVIGATION, 0);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("status",staus);
                        editor.putString("checkedStatus",checkedStatus);
                        editor.commit();

                        if(sosArray.length() == 0)
                        {
                            Intent intent = new Intent(CustomViewsActivity.this, SubmitSosContactActivity.class);
                            intent.putExtra("STATUS","notview");
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            //full_name1,
                            // contact_number1,
                            // full_name2,
                            // contact_number2,
                            // full_name3,
                            // contact_number3,
                            // full_name4,
                            // contact_number4;

                            JSONObject jsonObject = sosArray.getJSONObject(0);

                            if(jsonObject.has("full_name1") && !jsonObject.isNull("full_name1"))
                            {
                                full_name1 = jsonObject.getString("full_name1");
                            }
                            if(jsonObject.has("contact_number1") && !jsonObject.isNull("contact_number1"))
                            {
                                contact_number1 = jsonObject.getString("contact_number1");
                            }


                            if(jsonObject.has("full_name2") && !jsonObject.isNull("full_name2"))
                            {
                                full_name2 = jsonObject.getString("full_name2");
                            }
                            if(jsonObject.has("contact_number2") && !jsonObject.isNull("contact_number2"))
                            {
                                contact_number2 = jsonObject.getString("contact_number2");
                            }


                            if(jsonObject.has("full_name3") && !jsonObject.isNull("full_name3"))
                            {
                                full_name3 = jsonObject.getString("full_name3");
                            }
                            if(jsonObject.has("contact_number3") && !jsonObject.isNull("contact_number3"))
                            {
                                contact_number3 = jsonObject.getString("contact_number3");
                            }


                            if(jsonObject.has("full_name4") && !jsonObject.isNull("full_name4"))
                            {
                                full_name4 = jsonObject.getString("full_name4");
                            }
                            if(jsonObject.has("contact_number4") && !jsonObject.isNull("contact_number4"))
                            {
                                contact_number4 = jsonObject.getString("contact_number4");
                            }

                            if(!full_name1.equals(Constants.NA))
                            {
                                new DBHelper(CustomViewsActivity.this).addEmergencyCotacts(full_name1, contact_number1);
                            }
                            if(!full_name2.equals(Constants.NA))
                            {
                                new DBHelper(CustomViewsActivity.this).addEmergencyCotacts(full_name2, contact_number2);
                            }
                            if(!full_name3.equals(Constants.NA))
                            {
                                new DBHelper(CustomViewsActivity.this).addEmergencyCotacts(full_name3, contact_number3);
                            }
                            if(!full_name4.equals(Constants.NA))
                            {
                                new DBHelper(CustomViewsActivity.this).addEmergencyCotacts(full_name4, contact_number4);
                            }


                            Intent i = new Intent(CustomViewsActivity.this, DashboardActivity.class);
                            startActivity(i);
                            finish();
                        }



                    }
                    else if (successString.equals("0"))
                    {
                        displayFailureMessage(CustomViewsActivity.this, "Oops...", "OK", "Something went wrong!\nEither emailId or Password.\nPlease Try Again..");
                    }
                    else if (successString.equals("3"))
                    {
                        displayFailureMessage(CustomViewsActivity.this, "Oops...", "OK", "Device Not Activated.\nPlease Try Again..");
                    }
                    else if (successString.equals("2"))
                    {
                        displayFailureMessage(CustomViewsActivity.this, "Oops...", "OK", "Already logged In \nPlease Try Later..");

//                        Intent intent = new Intent(CustomViewsActivity.this, SubmitSosContactActivity.class);
//                        startActivity(intent);
//                        finish();

                    }
//            else {
//                displayFailureMessage(CustomViewsActivity.this, "Oops...", "OK", "Something went wrong!\nPlease Try Again..");
//            }

                } else {
                    Toast.makeText(CustomViewsActivity.this, "incorect json", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(CustomViewsActivity.this, "0 or Null json", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void handleVolleyerrorProgressNew(VolleyError error) {
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {

            Toast.makeText(CustomViewsActivity.this, "Oops!Server Timeout Please refresh", Toast.LENGTH_LONG).show();

        } else if (error instanceof AuthFailureError) {

            Toast.makeText(CustomViewsActivity.this, "AuthFailureError", Toast.LENGTH_LONG).show();

        } else if (error instanceof ServerError) {

            Toast.makeText(CustomViewsActivity.this, "ServerError", Toast.LENGTH_LONG).show();

        } else if (error instanceof NetworkError) {

            Toast.makeText(CustomViewsActivity.this, "NetworkError", Toast.LENGTH_LONG).show();

        } else if (error instanceof ParseError) {

            Toast.makeText(CustomViewsActivity.this, "ParseError", Toast.LENGTH_LONG).show();

        }
    }


    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }


    }

    public static void displayFailureMessage(Context context,
                                             String titleText,
                                             String confirmText,
                                             String contentText) {
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(titleText)
                .setConfirmText(confirmText)
                .setContentText(contentText)

                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();

                    }
                })
                .show();
    }

    public static void displaySuccessMessage(Context context,
                                             String titleText,
                                             String confirmText,
                                             String contentText) {
        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(titleText)
                .setConfirmText(confirmText)
                .setContentText(contentText)

                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();

                    }
                })
                .show();
    }

    public void forgot_password(View view)
    {
//        Intent intent = new Intent(CustomViewsActivity.this, ForgetPassActivity.class);
//        startActivity(intent);
       // email_id = input_email_id.getText().toString().trim();
        //input_email = (EditText) findViewById(R.id.input_email);
        email = input_email.getText().toString().trim();

        if (!validateEmail())
        {
            return;
        }
        if (ConnectionStatus.isConnectingToInternet(getApplicationContext()))
        {
            forgetPassword();
        }
        else
        {
            //displayFailureMessage(CustomViewsActivity.this, "Oops...", "OK", "Something went wrong!\nPlease Try Again..");
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }
    private void forgetPassword() {


        final KProgressHUD progressDialog = KProgressHUD.create(CustomViewsActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait...")
                //.setDetailsLabel("Downloading data")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.webUrl)+""+getResources().getString(R.string.activity_forgot_password_url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();
                        parsePassword(response);
                        Log.d(">>", "Response :" + response.toString());

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressDialog.dismiss();

                        handleError(error,"forgot");
                        //handleVolleyerrorProgressNew(error);


                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email_id", email);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    private void parsePassword(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            String successString = jsonResponse.getString("status");

            if (successString.equals("1"))
            {

                displaySuccessMessage(CustomViewsActivity.this, "Please Check", "OK", "New password is generated and sent on your emailId ");

            }
            else
            {
                displayFailureMessage(CustomViewsActivity.this, "Oops...", "OK", "Entered EmailID is not registered!\nPlease Try Again..");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void txt_terms_condition(View view) {
        Intent intent = new Intent(CustomViewsActivity.this, ActivityTermsConditions.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {

            String result = data.getStringExtra("checked_box");
           // Toast.makeText(CustomViewsActivity.this, "" + result, Toast.LENGTH_SHORT).show();
            if (result.equals("true")) {
                btn_register.setVisibility(View.VISIBLE);
                btn_register.setEnabled(true);
            }else
            {
                btn_register.setEnabled(false);
                displayFailureMessage(CustomViewsActivity.this, "Oops...Something went wrong", "OK", "As you have not accepted the terms and condition,you are not able to register,\nPlease Accept the terms and condition to register successfully.");
            }

        }
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        moveTaskToBack(true);
//        checkbox_password.setChecked(false);
//        getSharedPreferences("com",0).edit().clear().commit();
//    }


}
