package snsystems.obd.trips;

import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;
import snsystems.obd.activity.MainNavigationActivity;
import snsystems.obd.classes.T;
import snsystems.obd.interfaces.DirectionFinderListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;


import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.Manifest;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class TripMapsActivity extends AnimationActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        ResultCallback<Status>,
        DirectionFinderListener {


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private GoogleApiClient googleApiClient;
    private Location lastLocation;

    TimerTask doAsynchronousTask;

    int globalIndex;

    int count = 0;


    private SharedPreferences.Editor editor;

    private Marker locationMarker;
    private LocationRequest locationRequest;
    // Defined in mili seconds.
    // This number in extremely low, and should be used only for debug
    // private final int UPDATE_INTERVAL =  1000;
    // private final int FASTEST_INTERVAL = 900;

    private final int UPDATE_INTERVAL =  1 * 60 * 1000; // 1 minutes
    private final int FASTEST_INTERVAL = 30 * 1000;  // 30 secs

    private Marker geoFenceMarker;

    private Marker [] GEO_FENCE_MARKER = new Marker[3];

    //create geofence
    private static final long GEO_DURATION = 60 * 60 * 1000;
    private static final String GEOFENCE_REQ_ID = "My Geofence";
    private static final float GEOFENCE_RADIUS = 500.0f; // in meters

    private PendingIntent geoFencePendingIntent;
    private ArrayList<Marker> routeMarker = new ArrayList<>();

    private ProgressDialog progressDialog;

    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();


    private List<Marker> haltMarkers = new ArrayList<>();


    //draw route
    ArrayList<LatLng> MarkerPoints = new ArrayList<>();



    private ArrayList<LatLng> ROUTE_MARKERS = new ArrayList<>();
    private ArrayList<LatLng> HALT_ROUTE_MARKERS = new ArrayList<>();

    CameraUpdate cameraUpdate;

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Bind(R.id.sourceAddressTextview)
    TextView sourceAddressTextview;

    @Bind(R.id.destinationAddressTextview)
    TextView destinationAddressTextview;

    @Bind(R.id.avgSpeedTextView)
    TextView avgSpeedTextView;

    @Bind(R.id.maxSpeedTextView)
    TextView maxSpeedTextView;

    @Bind(R.id.haltsTextView)
    TextView haltsTextView;

    @Bind(R.id.kilometersTextView)
    TextView kilometersTextView;

//    @Bind(R.id.offTimeTextView)
//    TextView offTimeTextView;



//    @Bind(R.id.alertsTextView)
//    TextView alertsTextView;

    @Bind(R.id.onoffTimeTextView)
    TextView onoffTimeTextView;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_maps);

        ButterKnife.bind(this);
        //setUpMapIfNeeded();

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeMap();

//        double distqn = distance(18.501832,73.863591,18.457532,73.867746);
//
//        T.t(TripMapsActivity.this,""+distqn);


        //drawRoutess();


        // addRouteMArkers();

        // create GoogleApiClient
        createGoogleApi();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        LatLng india = new LatLng(28.704059, 77.102490);
        // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(india,4));

        displayMarkers();





        // mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // mMap.setOnMapClickListener(this);
        //mMap.setOnMarkerClickListener(this);

        //setUpMap();

        //setupDeofence();

        //setCameraZoom();

        //drawGeofence();


        //displayMarkers();

        //mapFlyOverCar();



    }

    private void displayMarkers(){

        try {


            double total_distance = 0;


            Bundle b = this.getIntent().getExtras();
            if (b != null) {

                String[] latLongArray = b.getStringArray("routeLatLong");
                String[] haltslatLongArray = b.getStringArray("haltLatLong");

                for (int i = 0; i < latLongArray.length; i++) {
                    String[] data = latLongArray[i].split("#");
                    ROUTE_MARKERS.add(new LatLng(Double.valueOf(data[0]), Double.valueOf(data[1])));

                }
                for (int i = 0; i < haltslatLongArray.length; i++) {
                    String[] data = latLongArray[i].split("#");
                    HALT_ROUTE_MARKERS.add(new LatLng(Double.valueOf(data[0]), Double.valueOf(data[1])));

                }

                for (int i = 0; i < ROUTE_MARKERS.size() - 1; i++) {
                    double total_distance1 = distanceBetween(ROUTE_MARKERS.get(i), ROUTE_MARKERS.get(i + 1));

                    total_distance = total_distance + total_distance1;
                }

                //Log.e("TOATAL_DISTANCE",""+(total_distance/1000));


                String sourceAddress = b.getString("sourceAddress");
                String destinationAddress = b.getString("destinationAddress");
//            String sourceDate = b.getString("sourceDate");
//            String sourceTime = b.getString("sourceTime");
//            String destinationDate = b.getString("destinationDate");
//            String destinationTime = b.getString("destinationTime");
                String avgSpeed = b.getString("avgSpeed");
                String maxSpeed = b.getString("maxSpeed");
                String noOfHalts = b.getString("noOfHalts");
                String tripKilometeres = b.getString("tripKilometeres");


                String totalOnTime1 = b.getString("totalOnTime");
                String totalOffTime1 = b.getString("totalOffTime");

                String totalOnTime = T.getDurationString(Integer.valueOf(totalOnTime1));
                String totalOffTime = T.getDurationString(Integer.valueOf(totalOffTime1));

                Log.e("totalOnTime", "" + totalOnTime);

                sourceAddressTextview.setText(sourceAddress);
                destinationAddressTextview.setText(destinationAddress);

                avgSpeedTextView.setText("" + avgSpeed + "KM/hr");
                maxSpeedTextView.setText("" + maxSpeed + "KM/hr");
                haltsTextView.setText("" + noOfHalts);
                kilometersTextView.setText("" + Math.ceil((total_distance / 1000)) + "KM");
                //Log.e("totalOffTime",""+T.timeConvert(Integer.valueOf(totalOffTime)));
                onoffTimeTextView.setText("" + totalOnTime);
                // offTimeTextView.setText(""+totalOffTime);

            }
            //source marker
            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_sourece_a))
                    //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .position(ROUTE_MARKERS.get(0))));

            //destination marker
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_a))
                    //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .position(ROUTE_MARKERS.get(ROUTE_MARKERS.size() - 1))));

            for (int i = 0; i < HALT_ROUTE_MARKERS.size(); i++) {
                LatLng latLng = HALT_ROUTE_MARKERS.get(i);
                String address = T.getAddress(TripMapsActivity.this, latLng.latitude, latLng.longitude);
                mMap.addMarker(new MarkerOptions()
                        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_location_48dp))
                        .title(address)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                        .position(HALT_ROUTE_MARKERS.get(i)));
            }


            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ROUTE_MARKERS.get(0), 14));


            //draw route here
            mMap.addPolyline((new PolylineOptions())
                    .addAll(ROUTE_MARKERS)
                    .width(4)
                    .color(Color.BLUE)
                    .geodesic(true));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    public static Double distanceBetween(LatLng point1, LatLng point2) {
        if (point1 == null || point2 == null) {
            return null;
        }

        return SphericalUtil.computeDistanceBetween(point1, point2);
    }


    public void playCarRoute(View view) {

        naviagateVehicle();
    }
    private void naviagateVehicle()
    {

        final Handler handler = new Handler();
        final Timer timerrr = new Timer();
        doAsynchronousTask = new TimerTask()
        {
            @Override
            public void run()
            {
                handler.post(new Runnable()
                {
                    @SuppressWarnings("unchecked")
                    public void run() {
                        try
                        {
                            if (count < ROUTE_MARKERS.size() - 1)
                            {
                                //T.t(MapsActivity.this,"Hello");

                                Marker markerName = mMap.addMarker(new MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location))
                                        .title("Source Address")
                                        .position(ROUTE_MARKERS.get(count)));

                                originMarkers.add(markerName);

                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ROUTE_MARKERS.get(count), 18));


                                count++;
                            }
                            else
                            {
                                timerrr.cancel();
                                // count = 0;
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timerrr.schedule(doAsynchronousTask, 0, 100);
    }

    public void finishActivity(View view)
    {
        finish();
    }


    private void drawRoutess()
    {
        boolean n = T.checkConnection(TripMapsActivity.this);

        if(n)
        {
            try
            {

                String sourceAddress = "Swargate, Pune, Maharashtra, India";
                String destinationAddress = "Deccan Gymkhana, Pune, Maharashtra 411004, India";

                //new FindPath(this, sourceAddress, destinationAddress).execute();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            T.t(TripMapsActivity.this,"Oops ! network connection off");
        }

    }

    public void drawRoute(View view)
    {

    }




    @Override
    public void onDirectionFinderStart() {

        progressDialog = ProgressDialog.show(this, "Please wait...",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }

    }



    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {


        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();





//        for (Route route : routes)
//        {
//
//             mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 14));
//
//           // ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
//            //((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);
//
//            originMarkers.add(mMap.addMarker(new MarkerOptions()
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_source_location_48dp))
//                    .title(route.startAddress)
//                    .position(route.startLocation)));
//            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_location_48dp))
//                    .title(route.endAddress)
//                    .position(route.endLocation)));
//
//            PolylineOptions polylineOptions = new PolylineOptions().
//                    geodesic(true).
//                    color(Color.BLUE).
//                    width(10);
//
//            for (int i = 0; i < route.points.size(); i++)
//                polylineOptions.add(route.points.get(i));
//
//            polylinePaths.add(mMap.addPolyline(polylineOptions));
//            Log.d("poly", "" + polylineOptions);
//        }

//        ArrayList<HashMap<String, String>> myList = new ArrayList<HashMap<String, String>>();
//
//        HashMap<String, String> latt = new HashMap<String, String>();
//        latt.put("lat","gfhgf");
//        latt.put("lat","gfhgf");
//        latt.put("lat","gfhgf");
//        latt.put("lat","gfhgf");
//
//
//        myList.add(latt);
//
//
//
//
//        ArrayList<LatLng> points = null;
//        PolylineOptions lineOptions = null;
//        MarkerOptions markerOptions = new MarkerOptions();
//
//        // Traversing through all the routes
//        for(int i=0;i<myList.size();i++)
//        {
//            points = new ArrayList<LatLng>();
//            lineOptions = new PolylineOptions();
//
//            // Fetching i-th route
//            List<HashMap<String, String>> path = (List<HashMap<String, String>>) myList.get(i);
//
//            // Fetching all the points in i-th route
//            for(int j=0;j<path.size();j++)
//            {
//                HashMap<String,String> point = path.get(j);
//
//                double lat = Double.parseDouble(point.get("lat"));
//                double lng = Double.parseDouble(point.get("lng"));
//                LatLng position = new LatLng(lat, lng);
//
//                points.add(position);
//            }
//
//            // Adding all the points in the route to LineOptions
//            lineOptions.addAll(points);
//            lineOptions.width(2);
//            lineOptions.color(Color.RED);
//        }
//
//        // Drawing polyline in the Google Map for the i-th route
//        mMap.addPolyline(lineOptions);


    }

    private void addRouteMArkers()
    {

        // inside your loop:
        //routeMarker.add(new MarkerOptions().position(new LatLng(geo1Dub,geo2Dub)));
        //routeMarker.add(new MarkerOptions().position(new LatLng(geo1Dub,geo2Dub)));

    }


    private void setupDeofence() {

        mMap.addMarker( new MarkerOptions()
                .position( new LatLng(18.520430, 73.856744) )
                .title("Fence " + "Pune,India")
                .snippet("Radius: " + 200) )
                .showInfoWindow();

        //Instantiates a new CircleOptions object +  center/radius
        CircleOptions circleOptions = new CircleOptions()
                .center( new LatLng(18.520430, 73.856744) )
                .radius( 200 )
                .fillColor(0x40ff0000)
                .strokeColor(Color.TRANSPARENT)
                .strokeWidth(2);

        Circle circle = mMap.addCircle(circleOptions);

    }
    // Create GoogleApiClient instance
    private void createGoogleApi()
    {
       // T.t(TripMapsActivity.this, "createGoogleApi()");
        if ( googleApiClient == null ) {
            googleApiClient = new GoogleApiClient.Builder( this )
                    .addConnectionCallbacks( this )
                    .addOnConnectionFailedListener( this )
                    .addApi( LocationServices.API )
                    .build();
        }
    }



    private void initializeMap() {

        if (mMap == null) {
            SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFrag.getMapAsync(this);
        }

        Bundle bundle = getIntent().getExtras();

        if(bundle != null)
        {
            sourceAddressTextview.setText(bundle.getString("sourceAddress"));
            destinationAddressTextview.setText(bundle.getString("destinationAddress"));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }






    private void setCameraZoom() {


        CameraPosition INIT =
                new CameraPosition.Builder()
                        .target(new LatLng(18.520430, 73.856744))
                        .zoom( 17.2F )
                        .bearing( 300F) // orientation
                                //.tilt( 50F) // viewing angle
                        .build();
        // use GooggleMap mMap to move camera into position
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(INIT));


    }

    public void satelliteView(View view)
    {

        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }


    @Override
    public void onConnected(Bundle bundle) {

        // T.t(MapsActivity.this, "onConnected()");

            //check m device for permissions
           // getLastKnownLocation(); //for display current location when nw on
        //--------------------------------------------------------------------

    }
    public void navigateToMyLocation(View view)
    {
        //need to check network connection on off

        boolean nw = T.checkConnection(TripMapsActivity.this);

        if(nw)
        {
            // M permission for location

            boolean m = T.sdkLevel();

            if(m)
            {

                if (checkMPermission())
                {

                    //Snackbar.make(view,"Permission already granted.",Snackbar.LENGTH_LONG).show();

                    checkLocationServices();

                }
                else
                {

                    requestMPermission();
                    //Snackbar.make(view,"Please request permission.",Snackbar.LENGTH_LONG).show();
                }
            }
            else
            {
                checkLocationServices();
            }

        }
        else
        {
            T.t(TripMapsActivity.this, getResources().getString(R.string.connection));
        }

    }
    private void requestMPermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(TripMapsActivity.this,Manifest.permission.ACCESS_FINE_LOCATION))
        {

            Toast.makeText(TripMapsActivity.this,"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();

        }
        else
        {

            ActivityCompat.requestPermissions(TripMapsActivity.this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    T.t(TripMapsActivity.this, "Permission Granted, Now you can access location data.");

                }
                else
                {
                    T.t(TripMapsActivity.this,"Permission Denied, You cannot access location data.");



                }
                break;
        }
    }
    private boolean checkMPermission()
    {
        int result = ContextCompat.checkSelfPermission(TripMapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void checkLocationServices()
    {

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try
        {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        catch(Exception ex)
        {

        }

        try
        {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled)
        {


            new SweetAlertDialog(TripMapsActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setConfirmText("Enable")
                    .setCancelText("Cancel")
                    .setContentText("Location Off...")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                    {
                        @Override
                        public void onClick(SweetAlertDialog sDialog)
                        {
                            sDialog.dismissWithAnimation();

                            Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);

                        }
                    })
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {

                            sweetAlertDialog.dismissWithAnimation();

                        }
                    })
                    .show();


        }
        else
        {
            mMap.animateCamera(cameraUpdate);
        }
    }

    // Get last known location
    private void getLastKnownLocation()
    {
        Log.d("TAG", "getLastKnownLocation()");
        if ( checkPermission() )
        {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if ( lastLocation != null )
            {
                Log.i("TAG", "LasKnown location. " + "Long: " + lastLocation.getLongitude() + " | Lat: " + lastLocation.getLatitude());
                writeLastLocation();
                startLocationUpdates();
            }
            else
            {
                Log.w("TAG", "No location retrieved yet");
                startLocationUpdates();
            }
        }
        else askPermission();
    }
    // Start location Updates
    private void startLocationUpdates(){
        Log.i("TAG", "startLocationUpdates()");
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        if ( checkPermission() )
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }
    private void writeLastLocation()
    {
        writeActualLocation(lastLocation);
    }
    // Asks for permission
    private void askPermission()
    {
        Log.d("TAG", "askPermission()");
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                12
        );
    }
    // Verify user's response of the permission requested


    // App cannot work without the permissions
    private void permissionsDenied() {
        Log.w("TAG", "permissionsDenied()");
    }

    // Check for permission to access Location
    private boolean checkPermission() {
        Log.d("TAG", "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED );
    }

    @Override
    public void onConnectionSuspended(int i) {

        // T.t(MapsActivity.this, "onConnectionSuspended()");


    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        //T.t(MapsActivity.this, "onConnectionFailed()");

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

    @Override
    public void onLocationChanged(Location location) {


//        T.t(MapsActivity.this, ""+"onLocationChanged ["+location+"]");
            lastLocation = location;


        // Setting Current Longitude
        Log.d(">>", "Longitude:" + location.getLongitude());
        // Setting Current Latitude
        Log.d(">>", "Latitude:" + location.getLatitude());

        editor.putString("Longitude", "" + location.getLongitude()).commit();
        editor.putString("Latitude", "" + location.getLatitude()).commit();

    }
    // Write location coordinates on UI
    private void writeActualLocation(Location location)
    {
        //textLat.setText( "Lat: " + location.getLatitude() );
        //textLong.setText( "Long: " + location.getLongitude() );
        //T.t(MapsActivity.this, "" + "Lat: " + location.getLatitude() + "" + "Long: " + location.getLongitude());
        markerLocation(new LatLng(location.getLatitude(), location.getLongitude()));
    }



    private void markerLocation(LatLng latLng)
    {

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location_black_36dp);
        Log.i("TAG", "markerLocation(" + latLng + ")");
        //String title = latLng.latitude + ", " + latLng.longitude;

        String title = getAddress(latLng.latitude, latLng.longitude);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)

                .title(title);
        if ( mMap!=null ) {
            // Remove the anterior marker
            if ( locationMarker != null )
                locationMarker.remove();
            locationMarker = mMap.addMarker(markerOptions);
            locationMarker.setIcon(icon);
            //locationMarker.setI
            float zoom = 14f;
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
            mMap.animateCamera(cameraUpdate);
        }


        //drawGeoforCurrentLoation(latLng);
    }

    private String  getAddress(double lat, double landi){


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

            locationAddress = address +", "+city+", "+state+", "+country+".";
        }
        catch (Exception e)
        {

        }

        return  locationAddress;
    }

    private void drawGeoforCurrentLoation(LatLng latLng) {

        T.t(TripMapsActivity.this, "drawGeofence ");


        //if ( geoFenceLimits != null )
        // geoFenceLimits.remove();

        CircleOptions circleOptions = new CircleOptions()

                //.center( geoFenceMarker.getPosition())
                .center(latLng)
                        //.strokeColor(Color.TRANSPARENT)
                .strokeColor(Color.argb(50, 70, 70, 70))
                        //.fillColor( Color.argb(100, 150,150,150) )
                .fillColor(0x40ff0000)
                        //.radius(GEOFENCE_RADIUS)
                .strokeWidth(2);
        geoFenceLimits = mMap.addCircle( circleOptions );
    }

    @Override
    public void onMapClick(LatLng latLng)
    {

        Log.e("latLnglatLng",""+latLng);
        //draw route
        // Already two locations
        if (MarkerPoints.size() > 1)
        {
            MarkerPoints.clear();
            mMap.clear();
        }
        // Adding new item to the ArrayList
        MarkerPoints.add(latLng);

        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();

        // Setting the position of the marker
        options.position(latLng);

        /**
         * For the start location, the color of marker is GREEN and
         * for the end location, the color of marker is RED.
         */
        if (MarkerPoints.size() == 1)
        {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }
        else if (MarkerPoints.size() == 2)
        {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
        // Add new marker to the Google Map Android API V2
        mMap.addMarker(options);

        // Checks, whether start and end locations are captured
        if (MarkerPoints.size() >= 2)
        {
            LatLng origin = MarkerPoints.get(0);
            LatLng dest = MarkerPoints.get(1);

            // Getting URL to the Google Directions API
            String url = Support.getUrl(origin, dest);
            Log.d("onMapClick", url.toString());
            FetchUrl FetchUrl = new FetchUrl();

            // Start downloading json data from Google Directions API
            FetchUrl.execute(url);
            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        }



        // T.t(MapsActivity.this, "" + "onMapClick(" + latLng + ")");

        //set markers fo create geo fence according to lat and long
        // markerForGeofence(latLng);
    }




    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }
    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    // Create a marker for the geofence creation
    private void markerForGeofence(LatLng latLng)
    {
        Log.e("TAGtag", "markerForGeofence(" + latLng + ")");
        String title = latLng.latitude + ", " + latLng.longitude;
        Log.e("TAGtag", ""+title);
        // Define marker options
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title(title);
        if ( mMap!=null )
        {
            // Remove last geoFenceMarker
            if (geoFenceMarker != null)
            {
                geoFenceMarker.remove();
            }


            geoFenceMarker = mMap.addMarker(markerOptions);
            //geoFenceMarker = mMap.addMarker(markerOptions);
        }

    }
    public void setGeofence(View view) {


        //startGeofence();
        markerForGeofence(new LatLng(18.487485122709717, 73.85510969907045),0);
    }


    public void setGeofenceTwo(View view)
    {

        markerForGeofence(new LatLng(18.47890534667124, 73.84159002453089),1);

    }
    public void setGeofenceThree(View view) {

        markerForGeofence(new LatLng(18.492122385653744, 73.87124687433241), 2);

    }
    // Create a marker for the geofence creation
    private void markerForGeofence(LatLng latLng,int indexPos)
    {
        Log.e("TAGtag", "markerForGeofence(" + latLng + ")");
        String title = latLng.latitude + ", " + latLng.longitude;
        Log.e("TAGtag", ""+title);
        // Define marker options
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .title(title);
        if ( mMap!=null )
        {
            // Remove last geoFenceMarker
            if (GEO_FENCE_MARKER[indexPos] != null)
            {
                //geoFenceMarker.remove();
            }


            GEO_FENCE_MARKER[indexPos] = mMap.addMarker(markerOptions);
            //geoFenceMarker = mMap.addMarker(markerOptions);
        }
        startGeofence(indexPos);
    }

    // Start Geofence creation process
    private void startGeofence(int indexPos)
    {

        globalIndex = indexPos;
        // Log.e("globalIndex",""+globalIndex);
        T.t(TripMapsActivity.this, "startGeofence");

        if( GEO_FENCE_MARKER[indexPos] != null )
        {
            Geofence geofence = createGeofence( GEO_FENCE_MARKER[indexPos].getPosition(), GEOFENCE_RADIUS );
            GeofencingRequest geofenceRequest = createGeofenceRequest( geofence );
            addGeofence(geofenceRequest);
        }
        else
        {

            T.t(TripMapsActivity.this, "Geofence marker is null");

        }
    }
    // Add the created GeofenceRequest to the device's monitoring list
    private void addGeofence(GeofencingRequest request) {

        T.t(TripMapsActivity.this, "addGeofence");
        if (checkPermission())
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    request,
                    createGeofencePendingIntent()
            ).setResultCallback(this);// check here
    }
    private final int GEOFENCE_REQ_CODE = 0;
    private PendingIntent createGeofencePendingIntent() {

        T.t(TripMapsActivity.this, "createGeofencePendingIntent");

        if ( geoFencePendingIntent != null )
            return geoFencePendingIntent;

        Intent intent = new Intent( this, GeofenceTrasitionService.class);
        return PendingIntent.getService(this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    //create geofence
    // Create a Geofence
    private Geofence createGeofence( LatLng latLng, float radius )
    {
        T.t(TripMapsActivity.this, "createGeofence");
        return new Geofence.Builder()
                .setRequestId(GEOFENCE_REQ_ID)
                .setCircularRegion( latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration(GEO_DURATION)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }
    // Create a Geofence Request
    private GeofencingRequest createGeofenceRequest(Geofence geofence) {


        T.t(TripMapsActivity.this, "createGeofenceRequest");
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();
    }
    @Override
    public void onResult(@NonNull Status status)
    {


        T.t(TripMapsActivity.this, "onResult: " + status);
        if ( status.isSuccess() )
        {
            drawGeofence();
        }
        else
        {
            // inform about fail
        }
    }
    // Draw Geofence circle on GoogleMap
    private Circle geoFenceLimits;
    private void drawGeofence() {

        T.t(TripMapsActivity.this, "drawGeofence ");


        //if ( geoFenceLimits != null )
        // geoFenceLimits.remove();

        CircleOptions circleOptions = new CircleOptions()

                //.center( geoFenceMarker.getPosition())
                .center( GEO_FENCE_MARKER[globalIndex].getPosition())
                        //.strokeColor(Color.TRANSPARENT)
                .strokeColor(Color.argb(50, 70, 70, 70))
                .fillColor( Color.argb(100, 150,150,150) )
                        // .fillColor(0x40ff0000)
                .radius( GEOFENCE_RADIUS )
                .strokeWidth(2);
        geoFenceLimits = mMap.addCircle( circleOptions );
    }

    public static Intent makeNotificationIntent(Context applicationContext, String msg)
    {
        Intent i = new Intent(applicationContext,MainNavigationActivity.class);
        i.putExtra("DATA",""+msg);

        return i;

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trip_maps, menu);
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
}
