package snsystems.obd.trips;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;
import snsystems.obd.activity.FeedbackActivity;
import snsystems.obd.classes.T;
import snsystems.obd.classes.Validations;
import snsystems.obd.classes.VolleyResponseClass;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.docupload.DocumentUploadActivity;
import snsystems.obd.interfaces.VolleyCallback;
import snsystems.obd.interfaces.VolleyErrorCallback;

public class ViewTripActivity extends AnimationActivity implements DatePickerDialog.OnDateSetListener{



    private RecyclerView
            trips_details_RecyclerView;

    private TripsAdapter
            carLogAdapter;

    private ArrayList<TripsInformation> TRIPS_INFORMATION = new ArrayList<>();

    @Bind(R.id.toolbar)
    Toolbar toolbar;

//    @Bind(R.id.fromDateButton)
//    Button fromDateButton;
//
//    @Bind(R.id.toDateButton)
//    Button toDateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trip);


        initializeWidgets();

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(T.checkConnection(ViewTripActivity.this))
        {
            getTripsData();
        }
        else
        {
            T.t(ViewTripActivity.this,"Network connection off");
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

    private void getTripsData()
    {

//        String[] parameters = {
//
//                "device_id" + "#" + device_id,
//                "from_date" + "#" + from_date,
//                "to_date" + "#" + to_date
//        };


        String device_id_mail = S.getDeviceIdUserName(new DBHelper(ViewTripActivity.this));

        String [] data = device_id_mail.split("#");

        String[] parameters = {

                "device_id" + "#" + data[0]

        };



        VolleyResponseClass.getResponseProgressDialogError(
                new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {

                        //Log.e("RESPONSE", "" + result);
                        TRIPS_INFORMATION = parseTripsData(result);

                        carLogAdapter.setTripsData(TRIPS_INFORMATION);
                        carLogAdapter.notifyDataSetChanged();


                    }
                },
                new VolleyErrorCallback() {
                    @Override
                    public void onError(VolleyError result) {


                        handleError(result,"view");
                    }
                },
                ViewTripActivity.this,
                getResources().getString(R.string.webUrl) + "" + getResources().getString(R.string.tripsData),
                trips_details_RecyclerView,
                parameters,
                "Fetching trips...");

    }
    private void handleError(VolleyError error,String status)
    {
        try
        {

            if(error instanceof TimeoutError || error instanceof NoConnectionError)
            {

                displayError("TimeoutError/NoConnectionError","Server not responding or no connection.",status);


            }
            else if(error instanceof AuthFailureError)
            {

                displayError("AuthFailureError","Remote server returns (401) Unauthorized?.",status);

            }
            else if(error instanceof ServerError)
            {


                displayError("ServerError","Wrong webservice call or wrong webservice url.",status);

            }
            else if (error instanceof NetworkError)
            {
                displayError("NetworkError","you doesn't have a data connection and wi-fi Connection.",status);

            }
            else if(error instanceof ParseError)
            {

                displayError("NetworkError","Incorrect json response.",status);
            }



        }
        catch (Exception e)
        {

        }

    }

    private void displayError(String title, String error, final String statusFlag)
    {

        new SweetAlertDialog(ViewTripActivity.this, SweetAlertDialog.ERROR_TYPE)
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

                        if(statusFlag.equals("view"))
                        {
                            getTripsData();
                        }
                    }
                })
                .show();

    }
    private ArrayList<TripsInformation> parseTripsData(String result)
    {
//        String trip_master_id = Constants.NA;
//        String source_address = Constants.NA;
//        String destination_address = Constants.NA;
//        String source_datetime = Constants.NA;
//        String destination_datetime = Constants.NA;

        ArrayList<TripsInformation> tripsArraylist = new ArrayList<>();
        try
        {
            if(result != null || result.length() > 0)
            {
                Object json = new JSONTokener(result).nextValue();
                if (json instanceof JSONObject)
                {
                    JSONObject tripsJsonObject = new JSONObject(result);
                    String status = tripsJsonObject.getString("success");

                    if(status.equals("1"))
                    {

                        JSONArray tripsJsonArray = tripsJsonObject.getJSONArray("alltrips");

                        for(int i = 0; i < tripsJsonArray.length(); i++)
                        {
                            JSONObject trripsJsonObject = tripsJsonArray.getJSONObject(i);

                            String trip_master_id = trripsJsonObject.getString("trip_master_id");
                            String source_address = trripsJsonObject.getString("source_address");
                            String destination_address = trripsJsonObject.getString("destination_address");
                            String source_datetime = trripsJsonObject.getString("source_datetime");
                            String destination_datetime = trripsJsonObject.getString("destination_datetime");

//                            if(trripsJsonObject.has("trip_master_id") && !trripsJsonObject.isNull("trip_master_id"))
//                            {
//                                trip_master_id = trripsJsonObject.getString("trip_master_id");
//                            }
//
//                            if(trripsJsonObject.has("source_address") && !trripsJsonObject.isNull("source_address"))
//                            {
//                                source_address = trripsJsonObject.getString("source_address");
//                            }
//                            if(trripsJsonObject.has("destination_address") && !trripsJsonObject.isNull("destination_address"))
//                            {
//                                destination_address = trripsJsonObject.getString("destination_address");
//                            }
//                            if(trripsJsonObject.has("source_datetime") && !trripsJsonObject.isNull("source_datetime"))
//                            {
//                                source_datetime = trripsJsonObject.getString("source_datetime");
//                            }
//                            if(trripsJsonObject.has("destination_datetime") && !trripsJsonObject.isNull("destination_datetime"))
//                            {
//                                destination_datetime = trripsJsonObject.getString("destination_datetime");
//                            }

                            String [] sourceDateTimeData = source_datetime.split(" ");

                            String [] destinationDateTimeData = destination_datetime.split(" ");

                            TripsInformation tripsInformation = new TripsInformation();

                            tripsInformation.setId(trip_master_id);

                            tripsInformation.setSourceDate(sourceDateTimeData[0]);
                            tripsInformation.setSourceTime(sourceDateTimeData[1]);

                            tripsInformation.setDestinationDate(destinationDateTimeData[0]);
                            tripsInformation.setDestinationTime(destinationDateTimeData[1]);

                            tripsInformation.setSourceAddress(source_address);
                            tripsInformation.setDestinationAddress(destination_address);





                            tripsArraylist.add(tripsInformation);

                        }

                    }
                    else
                    {
                        T.t(ViewTripActivity.this,"Trip not found");
                    }
                }
                else
                {
                    T.t(ViewTripActivity.this,"incorect json");
                }
            }
            else
            {
                T.t(ViewTripActivity.this,"0 or null json");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }

        return tripsArraylist;
    }

    private void initializeWidgets() {

        trips_details_RecyclerView = (RecyclerView)findViewById(R.id.trips_details_RecyclerView);
        trips_details_RecyclerView.setLayoutManager(new LinearLayoutManager(ViewTripActivity.this));
        carLogAdapter  = new TripsAdapter(ViewTripActivity.this);
        trips_details_RecyclerView.setAdapter(carLogAdapter);

    }

//    public void selectFromDate(View view) {
//
//        Calendar now = Calendar.getInstance();
//
//        DatePickerDialog dpd = DatePickerDialog.newInstance(
//                ViewTripActivity.this,
//                now.get(Calendar.YEAR),
//                now.get(Calendar.MONTH),
//                now.get(Calendar.DAY_OF_MONTH)
//        );
//
//        //dpd.setMinDate(Calendar.getInstance());
//        dpd.setAccentColor(Color.parseColor("#0066B3"));
//        dpd.show(getFragmentManager(), "Pick Date");
//
//        dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener()
//        {
//            @Override
//            public void onDateSet(DatePickerDialog view, int year, int month, int date)
//            {
//                //String dateSet = "" + date + "-" + (++month) + "-" + year;
//                String dateSet = "" + year + "-" + (++month) + "-" + date;
//
//                String finalDate = T.parseDate(dateSet);
//
//                fromDateButton.setText(finalDate);
//
//
//
//            }
//        });
//
//    }

//    public void selectToDate(View view) {
//
//
//        Calendar now = Calendar.getInstance();
//
//        DatePickerDialog dpd = DatePickerDialog.newInstance(
//                ViewTripActivity.this,
//                now.get(Calendar.YEAR),
//                now.get(Calendar.MONTH),
//                now.get(Calendar.DAY_OF_MONTH)
//        );
//
//        //dpd.setMinDate(Calendar.getInstance());
//        dpd.setAccentColor(Color.parseColor("#0066B3"));
//        dpd.show(getFragmentManager(), "Pick Date");
//
//        dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener()
//        {
//            @Override
//            public void onDateSet(DatePickerDialog view, int year, int month, int date)
//            {
//                //String dateSet = "" + date + "-" + (++month) + "-" + year;
//                String dateSet = "" + year + "-" + (++month) + "-" + date;
//
//                String finalDate = T.parseDate(dateSet);
//
//                toDateButton.setText(finalDate);
//
//
//
//            }
//        });
//    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

    }

//    public void searchTrip(View view)
//    {
//
//
//        if(!Validations.validateButtnEmpty(fromDateButton,"select from date",ViewTripActivity.this,"from Date"))
//        {
//            return;
//        }
//        if(!Validations.validateButtnEmpty(toDateButton,"select to date",ViewTripActivity.this,"to Date"))
//        {
//            return;
//        }
//
//        boolean c = T.checkConnection(ViewTripActivity.this);
//
//        if(c)
//        {
//            //String device_id,String from_date,String to_date
//            String device_id_mail = S.getDeviceIdUserName(new DBHelper(ViewTripActivity.this));
//
//            String [] data = device_id_mail.split("#");
//
//            getTripsData(data[0],fromDateButton.getText().toString(),toDateButton.getText().toString());
//        }
//        else
//        {
//            T.t(ViewTripActivity.this,"Network connection off");
//        }
//
//    }
}
