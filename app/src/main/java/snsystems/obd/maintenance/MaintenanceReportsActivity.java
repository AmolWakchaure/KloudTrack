package snsystems.obd.maintenance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;
import snsystems.obd.classes.T;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;

public class MaintenanceReportsActivity extends AnimationActivity
{

    @Bind(R.id.maintenaceReportsRecyclerView)
    RecyclerView maintenaceReportsRecyclerView;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private MaintenanceReportsAdapter maintenanceReportsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_reports);

        initialize();

        getDeviceWiseMaintenanceData();
    }

    private void getDeviceWiseMaintenanceData()
    {


        Bundle bundle = getIntent().getExtras();

        if(bundle != null)
        {
            ArrayList<MaintewnanceInformation> maintenanceData = S.getMaintenanceVehicleWiseData(new DBHelper(MaintenanceReportsActivity.this),bundle.getString("vehicle_name"));

            if(!maintenanceData.isEmpty())
            {

                maintenanceReportsAdapter.setMaintenanceData(maintenanceData);
                maintenanceReportsAdapter.notifyDataSetChanged();

            }
            else
            {
                T.t(MaintenanceReportsActivity.this,"Maintenance Details not Found");
            }
        }
        else
        {
            T.t(MaintenanceReportsActivity.this,"Empty bundle");
        }






    }

    private void initialize() {

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        maintenaceReportsRecyclerView.setLayoutManager(new LinearLayoutManager(MaintenanceReportsActivity.this));
        maintenanceReportsAdapter = new MaintenanceReportsAdapter(MaintenanceReportsActivity.this);
        maintenaceReportsRecyclerView.setAdapter(maintenanceReportsAdapter);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_maintenance_reports, menu);
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
