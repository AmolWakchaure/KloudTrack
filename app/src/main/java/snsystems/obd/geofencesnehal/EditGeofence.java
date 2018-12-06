package snsystems.obd.geofencesnehal;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import snsystems.obd.R;
import snsystems.obd.database.DBHelper;

public class EditGeofence extends AppCompatActivity implements OnMapReadyCallback
{

    private GoogleMap map;
    private Circle circle;
    DiscreteSeekBar seekBar;
    TextView txt_radius;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_geofence);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_geo_fence_edit);

        mapFragment.getMapAsync(this);

        seekBar = (DiscreteSeekBar) findViewById(R.id.radiusSeekbar_edit);
        txt_radius = (TextView) findViewById(R.id.edit_geo_radius);

        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int progress, boolean fromUser) {
                String lat_long = getIntent().getExtras().getString("edit_lat_long");
                String id = getIntent().getExtras().getString("edit_id");

                txt_radius.setText(progress + " km");
                String[] a = lat_long.toString().split(",");
                String lat_value = a[0];
                String long_value = a[1];

                DBHelper dbHelper = new DBHelper(EditGeofence.this);
                dbHelper.updateGeofence(id, progress);

                map.clear();
                createGeofence(Double.valueOf(lat_value), Double.valueOf(long_value), progress * 1000, "CIRCLE", "GEOFENCE");
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });

//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
//
//                String lat_long = getIntent().getExtras().getString("edit_lat_long");
//
//                String[] a = lat_long.toString().split(",");
//                String lat_value = a[0];
//                String long_value = a[1];
//
//                DBHelper dbHelper = new DBHelper(EditGeofence.this);
//                dbHelper.updateGeofence(lat_long, (progress));
//
//                map.clear();
//                createGeofence(Double.valueOf(lat_value), Double.valueOf(long_value), progress, "CIRCLE", "GEOFENCE");
//
////                CircleOptions circleOptions = new CircleOptions()
////
////                        //.center(new LatLng(18.520430, 73.856744))
////                        .center(new LatLng(Double.valueOf(lat_value), Double.valueOf(long_value)))
////                        .radius(progress)
////                        .fillColor(Color.parseColor("#51000000"))
////                        //.strokeColor(Color.TRANSPARENT)
////                        .strokeColor(Color.RED)
////                        .strokeWidth(2);
////
////                circle = map.addCircle(circleOptions);
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//
//
//        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        String radius = getIntent().getExtras().getString("edit_radius");
        txt_radius.setText(radius + " km");

//        seekBar.setProgress(Integer.valueOf(radius));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //set google map camera position by default on india
        map = googleMap;

        String lat_long = getIntent().getExtras().getString("edit_lat_long");
        String radius = getIntent().getExtras().getString("edit_radius");

        String[] a = lat_long.toString().split(",");
        String lat_value = a[0];
        String long_value = a[1];

        LatLng india = new LatLng(Double.valueOf(lat_value), Double.valueOf(long_value));
        // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(india, 4));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(india,
                13f));

        map.addMarker(new MarkerOptions().position(india));

        createGeofence(Double.valueOf(lat_value), Double.valueOf(long_value), Integer.valueOf(radius) * 1000, "CIRCLE", "GEOFENCE");
    }

    private void createGeofence(double latitude, double longitude, int radius,
                                String geofenceType, String title) {

        Marker stopMarker = map.addMarker(new MarkerOptions()
                .draggable(true)
                .position(new LatLng(latitude, longitude))
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker
                        (BitmapDescriptorFactory.HUE_BLUE)));


        map.addCircle(new CircleOptions()
                .center(new LatLng(latitude, longitude)).radius(radius)
                .fillColor(Color.parseColor("#51000000")).strokeColor(Color.RED).strokeWidth(2));

//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude),
//                radius/100));
    }

    public void edit_geo_fence(View view) {
        finish();
    }
}
