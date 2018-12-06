package snsystems.obd.sos;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.getbase.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;
import snsystems.obd.classes.T;
import snsystems.obd.classes.VolleyResponseClass;
import snsystems.obd.dashboard.DashboardActivity;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.health.HealthNewActivity;
import snsystems.obd.interfaces.Constants;
import snsystems.obd.interfaces.VolleyCallback;
import snsystems.obd.interfaces.VolleyErrorCallback;

public class SendSosActivity extends AnimationActivity
{


    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.sendSosContactsRelativeLayout)
    RelativeLayout sendSosContactsRelativeLayout;



    private SharedPreferences smstextPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sos);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setClickListner();

    }

    private void setClickListner() {

        sendSosContactsRelativeLayout.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {


                if(T.isSimSupport(SendSosActivity.this))
                {
                    boolean status  = S.checkLogin(new DBHelper(SendSosActivity.this));

                    if(status)
                    {

                       // T.t(SendSosActivity.this,"Ready to send sms.");

                        checkPermissionforsms();

                        boolean c = T.checkConnection(SendSosActivity.this);
                        if(c)
                        {
                            sendSignalToWebApp();
                        }
                        else
                        {
                            T.t(SendSosActivity.this,"Network connection off. Message will send from device wait");
                        }

                    }
                    else
                    {
                        displayDialog(
                                "Oops...",
                                "Ok",
                                "Close",
                                "No Sos contacts found");
                        //T.displaySuccessMessage(MessageActivity.this, "Success", "Close", "SMS successfully send");
                    }
                }
                else
                {
                    T.displayErrorMessage(SendSosActivity.this,"Oops...","Cancel","Please, insert sim card to send SoS.");
                }

                return false;
            }
        });
    }




    private void displayDialog(String title,String confirmText,String cancelText,String contentText)
    {
        //
        new SweetAlertDialog(SendSosActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setConfirmText(confirmText)
                .setCancelText(cancelText)
                .setContentText(contentText)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();

                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        finish();

                    }
                })
                .show();

    }
    private void sendSignalToWebApp()
    {


        String device_id_mail = S.getDeviceIdUserName(new DBHelper(SendSosActivity.this));

        String [] data = device_id_mail.split("#");

        boolean c = T.checkConnection(SendSosActivity.this);

        if(c)
        {
            String [] parameters =
                    {
                            "device_id"+"#"+data[0],
                            "username"+"#"+data[1]
                    };
            VolleyResponseClass.getResponseProgressDialogError(
                    new VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {
                            // Log.e("VolleyResponse", "" + result);
                            //{"status":1,"success":"Success! Alert added successfully."}

                            parseResponseSms(result);


                        }
                    },
                    new VolleyErrorCallback() {
                        @Override
                        public void onError(VolleyError result) {

                            handleError(result);


                        }
                    },
                    SendSosActivity.this,
                    getResources().getString(R.string.webUrl) + "" + getResources().getString(R.string.sendSosSignalTowebapp),
                    new EditText(SendSosActivity.this),
                    parameters,
                    "Sending sos...");
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

        new SweetAlertDialog(SendSosActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setConfirmText("Try again")
                .setContentText(error)

                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                {
                    @Override
                    public void onClick(SweetAlertDialog sDialog)
                    {
                        sDialog.dismissWithAnimation();
                        sendSignalToWebApp();
                    }
                })
                .show();

    }


    private void parseResponseSms(String result)
    {

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


                    }
                }
                else
                {
                    T.t(SendSosActivity.this,"incorect json");
                }
            }
            else
            {
                T.t(SendSosActivity.this,"0 or null json");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
    }

    private void checkPermissionforsms()
    {

        ArrayList<String> sosContacts = S.getSosContact(new DBHelper(SendSosActivity.this));

        if(sosContacts.isEmpty())
        {
            T.t(SendSosActivity.this,"Contacts not inserted, please add sos contact");
        }
        else
        {
            smstextPreferences = getSharedPreferences(Constants.SMS_TEXT, 0);
            SharedPreferences.Editor editor = smstextPreferences.edit();
            editor.commit();


            String vehicle_number = smstextPreferences.getString("vehicle_number","");
            String latitude = smstextPreferences.getString("latitude","");
            String longitude = smstextPreferences.getString("longitude","");
            String address = smstextPreferences.getString("address","");
            String name = smstextPreferences.getString("name","");
            String mobile = smstextPreferences.getString("mobile","");

            String link = "http://maps.google.com/maps?q=loc:"+latitude+","+longitude;

            //String latLongUrl = "http://maps.google.com/maps?q=loc:"+latitude+","+longitude+"("+address.replace(" ","%20")+")";

            //String smsText = "Vehicle Number : "+vehicle_number+",Mobile:"+mobile+",Person Name: "+name+",Location: "+latLongUrl+", Address:"+address+"";
            String smsText = "Vehicle Number : "+vehicle_number+",Mobile:"+mobile+",Person Name: "+name+",Location:"+link+"";

            Log.e("smsText",""+smsText);

            if ( T.checkPermission(SendSosActivity.this, Manifest.permission.SEND_SMS) )
            {
                sendSosContactsRelativeLayout.setBackgroundResource(R.drawable.ic_sos_active);

                //Toast.makeText(getApplicationContext(), "Message Sent successfully!", Toast.LENGTH_LONG).show();

//                Intent intent =new Intent();
//                PendingIntent pi=PendingIntent.getActivity(getApplicationContext(), 0, intent,0);
//
//                SmsManager sms= SmsManager.getDefault();
//                sms.sendTextMessage("+918806283610", null, smsText, pi,null);
//
//
//                Toast.makeText(getApplicationContext(), "Message Sent successfully!", Toast.LENGTH_LONG).show();

                for(int i = 0; i < sosContacts.size(); i++)
                {


                    if(!(sosContacts.get(i).equals("") || sosContacts.get(i).equals(null) || sosContacts.get(i) == null))
                    {
                        Intent intent =new Intent();
                        PendingIntent pi=PendingIntent.getActivity(getApplicationContext(), 0, intent,0);

                        SmsManager sms= SmsManager.getDefault();
                        sms.sendTextMessage(sosContacts.get(i), null, smsText, pi,null);


                        Toast.makeText(getApplicationContext(), "Message Sent successfully!", Toast.LENGTH_LONG).show();
                    }

                }
            }

            else T.askPermission(SendSosActivity.this,Manifest.permission.SEND_SMS);
        }


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send_sos, menu);
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
