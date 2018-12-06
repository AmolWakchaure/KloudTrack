package snsystems.obd.notificationalerts;

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
import snsystems.obd.alerts.AlertInformation;
import snsystems.obd.database.DBHelper;

/**
 * Created by snsystem_amol on 3/3/2017.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolderCarLog> {

    private ArrayList<NotificationInformation> carlogInformation = new ArrayList<>();
    private LayoutInflater layoutInflater;

    View view ;

    public NotificationAdapter(Context context)
    {
        layoutInflater = LayoutInflater.from(context);

    }

    public void setAlertData(ArrayList<NotificationInformation> countryInformationsdata) {
        this.carlogInformation = countryInformationsdata;
        notifyItemRangeChanged(0, countryInformationsdata.size());
    }

    @Override
    public NotificationAdapter.ViewHolderCarLog onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.notification_row, parent, false);
        NotificationAdapter.ViewHolderCarLog viewHolderScheduleholde = new NotificationAdapter.ViewHolderCarLog(view);
        return viewHolderScheduleholde;
    }

    @Override
    public void onBindViewHolder(NotificationAdapter.ViewHolderCarLog holder, int position) {
        try
        {

            NotificationInformation carLogInformation = carlogInformation.get(position);

            holder.notificationNameTextView.setText(carLogInformation.getNotificationName());
            holder.levelTextView.setText(carLogInformation.getNotificationLevel());
            holder.alertDateTime.setText(carLogInformation.getNotificationDateTime());



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
                notificationNameTextView,
                levelTextView,
                alertDateTime;





        public ViewHolderCarLog(final View itemView)
        {
            super(itemView);

            try
            {
                notificationNameTextView = (TextView) itemView.findViewById(R.id.notificationNameTextView);
                levelTextView = (TextView) itemView.findViewById(R.id.levelTextView);
                alertDateTime = (TextView) itemView.findViewById(R.id.alertDateTime);

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