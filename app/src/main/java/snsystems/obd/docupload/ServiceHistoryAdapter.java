package snsystems.obd.docupload;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import snsystems.obd.R;
import snsystems.obd.classes.T;


/**
 * Created by shree on 08-Feb-17.

public class ServiceHistoryAdapter {
}*/
public class ServiceHistoryAdapter extends RecyclerView.Adapter<ServiceHistoryAdapter.ViewHolderCarLog> {

    private ArrayList<ServiceHistoryInformation> carlogInformation = new ArrayList<>();
    private LayoutInflater layoutInflater;

    View view ;

    public ServiceHistoryAdapter(Context context)
    {
        layoutInflater = LayoutInflater.from(context);

    }

    public void setHistoryData(ArrayList<ServiceHistoryInformation> countryInformationsdata) {
        this.carlogInformation = countryInformationsdata;
        notifyItemRangeChanged(0, countryInformationsdata.size());
    }

    @Override
    public ViewHolderCarLog onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.row_for_service_history, parent, false);
        ViewHolderCarLog viewHolderScheduleholde = new ViewHolderCarLog(view);
        return viewHolderScheduleholde;
    }

    @Override
    public void onBindViewHolder(ViewHolderCarLog holder, int position) {
        try
        {



            ServiceHistoryInformation carLogInformation = carlogInformation.get(position);



            holder.historyDate.setText(T.changeDateFormat(carLogInformation.getServiceDate()));
            holder.historyCost.setText(carLogInformation.getServiceCharges());
            holder.historyRemark.setText(carLogInformation.getServiceRemark());



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
                historyDate,
                historyCost,
                historyRemark;




        public ViewHolderCarLog(final View itemView)
        {
            super(itemView);

            try
            {



                historyDate = (TextView) itemView.findViewById(R.id.historyDate);
                        historyCost = (TextView) itemView.findViewById(R.id.historyCost);
                        historyRemark = (TextView) itemView.findViewById(R.id.historyRemark);





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
