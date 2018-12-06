package snsystems.obd.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import snsystems.obd.R;
import snsystems.obd.adapter.MenuListAdapter;
import snsystems.obd.classes.ConnectionStatus;
import snsystems.obd.pojo.ItemObject;
import snsystems.obd.devicemgt.ActivityManageDevice;

/**
 * Created by Admin on 12/30/2016.
 */
public class MainNavigationActivity extends ActionBarActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    String[] titles = {"MY PROFILE", "CHANGE PASSWORD", "MANAGE ACCOUNT", "EMERGENCY CONTACT", "DOCUMENT UPLOAD", "LOGOUT"};
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar topToolBar;
    FloatingActionButton fb_locate, fb_sos, fb_trips, fb_health, fb_geo;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_main);
        prefs = this.getSharedPreferences("com", Context.MODE_PRIVATE);
        editor = prefs.edit();
        mTitle = mDrawerTitle = getTitle();

        topToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
//        topToolBar.setLogo(R.drawable.ic_activity);
        topToolBar.setLogoDescription(getResources().getString(R.string.logo_desc));

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        List<ItemObject> listViewItems = new ArrayList<>();
        listViewItems.add(new ItemObject("MY \n PROFILE", R.drawable.ic_account_box_white_48dp));
        listViewItems.add(new ItemObject("CHANGE \n PASSWORD", R.drawable.ic_https_white_48dp));
        listViewItems.add(new ItemObject("MANAGE \n ACCOUNT", R.drawable.ic_settings_input_hdmi_white_48dp));
        listViewItems.add(new ItemObject("EMERGENCY \n CONTACT", R.drawable.ic_contact_phone_white_48dp));
        listViewItems.add(new ItemObject("DOCUMENT \n UPLOAD", R.drawable.ic_description_white_48dp));
        listViewItems.add(new ItemObject("LOGOUT", R.drawable.ic_exit_to_app_white_48dp));

        mDrawerList.setAdapter(new MenuListAdapter(this, listViewItems));

        mDrawerToggle = new ActionBarDrawerToggle(MainNavigationActivity.this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // make Toast when click
                Toast.makeText(getApplicationContext(), "Position " + position, Toast.LENGTH_LONG).show();
                selectItemFragment(position);
            }
        });

        fb_locate = (FloatingActionButton) findViewById(R.id.fb_locate_my_car);
        fb_sos = (FloatingActionButton) findViewById(R.id.fb_sos);
        fb_health = (FloatingActionButton) findViewById(R.id.fb_health);
        fb_trips = (FloatingActionButton) findViewById(R.id.fb_trips);
        fb_geo = (FloatingActionButton) findViewById(R.id.fb_geo_fencing);

        fb_locate.setSize(FloatingActionButton.SIZE_MINI);
        fb_locate.setColorNormalResId(R.color.fb_button_normal);
        fb_locate.setColorPressedResId(R.color.fb_button_pressed);
        fb_locate.setIcon(R.drawable.ic_room_white_24dp);
        fb_locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_LONG).show();
//                Intent intent_device_id = new Intent(getApplicationContext(), ActivityManageDevice.class);
//                startActivity(intent_device_id);
            }
        });

        fb_sos.setSize(FloatingActionButton.SIZE_MINI);
        fb_sos.setColorNormalResId(R.color.fb_button_normal);
        fb_sos.setColorPressedResId(R.color.fb_button_pressed);
        fb_sos.setIcon(R.drawable.ic_contact_mail_white_24dp);
        fb_sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent_device_id = new Intent(getApplicationContext(), SosActivity.class);
//                startActivity(intent_device_id);
            }
        });

        fb_health.setSize(FloatingActionButton.SIZE_MINI);
        fb_health.setColorNormalResId(R.color.fb_button_normal);
        fb_health.setColorPressedResId(R.color.fb_button_pressed);
        fb_health.setIcon(R.drawable.ic_engine_white_24dp);
        fb_health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent_device_id = new Intent(getApplicationContext(), HealthActivity.class);
//                startActivity(intent_device_id);
            }
        });
        fb_trips.setSize(FloatingActionButton.SIZE_MINI);
        fb_trips.setColorNormalResId(R.color.fb_button_normal);
        fb_trips.setColorPressedResId(R.color.fb_button_pressed);
        fb_trips.setIcon(R.drawable.ic_trending_up_white_24dp);
        fb_trips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent_device_id = new Intent(getApplicationContext(), TripsActivity.class);
//                startActivity(intent_device_id);
            }
        });

        fb_geo.setSize(FloatingActionButton.SIZE_MINI);
        fb_geo.setColorNormalResId(R.color.fb_button_normal);
        fb_geo.setColorPressedResId(R.color.fb_button_pressed);
        fb_geo.setIcon(R.drawable.ic_my_location_white_24dp);
        fb_geo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "For Geo-fencing", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void selectItemFragment(int position) {

        Fragment fragment = null;
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            default:
            case 0:
//                Intent intent_user_profile = new Intent(getApplicationContext(), ActivityUserProfile.class);
//                startActivity(intent_user_profile);
//                fragment = new DefaultFragment();
                break;
            case 1:
//                Intent intent_device_id = new Intent(getApplicationContext(), ChangePasswordActivity.class);
//                startActivity(intent_device_id);
//                fragment = new DefaultFragment();
                break;
            case 2:
//                fragment = new DefaultFragment();
                Intent intent_manage_acc = new Intent(getApplicationContext(), ActivityManageDevice.class);
                startActivity(intent_manage_acc);
                break;
            case 3:
//                fragment = new DefaultFragment();
//                Intent intent_emergency_contact = new Intent(getApplicationContext(), EmergencyContactsActivity.class);
//                startActivity(intent_emergency_contact);
                break;
            case 4:
//                fragment = new DefaultFragment();
//                Intent intent_document_upload = new Intent(getApplicationContext(), DocumentUploadActivity.class);
//                startActivity(intent_document_upload);
                break;
            case 5:
//                fragment = new DefaultFragment();
                if (ConnectionStatus.isConnectingToInternet(getApplicationContext())) {
                    logoutUser();

                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                }
                editor.putBoolean("logout", true).commit();
                Toast.makeText(getApplicationContext(), "User Logged Off..", Toast.LENGTH_LONG).show();
                Intent intent_logout = new Intent(getApplicationContext(),
                        CustomViewsActivity.class);
                intent_logout.putExtra("finish", true);
                intent_logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent_logout);
                finish();
                break;
        }

        mDrawerList.setItemChecked(position, true);
        setTitle(titles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }
    private void logoutUser() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.webUrl)+""+getResources().getString(R.string.user_logout),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Log.d(">>",">>response"+response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                     //   handleVolleyerrorProgressNew(error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username",prefs.getString("emailId", ""));
//                Log.d(">>", ">>>Send to server " +prefs.getString("emailId", ""));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
