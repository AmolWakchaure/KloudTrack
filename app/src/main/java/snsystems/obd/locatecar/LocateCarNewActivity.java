package snsystems.obd.locatecar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import snsystems.obd.R;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;
import snsystems.obd.activity.CustomViewsActivity;
import snsystems.obd.activity.FeedbackActivity;
import snsystems.obd.advertise.DisplayAdvertiseActivity;
import snsystems.obd.advertise.DisplayAdvertiseAdapter;
import snsystems.obd.classes.T;
import snsystems.obd.classes.VolleyResponseClass;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.devicemgt.ActivityAddDevice;
import snsystems.obd.drawer.NavigationDrawerFragment;
import snsystems.obd.drawer.NavigationDrawerFragmentUpdate;
import snsystems.obd.health.VehicleHealthActivity;
import snsystems.obd.interfaces.Constants;
import snsystems.obd.interfaces.VolleyCallback;
import snsystems.obd.interfaces.VolleyErrorCallback;
import snsystems.obd.locatecar.LocateCarActivity;
import snsystems.obd.notificationalerts.DisplayNotificationDetailsActivity;
import snsystems.obd.notificationalerts.MakeAlertActivity;
import snsystems.obd.services.FloatingFaceBubbleService;
import snsystems.obd.sos.SubmitSosContactActivity;
import snsystems.obd.trips.TripMapsActivity;
import snsystems.obd.trips.TripsTabActivity;


public class LocateCarNewActivity extends AnimationActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback,
        LocationListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener
{





    private SupportMapFragment mapFragment;
    private GoogleMap map;

    private final int UPDATE_INTERVAL =  1 * 60 * 1000; // 1 minutes
    private final int FASTEST_INTERVAL = 30 * 1000;  // 30 secs

    @Bind(R.id.toolbar)
    Toolbar toolbar;


    Marker carMarker;

    private GoogleApiClient googleApiClient;




    private LocationRequest locationRequest;
    //advertise


//    @Bind(R.id.toolbar)
//    Toolbar toolbar;

    private DisplayAdvertiseAdapter
            productCategoryAdapter;

    ArrayList<String> vehicleNAmes;

    private String device_id_get;

    private Location lastLocation;


    public int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_locate_car_new);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);



        initGMaps();

        // startServices();
        createGoogleApi();
        boolean c = T.checkConnection(LocateCarNewActivity.this);

        if(c)
        {
            String device_id_get = S.getDeviceId(new DBHelper(LocateCarNewActivity.this));
            getDashBoardDetails(device_id_get);

        }
        else
        {
            T.t(LocateCarNewActivity.this,"Network connection off");
        }
        // insertUserDefinedAlerts();


        //move to 50 Km/s

    }

    private void createGoogleApi()
    {
        //T.t(DashboardActivity.this, "createGoogleApi()");
        if ( googleApiClient == null ) {
            googleApiClient = new GoogleApiClient.Builder( this )
                    .addConnectionCallbacks( this )
                    .addOnConnectionFailedListener( this )
                    .addApi( LocationServices.API )
                    .build();
        }
    }

    private void getDashBoardDetails(String device_id) {


        String [] parameters =
                {
                        "device_id"+"#"+device_id
                        //"device_id"+"#"+"40000003"

                };
        VolleyResponseClass.getResponseProgressDialogError(
                new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        //Log.e("VolleyResponse", "" + result);

                        //T.t(DisplayAdvertiseActivity.this,""+result);
                        // {"success":"1","dashboard_data":[{"fuel_level":"4","rpm":"200","vehicle_speed":"85","latitude":"18.5018","longitude":"73.8636","vehicle_name":"honda city","vehicle_make":"honda","vehicle_model":"honda city","vehicle_make_year":"2016","vehicle_gear_type":"asa","vehicle_fuel_type":"dsf"}]}

                        parseDashboardResponse(result);


                    }
                },
                new VolleyErrorCallback() {
                    @Override
                    public void onError(VolleyError result) {

                        T.t(LocateCarNewActivity.this,""+result);

                    }
                },
                LocateCarNewActivity.this,
                getResources().getString(R.string.webUrl) + "" + getResources().getString(R.string.displayDashboard),
                toolbar,
                parameters,
                "Locating Vehicle...");


    }
    private void parseDashboardResponse(String result) {


        String latitude = Constants.NA;
        String longitude = Constants.NA;
        String vehicle_number = Constants.NA;//add again
        String vehicle_name = Constants.NA;
        String vehicle_make = Constants.NA;
        String vehicle_model = Constants.NA;
        String vehicle_make_year = Constants.NA;
        String vehicle_gear_type = Constants.NA;
        String vehicle_fuel_type = Constants.NA;

        try
        {
            if(result != null || result.length() > 0)
            {
                Object json = new JSONTokener(result).nextValue();
                if (json instanceof JSONObject)
                {
                    JSONObject tripsJsonObject = new JSONObject(result);
                    String status = tripsJsonObject.getString("success");

                    if(status.equals("1"))
                    {


                        JSONArray dashboardJsonArray = tripsJsonObject.getJSONArray("dashboard_data");

                        JSONObject ddasJsonObject = dashboardJsonArray.getJSONObject(0);

                        if(ddasJsonObject.has("latitude") && !ddasJsonObject.isNull("latitude"))
                        {
                            latitude = ddasJsonObject.getString("latitude");
                        }
                        if(ddasJsonObject.has("longitude") && !ddasJsonObject.isNull("longitude"))
                        {
                            longitude = ddasJsonObject.getString("longitude");
                        }
                        if(ddasJsonObject.has("vehicle_name") && !ddasJsonObject.isNull("vehicle_name"))
                        {
                            vehicle_name = ddasJsonObject.getString("vehicle_name");
                        }
                        if(ddasJsonObject.has("vehicle_no") && !ddasJsonObject.isNull("vehicle_no"))
                        {
                            vehicle_number = ddasJsonObject.getString("vehicle_no");
                        }
                        if(ddasJsonObject.has("vehicle_make") && !ddasJsonObject.isNull("vehicle_make"))
                        {
                            vehicle_make = ddasJsonObject.getString("vehicle_make");
                        }
                        if(ddasJsonObject.has("vehicle_model") && !ddasJsonObject.isNull("vehicle_model"))
                        {
                            vehicle_model = ddasJsonObject.getString("vehicle_model");
                        }
                        if(ddasJsonObject.has("vehicle_make_year") && !ddasJsonObject.isNull("vehicle_make_year"))
                        {
                            vehicle_make_year = ddasJsonObject.getString("vehicle_make_year");
                        }
                        if(ddasJsonObject.has("vehicle_gear_type") && !ddasJsonObject.isNull("vehicle_gear_type"))
                        {
                            vehicle_gear_type = ddasJsonObject.getString("vehicle_gear_type");
                        }
                        if(ddasJsonObject.has("vehicle_fuel_type") && !ddasJsonObject.isNull("vehicle_fuel_type"))
                        {
                            vehicle_fuel_type = ddasJsonObject.getString("vehicle_fuel_type");
                        }

                        setBirdEyeView(
                                Double.valueOf(latitude),
                                Double.valueOf(longitude),
                                vehicle_number,
                                vehicle_name,
                                vehicle_make,
                                vehicle_model,
                                vehicle_make_year,
                                vehicle_gear_type,
                                vehicle_fuel_type);

                    }
                    else
                    {
                        new SweetAlertDialog(LocateCarNewActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setConfirmText("OK")
                                .setContentText("Vehicle details not found")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog)
                                    {
                                        sDialog.dismissWithAnimation();

                                    }
                                })
                                .show();
                    }

                }
                else
                {
                    T.t(LocateCarNewActivity.this, "incorect json");
                }
            }
            else
            {
                T.t(LocateCarNewActivity.this,"0 or null json");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
    }

    private void setBirdEyeView(
            final Double latitude,
            final Double longitude,
            final String vehicle_number,
            final String vehicle_name,
            final String vehicle_make,
            final String vehicle_model,
            String vehicle_make_year,
            String vehicle_gear_type,
            final String vehicle_fuel_type)
    {

        if (carMarker != null) {
            carMarker.remove();
        }

        carMarker = map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_launcher))
                .position(new LatLng(latitude, longitude))
                .title("PUNE"));


        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 14));

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            public View getInfoWindow(Marker arg0) {
                View v = getLayoutInflater().inflate(R.layout.activity_show_info, null);

                TextView vehicleAddressTextView = (TextView) v.findViewById(R.id.vehicleAddressTextView);
                TextView vehicleNumberTextView = (TextView) v.findViewById(R.id.vehicleNumberTextView);
                TextView vehicleNameAddressTextView = (TextView) v.findViewById(R.id.vehicleNameAddressTextView);
                TextView vehicleMakeTextView = (TextView) v.findViewById(R.id.vehicleMakeTextView);
                TextView vehicleModelTextView = (TextView) v.findViewById(R.id.vehicleModelTextView);
                TextView vehicleFuelTypeTextView = (TextView) v.findViewById(R.id.vehicleFuelTypeTextViewdfdg);

                String addressV = getAddress(latitude,longitude);

                vehicleAddressTextView.setText(Html.fromHtml("<b>Address: </b>"+addressV));
                vehicleNumberTextView.setText(Html.fromHtml("<b>Vehicle No: </b>"+vehicle_number));
                vehicleNameAddressTextView.setText(Html.fromHtml("<b>Name: </b>"+vehicle_name));
                vehicleMakeTextView.setText(Html.fromHtml("<b>Make: </b>"+vehicle_make));
                vehicleModelTextView.setText(Html.fromHtml("<b>Model: </b>"+vehicle_model));
                vehicleFuelTypeTextView.setText(Html.fromHtml("<b>Fuel Type: </b>"+vehicle_fuel_type));
                return v;
            }

            public View getInfoContents(Marker arg0) {
                return null;
            }
        });

        //       getSupportActionBar().setTitle(Html.fromHtml("<small> <font color='#ffffff'>GENSET</font></small"));
    }

    private String getAddress(Double lat, Double landi) {


        String locationAddress = null;
        try
        {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            addresses = geocoder.getFromLocation(lat, landi, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            locationAddress = address + ", " + city + ", " + state + ", " + country + ","+postalCode+".";
        } catch (Exception e) {

        }

        return locationAddress;
    }

    // Initialize GoogleMaps
    private void initGMaps()
    {

        if (map == null)
        {
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }


    // Callback called when Map is ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Log.d(TAG, "onMapReady()");
        map = googleMap;
        map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(this);

        //set google map camera position by default on india
        map = googleMap;
        LatLng india = new LatLng(28.704059, 77.102490);
        // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(india,4));
    }

    // Callback called when Map is touched
    @Override
    public void onMapClick(LatLng latLng) {
        // Log.d(TAG, "onMapClick(" + latLng + ")");
    }

    // Callback called when Marker is touched
    @Override
    public boolean onMarkerClick(Marker marker) {
        // Log.d(TAG, "onMarkerClickListener: " + marker.getPosition());
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_locate_car, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home)
        {
            finish();
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onLocationChanged(Location location) {

        lastLocation = location;


    }

    @Override
    public void onConnected(Bundle bundle) {

        // T.t(MapsActivity.this, "onConnected()");

        //check m device for permissions


    }

    @Override
    protected void onStart() {
        super.onStart();

        // Call GoogleApiClient connection when starting the Activity
        googleApiClient.connect();
    }
    @Override
    protected void onStop() {
        super.onStop();

        // Disconnect GoogleApiClient when stopping Activity
        googleApiClient.disconnect();
    }
    private void askPermission()
    {
        Log.d("TAG", "askPermission()");
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                12
        );
    }
    // Check for permission to access Location
    private boolean checkPermission() {
        Log.d("TAG", "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED );
    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
