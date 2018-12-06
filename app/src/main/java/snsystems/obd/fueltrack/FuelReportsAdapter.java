package snsystems.obd.fueltrack;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import snsystems.obd.R;
import snsystems.obd.maintenance.MaintewnanceInformation;

/**
 * Created by snsystem_amol on 2/27/2017.
 */

//public class FuelReportsAdapter {
//}
public class FuelReportsAdapter extends RecyclerView.Adapter<FuelReportsAdapter.ViewHolderCarLog> {

    private ArrayList<FuelInformation> carlogInformation = new ArrayList<>();
    private LayoutInflater layoutInflater;

    View view ;

    public FuelReportsAdapter(Context context)
    {
        layoutInflater = LayoutInflater.from(context);

    }

    public void setMaintenanceData(ArrayList<FuelInformation> countryInformationsdata) {
        this.carlogInformation = countryInformationsdata;
        notifyItemRangeChanged(0, countryInformationsdata.size());
    }

    @Override
    public FuelReportsAdapter.ViewHolderCarLog onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.row_for_fuel, parent, false);
        FuelReportsAdapter.ViewHolderCarLog viewHolderScheduleholde = new FuelReportsAdapter.ViewHolderCarLog(view);
        return viewHolderScheduleholde;
    }

    @Override
    public void onBindViewHolder(FuelReportsAdapter.ViewHolderCarLog holder, int position) {
        try
        {
            //vehicleAddressTextView.setText(Html.fromHtml("<b>Address: </b>"+addressV));
            FuelInformation carLogInformation = carlogInformation.get(position);

            String cost = carLogInformation.getCost();
            String oldKm = carLogInformation.getOldKm();
            String newKm = carLogInformation.getNewKm();
            String ltrs = carLogInformation.getFuelLtrs();

            holder.textViewVehicleName.setText(carLogInformation.getVehicleName());
            holder.textViewdate.setText(Html.fromHtml("<b>Date: </b>"+carLogInformation.getDate()));
            holder.textViewFuelType.setText(Html.fromHtml("<b>Fuel Type: </b>"+carLogInformation.getFuelType()));
            holder.textViewfuelLtrs.setText(Html.fromHtml("<b>Fuel: </b>"+carLogInformation.getFuelLtrs()+" Ltr"));
            holder.textViewCost.setText(Html.fromHtml("<b>Cost: </b>"+carLogInformation.getCost()));



            holder.textViewltrperhundred.setText(Html.fromHtml("<b>Ltr per 100km: </b>"+Math.ceil(Float.valueOf(ltrs) / Float.valueOf(newKm) * 100)));
            holder.textViewkmperltr.setText(Html.fromHtml("<b>Km per ltr: </b>"+Math.ceil(Float.valueOf(newKm) / Float.valueOf(ltrs))));
            holder.textViewpriceperkm.setText(Html.fromHtml("<b>Price per km: </b>"+Math.ceil(Float.valueOf(cost) / Float.valueOf(newKm))));
            holder.textViewOldkm.setText(Html.fromHtml("<b>km: </b>"+oldKm));


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
                textViewdate,
                textViewFuelType,
                textViewfuelLtrs,
                textViewCost,
                textViewltrperhundred,
                textViewkmperltr,
                textViewpriceperkm,
                textViewOldkm;





        public ViewHolderCarLog(final View itemView)
        {
            super(itemView);

            try
            {


                textViewVehicleName = (TextView) itemView.findViewById(R.id.textViewVehicleName);
                textViewdate = (TextView) itemView.findViewById(R.id.textViewdate);
                textViewFuelType = (TextView) itemView.findViewById(R.id.textViewFuelType);
                textViewfuelLtrs = (TextView) itemView.findViewById(R.id.textViewfuelLtrs);
                textViewCost = (TextView) itemView.findViewById(R.id.textViewCost);

                textViewltrperhundred = (TextView) itemView.findViewById(R.id.textViewltrperhundred);
                        textViewkmperltr = (TextView) itemView.findViewById(R.id.textViewkmperltr);
                        textViewpriceperkm = (TextView) itemView.findViewById(R.id.textViewpriceperkm);
                        textViewOldkm = (TextView) itemView.findViewById(R.id.textViewOldkm);


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