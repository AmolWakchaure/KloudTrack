package snsystems.obd.performancedash;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;
import snsystems.obd.classes.CurrentMonthDates;
import snsystems.obd.classes.MyApplication;
import snsystems.obd.classes.T;
import snsystems.obd.classes.VolleyResponseClass;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.graphs.speed.SpeedLineChartActivity;
import snsystems.obd.interfaces.Constants;
import snsystems.obd.interfaces.VolleyCallback;
import snsystems.obd.interfaces.VolleyErrorCallback;
import snsystems.obd.maintenance.UpdateServiceScheduleActivity;

public class PerformanceDashboardActivity extends AnimationActivity
{

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.dailyWeeklyMonthlyeButton)
    Button dailyWeeklyMonthlyeButton;

    @Bind(R.id.selectMonthButton)
    Button selectMonthButton;

    @Bind(R.id.hideCardView)
    CardView hideCardView;


    @Bind(R.id.totalDistanceTextView)
    TextView totalDistanceTextView;

    @Bind(R.id.favgSpeedTextView)
    TextView favgSpeedTextView;

    @Bind(R.id.haltsTextView)
    TextView haltsTextView;

    @Bind(R.id.alertsTextView)
    TextView alertsTextView;

    @Bind(R.id.rpmAlertTextView)
    TextView rpmAlertTextView;

    @Bind(R.id.speedAlertTextView)
    TextView speedAlertTextView;

    @Bind(R.id.troubleAlertTextView)
    TextView troubleAlertTextView;

    @Bind(R.id.dateTextView)
    TextView dateTextView;


    private ArrayList<String> MONTHS = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance_dashboard);


        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addMonths();

        if(T.checkConnection(PerformanceDashboardActivity.this))
        {
            getDailyPerformanceReport("daily");
        }
        else
        {
            T.t(PerformanceDashboardActivity.this,"Network connection off");
        }
    }

    private void addMonths()
    {


        MONTHS.add("Jan");
        MONTHS.add("Feb");
        MONTHS.add("Mar");
        MONTHS.add("Apr");
        MONTHS.add("May");
        MONTHS.add("Jun");
        MONTHS.add("Jul");
        MONTHS.add("Aug");
        MONTHS.add("Sep");
        MONTHS.add("Oct");
        MONTHS.add("Nov");
        MONTHS.add("Dec");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_per, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {

            case android.R.id.home:
            {
                finish();
                break;
            }
            case R.id.speedGraph:
            {
                Intent i = new Intent(PerformanceDashboardActivity.this, SpeedLineChartActivity.class);
                i.putExtra("graphName","speed");
                startActivity(i);
                break;
            }
            case R.id.rpmGraph:
            {
                Intent i = new Intent(PerformanceDashboardActivity.this, SpeedLineChartActivity.class);
                i.putExtra("graphName","rpm");
                startActivity(i);

                break;
            }
            case R.id.onTimeGraph:
            {
                Intent i = new Intent(PerformanceDashboardActivity.this, SpeedLineChartActivity.class);
                i.putExtra("graphName","ontime");
                startActivity(i);
                break;
            }
            case R.id.batteryVoltageGraph:
            {
                Intent i = new Intent(PerformanceDashboardActivity.this, SpeedLineChartActivity.class);
                i.putExtra("graphName","battery_voltage");
                startActivity(i);
                break;
            }
            case R.id.coolantTempratureGraph:
            {
                Intent i = new Intent(PerformanceDashboardActivity.this, SpeedLineChartActivity.class);
                i.putExtra("graphName","coolant_temp");
                startActivity(i);
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void selectDailyWeeklyMonthly(View view)
    {


                new MaterialDialog.Builder(PerformanceDashboardActivity.this)
                .title("Select Preference")
                .items(R.array.selec_dwm)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice()
                {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        try
                        {

                            dailyWeeklyMonthlyeButton.setText(text.toString());

                            if(text.toString().equals("Monthly"))
                            {
                                if(T.checkConnection(PerformanceDashboardActivity.this))
                                {
                                    hideCardView.setVisibility(View.GONE);
                                    getDailyPerformanceReport("monthly");

                                }
                                else
                                {
                                    T.t(PerformanceDashboardActivity.this,"Network connection off");
                                }

//                                String [] datess = CurrentMonthDates.getDateRange().split("#");
//
//                                setWeeklyData(datess[0],datess[1]);


                            }
                            else if(text.toString().equals("Daily"))
                            {
                                if(T.checkConnection(PerformanceDashboardActivity.this))
                                {
                                    hideCardView.setVisibility(View.GONE);
                                    getDailyPerformanceReport("daily");

                                }
                                else
                                {
                                    T.t(PerformanceDashboardActivity.this,"Network connection off");
                                }
                            }
                            else if(text.toString().equals("Weekly"))
                            {
                                if(T.checkConnection(PerformanceDashboardActivity.this))
                                {
                                    hideCardView.setVisibility(View.GONE);
                                    getDailyPerformanceReport("weekly");

                                }
                                else
                                {
                                    T.t(PerformanceDashboardActivity.this,"Network connection off");
                                }

//                                String [] fromToDates = T.getWeekDates().split("#");
//
//                                String [] fromDates = fromToDates[0].split(" ");//1
//                                String [] toDates = fromToDates[1].split(" ");//1
//
//                                setWeeklyData(fromDates[1],toDates[1]);

                            }

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        return true;
                    }


                })
                .show();

    }

    private void setWeeklyData(String fromDate, String toDate) {

        String data1 = S.getWeeklyMonthlyDetails(MyApplication.db,fromDate,toDate);

        if(data1.equals(Constants.NA))
        {
            dateTextView.setText("Details not available");
            dateTextView.setTextColor(Color.parseColor("#D50000"));
        }
        else
        {
            String [] data = data1.split("#");



            favgSpeedTextView.setText(data[0]);
            alertsTextView.setText(data[1]);
            haltsTextView.setText(data[2]);
            rpmAlertTextView.setText(data[3]);
            speedAlertTextView.setText(data[4]);
            troubleAlertTextView.setText(data[5]);
            dateTextView.setText("From Date: "+fromDate+" To Date: "+toDate);
        }
    }

    private void getDailyPerformanceReport(final String inputStatus)
    {

        String device_id_mail = S.getDeviceIdUserName(MyApplication.db);

        String [] data = device_id_mail.split("#");

        String [] parameters =
                {
                        "device_id"+"#"+data[0],
                        "keyword"+"#"+inputStatus

                };
        VolleyResponseClass.getResponseProgressDialogError(
                new VolleyCallback()
                {
                    @Override
                    public void onSuccess(String result)
                    {

                        Log.e("result",""+result);
                        parseResponse(result);

                    }
                },
                new VolleyErrorCallback()
                {
                    @Override
                    public void onError(final VolleyError result)
                    {

                        new SweetAlertDialog(PerformanceDashboardActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setConfirmText("Try again")
                                .setContentText(""+result)
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

                                        handleError(result,inputStatus);
                                    }
                                })
                                .show();


                    }
                },
                PerformanceDashboardActivity.this,
                getResources().getString(R.string.webUrl) + "" + getResources().getString(R.string.dailyPerformance),
                hideCardView,
                parameters,
                "Please wait...");
    }

    private void parseResponse(String result) {


        String average_speed = Constants.ZERO;
        String total_alert_count = Constants.ZERO;
        String halt_count = Constants.ZERO;
        String rpm_alert_count = Constants.ZERO;
        String speed_alert_count = Constants.ZERO;
        String trouble_alert_count = Constants.ZERO;
        String today_date = Constants.DATEE;

        ArrayList<LatLng> DISTANCE_LAT_LONG = new ArrayList<>();

        //{"status":1,"success":"Success! Record added successfully."}
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

                         if(tripsJsonObject.has("average_speed") && !tripsJsonObject.isNull("average_speed"))
                         {
                             average_speed = tripsJsonObject.getString("average_speed");
                         }
                        if(tripsJsonObject.has("total_alert_count") && !tripsJsonObject.isNull("total_alert_count"))
                        {
                            total_alert_count = tripsJsonObject.getString("total_alert_count");
                        }
                        if(tripsJsonObject.has("halt_count") && !tripsJsonObject.isNull("halt_count"))
                        {
                            halt_count = tripsJsonObject.getString("halt_count");
                        }
                        if(tripsJsonObject.has("rpm_alert_count") && !tripsJsonObject.isNull("rpm_alert_count"))
                        {
                            rpm_alert_count = tripsJsonObject.getString("rpm_alert_count");
                        }
                        if(tripsJsonObject.has("speed_alert_count") && !tripsJsonObject.isNull("speed_alert_count"))
                        {
                            speed_alert_count = tripsJsonObject.getString("speed_alert_count");
                        }
                        if(tripsJsonObject.has("trouble_alert_count") && !tripsJsonObject.isNull("trouble_alert_count"))
                        {
                            trouble_alert_count = tripsJsonObject.getString("trouble_alert_count");
                        }
                        if(tripsJsonObject.has("today_date") && !tripsJsonObject.isNull("today_date"))
                        {
                            today_date = tripsJsonObject.getString("today_date");
                        }


                        favgSpeedTextView.setText(average_speed);
                        alertsTextView.setText(total_alert_count);
                        haltsTextView.setText(halt_count);
                        rpmAlertTextView.setText(rpm_alert_count);
                        speedAlertTextView.setText(speed_alert_count);
                        troubleAlertTextView.setText(trouble_alert_count);
                        dateTextView.setText("Date: "+today_date);

                        JSONArray lat_longJsonArray = tripsJsonObject.getJSONArray("lat_long");

                        if(lat_longJsonArray.length() > 0)
                        {
                            for(int i = 0; i < lat_longJsonArray.length(); i++)
                            {
                                JSONObject latlongJsonObject = lat_longJsonArray.getJSONObject(i);

                                String latitude = latlongJsonObject.getString("latitude");
                                String longitude = latlongJsonObject.getString("longitude");

                                DISTANCE_LAT_LONG.add(new LatLng(Double.valueOf(latitude),Double.valueOf(longitude)));

                            }
                            double toatalDiatance = T.calculateDistanceUsingLatLong(DISTANCE_LAT_LONG);

                            if(toatalDiatance > 1000)
                            {
                                totalDistanceTextView.setText(""+Math.floor((toatalDiatance / 1000)) + " KM");
                            }
                            else
                            {
                                totalDistanceTextView.setText(""+Math.floor(toatalDiatance) + " KM");
                            }

                        }
                        else
                        {
                            totalDistanceTextView.setText("0");
                        }


                        //MyApplication.db.insertPerformanceDetails(average_speed,total_alert_count,halt_count,rpm_alert_count,speed_alert_count,trouble_alert_count,today_date);

                        //setSqliteData();

                    }
                    else
                    {
                        T.displayErrorMessage(PerformanceDashboardActivity.this, "Oops...", "Cancel", "Performance report not found.");
                    }
                }
                else
                {
                    T.t(PerformanceDashboardActivity.this,"incorrect json");
                }
            }
            else
            {
                T.t(PerformanceDashboardActivity.this,"0 or null json");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
    }

    private void setSqliteData()
    {


        String data1 = S.getPerformanceDetails(MyApplication.db);

        if(data1.equals(Constants.NA))
        {
            dateTextView.setText("Details not available");
            dateTextView.setTextColor(Color.parseColor("#D50000"));
        }
        else
        {
            String [] data = data1.split("#");

            favgSpeedTextView.setText(data[0]);
            alertsTextView.setText(data[1]);
            haltsTextView.setText(data[2]);
            rpmAlertTextView.setText(data[3]);
            speedAlertTextView.setText(data[4]);
            troubleAlertTextView.setText(data[5]);
            dateTextView.setText("Date: "+data[6]);
        }

    }

    private void handleError(VolleyError error,String inputStatus)
    {
        try
        {

            if(error instanceof TimeoutError || error instanceof NoConnectionError)
            {

                displayError("TimeoutError/NoConnectionError","Server not responding or no connection.",inputStatus);


            }
            else if(error instanceof AuthFailureError)
            {

                displayError("AuthFailureError","Remote server returns (401) Unauthorized?.",inputStatus);

            }
            else if(error instanceof ServerError)
            {


                displayError("ServerError","Wrong webservice call or wrong webservice url.",inputStatus);

            }
            else if (error instanceof NetworkError)
            {
                displayError("NetworkError","you doesn't have a data connection and wi-fi Connection.",inputStatus);

            }
            else if(error instanceof ParseError)
            {

                displayError("NetworkError","Incorrect json response.",inputStatus);
            }



        }
        catch (Exception e)
        {

        }

    }
    private void displayError(String title,
                              String error,
                              final String inputStatus)
    {

        new SweetAlertDialog(PerformanceDashboardActivity.this, SweetAlertDialog.ERROR_TYPE)
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
                        getDailyPerformanceReport(inputStatus);
                    }
                })
                .show();

    }

    public void selectMonthly(View view)
    {


        Calendar cal = Calendar.getInstance();
        
        
        ArrayList<String> months = maintainMonth(new SimpleDateFormat("MMM").format(cal.getTime()));


        new MaterialDialog.Builder(PerformanceDashboardActivity.this)
                .title("Select Month")
                .items(months)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice()
                {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        try
                        {

                            selectMonthButton.setText(text.toString());

                            getMonthWiseReport(text.toString());

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        return true;
                    }
                })
                .show();

    }

    private void getMonthWiseReport(String monthName)
    {

        String dateFormat = null;

        String currentYear = getCurrentYear();

        if(monthName.equals(MONTHS.get(0)))
        {
            dateFormat = "01-"+currentYear;
        }
        else if(monthName.equals(MONTHS.get(1)))
        {
            dateFormat = "02-"+currentYear;
        }
        else if(monthName.equals(MONTHS.get(2)))
        {
            dateFormat = "03-"+currentYear;
        }
        else if(monthName.equals(MONTHS.get(3)))
        {
            dateFormat = "04-"+currentYear;
        }
        else if(monthName.equals(MONTHS.get(4)))
        {
            dateFormat = "05-"+currentYear;
        }
        else if(monthName.equals(MONTHS.get(5)))
        {
            dateFormat = "06-"+currentYear;
        }
        else if(monthName.equals(MONTHS.get(6)))
        {
            dateFormat = "07-"+currentYear;
        }
        else if(monthName.equals(MONTHS.get(7)))
        {
            dateFormat = "08-"+currentYear;
        }
        else if(monthName.equals(MONTHS.get(8)))
        {
            dateFormat = "09-"+currentYear;
        }
        else if(monthName.equals(MONTHS.get(9)))
        {
            dateFormat = "10-"+currentYear;
        }
        else if(monthName.equals(MONTHS.get(10)))
        {
            dateFormat = "11-"+currentYear;
        }
        else if(monthName.equals(MONTHS.get(11)))
        {
            dateFormat = "12-"+currentYear;
        }
        
        Log.e("dateFormat",""+dateFormat);
    }

    private String getCurrentYear() {

        String yaerr = null;
        try
        {
           yaerr = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return yaerr;
    }


    public  ArrayList<String> maintainMonth(String monthNameIndfexData)
    {

        ArrayList<String> months = new ArrayList<>();
        
        int indexC = 0;

        try
        {

            for (int i = 0; i < MONTHS.size(); i++)
            {
                if (MONTHS.get(i) .equalsIgnoreCase(monthNameIndfexData))
                {

                    indexC = i;
                    break;
                }
            }

            for(int i = 0; i < indexC + 1; i++)
            {
                months.add(MONTHS.get(i));
            }

        }
        catch (Exception e)
        {

        }


        return months;

    }
}
