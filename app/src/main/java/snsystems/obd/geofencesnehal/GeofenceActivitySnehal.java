package snsystems.obd.geofencesnehal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.MapFragment;

import java.util.ArrayList;
import java.util.HashSet;

import cn.pedant.SweetAlert.SweetAlertDialog;
import snsystems.obd.R;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;

public class GeofenceActivitySnehal extends AppCompatActivity {

    ListView recyclerview_geofence;
    DBHelper dbHelper;
    ArrayList<GeoFenceObjectClass> GEOFENCE_DATA;
    String[] data;
    private MapAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence_new);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Geofencing ");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DBHelper(this);

        recyclerview_geofence = (ListView) findViewById(R.id.recyclerview_geofence);

        String device_id_mail = S.getDeviceIdUserName(new DBHelper(GeofenceActivitySnehal.this));
        data = device_id_mail.split("#");

        GEOFENCE_DATA = new ArrayList<>();

    }

    public void add_places(View view) {

        if (GEOFENCE_DATA.size() < 3) {
            Intent intent_add_places = new Intent(GeofenceActivitySnehal.this, MapsActivity.class);
            startActivity(intent_add_places);
        } else {
            new SweetAlertDialog(GeofenceActivitySnehal.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops !")
                    .setConfirmText("Sorry")
                    .setContentText("You can not add more than 3 geofence....")

                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();

                        }
                    })
                    .show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        GEOFENCE_DATA = dbHelper.selectGeofence(data[0], "on");

        mAdapter = new MapAdapter(this, GEOFENCE_DATA);
        recyclerview_geofence.setAdapter(mAdapter);
    }


    private class MapAdapter extends ArrayAdapter<GeoFenceObjectClass> {
        Context context;
        private ArrayList<GeoFenceObjectClass> list;
        private final HashSet<MapFragment> mMaps = new HashSet<>();

        public MapAdapter(Context context, ArrayList<GeoFenceObjectClass> locations) {

            super(context, R.layout.activity_geo_fence_recycler_adapter, locations);
            this.context = context;
            this.list = locations;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            final ViewHolder holder;
            final GeoFenceObjectClass item = getItem(position);

            if (row == null) {

                row = getLayoutInflater().inflate(R.layout.activity_geo_fence_recycler_adapter, null);

                holder = new ViewHolder();

                holder.txt_geo_fence_name = (TextView) row.findViewById(R.id.txt_geo_fence_name);
                holder.txt_radius = (TextView) row.findViewById(R.id.geofenceRadious);
                holder.btn_geo_fence_delete = (Button) row.findViewById(R.id.btn_geo_fence_delete);
                holder.cardView_1 = (CardView) row.findViewById(R.id.cardView_1);
                holder.imageView = (ImageView) row.findViewById(R.id.img_alert_info);
                holder.switch_arrive = (SwitchCompat) row.findViewById(R.id.switch_arrive);
                holder.switch_depart = (SwitchCompat) row.findViewById(R.id.switch_depart);

                holder.switch_arrive.setChecked(Boolean.parseBoolean(item.getGetGeo_fence_arrive_alert()));
                holder.switch_depart.setChecked(Boolean.parseBoolean(item.getGetGeo_fence_depart_alert()));

                holder.switch_arrive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if (isChecked) {
                            new DBHelper(GeofenceActivitySnehal.this).updateGeofenceArriveAlert(item.getGeo_fence_id(), "true");
                        } else {
                            new DBHelper(GeofenceActivitySnehal.this).updateGeofenceArriveAlert(item.getGeo_fence_id(), "false");
                        }
                    }
                });

                holder.switch_depart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if (isChecked) {
                            new DBHelper(GeofenceActivitySnehal.this).updateGeofenceDepartAlert(item.getGeo_fence_id(), "true");
                        } else {
                            new DBHelper(GeofenceActivitySnehal.this).updateGeofenceDepartAlert(item.getGeo_fence_id(), "false");
                        }
                    }
                });

                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                                .setTitleText("Geofence Alert Setting")
                                .setConfirmText("OK")
                                .setContentText("Set switch-buton to receive vehicle's Arrive-Departure in/out Geofence ")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                    }
                                })
                                .show();
                    }
                });

                row.setTag(holder);

            } else {
                holder = (ViewHolder) row.getTag();
            }


            holder.txt_geo_fence_name.setText(item.getGeo_fence_name());
            holder.txt_radius.setText(Integer.valueOf(item.getGeo_fence_radius()) + " km");

            holder.btn_geo_fence_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(GeofenceActivitySnehal.this);

                    alertDialog2.setTitle("Confirm Delete...");

                    alertDialog2.setMessage("Are you sure you want delete this geofence?");

                    alertDialog2.setIcon(R.mipmap.ic_launcher);

                    alertDialog2.setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    DBHelper dbHelper = new DBHelper(GeofenceActivitySnehal.this);
                                    dbHelper.deleteGeofence(item.geo_fence_id);
                                    list.remove(position);
                                    notifyDataSetChanged();

                                }
                            });

                    alertDialog2.setNegativeButton("NO",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });


                    alertDialog2.show();
                }
            });


            holder.cardView_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(GeofenceActivitySnehal.this, EditGeofence.class);
                    intent.putExtra("edit_lat_long", GEOFENCE_DATA.get(position).getGetGeo_fence_lat_long());
                    intent.putExtra("edit_radius", GEOFENCE_DATA.get(position).getGeo_fence_radius());
                    intent.putExtra("edit_id", GEOFENCE_DATA.get(position).getGeo_fence_id());

                    startActivity(intent);
                }
            });

            return row;
        }

        public HashSet<MapFragment> getMaps() {
            return mMaps;
        }
    }


    class ViewHolder {

        TextView txt_geo_fence_name, txt_radius;
        Button btn_geo_fence_delete;
        SwitchCompat switch_arrive, switch_depart;
        CardView cardView_1;
        ImageView imageView;
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
}
