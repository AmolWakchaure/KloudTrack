package snsystems.obd.geofencesnehal;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;

public class MapsActivity extends AnimationActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static String TAG = "MAP LOCATION";
    Context mContext;
//    TextView mLocationMarkerText;
    private LatLng mCenterLatLong;
    private Circle circle;

    private AddressResultReceiver mResultReceiver;

    protected String mAddressOutput;
    protected String mAreaOutput;
    protected String mCityOutput;
    protected String mStateOutput;

    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_places);
        mContext = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select Location ");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_geo_fence);

        mapFragment.getMapAsync(this);
        mResultReceiver = new AddressResultReceiver(new Handler());

        if (checkPlayServices()) {

            if (!AppUtils.isLocationEnabled(mContext)) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setMessage("Location not enabled!");
                dialog.setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                });

                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub

                    }
                });
                dialog.show();
            }
            buildGoogleApiClient();
        } else {
            Toast.makeText(mContext, "Location not supported in this device", Toast.LENGTH_SHORT).show();
        }

    }


    public void markerclick(final double lat_value, final double long_value) {



        LayoutInflater li = LayoutInflater.from(MapsActivity.this);
        View promptsView = li.inflate(R.layout.activity_set_location, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapsActivity.this);

        alertDialogBuilder.setView(promptsView);

        TextView txt_geofence_address = (TextView) promptsView.findViewById(R.id.txt_geofence_address);

        txt_geofence_address.setText(getCompleteAddressString(lat_value, long_value));

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("SELECT", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        MarkerOptions markerOptions = new MarkerOptions();

                        markerOptions.position(new LatLng(lat_value, long_value));

                        markerOptions.title(lat_value + " : " + long_value);

                        mMap.addMarker(new MarkerOptions().position(new LatLng(mCenterLatLong.latitude, mCenterLatLong.longitude)));

                        mMap.addMarker(markerOptions);

                        drawGeofence(new LatLng(lat_value, long_value), 1000);
                        set_geo_fence_name(lat_value, long_value);

                    }
                })
                .setNegativeButton("CHANGE LOCATION", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "OnMapReady");
        mMap = googleMap;

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.d("Camera postion change" + "", cameraPosition + "");
                mCenterLatLong = cameraPosition.target;


                try {

                    Location mLocation = new Location("");
                    mLocation.setLatitude(mCenterLatLong.latitude);
                    mLocation.setLongitude(mCenterLatLong.longitude);

                    startIntentService(mLocation);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            changeMap(mLastLocation);
            Log.d(TAG, "ON connected");

        } else
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, this);

            } catch (Exception e) {
                e.printStackTrace();
            }
        try
        {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location != null)
                changeMap(location);
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            mGoogleApiClient.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //finish();
            }
            return false;
        }
        return true;
    }

    private void changeMap(Location location) {

        Log.d(TAG, "Reaching map" + mMap);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        }

        if (mMap != null) {
            mMap.getUiSettings().setZoomControlsEnabled(false);
            LatLng latLong;

            latLong = new LatLng(location.getLatitude(), location.getLongitude());

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLong).zoom(15f).build();

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

            startIntentService(location);


        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void select_this_location(View view) {
        markerclick(mCenterLatLong.latitude, mCenterLatLong.longitude);
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            mAddressOutput = resultData.getString(AppUtils.LocationConstants.RESULT_DATA_KEY);

            mAreaOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_AREA);

            mCityOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_CITY);
            mStateOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_STREET);

            displayAddressOutput();

            if (resultCode == AppUtils.LocationConstants.SUCCESS_RESULT) {
            }
        }

    }

    protected void displayAddressOutput() {
        try {
            if (mAreaOutput != null)
                // mLocationText.setText(mAreaOutput+ "");
                Log.d(">>", ">>" + mAreaOutput);
            //mLocationText.setText(mAreaOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawGeofence(LatLng latLng, int radiousInMtr) {

        CircleOptions circleOptions = new CircleOptions()

                .center(latLng)
                .radius(radiousInMtr)
                .fillColor(Color.parseColor("#51000000"))
                .strokeColor(Color.RED)
                .strokeWidth(2);
        circle = mMap.addCircle(circleOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    protected void startIntentService(Location mLocation) {

        Intent intent = new Intent(this, FetchIntentAddressService.class);
        intent.putExtra(AppUtils.LocationConstants.RECEIVER, mResultReceiver);

        intent.putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, mLocation);

        startService(intent);
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
//                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");

                    strReturnedAddress.append(returnedAddress.getAddressLine(i));

                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current", "" + strReturnedAddress.toString());
            } else {
                Log.w("My Current", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current", "Canont get Address!" + e.getMessage());
        }
        return strAdd;
    }

    public void set_geo_fence_name(final double lat_value, final double long_value) {
        LayoutInflater li = LayoutInflater.from(MapsActivity.this);
        View promptsView = li.inflate(R.layout.activity_set_geo_fence_name, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapsActivity.this);

        alertDialogBuilder.setView(promptsView);

        final EditText et_geofence_name = (EditText) promptsView.findViewById(R.id.et_geofence_name);
        final CheckBox checkbox_home = (CheckBox) promptsView.findViewById(R.id.checkbox_home);
        final CheckBox checkbox_school = (CheckBox) promptsView.findViewById(R.id.checkbox_school);
        final CheckBox checkbox_work = (CheckBox) promptsView.findViewById(R.id.checkbox_work);

        et_geofence_name.setSelection(et_geofence_name.getText().toString().length());

        checkbox_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox chk = (CheckBox) view;
                if (chk.isChecked()) {

                    et_geofence_name.setText("Home");
                    et_geofence_name.setSelection(et_geofence_name.getText().toString().length());

                    checkbox_school.setEnabled(false);
                    checkbox_work.setEnabled(false);
                }
                else {

                    et_geofence_name.getText().clear();
                    checkbox_school.setEnabled(true);
                    checkbox_work.setEnabled(true);
                }
            }
        });

        checkbox_school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox chk = (CheckBox) view;
                if (chk.isChecked()) {

                    et_geofence_name.setText("School");
                    et_geofence_name.setSelection(et_geofence_name.getText().toString().length());

                    checkbox_home.setEnabled(false);
                    checkbox_work.setEnabled(false);

                } else {

                    et_geofence_name.getText().clear();
                    checkbox_home.setEnabled(true);
                    checkbox_work.setEnabled(true);
                }
            }
        });

        checkbox_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox chk = (CheckBox) view;
                if (chk.isChecked()) {

                    et_geofence_name.setText("Work");
                    et_geofence_name.setSelection(et_geofence_name.getText().toString().length());

                    checkbox_school.setEnabled(false);
                    checkbox_home.setEnabled(false);
                }
                else {

                    et_geofence_name.getText().clear();
                    checkbox_school.setEnabled(true);
                    checkbox_home.setEnabled(true);
                }
            }
        });

             alertDialogBuilder.setCancelable(false)
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(">>", "geo_fence_name>>" + et_geofence_name.getText().toString());

                        String device_id_mail = S.getDeviceIdUserName(new DBHelper(MapsActivity.this));
                        String[] data = device_id_mail.split("#");

                        new DBHelper(MapsActivity.this).storeGeofence(
                                data[0],
                                lat_value + "," + long_value,
                                String.valueOf(1),
                                "on", et_geofence_name.getText().toString()
                        );
                        finish();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });


        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();

    }
}
