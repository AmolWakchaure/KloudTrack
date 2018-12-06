package snsystems.obd.geofencing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;
import snsystems.obd.activity.FeedbackActivity;
import snsystems.obd.classes.MyApplication;
import snsystems.obd.classes.T;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.trips.PlacesAutoCompleteAdapter;
import snsystems.obd.trips.RecyclerItemClickListener;

public class GeoFencingActivity extends AnimationActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,// for google api client
        GoogleApiClient.OnConnectionFailedListener// for google api client
{

    private Circle circle;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    MyReceiver myReceiver;

    protected GoogleApiClient
            mGoogleApiClient;

    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(
            new LatLng(-0, 0), new LatLng(0, 0));

    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;

    @Bind(R.id.sourceRecyclerView)
    RecyclerView sourceRecyclerView;

    @Bind(R.id.sourceAddressEditText)
    EditText sourceAddressEditText;

    private ArrayList<LatLng> GEOFENCE_POINTS = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_fencing);
        ButterKnife.bind(this);
        buildGoogleApiClient();

//        setSupportActionBar(toolbar);
//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        setUpMapIfNeeded();

        initialize();
        setClickListner();

        //startServiceHere();


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

        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(GeoFencingActivity.this, R.layout.searchview_adapter, mGoogleApiClient, BOUNDS_INDIA, null);


        sourceRecyclerView.setLayoutManager(new LinearLayoutManager(GeoFencingActivity.this));

        sourceRecyclerView.setAdapter(mAutoCompleteAdapter);

    }

    //build google api client
    protected synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(GeoFencingActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
    }
    private void startServiceHere() {

        //Register BroadcastReceiver
        //to receive event from our service
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CheckEntryExitService.MY_ACTION);
        registerReceiver(myReceiver, intentFilter);

        //Start our own service
        Intent intent = new Intent(GeoFencingActivity.this, CheckEntryExitService.class);
        startService(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_geofencing, menu);
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
    private void setClickListner()
    {

        sourceAddressEditText.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sourceRecyclerView.setVisibility(View.VISIBLE);
                if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {
                    mAutoCompleteAdapter.getFilter().filter(s.toString());
                } else if (!mGoogleApiClient.isConnected()) {
                    Toast.makeText(GeoFencingActivity.this, "api not connected", Toast.LENGTH_SHORT).show();

                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        });

        sourceRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(GeoFencingActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        final PlacesAutoCompleteAdapter.PlaceAutocomplete item = mAutoCompleteAdapter.getItem(position);
                        final String placeId = String.valueOf(item.placeId);
                        Log.i("TAG", "Autocomplete item selected: " + item.description);
                        sourceAddressEditText.setText(item.description);
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);



                        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                                .getPlaceById(mGoogleApiClient, placeId);
                        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                            @Override
                            public void onResult(PlaceBuffer places)
                            {
                                if (places.getCount() == 1)
                                {
                                    //Do the things here on Click.....
                                    //  Toast.makeText(getActivity(), String.valueOf(places.get(0).getLatLng()), Toast.LENGTH_SHORT).show();

                                    final Place myPlace = places.get(0);
                                    LatLng queriedLocation = myPlace.getLatLng();

                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(queriedLocation, 15));


                                }
                                else
                                {
                                    Toast.makeText(GeoFencingActivity.this, "SOMETHING_WENT_WRONG", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });



                        sourceRecyclerView.setVisibility(View.GONE);
                    }
                })
        );

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng latLng)
            {

                if(GEOFENCE_POINTS.size() == 2)
                {
                    T.t(GeoFencingActivity.this,"More than two geofence is not allowed");
                }
                else
                {
                    GEOFENCE_POINTS.add(latLng);
                    // Creating a marker
                    MarkerOptions markerOptions = new MarkerOptions();

                    // Setting the position for the marker
                    markerOptions.position(latLng);

                    // Setting the title for the marker.
                    // This will be displayed on taping the marker
                    markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                    // Clears the previously touched position
                    //mMap.clear();

                    // Animating to the touched position
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                    // Placing a marker on the touched position
                    mMap.addMarker(markerOptions);

                    //insert geofence data into sqlite
                    String device_id_mail = S.getDeviceIdUserName(MyApplication.db);
                    String [] data = device_id_mail.split("#");

//                    MyApplication.db.storeGeofence(
//
//                            data[0],
//                            latLng.latitude+","+latLng.longitude,
//                            String.valueOf(1000),
//                            "on"
//
//                    );

                    drawGeofence(latLng,1000);




                    //checkMarkerInsideGeofenceOrNot(latLng.latitude, latLng.longitude);
                }

            }
        });
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

    @Override
    protected void onStop() {
        super.onStop();


        //googleApiClient.disconnect();
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFrag.getMapAsync(this);
            // Getting a reference to the map
            mMap = mapFrag.getMap();
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        LatLng india = new LatLng(28.704059, 77.102490);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(india,4));


      //  drawGeofence();

        String device_id_mail = S.getDeviceIdUserName(MyApplication.db);
        String [] data = device_id_mail.split("#");

        //select geofence data from sqlite
        ArrayList<String> GEOFENCE_DATA = S.selectGeofenceData(MyApplication.db,data[0],"on");

        if(GEOFENCE_DATA.isEmpty())
        {
            T.t(GeoFencingActivity.this,"No geofence created");
        }
        else
        {

            for(int i = 0; i < GEOFENCE_DATA.size(); i++)
            {
                String [] geofenceData = GEOFENCE_DATA.get(i).split("#");

                String device_id = geofenceData[0];
                String [] address_latlong = geofenceData[1].split(",");
                String radious = geofenceData[2];
                String active_status = geofenceData[3];

                GEOFENCE_POINTS.add(new LatLng(Double.valueOf(address_latlong[0]),Double.valueOf(address_latlong[1])));

                drawGeofence(new LatLng(Double.valueOf(address_latlong[0]),Double.valueOf(address_latlong[1])),Integer.valueOf(radious));
            }

//            if(GEOFENCE_DATA.size() == 1)
//            {
//                String [] geofenceData = GEOFENCE_DATA.get(0).split("#");
//
//                String device_id = geofenceData[0];
//                String [] address_latlong = geofenceData[1].split(",");
//                String radious = geofenceData[2];
//                String active_status = geofenceData[3];
//
//                drawGeofence(new LatLng(Double.valueOf(address_latlong[0]),Double.valueOf(address_latlong[1])),Integer.valueOf(radious));
//            }
//            else if(GEOFENCE_DATA.size() == 2)
//            {
//
//            }
//
//            //drawGeofence(LatLng latLng, int radiousInMtr)
        }



    }

    private void animateCamera() {

        Marker marker1,marker2,marker3,marker4;

        LatLng pune = new LatLng(18.520430, 73.856744);
        LatLng mumbai = new LatLng(18.512494, 73.861181);
        LatLng nashik = new LatLng(19.997453, 73.789802);
        LatLng chiplun = new LatLng(17.532311, 73.517793);

        marker1 = mMap.addMarker(new MarkerOptions()
                .position(pune)
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_off_vehicle))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title("OFF"));
        marker2 = mMap.addMarker(new MarkerOptions()
                .position(mumbai)
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_off_vehicle))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title("OFF"));
//        marker3 = mMap.addMarker(new MarkerOptions()
//                .position(nashik)
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_on_vehicle))
//                //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
//                .title("ON"));
//
//        marker4 = mMap.addMarker(new MarkerOptions()
//                .position(chiplun)
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_on_vehicle))
//                //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
//                .title("ON"));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();


        builder.include(marker1.getPosition());
        builder.include(marker2.getPosition());
//        builder.include(marker3.getPosition());
//        builder.include(marker4.getPosition());

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 12% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mMap.animateCamera(cu);
    }

    private void drawGeofence(LatLng latLng, int radiousInMtr)
    {

        CircleOptions circleOptions = new CircleOptions()

                //.center(new LatLng(18.520430, 73.856744))
                .center(latLng)
                .radius(radiousInMtr)
                .fillColor(Color.parseColor("#51000000"))
                //.strokeColor(Color.TRANSPARENT)
                .strokeColor(Color.RED)
                .strokeWidth(2);
        circle = mMap.addCircle(circleOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        //cameraAnimation(new LatLng(18.520430, 73.856744));

        //important for to add marker in geofence area to check inside or outside
        //addMarker();

    }

    private void addMarker() {

//        double latitude = 18.520495;//inside
//        double longitude =73.855988 ;

        double latitude = 18.517656;//outside
        double longitude = 73.857254;

        // create marker
        MarkerOptions marker = new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title("Hello Maps ");

        // adding marker
        mMap.addMarker(marker);

        checkMarkerInsideGeofenceOrNot(latitude, longitude);
    }

    public void checkInsideOutside(View view)
    {
        double latitude = 18.510415;
        double longitude = 73.876807;

        checkMarkerInsideGeofenceOrNot(latitude,longitude);

    }

    private void checkMarkerInsideGeofenceOrNot(double latitude, double longitude) {

        float[] distance = new float[2];

        Location.distanceBetween(latitude, longitude, circle.getCenter().latitude, circle.getCenter().longitude, distance);

        if (distance[0] > circle.getRadius())
        {
            Toast.makeText(getBaseContext(), "Outside Geo fence ", Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(getBaseContext(), "Inside geo fence", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isPointInPolygon(LatLng tap, ArrayList<LatLng> vertices) {
        int intersectCount = 0;
        for (int j = 0; j < vertices.size() - 1; j++) {
            if (rayCastIntersect(tap, vertices.get(j), vertices.get(j + 1))) {
                intersectCount++;
            }
        }
        return ((intersectCount % 2) == 1); // odd = inside, even = outside;
    }

    private boolean rayCastIntersect(LatLng tap, LatLng vertA, LatLng vertB) {

        double aY = vertA.latitude;
        double bY = vertB.latitude;
        double aX = vertA.longitude;
        double bX = vertB.longitude;
        double pY = tap.latitude;
        double pX = tap.longitude;

        if ((aY > pY && bY > pY) || (aY < pY && bY < pY)
                || (aX < pX && bX < pX)) {
            return false; // a and b can't both be above or below pt.y, and a or
            // b must be east of pt.x
        }

        double m = (aY - bY) / (aX - bX); // Rise over run
        double bee = (-aX) * m + aY; // y = mx + b
        double x = (pY - bee) / m; // algebra is neat!

        return x > pX;
    }

    private void cameraAnimation(LatLng latLong)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLong));
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




    private class MyReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub

            String  datapassed = arg1.getStringExtra("DATAPASSED");

            Log.e("FROM_ACTIVITY",""+datapassed);

            //T.t(GeoFencingActivity.this,""+datapassed);


        }

    }

    public void editeGeofence(View view)
    {
        startActivity(new Intent(GeoFencingActivity.this,EditeGeofenceActivity.class));
    }




}
