package snsystems.obd.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebView;
import android.widget.Toast;

import java.util.ArrayList;

import snsystems.obd.R;
import snsystems.obd.dashboard.DashboardActivity;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.interfaces.Constants;

public class SplashScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;

    WebView webView;

    private String [] alertsName = {

            "Fuel Level",
            "Speed",
            "RPM",
            "Maintenance",
            "Service Due",
            "Subscription"
    };

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_new);

        insertUserDefinedAlerts();

        setTimerAnimation();

    }

    private void setTimerAnimation()
    {
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {

                setNavigation();

            }
        }, SPLASH_TIME_OUT);

    }

    private void setNavigation() {


        preferences = getSharedPreferences(Constants.NAVIGATION, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.commit();

        String status = preferences.getString("status","0");
        String checkedStatus = preferences.getString("checkedStatus","unchecked");

        if(status.equals("0") && checkedStatus.equals("unchecked"))
        {
            Intent i = new Intent(SplashScreen.this, CustomViewsActivity.class);
            startActivity(i);
            finish();
        }
        else
        {

            Intent i = new Intent(SplashScreen.this, DashboardActivity.class);
            startActivity(i);
            finish();
        }
    }

    private void insertUserDefinedAlerts()
    {

        boolean alerts = S.checkUserAlertsAvailablere(new DBHelper(SplashScreen.this));

        if(!alerts)
        {

            for(int i = 0; i < alertsName.length; i++)
            {
                new DBHelper(SplashScreen.this).insertUserAlerts(alertsName[i]);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}