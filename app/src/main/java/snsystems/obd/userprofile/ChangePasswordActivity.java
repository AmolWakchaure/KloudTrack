package snsystems.obd.userprofile;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;
import snsystems.obd.activity.FeedbackActivity;
import snsystems.obd.classes.ConnectionStatus;
import snsystems.obd.classes.T;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.interfaces.Constants;
import snsystems.obd.performancedash.PerformanceDashboardActivity;

/**
 * Created by sns003 on 12/29/2016.
 */
public class ChangePasswordActivity extends AnimationActivity {


    @Bind(R.id.toolbar)
    Toolbar toolbar;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private DBHelper db;
    EditText input_new_password,input_old_passwrd,input_confirm_password;
    TextInputLayout inputLayoutOldPassword,inputLayoutNewPassword,inputLayoutConfirmPassword;
    String old_password,new_password,confirm_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_passwrd);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        db = new DBHelper(ChangePasswordActivity.this);
        prefs = this.getSharedPreferences("com", Context.MODE_PRIVATE);
        editor = prefs.edit();

        init();

//        getSupportActionBar().setTitle(Html.fromHtml("<large> <font color='#ffffff'>CHANGE PASSWORD</font></large>"));
//        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#0066B3"));
//        getSupportActionBar().setBackgroundDrawable(colorDrawable);
//        getSupportActionBar().setHomeButtonEnabled(true);

    }

    private void init() {
        inputLayoutOldPassword = (TextInputLayout) findViewById(R.id.inputLayout1);
        inputLayoutNewPassword = (TextInputLayout) findViewById(R.id.inputLayout2);
        inputLayoutConfirmPassword = (TextInputLayout) findViewById(R.id.inputLayout3);
        input_old_passwrd = (EditText) findViewById(R.id.input_old_passwrd);
        input_new_password = (EditText) findViewById(R.id.input_new_password);
        input_confirm_password = (EditText) findViewById(R.id.input_confirm_password);
    }
    private void submitForm() {
        old_password = input_old_passwrd.getText().toString().trim();
        new_password = input_new_password.getText().toString().trim();
        confirm_password = input_confirm_password.getText().toString().trim();

        if (!validateOldPassword()) {
            return;
        }
        if (!validateNewPassword()) {
            return;
        }
        if (!validateConfirmPassword()) {
            return;
        }
        if (ConnectionStatus.isConnectingToInternet(getApplicationContext())) {
            changePassword();
        } else {
          //  displayFailureMessage(ChangePasswordActivity.this, "Oops...", "OK", "Something went wrong!\nPlease Try Again..");
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }

    }
    private boolean validateOldPassword() {
        if (old_password.isEmpty()) {
            inputLayoutOldPassword.setError(getString(R.string.err_msg_password));
            requestFocus(input_old_passwrd);
            return false;
        } else {
            inputLayoutOldPassword.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validateNewPassword() {
        if (new_password.isEmpty()) {
            inputLayoutNewPassword.setError(getString(R.string.err_msg_password));
            requestFocus(input_new_password);
            return false;
        } else {
            inputLayoutNewPassword.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validateConfirmPassword() {
        if ( confirm_password.isEmpty() || !new_password.equals(confirm_password)) {
            inputLayoutConfirmPassword.setError(getString(R.string.err_msg_confirm_pass));
            requestFocus(input_confirm_password);
            return false;
        } else {
            inputLayoutConfirmPassword.setErrorEnabled(false);
        }

        return true;
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    private void changePassword()
    {
        String device_id_mail = S.getDeviceIdUserName(new DBHelper(ChangePasswordActivity.this));
        final String [] data = device_id_mail.split("#");


        final KProgressHUD progressDialog = KProgressHUD.create(ChangePasswordActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Changing password...")
                //.setDetailsLabel("Downloading data")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.webUrl)+""+getResources().getString(R.string.activity_change_password_url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {

                        progressDialog.dismiss();

                       // Log.e("ResponseResponse", "Response :" + response.toString());

                        parseResponse(response);

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {

                        progressDialog.dismiss();
                        //handleVolleyerrorProgressNew(error);
                      //Log.d(">>", "Error :" + error.toString());
                        new SweetAlertDialog(ChangePasswordActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setConfirmText("Try again")
                                .setContentText(""+error)
                                .setCancelText("Cancel")
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

                                        changePassword();
                                    }
                                })
                                .show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("email_id", data[1]);
                params.put("old_password", old_password);
                params.put("new_password",new_password);

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
                        input_old_passwrd.setText("");
                        input_new_password.setText("");
                        input_confirm_password.setText("");


                        new SweetAlertDialog(ChangePasswordActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Success")
                                .setConfirmText("Ok")
                                .setContentText("Password successfully change.")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                                {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog)
                                    {
                                        sDialog.dismissWithAnimation();

                                        finish();

                                    }
                                })
                                .show();


                    }
                    else
                    {
                        T.displayErrorMessage(ChangePasswordActivity.this,"Fail","OK","Unable to change your password.");
                    }
                }
                else
                {
                    T.t(ChangePasswordActivity.this, "incorect json");
                }
            }
            else
            {
                T.t(ChangePasswordActivity.this,"0 or null json");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
    }


    public void handleVolleyerrorProgressNew(VolleyError error) {
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {

            Toast.makeText(ChangePasswordActivity.this, "Oops!Server Timeout Please refresh", Toast.LENGTH_LONG).show();

        } else if (error instanceof AuthFailureError) {

            Toast.makeText(ChangePasswordActivity.this, "AuthFailureError", Toast.LENGTH_LONG).show();

        } else if (error instanceof ServerError) {

            Toast.makeText(ChangePasswordActivity.this, "ServerError", Toast.LENGTH_LONG).show();

        } else if (error instanceof NetworkError) {

            Toast.makeText(ChangePasswordActivity.this, "NetworkError", Toast.LENGTH_LONG).show();

        } else if (error instanceof ParseError) {

            Toast.makeText(ChangePasswordActivity.this, "ParseError", Toast.LENGTH_LONG).show();

        }
    }
    public void change_pwd_submit(View view) {
        submitForm();

    }
    public void change_pwd_cancel(View view) {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_change_pass, menu);
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
        //select_done

        return super.onOptionsItemSelected(item);
    }
}
