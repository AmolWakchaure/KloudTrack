package snsystems.obd.drawer;

/**
 * Created by RAJE on 1/21/2016.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.Collections;
import java.util.List;

import snsystems.obd.R;


/**
 * Created by !! Amol !! on 23-12-2015.
 */
public class NavigationDrawerMenu extends RecyclerView.Adapter<NavigationDrawerMenu.MyViewHolder>{

    private LayoutInflater inflator;
    private Context context;
    List<NavigationDrawerInformation> data= Collections.emptyList();

   public NavigationDrawerMenu(Context context, List<NavigationDrawerInformation> data)
    {
        this.context=context;
        inflator=LayoutInflater.from(context);
        this.data=data;

    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view=inflator.inflate(R.layout.navigation_drawer_row, parent, false);
        MyViewHolder holder=new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        NavigationDrawerInformation current=data.get(position);

        //Log.e("Binder For nav item pos","Binder Call"+position);
        holder.textView.setText(current.title);
        holder.imageView.setImageResource(current.iconId);



    }


    @Override
    public int getItemCount() {
        return data.size();
    }
    class  MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imageView;


        public MyViewHolder(View itemView) {
            super(itemView);

            textView= (TextView) itemView.findViewById(R.id.listText);
            imageView= (ImageView) itemView.findViewById(R.id.listIcon);


        }



    }

}
