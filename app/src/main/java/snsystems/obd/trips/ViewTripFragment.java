package snsystems.obd.trips;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import snsystems.obd.R;
import snsystems.obd.classes.T;
import snsystems.obd.classes.VolleyResponseClass;
import snsystems.obd.interfaces.VolleyCallback;


/**
 * Created by shree on 07-Feb-17.
 */
public class ViewTripFragment extends Fragment
{

    private View view;

    private RecyclerView
            trips_details_RecyclerView;

    private TripsAdapter
            carLogAdapter;

    private ArrayList<TripsInformation> TRIPS_INFORMATION = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.view_trip_fragment, container, false);

        initializeWidgets();

        getTrips();

        return view;
    }

    private void getTrips() {

        boolean c = T.checkConnection(getActivity());


        /*
         $device_id = $this->post('device_id');
        $from_date = $this->post('from_date');
        $to_date = $this->post('to_date');

//         $device_id = '40000000';
//         $from_date = '2017-02-01 01:08:52';
//         $to_date = '2017-02-18 17:16:33';
         */

            if (c)
            {

                String[] parameters = {

                        "device_id" + "#" + "10000001",
                        "from_date" + "#" + "2017-02-21",
                        "to_date" + "#" + "2017-02-28"
                };

                getTripsData(parameters);

            }
            else
            {
//                backgroundRelative.setBackgroundColor(Color.parseColor("#ffffff"));
//                notificationHideRelativeLayout.setVisibility(View.VISIBLE);
//                imgHideLayout.setImageResource(R.drawable.ic_cloud_off_black_48dp);
//                textViewHideLayout.setText("Network connection off");

                T.t(getActivity(),"Network connection off");
            }

    }
    private void getTripsData(String[] parameters)
    {

        VolleyResponseClass.getResponseWithoutProgress(
                new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {

                        //Log.e("RESPONSE", "" + result);
                        TRIPS_INFORMATION = parseTripsData(result);

                        carLogAdapter.setTripsData(TRIPS_INFORMATION);
                        carLogAdapter.notifyDataSetChanged();


                    }
                },
                getActivity(),
                getResources().getString(R.string.webUrl)+""+getResources().getString(R.string.tripsData),
                trips_details_RecyclerView,
                parameters);

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

                    }
                }
                else
                {
                    T.t(getActivity(),"incorect json");
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

        return tripsArraylist;
    }

    private void initializeWidgets() {

        trips_details_RecyclerView = (RecyclerView)view.findViewById(R.id.trips_details_RecyclerView);
        trips_details_RecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        carLogAdapter  = new TripsAdapter(getActivity());
        trips_details_RecyclerView.setAdapter(carLogAdapter);

    }
}
