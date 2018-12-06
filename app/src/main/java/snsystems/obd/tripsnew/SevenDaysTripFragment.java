package snsystems.obd.tripsnew;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import snsystems.obd.R;
import snsystems.obd.activity.FeedbackActivity;
import snsystems.obd.classes.MyApplication;
import snsystems.obd.classes.T;
import snsystems.obd.classes.VolleyResponseClass;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.interfaces.Constants;
import snsystems.obd.interfaces.VolleyCallback;
import snsystems.obd.interfaces.VolleyErrorCallback;
import snsystems.obd.maintenance.ServiceHistoryAdapterrr;
import snsystems.obd.maintenance.ServiceHistoryInformationn;
import snsystems.obd.maintenance.UpdateServiceScheduleActivity;

/**
 * Created by snsystem_amol on 3/17/2017.
 */

public class SevenDaysTripFragment extends Fragment
{

    private String [] source_date_time = {

            "9:24AM 17-03-2017",
            "9:24AM 18-03-2017",
            "9:24AM 19-03-2017",
            "9:24AM 20-03-2017",
            "9:24AM 21-03-2017",
            "9:24AM 22-03-2017",
            "9:24AM 23-03-2017"

    };

    private String [] destination_date_time = {

            "9:24AM 17-03-2017",
            "9:24AM 18-03-2017",
            "9:24AM 19-03-2017",
            "9:24AM 20-03-2017",
            "9:24AM 21-03-2017",
            "9:24AM 22-03-2017",
            "9:24AM 23-03-2017"

    };

    private String [] source_address = {

            "Sahakar Nagar, Pune 411009,India",
            "Sahakar Nagar, Pune 411009,India",
            "Sahakar Nagar, Pune 411009,India",
            "Sahakar Nagar, Pune 411009,India",
            "Sahakar Nagar, Pune 411009,India",
            "Sahakar Nagar, Pune 411009,India",
            "Sahakar Nagar, Pune 411009,India"
    };

    private String [] destination_address = {

            "Sahakar Nagar, Pune 411009,India",
            "Sahakar Nagar, Pune 411009,India",
            "Sahakar Nagar, Pune 411009,India",
            "Sahakar Nagar, Pune 411009,India",
            "Sahakar Nagar, Pune 411009,India",
            "Sahakar Nagar, Pune 411009,India",
            "Sahakar Nagar, Pune 411009,India"
    };

    private String [] distance = {

            "10",
            "10",
            "10",
            "10",
            "10",
            "10",
            "10"
    };

    private String [] avg_speed = {


            "14",
            "14",
            "14",
            "14",
            "14",
            "14",
            "14"

    };

    private String [] halts = {

            "4",
            "4",
            "4",
            "4",
            "4",
            "4",
            "4"

    };


    private String [] halts_lat_long = {

            "18.501940#73.864119",
            "18.501820#73.864706",
            "18.501678#73.865581"


    };
    private String [] route_lat_long = {

            "18.501787#73.863588",
            "18.501894#73.863864",
            "18.501940#73.864119",
            "18.501891#73.864382",
            "18.501820#73.864706",
            "18.501734#73.865034",
            "18.501678#73.865581",
            "18.501647#73.866125"

    };


    private View view;


    @Bind(R.id.sevenDayTripRecyclerView)
    RecyclerView sevenDayTripRecyclerView;

    @Bind(R.id.notificationHideRelativeLayout)
    RelativeLayout notificationHideRelativeLayout;

    private SevenDayTripAdaper sevenDayTripAdaper;
    private ArrayList<TripInfoNew> SEVEN_DAY_TRIP = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
       // view =  inflater.inflate(R.layout.row_for_trip_seven_days, container, false);
        view =  inflater.inflate(R.layout.seven_day_trip_fragment, container, false);

        ButterKnife.bind(this, view);
        initialize();

        //setTripData();

        if(T.checkConnection(getActivity()))
        {
            getTripsDetails();
        }
        else
        {
            setTripsData();
           // T.t(getActivity(),"Network connection off");
        }

        return view;
    }

    private void getTripsDetails()
    {

        String device_id_mail = S.getDeviceIdUserName(MyApplication.db);

        String [] data = device_id_mail.split("#");

        String [] parameters =
                {
                        "device_id"+"#"+data[0],

                };
        VolleyResponseClass.getResponseProgressDialogError(
                new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {

                        parseResponse(result);

                    }
                },
                new VolleyErrorCallback()
                {
                    @Override
                    public void onError(VolleyError result)
                    {
                        handleError(result);
                    }
                },
                getActivity(),
                getResources().getString(R.string.webUrl) + "" + getResources().getString(R.string.getNormalTrips),
                sevenDayTripRecyclerView,
                parameters,
                "Please wait...");
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
    private void displayError(String title,
                              String error)
    {

        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
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
                        getTripsDetails();
                    }
                })
                .show();

    }
    private void parseResponse(String result)
    {

        String sourceAddresslatitude = Constants.NA;
        String sourceAddresslongitude = Constants.NA;

        String destinationAddresslatitude = Constants.NA;
        String destinationAddresslongitude = Constants.NA;

        String sourceCreatedDate = Constants.NA;
        String destinationCreatedDate = Constants.NA;

        String average_speed = Constants.NA;
        String today_date = Constants.NA;
        String halt_count = Constants.NA;

        String total_on_time = Constants.NA;
        String total_off_time = Constants.NA;



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

                        JSONArray tripLatLongJsonArray = tripsJsonObject.getJSONArray("trip_lat_long");
                        JSONArray source_lat_long_datetimeJsonArray = tripsJsonObject.getJSONArray("source_lat_long_datetime");
                        JSONArray destination_lat_long_datetimeJsonArray = tripsJsonObject.getJSONArray("destination_lat_long_datetime");
                        JSONArray total_on_off_timeJsonArray = tripsJsonObject.getJSONArray("total_on_off_time");
                        JSONArray halt_lat_longJsonArray = tripsJsonObject.getJSONArray("halt_lat_long");

                        JSONObject latlongdatetimeJsonObjectSource = source_lat_long_datetimeJsonArray.getJSONObject(0);
                        JSONObject latlongdatetimeJsonObjectDestination = destination_lat_long_datetimeJsonArray.getJSONObject(0);
                        JSONObject onOfftimeJsonObject = total_on_off_timeJsonArray.getJSONObject(0);


                        //lat long source
                        if(latlongdatetimeJsonObjectSource.has("latitude") && !latlongdatetimeJsonObjectSource.isNull("latitude"))
                        {
                            sourceAddresslatitude = latlongdatetimeJsonObjectSource.getString("latitude");
                        }

                        if(latlongdatetimeJsonObjectSource.has("longitude") && !latlongdatetimeJsonObjectSource.isNull("longitude"))
                        {
                            sourceAddresslongitude = latlongdatetimeJsonObjectSource.getString("longitude");
                        }

                        //source date time
                        if(latlongdatetimeJsonObjectSource.has("created_date") && !latlongdatetimeJsonObjectSource.isNull("created_date"))
                        {
                            sourceCreatedDate = latlongdatetimeJsonObjectSource.getString("created_date");
                        }

                        //lat long dest
                        if(latlongdatetimeJsonObjectDestination.has("latitude") && !latlongdatetimeJsonObjectDestination.isNull("latitude"))
                        {
                            destinationAddresslatitude = latlongdatetimeJsonObjectDestination.getString("latitude");
                        }

                        if(latlongdatetimeJsonObjectDestination.has("longitude") && !latlongdatetimeJsonObjectDestination.isNull("longitude"))
                        {
                            destinationAddresslongitude = latlongdatetimeJsonObjectDestination.getString("longitude");
                        }
                        //dest date time
                        if(latlongdatetimeJsonObjectDestination.has("created_date") && !latlongdatetimeJsonObjectDestination.isNull("created_date"))
                        {
                            destinationCreatedDate = latlongdatetimeJsonObjectDestination.getString("created_date");
                        }

                        //on off time
                        if(onOfftimeJsonObject.has("total_on_time") && !onOfftimeJsonObject.isNull("total_on_time"))
                        {
                            total_on_time = onOfftimeJsonObject.getString("total_on_time");
                        }
                        if(onOfftimeJsonObject.has("total_off_time") && !onOfftimeJsonObject.isNull("total_off_time"))
                        {
                            total_off_time = onOfftimeJsonObject.getString("total_off_time");
                        }

                        if(tripsJsonObject.has("average_speed") && !tripsJsonObject.isNull("average_speed"))
                        {
                            average_speed = tripsJsonObject.getString("average_speed");
                        }
                        if(tripsJsonObject.has("today_date") && !tripsJsonObject.isNull("today_date"))
                        {
                            today_date = tripsJsonObject.getString("today_date");
                        }
                        if(tripsJsonObject.has("halt_count") && !tripsJsonObject.isNull("halt_count"))
                        {
                            halt_count = tripsJsonObject.getString("halt_count");
                        }

                        MyApplication.db.insertTrips(

                                tripLatLongJsonArray.toString(),
                                halt_lat_longJsonArray.toString(),
                                sourceCreatedDate,
                                destinationCreatedDate,
                                sourceAddresslatitude+"#"+sourceAddresslongitude,
                                destinationAddresslatitude+"#"+destinationAddresslongitude,
                                average_speed,
                                total_on_time+"#"+total_off_time,
                                halt_count,
                                today_date

                        );



                    }
                    else
                    {
                        //
                        // T.displayErrorMessage(getActivity(), "Fail", "Cancel", "Trip details not found.");

                        //notificationHideRelativeLayout.setVisibility(View.VISIBLE);
                    }

                    setTripsData();
                }
                else
                {
                    T.t(getActivity(),"incorrect json");
                }
            }
            else
            {
                T.t(getActivity(),"0 or null json");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
    }

    private void setTripsData()
    {


        ArrayList<TripInfoNew> TRIPS = S.getTripsDetails(MyApplication.db,getActivity());

        if(TRIPS.isEmpty())
        {
            T.t(getActivity(),"Trips not found");

        }
        else
        {
            sevenDayTripAdaper.setTripsData(TRIPS);
            sevenDayTripAdaper.notifyDataSetChanged();
        }

    }

    private void setTripData()
    {

        for(int i = 0; i < source_date_time.length; i++)
        {
            TripInfoNew tripInfoNew = new TripInfoNew();

            String [] sorceDateTime = source_date_time[i].split(" ");
            String [] destinationDateTime = destination_date_time[i].split(" ");

            tripInfoNew.setSourceTime(sorceDateTime[0]);
            tripInfoNew.setSourceDate(sorceDateTime[1]);
            tripInfoNew.setDestinationTime(destinationDateTime[0]);
            tripInfoNew.setDestinationDate(destinationDateTime[1]);

            tripInfoNew.setSourceAddress(source_address[i]);
            tripInfoNew.setDestinationAddress(destination_address[i]);

            tripInfoNew.setDistance(distance[i]);
            tripInfoNew.setEngineHalts(halts[i]);
            tripInfoNew.setAvgSpeed(avg_speed[i]);
          //  tripInfoNew.setLatlongRouteArray(route_lat_long);
           // tripInfoNew.setLatlongHaltArray(halts_lat_long);

            SEVEN_DAY_TRIP.add(tripInfoNew);

        }

        sevenDayTripAdaper.setTripsData(SEVEN_DAY_TRIP);
        sevenDayTripAdaper.notifyDataSetChanged();

    }

    private void initialize()
    {


        sevenDayTripRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        sevenDayTripAdaper = new SevenDayTripAdaper(getActivity());
        sevenDayTripRecyclerView.setAdapter(sevenDayTripAdaper);

    }
}
