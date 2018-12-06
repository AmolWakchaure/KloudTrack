package snsystems.obd.carlog;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import snsystems.obd.R;

/**
 * Created by snsystem_amol on 2/24/2017.
*/
public class CarLogAdapterrr extends RecyclerView.Adapter<CarLogAdapterrr.ViewHolderCarLog> {

    private ArrayList<CarLogInfo> carlogInformation = new ArrayList<>();
    private LayoutInflater layoutInflater;

    View view ;

    public CarLogAdapterrr(Context context)
    {
        layoutInflater = LayoutInflater.from(context);

    }

    public void setCarLogData(ArrayList<CarLogInfo> countryInformationsdata) {
        this.carlogInformation = countryInformationsdata;
        notifyItemRangeChanged(0, countryInformationsdata.size());
    }

    @Override
    public ViewHolderCarLog onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.car_log_row, parent, false);
        ViewHolderCarLog viewHolderScheduleholde = new ViewHolderCarLog(view);
        return viewHolderScheduleholde;
    }

    @Override
    public void onBindViewHolder(ViewHolderCarLog holder, int position) {
        try
        {

            CarLogInfo carLogInformation = carlogInformation.get(position);



            holder.avgSpeedTextView.setText(carLogInformation.getAvgSpeed());
            holder.maxSpeedTextView.setText(carLogInformation.getMax_speed());
            holder.haltsTextView.setText(carLogInformation.getNo_of_halts());
            holder.totalKmTextView.setText(carLogInformation.getTotal_km());
            holder.onTimeTextView.setText(carLogInformation.getVehicle_on_time());
            holder.offSpeedTextView.setText(carLogInformation.getVehicle_off_time());

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

    class ViewHolderCarLog extends RecyclerView.ViewHolder implements View.OnClickListener{


        private TextView
                avgSpeedTextView,
                maxSpeedTextView,
                haltsTextView,
                totalKmTextView,
                onTimeTextView,
                offSpeedTextView;





        public ViewHolderCarLog(final View itemView)
        {
            super(itemView);

            try
            {



                avgSpeedTextView = (TextView) itemView.findViewById(R.id.avgSpeedTextView);
                        maxSpeedTextView = (TextView) itemView.findViewById(R.id.maxSpeedTextView);
                        haltsTextView = (TextView) itemView.findViewById(R.id.haltsTextView);
                        totalKmTextView = (TextView) itemView.findViewById(R.id.totalKmTextView);
                        onTimeTextView = (TextView) itemView.findViewById(R.id.onTimeTextView);
                        offSpeedTextView = (TextView) itemView.findViewById(R.id.offSpeedTextView);




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



            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }

    }




}
