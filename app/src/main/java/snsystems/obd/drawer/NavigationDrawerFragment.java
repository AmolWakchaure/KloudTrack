package snsystems.obd.drawer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import snsystems.obd.R;
import snsystems.obd.activity.CustomViewsActivity;
import snsystems.obd.activity.FeedbackActivity;
import snsystems.obd.classes.MyApplication;
import snsystems.obd.fueltrack.VehicleFuelTrackerActivity;
import snsystems.obd.fueltracknew.FuelTrackFillupReportsTabActivity;
import snsystems.obd.health.HealthNewActivity;
import snsystems.obd.health.VehicleHealthActivity;
import snsystems.obd.howtouse.HowToUseActivity;
import snsystems.obd.interfaces.Constants;
import snsystems.obd.maintenance.MaintenanceServiceTabActivity;
import snsystems.obd.maintenance.VehicleMaintenanceActivity;
import snsystems.obd.advertise.DisplayAdvertiseActivity;
import snsystems.obd.alerts.VehicleNotificationActivity;
import snsystems.obd.alertsnew.ManageAlertsTabsActivity;
import snsystems.obd.carlog.CarLogTabsActivity;
import snsystems.obd.classes.T;
import snsystems.obd.classes.VolleyResponseClass;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.docupload.DocumentUploadActivity;
import snsystems.obd.interfaces.VolleyCallback;
import snsystems.obd.notificationalerts.MakeAlertActivity;
import snsystems.obd.performancedash.PerformanceDashboardActivity;
import snsystems.obd.services.FloatingFaceBubbleService;
import snsystems.obd.sos.SubmitSosContactActivity;
import snsystems.obd.tutorial.AppTutorialActivity;
import snsystems.obd.devicemgt.ActivityManageDevice;
import snsystems.obd.userprofile.ActivityUserProfile;
import snsystems.obd.userprofile.ChangePasswordActivity;


/**
 * Created by shree on 17/02/2017.
 */
public class NavigationDrawerFragment extends Fragment {


    public static final String PREF_FILE_NAME="testpref";
    public static final String KEY_USER_LEARNED_DRAWER="user_learned_drawer";
    private RecyclerView recyclerView;
    private  View containerView;
    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationDrawerMenu adapter;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    private TextView textViewUserNAme;
    public NavigationDrawerFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserLearnedDrawer=Boolean.valueOf(readFromPreferences(getActivity(),KEY_USER_LEARNED_DRAWER,"false"));
        prefs = getActivity().getSharedPreferences("com", Context.MODE_PRIVATE);
        editor = prefs.edit();



        //display drawer first time
       /* if(savedInstanceState!=null){
            mFromSavedInstanceState=true;

        }*/

    }

    public static String readFromPreferences(Context context,String preferenceName,String defaultValue)
    {

        SharedPreferences sharedPreferences=context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName,defaultValue);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View layout=inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        recyclerView= (RecyclerView) layout.findViewById(R.id.drawerlist);
        textViewUserNAme = (TextView) layout.findViewById(R.id.textViewUserNAme);

        adapter=new NavigationDrawerMenu(getActivity(),getData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //get User name

        String username = S.getDeviceIdUserName(MyApplication.db);
        String [] data = username.split("#");

        if(username != null || username.length() > 0)
        {
            textViewUserNAme.setText(data[2]);
        }

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                if(position == 0)
                {

                    mDrawerLayout.closeDrawers();
                    Intent intent_user_profile = new Intent(view.getContext(), ActivityManageDevice.class);
                    startActivity(intent_user_profile);

                }

                //professional login
                if(position == 1)
                {

                    mDrawerLayout.closeDrawers();
                    Intent intent_user_profile = new Intent(view.getContext(), ActivityUserProfile.class);
                    startActivity(intent_user_profile);

                }
                //user profile
                else if(position == 2)
                {
                    mDrawerLayout.closeDrawers();
                    Intent intent_user_profile = new Intent(view.getContext(), SubmitSosContactActivity.class);
                    intent_user_profile.putExtra("STATUS","view");
                    startActivity(intent_user_profile);


                }
                //recent booking
                else if(position == 3)
                {
                    mDrawerLayout.closeDrawers();
                    Intent intent_user_profile = new Intent(view.getContext(), DocumentUploadActivity.class);
                    startActivity(intent_user_profile);

                }
                //enquiry
                else if(position == 4) {
                    mDrawerLayout.closeDrawers();
//                    Intent intent_user_profile = new Intent(view.getContext(), ManageAlertsActivity.class);
//                    startActivity(intent_user_profile);
                    Intent intent_user_profile = new Intent(view.getContext(), ManageAlertsTabsActivity.class);
                    startActivity(intent_user_profile);



                }
//                else if(position == 5) {
//                    mDrawerLayout.closeDrawers();
//                    Intent intent_user_profile = new Intent(view.getContext(), VehicleNotificationActivity.class);
//                    startActivity(intent_user_profile);
//
//
//
//                }
                else if(position == 5) {
                    mDrawerLayout.closeDrawers();
                    //Intent intent_user_profile = new Intent(view.getContext(), VehicleMaintenanceActivity.class);
                    Intent intent_user_profile = new Intent(view.getContext(), MaintenanceServiceTabActivity.class);
                    startActivity(intent_user_profile);



                }
                else if(position == 6) {
                    mDrawerLayout.closeDrawers();
                    Intent intent_user_profile = new Intent(view.getContext(), FuelTrackFillupReportsTabActivity.class);
                    startActivity(intent_user_profile);



                }
//                else if(position == 7)
//                {
//                    mDrawerLayout.closeDrawers();
//                    Intent intent_user_profile = new Intent(view.getContext(), CarLogTabsActivity.class);
//                    startActivity(intent_user_profile);
//
//                }
                else if(position == 7)
                {
                    mDrawerLayout.closeDrawers();
                    Intent intent_user_profile = new Intent(view.getContext(), PerformanceDashboardActivity.class);
                    startActivity(intent_user_profile);

                }
                else if(position == 8)
                {
                    mDrawerLayout.closeDrawers();
                    Intent intent_user_profile = new Intent(view.getContext(), ChangePasswordActivity.class);
                    startActivity(intent_user_profile);

                }
                else if(position == 9)
                {
                    mDrawerLayout.closeDrawers();
                    Intent intent_user_profile = new Intent(view.getContext(), FeedbackActivity.class);
                    startActivity(intent_user_profile);

                }
                else if(position == 10)
                {
                    mDrawerLayout.closeDrawers();
                    //Intent intent_user_profile = new Intent(view.getContext(), AppTutorialActivity.class);
                    Intent intent_user_profile = new Intent(view.getContext(), HowToUseActivity.class);
                    startActivity(intent_user_profile);

                }
                else if(position == 11)
                {
                    mDrawerLayout.closeDrawers();
                    Intent intent_user_profile = new Intent(view.getContext(), DisplayAdvertiseActivity.class);

                    startActivity(intent_user_profile);

                }
                else if(position == 12)
                {
                    mDrawerLayout.closeDrawers();
                  //  Intent intent_user_profile = new Intent(view.getContext(), VehicleHealthActivity.class);
                    Intent intent_user_profile = new Intent(view.getContext(), HealthNewActivity.class);
                    startActivity(intent_user_profile);

                }
                else if(position == 13)
                {
                    mDrawerLayout.closeDrawers();

                    boolean c = T.checkConnection(getActivity());

                    if(c)
                    {
                        logoutApp();
                    }
                    else
                    {
                        T.t(getActivity(),"Network connection off");
                    }
                }
//                else if(position == 15)
//                {
//                    mDrawerLayout.closeDrawers();
//                    Intent intent_user_profile = new Intent(view.getContext(), MakeAlertActivity.class);
//                    startActivity(intent_user_profile);
//
//                }
                //about
               /* else if(position == 4)
                {


                }*/



            }

            @Override
            public void longClick(View view, int position) {

            }


        }));


        return layout;
    }

    private void logoutApp()
    {


        String device_id_mail = S.getDeviceIdUserName(MyApplication.db);

        String [] data = device_id_mail.split("#");

        String [] parameters =
                {
                        "username"+"#"+data[1]
                };
        VolleyResponseClass.getResponseProgressDialog(
                new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.e("VolleyResponse", "" + result);
                        //{"status":1,"success":"Success! Record updated successfully."}

                        parseResponse(result);


                    }
                },
                getActivity(),
                getResources().getString(R.string.webUrl)+""+getResources().getString(R.string.user_logout),
                recyclerView,
                parameters,
                "Logout Please wait...");
    }

    private void parseResponse(String result) {

        try
        {
            if(result != null || result.length() > 0)
            {
                Object json = new JSONTokener(result).nextValue();
                if (json instanceof JSONObject)
                {
                    final JSONObject tripsJsonObject = new JSONObject(result);
                    String status = tripsJsonObject.getString("status");

                    if(status.equals("1"))
                    {
                        //editor.putBoolean("logout", true).commit();


                        new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Success")
                                .setConfirmText("OK")
                                .setContentText("Successfully logout.")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog)
                                    {

                                        sDialog.dismissWithAnimation();

                                        MyApplication.db.clearDbTableLogout();
                                        //getActivity().stopService(new Intent(getActivity(), FloatingFaceBubbleService.class));

                                        SharedPreferences preferences = getActivity().getSharedPreferences(Constants.NAVIGATION, 0);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("status","0");
                                        editor.putString("checkedStatus","unchecked");
                                        editor.commit();

                                        Intent intent_logout = new Intent(getActivity(), CustomViewsActivity.class);
                                        intent_logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent_logout);
                                        getActivity().finish();

////                                        Intent i = new Intent(getActivity(), CustomViewsActivity.class);
////                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////                                        startActivity(i);
//                                        editor.putBoolean("logout", true).commit();
//                                        editor.putBoolean("checkbox_password", true).commit();
//                                       // Toast.makeText(getApplicationContext(), "User Logged Off..", Toast.LENGTH_LONG).show();
//                                        Intent intent_logout = new Intent(getActivity(), CustomViewsActivity.class);
//                                        intent_logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                        startActivity(intent_logout);
//                                        getActivity().finish();
                                    }
                                })
                                .show();
                    }
                    else
                    {
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Fail...")
                                .setConfirmText("Try again")
                                .setContentText("Fail to logout try again.")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();

                                        logoutApp();
                                    }
                                })
                                .show();

                    }
                }
                else
                {
                    T.t(getActivity(), "incorect json");
                }
            }
            else
            {
                T.t(getActivity(),"0 or null json");
            }
        }
        catch(Exception e)
        {
            T.t(getActivity(),""+e);

        }
    }

    public static List<NavigationDrawerInformation> getData()
    {

        List<NavigationDrawerInformation> data=new ArrayList<>();
        int[] icons={
                R.drawable.ic_manage_device,
                R.drawable.ic_my_profile_24dp,
                R.drawable.ic_message_black_24dp,
                R.drawable.ic_document_upload_24dp,
                R.drawable.ic_notifications_black_24dp,
               // R.drawable.ic_vehicle_notification_24dp,
                R.drawable.ic_build_black_24dp,
                R.drawable.ic_local_gas_station_black_24dp,
               // R.drawable.ic_car_log_24dp,
                R.drawable.ic_dashboard_black_24dp,
                R.drawable.ic_lock_drawer_black_24dp,
                R.drawable.ic_feedback_black_24dp,
                R.drawable.ic_how_touse_24dp,
                R.drawable.ic_advertise_24dp,
                R.drawable.ic_health_24dp,
                R.drawable.ic_exit_to_app_black_24dp

        };
        String[] titles={
                "Manage Your Device",
                "My Profile",
                "Manage SoS",
                "Document Upload",
                "Manage Alerts",
               // "Vehicle Notification",
                "Maintenance and Service",
                "Fuel Tracker",
                //"Car Log",
                "Performance Dashboard",
                "Change Password",
                "Feedback",
                "How to use ?",
                "Advertise",
                "Health",
                "Logout"

        };

        for(int i=0;i<icons.length && i<titles.length;i++)
        {
            NavigationDrawerInformation current=new NavigationDrawerInformation();
            current.iconId=icons[i%icons.length];
            current.title=titles[i%titles.length];
            data.add(current);
        }
        return data;
    }
    public void setUp(int FragmantId,DrawerLayout drawerLayout, final Toolbar toolbar) {

        containerView=getActivity().findViewById(FragmantId);
        mDrawerLayout=drawerLayout;
        mDrawerToggle=new ActionBarDrawerToggle(getActivity(),drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){


            @Override
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
                if(!mUserLearnedDrawer)
                {
                    mUserLearnedDrawer=true;
                    saveToPreferences(getActivity(),KEY_USER_LEARNED_DRAWER,mUserLearnedDrawer+"");
                }
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView)
            {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            /*
            // 3.Navigation Drawer changeble color like shodow colors.
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset)
            {
                if (slideOffset<0.6)
                {
                    toolbar.setAlpha(1-slideOffset);
                }

            }*/
        };
       /* if (!mUserLearnedDrawer && !mFromSavedInstanceState)
        {
            mDrawerLayout.openDrawer(containerView);
        }*/
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        //for display option menue
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {

                mDrawerToggle.syncState();
            }
        });


    }

    public static void saveToPreferences(Context context,String preferenceName,String preferenceValue)
    {

        SharedPreferences sharedPreferences=context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(preferenceName,preferenceValue);
        editor.apply();
    }
    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        //detetect gestures and events supplied by motio event
        private GestureDetector gestureDetector;
        private  ClickListener clickListener;
        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener){

            this.clickListener=clickListener;
            gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    //return super.onSingleTapUp(e);
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {

                    View child=recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if(child!=null && clickListener!=null)
                    {

                        clickListener.longClick(child,recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child=rv.findChildViewUnder(e.getX(), e.getY());
            if(child!=null && clickListener!=null && gestureDetector.onTouchEvent(e))
            {
                clickListener.onClick(child,rv.getChildPosition(child));
            }

            // Log.e("AMOL WAKCHAURE","onInterceptTouchEvent"+gestureDetector.onTouchEvent(e)+""+e);
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
    public static interface ClickListener{
        public void onClick(View view, int position);
        public void longClick(View view, int position);
    }

}
