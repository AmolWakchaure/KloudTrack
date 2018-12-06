package snsystems.obd.devicemgt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;
import snsystems.obd.classes.ConnectionStatus;
import snsystems.obd.classes.T;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.interfaces.Constants;
import snsystems.obd.userprofile.ActivityUserProfile;

public class ActivityManageDevice extends AnimationActivity {

    TextView txt_device_id, txt_active_date,txt_device_name;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle(Html.fromHtml("<large> <font color='#ffffff'>MANAGE ACCOUNT</font></large>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        txt_device_id = (TextView) findViewById(R.id.txt_device_id);
        txt_active_date = (TextView) findViewById(R.id.txt_active_date);
        txt_device_name  = (TextView) findViewById(R.id.txt_device_name);

        if (ConnectionStatus.isConnectingToInternet(getApplicationContext())) {
            sendManageAccount("Please wait...");

        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }

        txt_device_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_update_device = new Intent(getApplicationContext(), ActivityUpdateDevice.class);
                intent_update_device.putExtra("device_id",txt_device_id.getText().toString());

                startActivityForResult(intent_update_device,3);
                //startActivity(intent_update_device);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if(requestCode == 3)
        {
            if (ConnectionStatus.isConnectingToInternet(getApplicationContext()))
            {
                sendManageAccount("Refreshing...");

            }
            else
            {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void addDevice(View view) {
        Intent intent_add_device = new Intent(getApplicationContext(), ActivityAddDevice.class);

        startActivity(intent_add_device);
    }


    private void sendManageAccount(final String message)
    {

        final String device_id = S.getDeviceId(new DBHelper(ActivityManageDevice.this));

       // T.t(ActivityManageDevice.this,""+device_id);

        final KProgressHUD progressDialog = KProgressHUD.create(ActivityManageDevice.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(message)
                //.setDetailsLabel("Downloading data")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.webUrl)+""+getResources().getString(R.string.activity_manage_account),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response_date) {

                        progressDialog.dismiss();

                        //Log.e("RESPONSE",""+response_date);
                        parseManageDeviceResponse(response_date,device_id);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressDialog.dismiss();
                        //sendManageAccount(message);
                        T.t(ActivityManageDevice.this,""+error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("device_id", device_id);
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
    private void parseManageDeviceResponse(String result,String vehicle_name_form) {

        String activation_end_date = Constants.NA;
        String vehicle_name = Constants.NA;
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
                        JSONArray deviceJsonArray = tripsJsonObject.getJSONArray("devicedata");

                        JSONObject devviceJsonObject = deviceJsonArray.getJSONObject(0);

                        if(devviceJsonObject.has("activation_end_date") && !devviceJsonObject.isNull("activation_end_date"))
                        {
                            activation_end_date = devviceJsonObject.getString("activation_end_date");
                        }
                        if(devviceJsonObject.has("vehicle_name") && !devviceJsonObject.isNull("vehicle_name"))
                        {
                            vehicle_name = devviceJsonObject.getString("vehicle_name");
                        }

                        String [] dataActDate = activation_end_date.split(" ");

                        String dateFormat = T.changeDateFormatDdMmYyyy(dataActDate[0]);

                        txt_active_date.setText(dateFormat);
                        txt_device_name.setText(vehicle_name);
                        txt_device_id.setText(vehicle_name_form);
                    }
                    else
                    {

                        T.t(ActivityManageDevice.this,"No device data found.");

                    }
                }
                else
                {
                    T.t(ActivityManageDevice.this, "incorect json");
                }
            }
            else
            {
                T.t(ActivityManageDevice.this,"0 or null json");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
    }


//    private void parseManageDeviceResponse(String result, String vehicle_name)
//    {
//        try {
//            JSONArray jsonArray = new JSONArray(response_date);
//            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
//
//            String end_date = jsonObject.getString("end_date");
//            Log.d(">>", "date>>" + end_date);
//
//            txt_active_date.setText(end_date);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void handleVolleyerrorProgressNew(VolleyError error) {
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {

            Toast.makeText(ActivityManageDevice.this, "Oops!Server Timeout Please refresh", Toast.LENGTH_LONG).show();

        } else if (error instanceof AuthFailureError) {

            Toast.makeText(ActivityManageDevice.this, "AuthFailureError", Toast.LENGTH_LONG).show();

        } else if (error instanceof ServerError) {

            Toast.makeText(ActivityManageDevice.this, "ServerError", Toast.LENGTH_LONG).show();

        } else if (error instanceof NetworkError) {

            Toast.makeText(ActivityManageDevice.this, "NetworkError", Toast.LENGTH_LONG).show();

        } else if (error instanceof ParseError) {

            Toast.makeText(ActivityManageDevice.this, "ParseError", Toast.LENGTH_LONG).show();

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



}
