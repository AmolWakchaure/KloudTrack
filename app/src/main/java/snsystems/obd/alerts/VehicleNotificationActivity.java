package snsystems.obd.alerts;

import android.content.Intent;
import android.graphics.Color;
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

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;
import snsystems.obd.classes.T;
import snsystems.obd.classes.VolleyResponseClass;
import snsystems.obd.database.DBHelper;
import snsystems.obd.interfaces.Constants;
import snsystems.obd.interfaces.VolleyCallback;


public class VehicleNotificationActivity extends AnimationActivity implements DatePickerDialog.OnDateSetListener{

    private Toolbar
            toolbar;

    private RecyclerView
            carlog_details_RecyclerView;

//    private SwipeRefreshLayout
//            activity_main_swipe_refresh_layout;

    private CarLogAdapter
            carLogAdapter;

    private LinearLayoutManager
            linearLayoutManager;

    private String [] on_off_image_status = {

            "on",
            "off",
            "on",
            "off",
            "on",
            "off",
            "on",
            "off",
            "on",
            "off",
            "off",
            "on",
            "off",
            "on",
            "off"
    };

    private String [] car_log_data = {

            "Engine On!",
            "Engine Off!",
            "Engine On!",
            "Engine Off!",
            "Engine On!",
            "Engine Off!",
            "Engine On!",
            "Engine Off!",
            "Engine On!",
            "Engine Off!",
            "Engine On!",
            "Engine Off!",
            "Engine On!",
            "Engine Off!",
            "Engine On!"

    };

    private String [] car_log_time = {

            "9:24 AM",
            "10:15 PM",
            "9:24 AM",
            "10:15 PM",
            "9:24 AM",
            "10:15 PM",
            "9:24 AM",
            "10:15 PM",
            "9:24 AM",
            "10:15 PM",
            "10:15 PM",
            "9:24 AM",
            "10:15 PM",
            "9:24 AM",
            "10:15 PM"
    };
    private String [] car_log_date = {

            "Thus, Nov 03",
            "Sun, Dec 12",
            "Thus, Nov 03",
            "Sun, Dec 12",
            "Thus, Nov 03",
            "Sun, Dec 12",
            "Thus, Nov 03",
            "Sun, Dec 12",
            "Thus, Nov 03",
            "Sun, Dec 12",
            "Sun, Dec 12",
            "Thus, Nov 03",
            "Sun, Dec 12",
            "Thus, Nov 03",
            "Sun, Dec 12"
    };

    //bottom refresh
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private ArrayList<CarLogInformation> CAR_LOG_INFORMATION = new ArrayList<>();


    @Bind(R.id.notificationHideRelativeLayout)
    RelativeLayout notificationHideRelativeLayout;

    @Bind(R.id.imgHideLayout)
    ImageView imgHideLayout;

    @Bind(R.id.textViewHideLayout)
    TextView textViewHideLayout;

    @Bind(R.id.progress_view)
    CircularProgressView progress_view;

    private static final String STATE_COUNTRIES ="state_news";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_log_events);
        ButterKnife.bind(this);

        initializeWidgets();


        if(savedInstanceState == null)
        {
            boolean c = T.checkConnection(VehicleNotificationActivity.this);

            if(c)
            {
                //get todays data here
                //parseDate("2017-01-16");
            }
            else
            {
                notificationHideRelativeLayout.setVisibility(View.VISIBLE);
                imgHideLayout.setImageResource(R.drawable.ic_cloud_off_black_48dp);
                textViewHideLayout.setText("Network connection off");
            }

        }
        else
        {
            CAR_LOG_INFORMATION = savedInstanceState.getParcelableArrayList(STATE_COUNTRIES);
            carLogAdapter.setCarLogData(CAR_LOG_INFORMATION);
        }


    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_COUNTRIES, CAR_LOG_INFORMATION);
    }



    private void createInputParameterJson(String inputDate)
    {

        try
        {
            //check alarm activate or not
            String alarmInputJson = new DBHelper(VehicleNotificationActivity.this).carLogJson(getResources().getString(R.string.alert_on));

            JSONArray jsonArray = new JSONArray(alarmInputJson);

            if(jsonArray.length() == 0)
            {
                //Log.e("PARAMETER","Please on alarm from alarm setting");
                new SweetAlertDialog(VehicleNotificationActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setConfirmText("OK")
                        .setContentText("Click ok to activate alarms.")
                        .setCancelText("CANCEL")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                        {
                            @Override
                            public void onClick(SweetAlertDialog sDialog)
                            {
                                sDialog.dismissWithAnimation();

                                Intent i = new Intent(VehicleNotificationActivity.this,ManageAlertsActivity.class);
                                startActivityForResult(i,2);

                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }
            else
            {
               /*
                params.put("device_id","2345");
                    params.put("date",inputDate);
                    params.put("paraarray",alarmInputJson);
                */


                boolean c = T.checkConnection(VehicleNotificationActivity.this);

                if(c)
                {
                    //getAlarmsData(alarmInputJson,inputDate);

                    String [] parameters = {

                            Constants.DEVICE_ID+"#"+"2345",
                            Constants.DATE+"#"+inputDate,
                            Constants.PARAARRAY + "#" + alarmInputJson

                    };
                    VolleyResponseClass.getResponseProgressDialog(
                            new VolleyCallback()
                            {
                                @Override
                                public void onSuccess(String result)
                                {
                                    //Log.e("VolleyResponse", "" + result);
                                    CAR_LOG_INFORMATION = parseAlarmData(result);
                                    carLogAdapter.setCarLogData(CAR_LOG_INFORMATION);
                                    carLogAdapter.notifyDataSetChanged();
                                }
                            },
                            VehicleNotificationActivity.this,
                            getResources().getString(R.string.webUrl)+""+getResources().getString(R.string.alarmData),
                            toolbar,
                            parameters,
                            "Getting Notifications...");


                }
                else
                {
                    notificationHideRelativeLayout.setVisibility(View.VISIBLE);
                    imgHideLayout.setImageResource(R.drawable.ic_cloud_off_black_48dp);
                    textViewHideLayout.setText("Network connection off");
                }

            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }
    private ArrayList<CarLogInformation> parseAlarmData(String response)
    {
        ArrayList<CarLogInformation> carlog_information = new ArrayList<>();
        try
        {

            notificationHideRelativeLayout.setVisibility(View.GONE);
            String created_date = Constants.NA;
            String alarm_name = Constants.NA;

            if(response != null || response.length() > 0)
            {
                JSONObject alarmJsonObject = new JSONObject(response);

                String status = alarmJsonObject.getString("success");

                if(status.equals("1"))
                {

                    JSONArray alarmJsonArray = alarmJsonObject.getJSONArray("alarmdata");

                    for(int i = 0; i < alarmJsonArray.length(); i++)
                    {
                        JSONArray innerJsonArray = alarmJsonArray.getJSONArray(i);

                        for(int j = 0; j < innerJsonArray.length(); j++)
                        {
                            JSONObject innerJsonObject = innerJsonArray.getJSONObject(j);

                            CarLogInformation carLogInformation = new CarLogInformation();

                            if(innerJsonObject.has("created_date") && !innerJsonObject.isNull("created_date"))
                            {

                                created_date = innerJsonObject.getString("created_date");

                            }
                            Iterator<?> keys = innerJsonObject.keys();


                            ArrayList<String> dataData = new ArrayList<>();

                            while( keys.hasNext() )
                            {
                                alarm_name = (String) keys.next();
                                dataData.add(alarm_name);

                            }


                            String [] data =created_date.split(" ");

                            carLogInformation.setCarlogDate(data[0]);
                            carLogInformation.setCarlogTime(data[1]);


                            for(int a = 0; a < dataData.size(); a++)
                            {
                                String alarmString = dataData.get(a).substring(0, Math.min(5, dataData.get(a).length()));

                                if(alarmString.equals("alarm"))
                                {
                                    carLogInformation.setEngineStatus(dataData.get(a));
                                }
                            }
                            carLogInformation.setImageStatus("on");


                            carlog_information.add(carLogInformation);


                        }
                    }
                }
                else
                {
                    notificationHideRelativeLayout.setVisibility(View.VISIBLE);
                    imgHideLayout.setImageResource(R.drawable.ic_notifications_none_black_48dp);
                    textViewHideLayout.setText("Notifications not Found");
                }
            }
            else
            {
                T.t(VehicleNotificationActivity.this,"null response or empty response");
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return carlog_information;
    }



    private void allocateData()
    {

        for(int i = 0 ; i < on_off_image_status.length; i++)
        {
            CarLogInformation carLogInformation = new CarLogInformation();

            carLogInformation.setImageStatus(on_off_image_status[i]);
            carLogInformation.setEngineStatus(car_log_data[i]);
            carLogInformation.setCarlogTime(car_log_time[i]);
            carLogInformation.setCarlogDate(car_log_date[i]);

            CAR_LOG_INFORMATION.add(carLogInformation);
        }

        carLogAdapter.setCarLogData(CAR_LOG_INFORMATION);
        carLogAdapter.notifyDataSetChanged();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 2)
        {
            String alarmInputJson = new DBHelper(VehicleNotificationActivity.this).carLogJson(getResources().getString(R.string.alert_on));
            Log.e("PARAMETER", "" + alarmInputJson);
        }
    }

    private void setListners()
    {

//        //for swipe to refresh
//        activity_main_swipe_refresh_layout.setColorSchemeResources(R.color.material_red_400, R.color.green, R.color.accentColor);
//        activity_main_swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        //refresh method call goes here
//
//                        T.s(toolbar, "Refreshing...");
//
//
//                        activity_main_swipe_refresh_layout.setRefreshing(false);
//                    }
//                }, 2500);
//            }
//        });

    }



    private void initializeWidgets()
    {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_white_24dp);

        carlog_details_RecyclerView = (RecyclerView)findViewById(R.id.carlog_details_RecyclerView);
        linearLayoutManager = new LinearLayoutManager(VehicleNotificationActivity.this);
        carlog_details_RecyclerView.setLayoutManager(linearLayoutManager);
        carLogAdapter = new CarLogAdapter(VehicleNotificationActivity.this);
        carlog_details_RecyclerView.setAdapter(carLogAdapter);


        //activity_main_swipe_refresh_layout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_car_log_events, menu);
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
        if(id == R.id.select_date)
        {

            selectDate();

        }

        return super.onOptionsItemSelected(item);
    }

    private void selectDate() {

        Calendar now = Calendar.getInstance();

        DatePickerDialog dpd = DatePickerDialog.newInstance(
                VehicleNotificationActivity.this,
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


                boolean c = T.checkConnection(VehicleNotificationActivity.this);

                if(c)
                {
                    //get date wise data here
                    parseDate(dateSet);
                }
                else
                {
                    notificationHideRelativeLayout.setVisibility(View.VISIBLE);
                    imgHideLayout.setImageResource(R.drawable.ic_cloud_off_black_48dp);
                    textViewHideLayout.setText("Network connection off");
                }



               // Log.e("DATEE",""+dateSet);
            }
        });
    }

    private void parseDate(String dateSet)
    {

        try
        {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Date convertDate = dateFormat.parse(dateSet);

            String  parseDate  = simpleDateFormat.format(convertDate);

            Log.e("parseDate",""+parseDate);

           createInputParameterJson(parseDate);//2017-01-16

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int i, int i1, int i2)
    {

    }


}
