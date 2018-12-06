package snsystems.obd.fueltrack;

import android.app.backup.FullBackupDataOutput;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.wallet.FullWalletRequest;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;
import snsystems.obd.classes.T;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.maintenance.MaintenanceReportsActivity;
import snsystems.obd.maintenance.MaintenanceReportsAdapter;
import snsystems.obd.maintenance.MaintewnanceInformation;

public class FuelReportsActivity extends AnimationActivity {


    @Bind(R.id.maintenaceReportsRecyclerView)
    RecyclerView maintenaceReportsRecyclerView;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private FuelReportsAdapter maintenanceReportsAdapter;
    private SharedPreferences preferences;

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel_reports);

        initialize();

        getReading();
        getDeviceWiseMaintenanceData();


    }

    private void getReading() {

        preferences = getSharedPreferences("reading", 0);
        SharedPreferences.Editor editor = preferences.edit();
       // editor.putString("status", "000");
        editor.commit();
        String reading = preferences.getString("status","");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fuel_reports, menu);
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

    private void getDeviceWiseMaintenanceData()
    {
        bundle = getIntent().getExtras();

        if(bundle != null)
        {
            ArrayList<FuelInformation> maintenanceData = S.getFuelData(new DBHelper(FuelReportsActivity.this),bundle.getString("vehicle_name"));

            if(!maintenanceData.isEmpty())
            {

                maintenanceReportsAdapter.setMaintenanceData(maintenanceData);
                maintenanceReportsAdapter.notifyDataSetChanged();

            }
            else
            {
                T.t(FuelReportsActivity.this,"fuel Details not Found");
            }

        }
        else
        {
            T.t(FuelReportsActivity.this,"bundle empty");
        }



    }
    private void initialize() {

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        maintenaceReportsRecyclerView.setLayoutManager(new LinearLayoutManager(FuelReportsActivity.this));
        maintenanceReportsAdapter = new FuelReportsAdapter(FuelReportsActivity.this);
        maintenaceReportsRecyclerView.setAdapter(maintenanceReportsAdapter);



    }

    public void displayCalculations(View view)
    {

        Intent i = new Intent(FuelReportsActivity.this,FuelCalculationActivity.class);
        i.putExtra("vehicle_name",bundle.getString("vehicle_name"));
        startActivity(i);
    }
}
