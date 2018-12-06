package snsystems.obd.fueltrack;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;
import snsystems.obd.classes.T;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;

public class FuelCalculationActivity extends AnimationActivity {

    private SharedPreferences preferences;

    @Bind(R.id.ltrsOneTextView)
    TextView ltrsOneTextView;

    @Bind(R.id.kmOneTextView)
    TextView kmOneTextView;

    @Bind(R.id.outputOneTextView)
    TextView outputOneTextView;

    @Bind(R.id.ltrsTwoTextView)
    TextView ltrsTwoTextView;

    @Bind(R.id.kmTwoTextView)
    TextView kmTwoTextView;

    @Bind(R.id.ouputTwoTextView)
    TextView ouputTwoTextView;

    @Bind(R.id.priceThreeTextView)
    TextView priceThreeTextView;

    @Bind(R.id.kmThreeTextView)
    TextView kmThreeTextView;

    @Bind(R.id.ouputThreeTextView)
    TextView ouputThreeTextView;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel_calculation);



        intitialise();



        getBundleData();
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

    private void intitialise() {

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



    }

    private void getBundleData() {

       // vehicle_name

        Bundle bundle = getIntent().getExtras();

        if(bundle != null)
        {
            String vehicle_name = bundle.getString("vehicle_name");

            String costLtrs = S.getCalculationDetails(new DBHelper(FuelCalculationActivity.this),vehicle_name);

            String [] data = costLtrs.split("#");


            preferences = getSharedPreferences("reading", 0);
            SharedPreferences.Editor editor = preferences.edit();
            //editor.putString("status", "000");
            editor.commit();

            String readingOld = preferences.getString("status","");

//            Log.e("COST_DATA","Cost : "+data[0]);
//            Log.e("COST_DATA","Ltrs : "+data[1]);
//            Log.e("COST_DATA","Km : "+readingOld);

            setCalculationData(data[0],data[1],readingOld);


            //get cost , ltrs, km
        }
        else
        {
            T.t(FuelCalculationActivity.this,"Bundle empty");
        }
    }

    private void setCalculationData(String cost, String ltrs, String km)
    {

        //60 litres / 800km * 100 = 7.5 litres
        //800km / 60 litres = 13.3km per litre
        // $130 / 800km  = $0.16 per kilometre.

        ltrsOneTextView.setText(ltrs);
        kmOneTextView.setText(km);
        outputOneTextView.setText(""+Math.ceil(Float.valueOf(ltrs) / Float.valueOf(km) * 100));

        ltrsTwoTextView.setText(ltrs);
        kmTwoTextView.setText(km);
        ouputTwoTextView.setText(""+Math.ceil(Float.valueOf(km) / Float.valueOf(ltrs)));

        priceThreeTextView.setText(cost);
        kmThreeTextView.setText(km);
        ouputThreeTextView.setText(""+Math.ceil(Float.valueOf(cost) / Float.valueOf(km)));




    }
}
