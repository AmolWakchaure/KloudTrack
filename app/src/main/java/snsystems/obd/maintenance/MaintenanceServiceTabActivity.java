package snsystems.obd.maintenance;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;
import snsystems.obd.classes.T;
import snsystems.obd.fueltracknew.FillupFuelFragment;
import snsystems.obd.fueltracknew.FuelReportsFragment;
import snsystems.obd.trips.ViewPagerAdapter;

public class MaintenanceServiceTabActivity extends AnimationActivity {


    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;

    ViewPagerAdapter adapter;
    FragmentTransaction ft;
    MaintenanceHistoryFragment maintenanceHistoryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_service_tab);

       // Log.e("Method_call","onCreate");


        initialize();
        setListner();
    }
    private void setListner()
    {

//        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
//            {
//
//            }
//
//            @Override
//            public void onPageSelected(int position)
//            {
//                T.t(MaintenanceServiceTabActivity.this,"ok..."+position);
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state)
//            {
//
//            }
//        });
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                viewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition())
                {
                    case 0:
                        // showToast("Two");
                        break;
                    case 1:
                        //showToast("Two");
                        break;
                    case 2:
                        //showToast("Three");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }
        });
    }

    private void initialize() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.tabanim_viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabanim_tabs);
        tabLayout.setupWithViewPager(viewPager);

        maintenanceHistoryFragment = new MaintenanceHistoryFragment();
    }

    private void setupViewPager(ViewPager viewPager)
    {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new FillupMaintenanceFragment(),"Fillup");
        adapter.addFrag(new MaintenanceHistoryFragment(),"History");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trips_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        String status = data.getStringExtra("status");

        if(requestCode == 6 && status.equals("true"))
        {

            initialize();
            viewPager.setCurrentItem(1);
        }
        else
        {
            viewPager.setCurrentItem(1);
        }
    }
}
