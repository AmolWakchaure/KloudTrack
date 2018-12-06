package snsystems.obd.alerts;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;
import snsystems.obd.classes.MyApplication;
import snsystems.obd.classes.T;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;


public class ManageAlertsActivity extends AnimationActivity {


    private Toolbar
            toolbar;

    private RecyclerView
            manage_alerts_RecyclerView;

    private SwipeRefreshLayout
            activity_main_swipe_refresh_layout;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_alerts);

        initializeWidgets();
        insertAlerts();

       // setListeners();

        allocateData();

        /*switchButton = (SwitchButton)findViewById(R.id.switchButton);

        switchButton.setChecked(false);
        //attach a listener to check for changes in state
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)
                {
                    T.s(switchButton,"Switch is currently ON");
                }
                else
                {
                    T.s(switchButton, "Switch is currently OFF");
                }
            }
        });

        //check the current state before we display the screen
        if(switchButton.isChecked())
        {
            T.s(switchButton, "Switch is currently ON");
        }
        else
        {
            T.s(switchButton, "Switch is currently OFF");
        }*/
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
    private void setListeners() {

        //for swipe to refresh
        activity_main_swipe_refresh_layout.setColorSchemeResources(R.color.material_red_400, R.color.green, R.color.accentColor);
        activity_main_swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //refresh method call goes here

                        T.s(toolbar, "Refreshing...");


                        activity_main_swipe_refresh_layout.setRefreshing(false);
                    }
                }, 2500);
            }
        });

        // for bottom refresh
        manage_alerts_RecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = manage_alerts_RecyclerView.getChildCount();
                totalItemCount = linearLayoutManager.getItemCount();
                firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached

                    // Do something


                    T.s(toolbar, "Scrolling here...");

                    loading = true;
                }
            }
        });


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

    private void initializeWidgets()
    {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
       // getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_white_24dp);

        manage_alerts_RecyclerView = (RecyclerView)findViewById(R.id.manage_alerts_RecyclerView);
        linearLayoutManager = new LinearLayoutManager(ManageAlertsActivity.this);
        manage_alerts_RecyclerView.setLayoutManager(linearLayoutManager);
        carLogAdapter = new AlertAdapter(ManageAlertsActivity.this);
        manage_alerts_RecyclerView.setAdapter(carLogAdapter);


        activity_main_swipe_refresh_layout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manage_alerts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {

            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
