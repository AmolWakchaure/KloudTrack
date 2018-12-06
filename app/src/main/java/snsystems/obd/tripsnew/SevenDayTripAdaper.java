package snsystems.obd.tripsnew;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

import snsystems.obd.R;
import snsystems.obd.classes.T;
import snsystems.obd.classes.VolleyResponseClass;
import snsystems.obd.interfaces.VolleyCallback;
import snsystems.obd.maintenance.ServiceHistoryInformationn;
import snsystems.obd.trips.TripMapsActivity;
import snsystems.obd.trips.TripsInformation;

/**
 * Created by snsystem_amol on 3/19/2017.

*/

public class SevenDayTripAdaper extends RecyclerView.Adapter<SevenDayTripAdaper.ViewHolderCarLog>
{

    private ArrayList<TripInfoNew> carlogInformation = new ArrayList<>();
    private LayoutInflater layoutInflater;

    double total_distance = 0;

    View view ;

    public SevenDayTripAdaper(Context context)
    {
        layoutInflater = LayoutInflater.from(context);

    }

    public void setTripsData(ArrayList<TripInfoNew> countryInformationsdata)
    {
        this.carlogInformation = countryInformationsdata;
        notifyItemRangeChanged(0, countryInformationsdata.size());
    }

    @Override
    public SevenDayTripAdaper.ViewHolderCarLog onCreateViewHolder(ViewGroup parent, int viewType)
    {

        view = layoutInflater.inflate(R.layout.row_for_trip_seven_days, parent, false);
        SevenDayTripAdaper.ViewHolderCarLog viewHolderScheduleholde = new SevenDayTripAdaper.ViewHolderCarLog(view);
        return viewHolderScheduleholde;
    }

    @Override
    public void onBindViewHolder(SevenDayTripAdaper.ViewHolderCarLog holder, int position) {
        try
        {




            TripInfoNew carLogInformation = carlogInformation.get(position);


            holder.sourceTimeTextView.setText(carLogInformation.getSourceTime());
            holder.sourceDateTextView.setText(carLogInformation.getSourceDate());
            holder.desinationTimeTextView.setText(carLogInformation.getDestinationTime());

            holder.desinationDateTextView.setText(carLogInformation.getDestinationDate());
            holder.sourceAddresstextView.setText(carLogInformation.getSourceAddress());
            holder.destinationAddresstextView.setText(carLogInformation.getDestinationAddress());



            holder.kmsTimeTextView.setText(carLogInformation.getDistance()+" Km");
            holder.kmhrTimeTextView.setText(carLogInformation.getAvgSpeed()+" Kmph");
            holder.minTimeTextView.setText(carLogInformation.getEngineHalts());

            String [] timeData = carLogInformation.getOnOffTime().split("#");
            holder.onTimeTextView.setText(T.getDurationString(Integer.valueOf(timeData[0])));
            //holder.offTimeTextView.setText(T.formatHoursAndMinutes(Integer.valueOf(timeData[1])));

            ArrayList<String> routeLatLongArrayforDistance = carLogInformation.getLatlongRouteArray();

            for(int i = 0; i < routeLatLongArrayforDistance.size() -1 ; i++)
            {
                String [] data1 = routeLatLongArrayforDistance.get(i).split("#");
                String [] data2 = routeLatLongArrayforDistance.get(i+1).split("#");

                double total_distance1 = distanceBetween(new LatLng(Double.valueOf(data1[0]),Double.valueOf(data1[1])), new LatLng(Double.valueOf(data2[0]),Double.valueOf(data2[1])));

                total_distance = total_distance + total_distance1;
            }
            holder.kmsTimeTextView.setText(""+Math.ceil((total_distance/1000))+"KM");

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

    @Override
    public int getItemCount()
    {
        return carlogInformation.size();
    }

    class ViewHolderCarLog extends RecyclerView.ViewHolder implements View.OnClickListener{


        private TextView
                sourceTimeTextView,
                sourceDateTextView,
                desinationTimeTextView,
                onTimeTextView,
                //offTimeTextView,
                desinationDateTextView,
                sourceAddresstextView,
                destinationAddresstextView,

                kmsTimeTextView,
                kmhrTimeTextView,
                minTimeTextView;






        public ViewHolderCarLog(final View itemView)
        {
            super(itemView);

            try
            {



                sourceTimeTextView = (TextView) itemView.findViewById(R.id.sourceTimeTextView);
                sourceDateTextView = (TextView) itemView.findViewById(R.id.sourceDateTextView);
                desinationTimeTextView = (TextView) itemView.findViewById(R.id.desinationTimeTextView);

                desinationDateTextView = (TextView) itemView.findViewById(R.id.desinationDateTextView);
                sourceAddresstextView = (TextView) itemView.findViewById(R.id.sourceAddresstextView);
                destinationAddresstextView = (TextView) itemView.findViewById(R.id.destinationAddresstextView);

                kmsTimeTextView = (TextView) itemView.findViewById(R.id.kmsTimeTextView);
                kmhrTimeTextView = (TextView) itemView.findViewById(R.id.kmhrTimeTextView);
                minTimeTextView = (TextView) itemView.findViewById(R.id.minTimeTextView);

                onTimeTextView = (TextView) itemView.findViewById(R.id.onTimeTextView);
                //offTimeTextView = (TextView) itemView.findViewById(R.id.offTimeTextView);







                itemView.setOnClickListener(this);
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

                try
                {


                    TripInfoNew tripInfoNew = carlogInformation.get(Integer.valueOf(getAdapterPosition()));

                    Bundle latlongBundle = new Bundle();

                    latlongBundle.putStringArrayList("routeLatLong", tripInfoNew.getLatlongRouteArray());
                    latlongBundle.putStringArrayList("haltLatLong", tripInfoNew.getLatlongHaltArray());


                    Intent i = new Intent(v.getContext(), SevenDayTripMapsActivity.class);

                    i.putExtras(latlongBundle);

                    v.getContext().startActivity(i);

//                    final String sourceDate = subItemInformation.getSourceDate();
//                    final String sourceTime = subItemInformation.getSourceTime();
//
//                    final String destinationDate = subItemInformation.getDestinationDate();
//                    final String destinationTime = subItemInformation.getDestinationTime();
//
//                    final String sourceAddress = subItemInformation.getSourceAddress();
//                    final String destinationAddress = subItemInformation.getDestinationAddress();




                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }

    }




}
