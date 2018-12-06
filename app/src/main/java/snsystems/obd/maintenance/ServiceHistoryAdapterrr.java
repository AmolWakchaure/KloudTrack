package snsystems.obd.maintenance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import snsystems.obd.R;
import snsystems.obd.classes.T;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;


/**
 * Created by snsystem_amol on 3/18/2017.
 */
public class ServiceHistoryAdapterrr extends RecyclerView.Adapter<ServiceHistoryAdapterrr.ViewHolderCarLog>
{

        private ArrayList<ServiceHistoryInformationn> carlogInformation = new ArrayList<>();
    private LayoutInflater layoutInflater;

    private Context context;

    View view ;

    public ServiceHistoryAdapterrr(Context context)
    {
        layoutInflater = LayoutInflater.from(context);

        this.context = context;

    }

    public void setHistoryData(ArrayList<ServiceHistoryInformationn> countryInformationsdata) {
        this.carlogInformation = countryInformationsdata;
        notifyItemRangeChanged(0, countryInformationsdata.size());
    }

    @Override
    public ServiceHistoryAdapterrr.ViewHolderCarLog onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.service_history_row, parent, false);
        ServiceHistoryAdapterrr.ViewHolderCarLog viewHolderScheduleholde = new ServiceHistoryAdapterrr.ViewHolderCarLog(view);
        return viewHolderScheduleholde;
    }

    @Override
    public void onBindViewHolder(ServiceHistoryAdapterrr.ViewHolderCarLog holder, int position) {
        try
        {



            ServiceHistoryInformationn carLogInformation = carlogInformation.get(position);

            String [] dataData = carLogInformation.getMaintenanceDate().split(" ");

            holder.dateTextView.setText(Html.fromHtml("<b>Date : </b>"+T.changeDateFormat(dataData[0])));
            holder.costTextView.setText(Html.fromHtml("<b>Cost : </b>"+"₹ "+carLogInformation.getServiceCost()));
            holder.remarkTextView.setText(Html.fromHtml("<b>Remark : </b>"+carLogInformation.getServiceRemark()));

            holder.vehicleNameTextView.setText(Html.fromHtml("<b>Vehicle Name : </b>"+S.getNameByDEviceId(new DBHelper(view.getContext()),carLogInformation.getDeviceId())));

            holder.serviceStatusTextView.setText(Html.fromHtml("<b>Status : </b>"+carLogInformation.getServiceStatus()));

            String serviceType = carLogInformation.getMaintenanceType();

            if(serviceType.equals("Select Service Type"))
            {
                holder.serviceTypeTextView.setVisibility(View.GONE);
            }
            else
            {
                holder.serviceTypeTextView.setText(Html.fromHtml("<b>Type : </b>"+carLogInformation.getMaintenanceType()));
            }

            String otherLovProblems = carLogInformation.getServiceOtherLov();

            if(otherLovProblems.equals("Lov"))
            {
                holder.serviceOtherProblemsTextView.setVisibility(View.GONE);
            }
            else
            {
                holder.serviceOtherProblemsTextView.setText(Html.fromHtml("<b>Other Problems : </b>"+carLogInformation.getServiceOtherLov()));
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
                dateTextView,
                costTextView,
                remarkTextView,

                vehicleNameTextView,
                serviceTypeTextView,
                serviceStatusTextView,
                serviceOtherProblemsTextView;

        private Button
                editeLinearLayout;




        public ViewHolderCarLog(final View itemView)
        {
            super(itemView);

            try
            {



                dateTextView = (TextView) itemView.findViewById(R.id.dateTextView);
                costTextView = (TextView) itemView.findViewById(R.id.costTextView);
                remarkTextView = (TextView) itemView.findViewById(R.id.remarkTextView);

                vehicleNameTextView = (TextView) itemView.findViewById(R.id.vehicleNameTextView);
                serviceTypeTextView = (TextView) itemView.findViewById(R.id.serviceTypeTextView);
                serviceStatusTextView = (TextView) itemView.findViewById(R.id.serviceStatusTextView);
                serviceOtherProblemsTextView = (TextView) itemView.findViewById(R.id.serviceOtherProblemsTextView);

                editeLinearLayout = (Button) itemView.findViewById(R.id.editeLinearLayout);




                editeLinearLayout.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        ServiceHistoryInformationn  subItemInformation = carlogInformation.get(Integer.valueOf(getAdapterPosition()));

                        Intent i = new Intent(view.getContext(),UpdateServiceScheduleActivity.class);

                        i.putExtra("id",subItemInformation.getId());
                        i.putExtra("deviceid",subItemInformation.getDeviceId());
                        i.putExtra("maintenanceDate",subItemInformation.getMaintenanceDate());
                        i.putExtra("maintenanceType",subItemInformation.getMaintenanceType());
                        i.putExtra("serviceRemark",subItemInformation.getServiceRemark());
                        i.putExtra("serviceStatus",subItemInformation.getServiceStatus());
                        i.putExtra("serviceOtherLov",subItemInformation.getServiceOtherLov());
                        i.putExtra("serviceCost",subItemInformation.getServiceCost());
                        ((Activity) context).startActivityForResult(i, 6);
                        //view.getContext().startActivity(i);




                    }
                });


                //reschedule.setOnClickListener(this);
                /*tContactNumber_TextView.setOnClickListener(this);*/


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
