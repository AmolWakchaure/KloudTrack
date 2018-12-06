package snsystems.obd.alerts;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import snsystems.obd.R;
import snsystems.obd.classes.T;


/**
 * Created by shree on 26-Dec-16.
*/
public class CarLogAdapter extends RecyclerView.Adapter<CarLogAdapter.ViewHolderCarLog> {

    private ArrayList<CarLogInformation> carlogInformation = new ArrayList<>();
    private LayoutInflater layoutInflater;

    View view ;

    public CarLogAdapter(Context context)
    {
        layoutInflater = LayoutInflater.from(context);

    }

    public void setCarLogData(ArrayList<CarLogInformation> countryInformationsdata) {
        this.carlogInformation = countryInformationsdata;
        notifyItemRangeChanged(0, countryInformationsdata.size());
    }

    @Override
    public ViewHolderCarLog onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.row_for_car_log_details, parent, false);
        ViewHolderCarLog viewHolderScheduleholde = new ViewHolderCarLog(view);
        return viewHolderScheduleholde;
    }

    @Override
    public void onBindViewHolder(ViewHolderCarLog holder, int position) {
        try
        {



            CarLogInformation carLogInformation = carlogInformation.get(position);


            String imageSatus =  carLogInformation.getImageStatus();

            if(imageSatus.equals("on"))
            {
                holder.onOffImageView.setImageResource(R.drawable.ic_car_engine_on_24dp);
                holder.textViewCarLogData.setTextColor(Color.parseColor("#000000"));
            }
            else
            {
                holder.onOffImageView.setImageResource(R.drawable.ic_car_engine_off_24dp);
                holder.textViewCarLogData.setTextColor(Color.parseColor("#000000"));
            }

            String  notificationName = carLogInformation.getEngineStatus().replace("alarm_","").replace("_", " ");
            holder.textViewCarLogData.setText(notificationName.substring(0, 1).toUpperCase() + notificationName.substring(1));
            holder.carLogTimeTextView.setText(T.changeTimeFormat(carLogInformation.getCarlogTime()));
            holder.carLogDateTextView.setText(T.changeDateFormat(carLogInformation.getCarlogDate()));
           // String sts = CountryInformationInformationObject.getStatuss();





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

    class ViewHolderCarLog extends RecyclerView.ViewHolder implements View.OnClickListener {




        private ImageView
                onOffImageView;
        private TextView
                textViewCarLogData,
                carLogTimeTextView,
                carLogDateTextView;


        public ViewHolderCarLog(View itemView)
        {
            super(itemView);

            try
            {

                onOffImageView  = (ImageView) itemView.findViewById(R.id.onOffImageView);
                textViewCarLogData = (TextView) itemView.findViewById(R.id.textViewCarLogData);
                carLogTimeTextView = (TextView) itemView.findViewById(R.id.carLogTimeTextView);
                carLogDateTextView = (TextView) itemView.findViewById(R.id.carLogDateTextView);


               // itemView.setOnClickListener(this);
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

