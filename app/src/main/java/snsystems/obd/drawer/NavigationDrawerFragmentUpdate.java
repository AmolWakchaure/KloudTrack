package snsystems.obd.drawer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import snsystems.obd.R;

/**
 * Created by Amol on 27-February-17.
 */

//public class NavigationDrawerFragmentUpdate
//{
//
//}
public class NavigationDrawerFragmentUpdate extends Fragment {


    public static final String PREF_FILE_NAME="testpref";
    public static final String KEY_USER_LEARNED_DRAWER="user_learned_drawer";

    private View containerView;
    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;


    public NavigationDrawerFragmentUpdate() {
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

        View layout=inflater.inflate(R.layout.new_drawer, container, false);




        return layout;
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

    public static interface ClickListener{
        public void onClick(View view, int position);
        public void longClick(View view, int position);
    }

}
