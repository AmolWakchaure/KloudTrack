package snsystems.obd.locatecar;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import snsystems.obd.R;

public class LocateCarActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback {

    Toolbar toolbar;
    private GoogleMap mMap;

    LocationManager locationManager;
    String provider;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    GoogleMap googleMap;
    private Routes rt;

    LatLng SOURCE, DESTINATION;

    String lat_source, longi_source, lat_dest, longi_dest;

    SupportMapFragment mapFragment;

    NumberFormat formatter = new DecimalFormat("#0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate_car);

       initialize();


        previousData();


    }

    private void previousData() {

                pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();

        googleMap = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map)).getMap();

        rt = new Routes();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Creating an empty criteria object
        Criteria criteria = new Criteria();

        // Getting the name of the provider that meets the criteria
        provider = locationManager.getBestProvider(criteria, false);

        if (provider != null && !provider.equals("")) {

            // Get the location from the given provider
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            Location location = locationManager.getLastKnownLocation(provider);

            locationManager.requestLocationUpdates(provider, 20000, 1, this);

            if (location != null)
                onLocationChanged(location);
            else
                Toast.makeText(getBaseContext(), "Location can't be retrieved", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getBaseContext(), "No Provider Found", Toast.LENGTH_SHORT).show();
        }

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        lat_dest = pref.getString("device_lat", "");//vehicle
        longi_dest = pref.getString("device_lang", "");//man

        lat_source = pref.getString("Latitude", "");
        longi_source = pref.getString("Longitude", "");

        if (!lat_dest.equals("") && !longi_dest.equals("")
                && !lat_source.equals("") && !longi_source.equals(""))
        {

            SOURCE = new LatLng(Double.parseDouble(lat_source), Double.parseDouble(longi_source));
            DESTINATION = new LatLng(Double.parseDouble(lat_dest), Double.parseDouble(longi_dest));

            rt = new Routes();

            rt.drawRoute(googleMap, LocateCarActivity.this, SOURCE, DESTINATION, true, "en");

            Location loc1 = new Location("");
            loc1.setLatitude(Double.parseDouble(lat_source));
            loc1.setLongitude(Double.parseDouble(longi_source));

            Location loc2 = new Location("");
            loc2.setLatitude(Double.parseDouble(lat_dest));
            loc2.setLongitude(Double.parseDouble(longi_dest));

            double distanceInMeters = loc1.distanceTo(loc2);

            Log.d(">>>", "dist>>" + formatter.format(distanceInMeters));



        }
    }

    private void initialize() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feedback, menu);
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


    @Override
    public void onLocationChanged(Location location) {

        // Setting Current Longitude
        Log.d(">>", "Longitude:" + location.getLongitude());
        // Setting Current Latitude
        Log.d(">>", "Latitude:" + location.getLatitude());

        editor.putString("Longitude", "" + location.getLongitude()).commit();
        editor.putString("Latitude", "" + location.getLatitude()).commit();
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    public void onMapReady(GoogleMap googleMap) {


       try
       {
           if (!SOURCE.equals("") && !DESTINATION.equals("")) {
               googleMap.addMarker(new MarkerOptions()
                       .position(SOURCE).draggable(true)
                       .alpha(0.7f)
                       .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                       .showInfoWindow();

               googleMap.addMarker(new MarkerOptions()
                       .position(DESTINATION).draggable(true)
                       .alpha(0.7f)
                       .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
                       .showInfoWindow();

               googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SOURCE, 10));
           }
       }
       catch (Exception e)
       {

       }
    }

}
