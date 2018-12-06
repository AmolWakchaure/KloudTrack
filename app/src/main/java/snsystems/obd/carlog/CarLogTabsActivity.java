package snsystems.obd.carlog;

import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;
import snsystems.obd.alerts.VehicleNotificationActivity;
import snsystems.obd.alertsnew.SystemDefinedAlertFragment;
import snsystems.obd.alertsnew.UserDefinedAlertFragment;
import snsystems.obd.classes.T;
import snsystems.obd.classes.VolleyResponseClass;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.interfaces.Constants;
import snsystems.obd.interfaces.VolleyCallback;
import snsystems.obd.trips.ViewPagerAdapter;

public class CarLogTabsActivity extends AnimationActivity implements DatePickerDialog.OnDateSetListener
{

    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;

    private RecyclerView
            carLogRecyclerView;


    private CarLogAdapterrr
            carLogAdapter;

    private LinearLayoutManager
            linearLayoutManager;

    private String [] data;


    private ArrayList<CarLogInfo> CAR_LOG_INFORMATION = new ArrayList<>();


    @Bind(R.id.notificationHideRelativeLayout)
    RelativeLayout notificationHideRelativeLayout;

    @Bind(R.id.imgHideLayout)
    ImageView imgHideLayout;

    @Bind(R.id.textViewHideLayout)
    TextView textViewHideLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_log_tabs);

        ButterKnife.bind(this);

        initialize();

        boolean c = T.checkConnection(CarLogTabsActivity.this);

        if(c)
        {
            /*
            "device_id"+"#"+"9000",
                        "log_date"+"#"+"2017-02-09"
             */

            String currentDate = T.getSystemDateTime();
            String [] dateData = currentDate.split(" ");

            String finalDate = T.parseDate(dateData[0]);

            //Log.e("DATEE",""+currentDate);
            getCarLogDetails(data[0],finalDate);
        }
        else
        {
            notificationHideRelativeLayout.setVisibility(View.VISIBLE);
            imgHideLayout.setImageResource(R.drawable.ic_cloud_off_black_48dp);
            textViewHideLayout.setText("No Connection");
        }
       // setListner();
    }
    private void getCarLogDetails(String device_id,String selectedDate)
    {

        String [] parameters =
                {
                        "device_id"+"#"+device_id,
                        "log_date"+"#"+selectedDate

                };
        VolleyResponseClass.getResponseProgressDialog(
                new VolleyCallback()
                {
                    @Override
                    public void onSuccess(String result)
                    {
                        //Log.e("RESPONSE",""+result);
                        CAR_LOG_INFORMATION = parseFeedback(result);
                        carLogAdapter.setCarLogData(CAR_LOG_INFORMATION);
                        carLogAdapter.notifyDataSetChanged();
                    }
                },
                CarLogTabsActivity.this,
                getResources().getString(R.string.webUrl)+""+getResources().getString(R.string.getCarLog),
                carLogRecyclerView,
                parameters,
                "Getting logs...");

    }
    private ArrayList<CarLogInfo> parseFeedback(String result)
    {

        String avg_speed = Constants.NA;
        String max_speed = Constants.NA;
        String no_of_halts = Constants.NA;
        String total_km = Constants.NA;
        String vehicle_on_time = Constants.NA;
        String vehicle_off_time = Constants.NA;

        ArrayList<CarLogInfo> CAR  = new ArrayList<>();
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
                        JSONArray logJsonArray = tripsJsonObject.getJSONArray("car_log_data");

                        for (int i = 0; i < logJsonArray.length(); i++)
                        {
                            JSONObject logJsonObject = logJsonArray.getJSONObject(i);

                            if(logJsonObject.has("avg_speed") && !logJsonObject.isNull("avg_speed"))
                            {
                                avg_speed = logJsonObject.getString("avg_speed");
                            }
                            if(logJsonObject.has("max_speed") && !logJsonObject.isNull("max_speed"))
                            {
                                max_speed = logJsonObject.getString("max_speed");
                            }
                            if(logJsonObject.has("no_of_halts") && !logJsonObject.isNull("no_of_halts"))
                            {
                                no_of_halts = logJsonObject.getString("no_of_halts");
                            }
                            if(logJsonObject.has("total_km") && !logJsonObject.isNull("total_km"))
                            {
                                total_km = logJsonObject.getString("total_km");
                            }
                            if(logJsonObject.has("vehicle_on_time") && !logJsonObject.isNull("vehicle_on_time"))
                            {
                                vehicle_on_time = logJsonObject.getString("vehicle_on_time");
                            }
                            if(logJsonObject.has("vehicle_off_time") && !logJsonObject.isNull("vehicle_off_time"))
                            {
                                vehicle_off_time = logJsonObject.getString("vehicle_off_time");
                            }

                            CarLogInfo carLogInfo = new CarLogInfo();

                            carLogInfo.setAvgSpeed(avg_speed);
                            carLogInfo.setMax_speed(max_speed);
                            carLogInfo.setNo_of_halts(no_of_halts);
                            carLogInfo.setTotal_km(total_km);
                            carLogInfo.setVehicle_on_time(vehicle_on_time);
                            carLogInfo.setVehicle_off_time(vehicle_off_time);

                            CAR.add(carLogInfo);


                        }
                    }
                    else
                    {
                        notificationHideRelativeLayout.setVisibility(View.VISIBLE);
                        imgHideLayout.setImageResource(R.drawable.ic_car_log_48dp);
                        textViewHideLayout.setText("Car log not found");

                    }
                }
                else
                {
                    T.t(CarLogTabsActivity.this, "incorect json");
                }
            }
            else
            {
                T.t(CarLogTabsActivity.this,"0 or null json");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }

        return CAR;

    }

    private void setListner() {

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        // showToast("Two");
                        break;
                    case 1:
                        //showToast("Two");
                        break;
                    case 2:
                        //showToast("Three");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }
    private void initialize() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        carLogRecyclerView = (RecyclerView)findViewById(R.id.carLogRecyclerView);
        linearLayoutManager = new LinearLayoutManager(CarLogTabsActivity.this);
        carLogRecyclerView.setLayoutManager(linearLayoutManager);
        carLogAdapter = new CarLogAdapterrr(CarLogTabsActivity.this);
        carLogRecyclerView.setAdapter(carLogAdapter);

        String device_id_mail = S.getDeviceIdUserName(new DBHelper(CarLogTabsActivity.this));
        data = device_id_mail.split("#");

//        viewPager = (ViewPager) findViewById(R.id.tabanim_viewpager);
//        setupViewPager(viewPager);
//        tabLayout = (TabLayout) findViewById(R.id.tabanim_tabs);
//        tabLayout.setupWithViewPager(viewPager);
    }
    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new CarLogReport(),"Table");
        adapter.addFrag(new CarLogGraph(),"Graph");

        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_car_log, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home)
        {
            finish();
        }

        if(id == R.id.select_date)
        {

            selectDate();

        }
        return super.onOptionsItemSelected(item);
    }
    private void selectDate() {

        Calendar now = Calendar.getInstance();

        DatePickerDialog dpd = DatePickerDialog.newInstance(
                CarLogTabsActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        //dpd.setMinDate(Calendar.getInstance());
        dpd.setAccentColor(Color.parseColor("#0066B3"));
        dpd.show(getFragmentManager(), "Pick Date");

        dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int month, int date)
            {
                //String dateSet = "" + date + "/" + (++month) + "/" + year;
                String dateSet = "" + year + "-" + (++month) + "-" + date;


                boolean c = T.checkConnection(CarLogTabsActivity.this);

                if(c)
                {
                      /*
            "device_id"+"#"+"9000",
                        "log_date"+"#"+"2017-02-09"
             */

                    //get date wise data here
                    String dataae = T.parseDate(dateSet);

                    getCarLogDetails(data[0],dataae);
                }
                else
                {
                    T.t(CarLogTabsActivity.this,"Network connection off");
                }




            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

    }
}
