package snsystems.obd.sos;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import snsystems.obd.R;
import snsystems.obd.activity.CustomViewsActivity;
import snsystems.obd.activity.FeedbackActivity;
import snsystems.obd.classes.T;
import snsystems.obd.classes.VolleyResponseClass;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.interfaces.Constants;
import snsystems.obd.interfaces.VolleyCallback;


public class MessageActivity extends AppCompatActivity
{

    private SharedPreferences smstextPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        checkContactsExixts();
    }


    private void checkPermission()
    {

        ArrayList<String> sosContacts = S.getSosContact(new DBHelper(MessageActivity.this));

        smstextPreferences = getSharedPreferences(Constants.SMS_TEXT, 0);
        SharedPreferences.Editor editor = smstextPreferences.edit();
        editor.commit();


        String vehicle_number = smstextPreferences.getString("vehicle_number","");
        String latitude = smstextPreferences.getString("latitude","");
        String longitude = smstextPreferences.getString("longitude","");
        String address = smstextPreferences.getString("address","");
        String name = smstextPreferences.getString("name","");
        String mobile = smstextPreferences.getString("mobile","");

        String smsText = "Vehicle Number : "+vehicle_number+",Mobile:"+mobile+",Person Name: "+name+",LatLong: "+latitude+","+longitude+","+address+"";

        if ( T.checkPermission(MessageActivity.this, Manifest.permission.SEND_SMS) )
        {

//            Intent intent =new Intent();
//            PendingIntent pi=PendingIntent.getActivity(getApplicationContext(), 0, intent,0);
//
//            SmsManager sms= SmsManager.getDefault();
//            sms.sendTextMessage("+918806283610", null, smsText, pi,null);
//
//            Toast.makeText(getApplicationContext(), "Message Sent successfully!", Toast.LENGTH_LONG).show();

            for(int i = 0; i < sosContacts.size(); i++)
            {
                Intent intent =new Intent();
                PendingIntent pi=PendingIntent.getActivity(getApplicationContext(), 0, intent,0);

                SmsManager sms= SmsManager.getDefault();
                sms.sendTextMessage(sosContacts.get(i), null, smsText, pi,null);

                Toast.makeText(getApplicationContext(), "Message Sent successfully!", Toast.LENGTH_LONG).show();
            }
        }

        else T.askPermission(MessageActivity.this,Manifest.permission.SEND_SMS);
    }

    private void getPermission()
    {

        new SweetAlertDialog(MessageActivity.this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Title")
                .setConfirmText("Yes")
                .setCancelText("No")
                .setContentText("Do you want to send sms.")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                {
                    @Override
                    public void onClick(SweetAlertDialog sDialog)
                    {

                        //checkContactsExixts();

                        //sendSmsFromDevice();
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


        String device_id_mail = S.getDeviceIdUserName(new DBHelper(MessageActivity.this));

        String [] data = device_id_mail.split("#");

        boolean c = T.checkConnection(MessageActivity.this);

        if(c)
        {
            String [] parameters =
                    {
                            "device_id"+"#"+data[0],
                            "username"+"#"+data[1]
                    };
            VolleyResponseClass.getResponseProgressDialog(
                    new VolleyCallback() {
                        @Override
                        public void onSuccess(String result)
                        {
                           // Log.e("VolleyResponse", "" + result);
                            //{"status":1,"success":"Success! Alert added successfully."}

                            parseResponse(result);


                        }
                    },
                    MessageActivity.this,
                    getResources().getString(R.string.webUrl)+""+getResources().getString(R.string.sendSosSignalTowebapp),
                    new EditText(MessageActivity.this),
                    parameters,
                    "Sending sos...");
        }

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

                       finish();
                    }
                }
                else
                {
                    T.t(MessageActivity.this,"incorect json");
                }
            }
            else
            {
                T.t(MessageActivity.this,"0 or null json");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
    }

    private void checkContactsExixts()
    {

        boolean status  = S.checkLogin(new DBHelper(MessageActivity.this));

                    if(status)
                    {

                        checkPermission();

                        boolean c = T.checkConnection(MessageActivity.this);
                        if(c)
                        {
                            sendSignalToWebApp();
                        }
                        else
                        {
                            T.t(MessageActivity.this,"Network connection off. Message will send from device wait");
                        }

                    }
                    else
                    {
                        displayDialog(
                                "Oops...",
                                "Ok",
                                "Close",
                                "No Sos contacts found, you need to login first.");
                        //T.displaySuccessMessage(MessageActivity.this, "Success", "Close", "SMS successfully send");
                    }
    }



    private void displayDialog(String title,String confirmText,String cancelText,String contentText)
    {
        //
        new SweetAlertDialog(MessageActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setConfirmText(confirmText)
                .setCancelText(cancelText)
                .setContentText(contentText)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();

                        Intent i = new Intent(MessageActivity.this, CustomViewsActivity.class);
                        startActivity(i);
                        finish();


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
    private void displayDialog(String title,String confirmText,String contentText)
    {
        //
        new SweetAlertDialog(MessageActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(title)
                .setConfirmText(confirmText)

                .setContentText(contentText)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();

                        finish();

                    }
                })

                .show();

    }


}
