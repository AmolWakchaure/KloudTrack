package snsystems.obd.alerts;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import snsystems.obd.R;
import snsystems.obd.database.DBHelper;


/**
 * Created by shree on 28-Dec-16.

public class AlertAdapter {
}*/
public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.ViewHolderCarLog> {

    private ArrayList<AlertInformation> carlogInformation = new ArrayList<>();
    private LayoutInflater layoutInflater;

    View view ;

    public AlertAdapter(Context context)
    {
        layoutInflater = LayoutInflater.from(context);

    }

    public void setAlertData(ArrayList<AlertInformation> countryInformationsdata) {
        this.carlogInformation = countryInformationsdata;
        notifyItemRangeChanged(0, countryInformationsdata.size());
    }

    @Override
    public ViewHolderCarLog onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.row_for_manage_alerts, parent, false);
        ViewHolderCarLog viewHolderScheduleholde = new ViewHolderCarLog(view);
        return viewHolderScheduleholde;
    }

    @Override
    public void onBindViewHolder(ViewHolderCarLog holder, int position) {
        try
        {

            AlertInformation carLogInformation = carlogInformation.get(position);


            String  notificationName = carLogInformation.getAlertData().replace("alarm_","").replace("_", " ");
            holder.textViewAlertsData.setText(notificationName.substring(0, 1).toUpperCase() + notificationName.substring(1));

            if(carLogInformation.isAlertStatus())
            {
                holder.switchButton.setChecked(true);
            }
            else
            {
                holder.switchButton.setChecked(false);
            }




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
                textViewAlertsData;

        private SwitchCompat
                switchButton;



        public ViewHolderCarLog(final View itemView)
        {
            super(itemView);

            try
            {


                textViewAlertsData = (TextView) itemView.findViewById(R.id.textViewAlertsData);
                switchButton = (SwitchCompat)itemView.findViewById(R.id.compatSwitch);




                // itemView.setOnClickListener(this);
                switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b)
                    {



                        AlertInformation  alertInformation = carlogInformation.get(Integer.valueOf(getAdapterPosition()));
                        String alertName = alertInformation.getAlertData();
                        if(b)
                        {
                            //update alarm true
                           new DBHelper(itemView.getContext()).updateAlerts(alertName,itemView.getResources().getString(R.string.alert_on));
                        }
                        else
                        {
                            //update alarm false
                            new DBHelper(itemView.getContext()).updateAlerts(alertName,itemView.getResources().getString(R.string.alert_off));
                        }

                    }
                });
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

                /*CallInformation  subItemInformation = countryInformationsadapter.get(Integer.valueOf(getAdapterPosition()));
                String mNumber = subItemInformation.getMobileNumber();

                if (v.getId() == placeCallRelativeLayout.getId())
                {

                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:".concat(mNumber)));
                    v.getContext().startActivity(callIntent);


                }
                if (v.getId() == itemView.getId())
                {
                    //check msg status

                    String statusSSS = checkStatus(v,db,mNumber);

                    if(statusSSS.equals("Not send"))
                    {
                        Intent i = new Intent(v.getContext(), SendMessageActivity.class);
                        i.putExtra("NUMBER", mNumber);
                        v.getContext().startActivity(i);
                    }
                    else
                    {
                        T.displayMessage(v.getContext(),"You are already send address information to this mobile number.");
                    }



                }*/

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }

    }




}
