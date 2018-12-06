package snsystems.obd.sos;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Toast;

import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;
import snsystems.obd.classes.T;

public class SendSmsActivity extends AnimationActivity
{

    String [] numbers = {"+918806283610","+918806283610","+918806283610"};
    String [] message = {"first message","second message","third message"};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_ams);

        //checkPermission();
    }

    private void checkPermission() {

        if ( T.checkPermission(SendSmsActivity.this, Manifest.permission.READ_PHONE_STATE) )
        {


        }

        else T.askPermission(SendSmsActivity.this,Manifest.permission.READ_PHONE_STATE);

    }

    public void sendSms(View view)
    {

        //


            if ( T.checkPermission(SendSmsActivity.this, Manifest.permission.SEND_SMS) )
            {

                for(int i = 0; i < numbers.length; i++)
                {
                    Intent intent=new Intent(getApplicationContext(),SendSmsActivity.class);
                    PendingIntent pi=PendingIntent.getActivity(getApplicationContext(), 0, intent,0);

                    SmsManager sms= SmsManager.getDefault();
                    sms.sendTextMessage(numbers[i], null, message[i], pi,null);

                    Toast.makeText(getApplicationContext(), "Message Sent successfully!", Toast.LENGTH_LONG).show();
                }

            }

            else T.askPermission(SendSmsActivity.this,Manifest.permission.SEND_SMS);





    }

}
