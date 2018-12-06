package snsystems.obd.trips;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import snsystems.obd.R;
import snsystems.obd.classes.T;
import snsystems.obd.classes.VolleyResponseClass;
import snsystems.obd.dashboard.DashboardActivity;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.interfaces.Constants;
import snsystems.obd.interfaces.VolleyCallback;
import snsystems.obd.interfaces.VolleyErrorCallback;


public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.ViewHolderTrips> {

    private ArrayList<TripsInformation> carlogInformation = new ArrayList<>();
    private LayoutInflater layoutInflater;

    View view ;

    public TripsAdapter(Context context)
    {
        layoutInflater = LayoutInflater.from(context);

    }

    public void setTripsData(ArrayList<TripsInformation> countryInformationsdata) {
        this.carlogInformation = countryInformationsdata;
        notifyItemRangeChanged(0, countryInformationsdata.size());
    }

    @Override
    public ViewHolderTrips onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.row_for_trips_parrent_update, parent, false);
        ViewHolderTrips viewHolderScheduleholde = new ViewHolderTrips(view);
        return viewHolderScheduleholde;
    }

    @Override
    public void onBindViewHolder(ViewHolderTrips holder, int position) {
        try
        {

            TripsInformation tripsInformation = carlogInformation.get(position);

            holder.sourceAddresstextView.setText(tripsInformation.getSourceAddress());
            holder.sourceDateTextView.setText(T.changeDateFormat(tripsInformation.getSourceDate()));
            holder.sourceTimeTextView.setText(T.changeTimeFormat(tripsInformation.getSourceTime()));

            holder.destinationAddresstextView.setText(tripsInformation.getDestinationAddress());
            holder.desinationDateTextView.setText(T.changeDateFormat(tripsInformation.getDestinationDate()));
            holder.desinationTimeTextView.setText(T.changeTimeFormat(tripsInformation.getDestinationTime()));

            //holder.kmsTimeTextView.setText(tripsInformation.getDistance());
           // holder.kmhrTimeTextView.setText(tripsInformation.getAvgSpeed()+" KM/hr");
            //holder.minTimeTextView.setText(tripsInformation.getEngineIdleTime());

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount()
    {
        return carlogInformation.size();
    }

    class ViewHolderTrips extends RecyclerView.ViewHolder implements View.OnClickListener {


        private TextView
                sourceAddresstextView,
                sourceDateTextView,
                sourceTimeTextView,
                destinationAddresstextView,
                desinationDateTextView,
                desinationTimeTextView,
                kmsTimeTextView,
                kmhrTimeTextView,
                minTimeTextView;
        private CircularProgressView

                progress_view;

        public ViewHolderTrips(View itemView)
        {
            super(itemView);

            try
            {


                sourceAddresstextView = (TextView)itemView.findViewById(R.id.sourceAddresstextView);
                        sourceDateTextView = (TextView)itemView.findViewById(R.id.sourceDateTextView);
                        sourceTimeTextView = (TextView)itemView.findViewById(R.id.sourceTimeTextView);
                        destinationAddresstextView = (TextView)itemView.findViewById(R.id.destinationAddresstextView);
                        desinationDateTextView = (TextView)itemView.findViewById(R.id.desinationDateTextView);
                        desinationTimeTextView = (TextView)itemView.findViewById(R.id.desinationTimeTextView);
//                        kmsTimeTextView = (TextView)itemView.findViewById(R.id.kmsTimeTextView);
//                        kmhrTimeTextView = (TextView)itemView.findViewById(R.id.kmhrTimeTextView);
//                        minTimeTextView = (TextView)itemView.findViewById(R.id.minTimeTextView);
//
//                progress_view = (CircularProgressView)itemView.findViewById(R.id.progress_view);



                 itemView.setOnClickListener(this);
                // placeCallRelativeLayout.setOnClickListener(this);
                /*reschedule.setOnClickListener(this);
                tContactNumber_TextView.setOnClickListener(this);*/


            }
            catch (Exception e)
            {
                e.printStackTrace();
            }




        }

        @Override
        public void onClick(final View v)
        {
            try
            {

                TripsInformation  subItemInformation = carlogInformation.get(Integer.valueOf(getAdapterPosition()));

                String trip_id = subItemInformation.getId();

              //  Log.e("trip_idtrip_id",""+trip_id);

                final String sourceDate = subItemInformation.getSourceDate();
                final String sourceTime = subItemInformation.getSourceTime();

                final String destinationDate = subItemInformation.getDestinationDate();
                final String destinationTime = subItemInformation.getDestinationTime();

                final String sourceAddress = subItemInformation.getSourceAddress();
                final String destinationAddress = subItemInformation.getDestinationAddress();

                boolean c = T.checkConnection(v.getContext());
                if(c)
                {
                    if (v.getId() == itemView.getId())
                    {


                        viewTrip(trip_id,
                                v,
                                sourceDate,
                                sourceTime,
                                destinationDate,
                                destinationTime,
                                sourceAddress,
                                destinationAddress,
                                sourceAddresstextView);




                    }
                }
                else
                {
                    T.t(v.getContext(),"Network connection off");
                }
//                if (v.getId() == itemView.getId())
//                {
//                    //check msg status
//
//                    String statusSSS = checkStatus(v,db,mNumber);
//
//                    if(statusSSS.equals("Not send"))
//                    {
//                        Intent i = new Intent(v.getContext(), SendMessageActivity.class);
//                        i.putExtra("NUMBER", mNumber);
//                        v.getContext().startActivity(i);
//                    }
//                    else
//                    {
//                        T.displayMessage(v.getContext(),"You are already send address information to this mobile number.");
//                    }
//
//
//
//                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }

    }

    private void viewTrip(final String trip_id, final View v, final String sourceDate, final String sourceTime, final String destinationDate, final String destinationTime, final String sourceAddress, final String destinationAddress, final TextView sourceAddresstextView) {

        String [] parameters = {

                "trip_id"+"#"+trip_id

                //"trip_id"+"#"+"12"


        };

        VolleyResponseClass.getResponseProgressDialogError(
                new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {

                        Log.e("RESPONSE", "" + result);
                        parseLatLong(
                                result,
                                v,
                                sourceDate,
                                sourceTime,
                                destinationDate,
                                destinationTime,
                                sourceAddress,
                                destinationAddress);

                    }
                },
                new VolleyErrorCallback()
                {
                    @Override
                    public void onError(VolleyError result)
                    {
                        handleError(result,
                                v,
                                trip_id,
                                sourceDate,
                                sourceTime,
                                destinationDate,
                                destinationTime,
                                sourceAddress,
                                destinationAddress,
                                sourceAddresstextView);
                    }
                },
                v.getContext(),
                v.getContext().getResources().getString(R.string.webUrl) + "" + v.getContext().getResources().getString(R.string.tripsRouteLatLong),
                sourceAddresstextView,
                parameters,
                "Getting route...");
    }

    private void handleError(VolleyError error, View v,String trip_id, final String sourceDate, final String sourceTime, final String destinationDate, final String destinationTime, final String sourceAddress, final String destinationAddress, TextView sourceAddresstextView)
    {
        try
        {

            if(error instanceof TimeoutError || error instanceof NoConnectionError)
            {

                displayError("TimeoutError/NoConnectionError","Server not responding or no connection.",v,trip_id,
                        sourceDate,
                        sourceTime,
                        destinationDate,
                        destinationTime,
                        sourceAddress,
                        destinationAddress,
                        sourceAddresstextView);


            }
            else if(error instanceof AuthFailureError)
            {

                displayError("AuthFailureError","Remote server returns (401) Unauthorized?.",v,trip_id,
                        sourceDate,
                        sourceTime,
                        destinationDate,
                        destinationTime,
                        sourceAddress,
                        destinationAddress,
                        sourceAddresstextView);

            }
            else if(error instanceof ServerError)
            {


                displayError("ServerError","Wrong webservice call or wrong webservice url.",v,trip_id,
                        sourceDate,
                        sourceTime,
                        destinationDate,
                        destinationTime,
                        sourceAddress,
                        destinationAddress,
                        sourceAddresstextView);

            }
            else if (error instanceof NetworkError)
            {
                displayError("NetworkError","you doesn't have a data connection and wi-fi Connection.",v,trip_id,
                        sourceDate,
                        sourceTime,
                        destinationDate,
                        destinationTime,
                        sourceAddress,
                        destinationAddress,
                        sourceAddresstextView);

            }
            else if(error instanceof ParseError)
            {

                displayError("NetworkError","Incorrect json response.",v,trip_id,
                        sourceDate,
                        sourceTime,
                        destinationDate,
                        destinationTime,
                        sourceAddress,
                        destinationAddress,
                        sourceAddresstextView);
            }



        }
        catch (Exception e)
        {

        }

    }

    private void displayError(String title, String error, final View v, final String trip_id, final String sourceDate, final String sourceTime, final String destinationDate, final String destinationTime, final String sourceAddress, final String destinationAddress, final TextView sourceAddresstextView)
    {

        new SweetAlertDialog(v.getContext(), SweetAlertDialog.ERROR_TYPE)
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



                        viewTrip(trip_id,v,
                                sourceDate,
                                sourceTime,
                                destinationDate,
                                destinationTime,
                                sourceAddress,
                                destinationAddress,
                                sourceAddresstextView);
                    }
                })
                .show();

    }
    private void parseLatLong(String result,
                              View v,
                              String sourceDate,
                              String sourceTime,
                              String destinationDate,
                              String destinationTime,
                              String sourceAddress,
                              String destinationAddress)
    {

        String [] latlong ;
        String [] haltPointlatlong ;
        String latitude = Constants.NA;
        String londitude = Constants.NA;

        String haltlatitude = Constants.NA;
        String haltlonditude = Constants.NA;

        String noOfHalts = Constants.NA;
        String avgSpeed = Constants.NA;
        String maxSpeed = Constants.NA;

        String tripKilometers = Constants.NA;

        String totalOnTime = Constants.NA;
        String totalOffTime = Constants.NA;
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

                        JSONArray tripsRoutLatLongJsonArray = tripsJsonObject.getJSONArray("trip_route");
                        JSONArray tripsHaltRoutPointLatLongJsonArray = tripsJsonObject.getJSONArray("halt_lat_long");
                        JSONArray tripsAvgMaxSpeedJsonArray = tripsJsonObject.getJSONArray("avg_max_speed");
                        JSONArray tripsKilometerJsonArray = tripsJsonObject.getJSONArray("total_km");
                        JSONArray totalOnTimeOffTimeKilometerJsonArray = tripsJsonObject.getJSONArray("total_ontime_offtime");

                        latlong = new String[tripsRoutLatLongJsonArray.length()];
                        haltPointlatlong = new String[tripsHaltRoutPointLatLongJsonArray.length()];

                        //route lat longs
                        for(int i = 0; i < tripsRoutLatLongJsonArray.length(); i++)
                        {
                            JSONObject innerJsonObject = tripsRoutLatLongJsonArray.getJSONObject(i);

                            if(innerJsonObject.has("latitude") && !innerJsonObject.isNull("latitude"))
                            {
                                latitude = innerJsonObject.getString("latitude");
                            }
                            if(innerJsonObject.has("longitude") && !innerJsonObject.isNull("longitude"))
                            {
                                londitude = innerJsonObject.getString("longitude");
                            }

                            if(!(latitude.equals("0") && londitude.equals("0")))
                            {

                                latlong[i] = latitude+"#"+londitude;
                            }



                            //double sdf =  distanceBetween(new LatLng(Double.valueOf(latitude), 73.863591), new LatLng(18.457532, 73.867746));
                        }

                        //halt lat longs
                        for(int i = 0; i < tripsHaltRoutPointLatLongJsonArray.length(); i++)
                        {
                            JSONObject innerJsonObject = tripsHaltRoutPointLatLongJsonArray.getJSONObject(i);

                            if(innerJsonObject.has("halt_latitude") && !innerJsonObject.isNull("halt_latitude"))
                            {
                                haltlatitude = innerJsonObject.getString("halt_latitude");
                            }
                            if(innerJsonObject.has("halt_longitude") && !innerJsonObject.isNull("halt_longitude"))
                            {
                                haltlonditude = innerJsonObject.getString("halt_longitude");
                            }

                            if(!(haltlatitude.equals("0") && haltlonditude.equals("0")))
                            {

                                haltPointlatlong[i] = haltlatitude+"#"+haltlonditude;
                            }

                        }

                        //no of halt count
                        if(tripsJsonObject.has("halt_count") && !tripsJsonObject.isNull("halt_count"))
                        {
                            noOfHalts = tripsJsonObject.getString("halt_count");
                        }

                        //avg speed, max speed
                        JSONObject avgMaxSpeedJsonObject = tripsAvgMaxSpeedJsonArray.getJSONObject(0);
                        if(avgMaxSpeedJsonObject.has("avg_speed") && !avgMaxSpeedJsonObject.isNull("avg_speed"))
                        {
                            avgSpeed = avgMaxSpeedJsonObject.getString("avg_speed");
                        }
                        if(avgMaxSpeedJsonObject.has("max_speed") && !avgMaxSpeedJsonObject.isNull("max_speed"))
                        {
                            maxSpeed = avgMaxSpeedJsonObject.getString("max_speed");
                        }

                        //trip kilometers
                        JSONObject tripKmJsonObject = tripsKilometerJsonArray.getJSONObject(0);
                        if(tripKmJsonObject.has("total_km") && !tripKmJsonObject.isNull("total_km"))
                        {
                            tripKilometers = tripKmJsonObject.getString("total_km");
                        }

                        //trip ontime off time
                        JSONObject tripOntimeOfftimeJsonObject = totalOnTimeOffTimeKilometerJsonArray.getJSONObject(0);
                        if(tripOntimeOfftimeJsonObject.has("total_on_time") && !tripOntimeOfftimeJsonObject.isNull("total_on_time"))
                        {
                            totalOnTime = tripOntimeOfftimeJsonObject.getString("total_on_time");
                        }
                        if(tripOntimeOfftimeJsonObject.has("total_off_time") && !tripOntimeOfftimeJsonObject.isNull("total_off_time"))
                        {
                            totalOffTime = tripOntimeOfftimeJsonObject.getString("total_off_time");
                        }

                        Bundle latlongBundle = new Bundle();

                        latlongBundle.putStringArray("routeLatLong", latlong);
                        latlongBundle.putStringArray("haltLatLong", haltPointlatlong);

                        Intent i = new Intent(v.getContext(), TripMapsActivity.class);
                        i.putExtra("sourceAddress", sourceAddress);
                        i.putExtra("destinationAddress", destinationAddress);
                        i.putExtra("sourceDate",sourceDate);
                        i.putExtra("sourceTime",sourceTime);
                        i.putExtra("destinationDate",destinationDate);
                        i.putExtra("destinationTime",destinationTime);

                        i.putExtra("avgSpeed",avgSpeed);
                        i.putExtra("maxSpeed",maxSpeed);

                        i.putExtra("noOfHalts",noOfHalts);
                        i.putExtra("tripKilometeres",tripKilometers);

                        i.putExtra("totalOnTime",totalOnTime);
                        i.putExtra("totalOffTime",totalOffTime);
                        i.putExtras(latlongBundle);

                        v.getContext().startActivity(i);

                    }
                    else if(status.equals("0"))
                    {
                        T.t(v.getContext(),"No route found");
                    }
                }
                else
                {
                    T.t(v.getContext(),"Zero or null json found");
                }
            }
            else
            {

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public static Double distanceBetween(LatLng point1, LatLng point2) {
        if (point1 == null || point2 == null) {
            return null;
        }

        return SphericalUtil.computeDistanceBetween(point1, point2);
    }


}
