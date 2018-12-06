package snsystems.obd.geofencing;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;
import snsystems.obd.classes.MyApplication;
import snsystems.obd.classes.T;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.trips.PlacesAutoCompleteAdapter;
import snsystems.obd.trips.RecyclerItemClickListener;

public class EditeGeofenceActivity extends AnimationActivity implements GoogleApiClient.ConnectionCallbacks,// for google api client
        GoogleApiClient.OnConnectionFailedListener// for google api client
{

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.geofencePointAddressAppCompatEditText)
    EditText geofencePointAddressAppCompatEditText;

    @Bind(R.id.geofenceRadious)
    TextView geofenceRadious;

    @Bind(R.id.radiousSeekbar)
    AppCompatSeekBar radiousSeekbar;


    @Bind(R.id.geofenceSecondPointAddressAppCompatEditText)
    EditText geofenceSecondPointAddressAppCompatEditText;

    @Bind(R.id.secondGeofenceRadious)
    TextView secondGeofenceRadious;

    @Bind(R.id.secondRadiousSeekbar)
    AppCompatSeekBar secondRadiousSeekbar;

    @Bind(R.id.firstGeofenceAddressRecyclerView)
    RecyclerView firstGeofenceAddressRecyclerView;

    @Bind(R.id.secondGeofenceAddressRecyclerView)
    RecyclerView secondGeofenceAddressRecyclerView;


    protected GoogleApiClient
            mGoogleApiClient;

    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(
            new LatLng(-0, 0), new LatLng(0, 0));

    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;

    //store lat long of points
    private String firstGeofenceLatLong,secondGeofenceLatLong;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edite_geofence);

        ButterKnife.bind(this);
        buildGoogleApiClient();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Geofence");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initialize();
        setListeners();

        getGeofenceData();

    }
    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            Log.v("Google API", "Dis-Connecting");
            mGoogleApiClient.disconnect();
        }
    }

    private void initialize() {

        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(EditeGeofenceActivity.this, R.layout.searchview_adapter, mGoogleApiClient, BOUNDS_INDIA, null);


        firstGeofenceAddressRecyclerView.setLayoutManager(new LinearLayoutManager(EditeGeofenceActivity.this));
        firstGeofenceAddressRecyclerView.setAdapter(mAutoCompleteAdapter);

        secondGeofenceAddressRecyclerView.setLayoutManager(new LinearLayoutManager(EditeGeofenceActivity.this));
        secondGeofenceAddressRecyclerView.setAdapter(mAutoCompleteAdapter);

    }
    //build google api client
    protected synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(EditeGeofenceActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
    }
    @Override
    protected void onResume() {
        super.onResume();


        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            Log.v("Google API", "Connecting");
            mGoogleApiClient.connect();
        }

        //googleApiClient.connect();
    }

    private void getGeofenceData() {

        String device_id_mail = S.getDeviceIdUserName(MyApplication.db);
        String [] data = device_id_mail.split("#");

        //select geofence data from sqlite
        ArrayList<String> GEOFENCE_DATA = S.selectGeofenceData(MyApplication.db,data[0],"on");

        if(!GEOFENCE_DATA.isEmpty())
        {

            if(GEOFENCE_DATA.size() == 1)
            {
                String [] geofenceData = GEOFENCE_DATA.get(0).split("#");

                String device_id = geofenceData[0];
                String [] address_latlong = geofenceData[1].split(",");
                String radious = geofenceData[2];
                String active_status = geofenceData[3];

                String addressPint = T.getAddress(EditeGeofenceActivity.this,Double.valueOf(address_latlong[0]),Double.valueOf(address_latlong[1]));

                if(addressPint.trim().isEmpty() || addressPint == null)
                {
                    geofencePointAddressAppCompatEditText.setText(address_latlong[0]+","+address_latlong[1]);
                }
                else
                {
                    geofencePointAddressAppCompatEditText.setText(addressPint);
                }

                geofenceRadious.setText(""+(Integer.valueOf(radious)/1000));
                radiousSeekbar.setProgress(Integer.valueOf(radious));
            }
            else if(GEOFENCE_DATA.size() == 2)
            {
                String [] geofenceData = GEOFENCE_DATA.get(0).split("#");

                String device_id = geofenceData[0];
                String [] address_latlong = geofenceData[1].split(",");
                String radious = geofenceData[2];
                String active_status = geofenceData[3];

                String addressPint = T.getAddress(EditeGeofenceActivity.this,Double.valueOf(address_latlong[0]),Double.valueOf(address_latlong[1]));

                if(addressPint == null)
                {
                    geofencePointAddressAppCompatEditText.setText(address_latlong[0]+","+address_latlong[1]);
                }
                else
                {
                    geofencePointAddressAppCompatEditText.setText(addressPint);
                }

                geofenceRadious.setText(""+(Integer.valueOf(radious)/1000));
                radiousSeekbar.setProgress(Integer.valueOf(radious));

                //two
                String [] geofenceData1 = GEOFENCE_DATA.get(1).split("#");

                String device_id1 = geofenceData1[0];
                String [] address_latlong1 = geofenceData1[1].split(",");
                String radious1 = geofenceData1[2];
                String active_status1 = geofenceData1[3];

                String addressPint1 = T.getAddress(EditeGeofenceActivity.this,Double.valueOf(address_latlong1[0]),Double.valueOf(address_latlong1[1]));

                if(addressPint1 == null)
                {
                    geofenceSecondPointAddressAppCompatEditText.setText(address_latlong1[0]+","+address_latlong1[1]);
                }
                else
                {
                    geofenceSecondPointAddressAppCompatEditText.setText(addressPint1);
                }

                secondGeofenceRadious.setText(""+(Integer.valueOf(radious1)/1000));
                secondRadiousSeekbar.setProgress(Integer.valueOf(radious1));
            }

        }

    }

    private void setListeners()
    {
        geofencePointAddressAppCompatEditText.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                firstGeofenceAddressRecyclerView.setVisibility(View.VISIBLE);
                if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {
                    mAutoCompleteAdapter.getFilter().filter(s.toString());
                } else if (!mGoogleApiClient.isConnected()) {
                    Toast.makeText(EditeGeofenceActivity.this, "api not connected", Toast.LENGTH_SHORT).show();

                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        });

        firstGeofenceAddressRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(EditeGeofenceActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        final PlacesAutoCompleteAdapter.PlaceAutocomplete item = mAutoCompleteAdapter.getItem(position);
                        final String placeId = String.valueOf(item.placeId);
                        Log.i("TAG", "Autocomplete item selected: " + item.description);
                        geofencePointAddressAppCompatEditText.setText(item.description);
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);



                        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                                .getPlaceById(mGoogleApiClient, placeId);
                        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                            @Override
                            public void onResult(PlaceBuffer places)
                            {
                                if (places.getCount() == 1)
                                {


                                    final Place myPlace = places.get(0);
                                    LatLng latLng = myPlace.getLatLng();

                                    firstGeofenceLatLong = latLng.latitude+","+latLng.longitude;

                                    T.t(EditeGeofenceActivity.this,"First :"+firstGeofenceLatLong);




                                }
                                else
                                {
                                    Toast.makeText(EditeGeofenceActivity.this, "SOMETHING_WENT_WRONG", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });



                        firstGeofenceAddressRecyclerView.setVisibility(View.GONE);
                    }
                })
        );

        geofenceSecondPointAddressAppCompatEditText.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                secondGeofenceAddressRecyclerView.setVisibility(View.VISIBLE);
                if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {
                    mAutoCompleteAdapter.getFilter().filter(s.toString());
                } else if (!mGoogleApiClient.isConnected()) {
                    Toast.makeText(EditeGeofenceActivity.this, "api not connected", Toast.LENGTH_SHORT).show();

                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        });

        secondGeofenceAddressRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(EditeGeofenceActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        final PlacesAutoCompleteAdapter.PlaceAutocomplete item = mAutoCompleteAdapter.getItem(position);
                        final String placeId = String.valueOf(item.placeId);
                        Log.i("TAG", "Autocomplete item selected: " + item.description);
                        geofenceSecondPointAddressAppCompatEditText.setText(item.description);
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);



                        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                                .getPlaceById(mGoogleApiClient, placeId);
                        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                            @Override
                            public void onResult(PlaceBuffer places)
                            {
                                if (places.getCount() == 1)
                                {



                                    final Place myPlace = places.get(0);
                                    LatLng latLng = myPlace.getLatLng();

                                    secondGeofenceLatLong = latLng.latitude+","+latLng.longitude;
                                    T.t(EditeGeofenceActivity.this,"Second :"+secondGeofenceLatLong);

                                }
                                else
                                {
                                    Toast.makeText(EditeGeofenceActivity.this, "SOMETHING_WENT_WRONG", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });



                        secondGeofenceAddressRecyclerView.setVisibility(View.GONE);
                    }
                })
        );

        radiousSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b)
            {
                geofenceRadious.setText((progress/1000)+ "Km");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });


        secondRadiousSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b)
            {
                secondGeofenceRadious.setText((progress/1000)+ "Km");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_geofence, menu);
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


    public void editFirstGeofence(View view) {

        /*
        String device_id_mail = S.getDeviceIdUserName(MyApplication.db;
                    String [] data = device_id_mail.split("#");

                    MyApplication.db.storeGeofence(

                            data[0],
                            latLng.latitude+","+latLng.longitude,
                            String.valueOf(1000),
                            "on"

                    );
         */
    }

    public void editSecondGeofence(View view) {
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
