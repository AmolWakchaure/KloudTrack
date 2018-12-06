package snsystems.obd.alertsnew;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import snsystems.obd.R;
import snsystems.obd.alerts.AlertAdapter;
import snsystems.obd.alerts.AlertInformation;
import snsystems.obd.alerts.ManageAlertsActivity;
import snsystems.obd.classes.MyApplication;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;

/**
 * Created by snsystem_amol on 2/21/2017.
 */

public class SystemDefinedAlertFragment extends Fragment
{

    private View view;


    private RecyclerView
            manage_alerts_RecyclerView;


    private AlertAdapter
            carLogAdapter;

    private LinearLayoutManager
            linearLayoutManager;

    private ArrayList<AlertInformation> CAR_LOG_INFORMATION = new ArrayList<>();

    private Boolean [] alert_status = {

            true,
            false,
            true,
            false,
            true,
            true,
            false,
            true,
            false,
            true,
            true,
            false,
            true,
            false,
            true

    };


    private String [] alert_data = {

            "alarm_engine_on",
            "alarm_petrol",
            "alarm_low_fuel"

    };

    //bottom refresh
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            // Inflate the layout for this fragment
            view =  inflater.inflate(R.layout.system_defined_alert_fragment, container, false);

            initializeWidgets();
            insertAlerts();

            // setListeners();

           // allocateData();





            return view;
        }
    private void allocateData()
    {
        ArrayList<String> ALERTS = S.selectAlerts(MyApplication.db);

        if(!ALERTS.isEmpty())
        {
            for(int i = 0 ; i < ALERTS.size(); i++)
            {

                String [] alerts_info = ALERTS.get(i).split("#");

                AlertInformation carLogInformation = new AlertInformation();

                carLogInformation.setAlertData(alerts_info[0]);
                carLogInformation.setAlertStatus(Boolean.valueOf(alerts_info[1]));

                CAR_LOG_INFORMATION.add(carLogInformation);
            }

            carLogAdapter.setAlertData(CAR_LOG_INFORMATION);
            carLogAdapter.notifyDataSetChanged();
        }






    }
    private void insertAlerts()
    {

        //check already alarm data
        boolean t = S.checkAlerts(MyApplication.db);

        if(!t)
        {
            for(int i = 0; i < alert_data.length; i++)
            {
                MyApplication.db.insertAlerts(alert_data[i]);
            }
        }


    }
    private void initializeWidgets()
    {


        manage_alerts_RecyclerView = (RecyclerView)view.findViewById(R.id.manage_alerts_RecyclerView);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        manage_alerts_RecyclerView.setLayoutManager(linearLayoutManager);
        carLogAdapter = new AlertAdapter(getActivity());
        manage_alerts_RecyclerView.setAdapter(carLogAdapter);


    }
}
