package snsystems.obd.maintenance;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import snsystems.obd.R;
import snsystems.obd.alerts.AlertInformation;
import snsystems.obd.database.DBHelper;

/**
 * Created by snsystem_amol on 2/27/2017.
 */

//public class MaintenanceReportsAdapter {
//}

public class MaintenanceReportsAdapter extends RecyclerView.Adapter<MaintenanceReportsAdapter.ViewHolderCarLog> {

    private ArrayList<MaintewnanceInformation> carlogInformation = new ArrayList<>();
    private LayoutInflater layoutInflater;

    View view ;

    public MaintenanceReportsAdapter(Context context)
    {
        layoutInflater = LayoutInflater.from(context);

    }

    public void setMaintenanceData(ArrayList<MaintewnanceInformation> countryInformationsdata) {
        this.carlogInformation = countryInformationsdata;
        notifyItemRangeChanged(0, countryInformationsdata.size());
    }

    @Override
    public MaintenanceReportsAdapter.ViewHolderCarLog onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.row_for_maintenance_reports, parent, false);
        MaintenanceReportsAdapter.ViewHolderCarLog viewHolderScheduleholde = new MaintenanceReportsAdapter.ViewHolderCarLog(view);
        return viewHolderScheduleholde;
    }

    @Override
    public void onBindViewHolder(MaintenanceReportsAdapter.ViewHolderCarLog holder, int position) {
        try
        {

            //vehicleAddressTextView.setText(Html.fromHtml("<b>Address: </b>"+addressV));
            MaintewnanceInformation carLogInformation = carlogInformation.get(position);

            holder.textViewVehicleName.setText(carLogInformation.getVehicleName());
            holder.textViewMaintenanceTypeData.setText(carLogInformation.getMaintenanceDetails());
            holder.textViewOdoReading.setText(Html.fromHtml("<b>ODO Reading: </b>"+carLogInformation.getOdoReading()));
            holder.textViewCost.setText(Html.fromHtml("<b>Cost: </b>"+carLogInformation.getmCost()));
            holder.textViewDate.setText(Html.fromHtml("<b>Date: </b>"+carLogInformation.getmDate()));
            holder.textViewAlerts.setText(Html.fromHtml("<b>Alerts: </b>"+carLogInformation.getmAlerts()));


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
                textViewVehicleName,
                textViewMaintenanceTypeData,
                textViewOdoReading,
                textViewCost,
                textViewDate,
                textViewAlerts;




        public ViewHolderCarLog(final View itemView)
        {
            super(itemView);

            try
            {


                textViewVehicleName = (TextView) itemView.findViewById(R.id.textViewVehicleName);
                textViewMaintenanceTypeData = (TextView) itemView.findViewById(R.id.textViewMaintenanceTypeData);
                textViewOdoReading = (TextView) itemView.findViewById(R.id.textViewOdoReading);
                textViewCost = (TextView) itemView.findViewById(R.id.textViewCost);
                textViewDate = (TextView) itemView.findViewById(R.id.textViewDate);
                textViewAlerts = (TextView) itemView.findViewById(R.id.textViewAlerts);

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
