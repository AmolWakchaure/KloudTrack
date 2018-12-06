
package snsystems.obd.notificationalerts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;
import snsystems.obd.classes.MyApplication;
import snsystems.obd.classes.T;
import snsystems.obd.dashboard.DashboardActivity;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;

public class DisplayNotificationDetailsActivity extends AnimationActivity
{

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.notification_alerts_details)
    RecyclerView notification_alerts_details;

    private NotificationAdapter
            notificationAdapter;

    private ArrayList<NotificationInformation> NOTIFICATION_INFO = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_notification_details);

        initialize();

        //getNotificationData();

        setNotificationData();



    }

    private void setNotificationData()
    {

        String device_id_mail = S.getDeviceIdUserName(MyApplication.db);

        String [] data = device_id_mail.split("#");

        ArrayList<NotificationInformation> alertss = S.getNotificationDashAlerts(MyApplication.db,data[0]);
        if(!alertss.isEmpty())
        {
            notificationAdapter.setAlertData(alertss);
            notificationAdapter.notifyDataSetChanged();

        }

    }

    private void getNotificationData() {

        Intent ii = getIntent();
        ArrayList<String> notification_list = ii.getStringArrayListExtra("notification_list");


        if(!notification_list.isEmpty())
        {
            //NOTIFICATION_DATA.add(fuel_alertname+"#"+fuel_level+"#"+fuel_data);
            for(int i = 0; i < notification_list.size(); i++)
            {
                String [] notiString = notification_list.get(i).split("#");

                NotificationInformation notificationInformation = new NotificationInformation();

                notificationInformation.setNotificationName(notiString[0]);
                notificationInformation.setNotificationLevel(notiString[1]);
                notificationInformation.setNotificationDateTime(notiString[2]);

                NOTIFICATION_INFO.add(notificationInformation);


            }
            notificationAdapter.setAlertData(NOTIFICATION_INFO);
            notificationAdapter.notifyDataSetChanged();


        }
    }

    private void initialize()
    {

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        notification_alerts_details.setLayoutManager(new LinearLayoutManager(DisplayNotificationDetailsActivity.this));
        notificationAdapter = new NotificationAdapter(DisplayNotificationDetailsActivity.this);
        notification_alerts_details.setAdapter(notificationAdapter);
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
