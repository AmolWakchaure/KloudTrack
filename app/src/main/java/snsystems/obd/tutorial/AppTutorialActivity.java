package snsystems.obd.tutorial;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nineoldandroids.view.ViewHelper;

import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;
import snsystems.obd.activity.MainNavigationActivity;
import snsystems.obd.dashboard.DashboardActivity;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.sos.SubmitSosContactActivity;

public class AppTutorialActivity extends AnimationActivity {

    static final int NUM_PAGES = 4;

    ViewPager pager;
    PagerAdapter pagerAdapter;
    LinearLayout circles;
    Button skip;
    Button done;
    ImageButton next;
    boolean isOpaque = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_app_tutorial);

        skip = Button.class.cast(findViewById(R.id.btn_skip));
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(2);
            }
        });

        next = ImageButton.class.cast(findViewById(R.id.btn_next));
        next.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(pager.getCurrentItem() + 1, true);
            }
        });

        done = Button.class.cast(findViewById(R.id.btn_done));
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // endTutorial();

                finish();
            }
        });

        pager = (ViewPager) findViewById(R.id.tour_pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setPageTransformer(true, new CrossfadePageTransformer());
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == NUM_PAGES - 2 && positionOffset > 0) {
                    if (isOpaque) {
                        pager.setBackgroundColor(Color.TRANSPARENT);
                        isOpaque = false;
                    }
                } else
                {
                    if (!isOpaque) {
                        pager.setBackgroundColor(getResources().getColor(R.color.primary_material_light));
                        isOpaque = true;
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);
                if (position == NUM_PAGES - 2) {
                    skip.setVisibility(View.GONE);
                    next.setVisibility(View.GONE);
                    done.setVisibility(View.VISIBLE);
                } else if (position < NUM_PAGES - 2) {
                    skip.setVisibility(View.VISIBLE);
                    next.setVisibility(View.VISIBLE);
                    done.setVisibility(View.GONE);
                } else if (position == NUM_PAGES - 1) {
                    //endTutorial();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        buildCircles();
    }
    private void buildCircles(){
        circles = LinearLayout.class.cast(findViewById(R.id.circles));

        float scale = getResources().getDisplayMetrics().density;
        int padding = (int) (5 * scale + 0.5f);

        for(int i = 0 ; i < NUM_PAGES - 1 ; i++)
        {
            ImageView circle = new ImageView(this);
            circle.setImageResource(R.drawable.ic_swipe_indicator_white_18dp);
            circle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            circle.setAdjustViewBounds(true);
            circle.setPadding(padding, 0, padding, 0);
            circles.addView(circle);
        }

        setIndicator(0);
    }
    private void setIndicator(int index){
        if(index < NUM_PAGES){
            for(int i = 0 ; i < NUM_PAGES - 1 ; i++){
                ImageView circle = (ImageView) circles.getChildAt(i);
                if(i == index){
                    circle.setColorFilter(getResources().getColor(R.color.circle_selected));
                }else {
                    circle.setColorFilter(getResources().getColor(android.R.color.transparent));
                }
            }
        }
    }
    public class CrossfadePageTransformer implements ViewPager.PageTransformer {
        @Override
        public void transformPage(View page, float position) {
            int pageWidth = page.getWidth();

            View backgroundView = page.findViewById(R.id.welcome_fragment);
            View text_head= page.findViewById(R.id.heading);
            View text_content = page.findViewById(R.id.content);

            View object1_2 = page.findViewById(R.id.img_1_2);
            View object1_3 = page.findViewById(R.id.img_1_3);
            View object1_4 = page.findViewById(R.id.img_1_4);
            View object1_5 = page.findViewById(R.id.img_1_5);
            View object1_6 = page.findViewById(R.id.img_1_6);
            View object1_7 = page.findViewById(R.id.img_1_7);

            View object2_1 = page.findViewById(R.id.img_2_1);
            View object2_2 = page.findViewById(R.id.img_2_2);
            View object2_3 = page.findViewById(R.id.img_2_3);

            View object3_1 = page.findViewById(R.id.img_3_1);
            View object3_2 = page.findViewById(R.id.img_3_2);

            if(0 <= position && position < 1){
                ViewHelper.setTranslationX(page, pageWidth * -position);
            }
            if(-1 < position && position < 0){
                ViewHelper.setTranslationX(page,pageWidth * -position);
            }

            if (!(position <= -1.0f || position >= 1.0f) && !(position == 0.0f)) {
                if(backgroundView != null) {
                    ViewHelper.setAlpha(backgroundView,1.0f - Math.abs(position));
                }

                if (text_head != null) {
                    ViewHelper.setTranslationX(text_head,pageWidth * position);
                    ViewHelper.setAlpha(text_head, 1.0f - Math.abs(position));
                }

                if (text_content != null) {
                    ViewHelper.setTranslationX(text_content,pageWidth * position);
                    ViewHelper.setAlpha(text_content, 1.0f - Math.abs(position));
                }

                // parallax effect
                //First Page
                if(object1_2 != null){
                    ViewHelper.setTranslationX(object1_2, pageWidth/2 * position);
                }
                if(object1_3 != null){
                    ViewHelper.setTranslationX(object1_3, pageWidth/2 * position);
                }
                if(object1_4 != null){
                    ViewHelper.setTranslationX(object1_4, pageWidth/2 * position);
                }
                if(object1_5 != null){
                    ViewHelper.setTranslationX(object1_5, (float) (pageWidth/1.5 * position));
                }

                if(object1_6 != null){
                    ViewHelper.setTranslationX(object1_6, (float) (pageWidth/1.2 * position));
                }

                if(object1_7 != null){
                    ViewHelper.setTranslationX(object1_7, (float) (pageWidth / 1.1 * position));
                }

                //Second Page
                if(object2_1 != null){
                    position = (position < 0) ? -position : position;
                    ViewHelper.setTranslationY(object2_1, (float) (pageWidth / 1.3 * -position));
                }

                if(object2_2 != null){
                    ViewHelper.setTranslationY(object2_2, (float) (pageWidth / 1.8 * -position));
                }

                if(object2_3 != null){
                    ViewHelper.setTranslationY(object2_3, (float) (pageWidth / 1.8 * -position));
                }

                //Third Page
                if(object3_1 != null){
                    position = (position < 0) ? -position : position;
                    ViewHelper.setTranslationX(object3_1, (float) (pageWidth / 1.3 * -position));
                }

                if(object3_2 != null){
                    position = (position > 0) ? -position : position;
                    ViewHelper.setTranslationX(object3_2, (float) (pageWidth / 1.8 * -position));
                }
            }
        }
    }
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            TutorialFragment tp = null;
            switch(position){
                case 0:
                    tp = TutorialFragment.newInstance(R.layout.tutorial_one);
                    break;
                case 1:
                    tp = TutorialFragment.newInstance(R.layout.tutorial_two);
                    break;
                case 2:
                    tp = TutorialFragment.newInstance(R.layout.tutorial_three);
                    break;
                case 3:
                    tp = TutorialFragment.newInstance(R.layout.tutorial_four);
                    break;
            }
            return tp;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
    private void endTutorial()
    {
        finish();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);

//        //check sos contacts present
//        boolean c = S.checkSOSContactsAvailable(new DBHelper(AppTutorialActivity.this));
//
//        if(c)
//        {
//            Intent i = new Intent(AppTutorialActivity.this, DashboardActivity.class);
//            startActivity(i);
//            finish();
//        }
//        else
//        {
//            Intent i = new Intent(AppTutorialActivity.this, SubmitSosContactActivity.class);
//            startActivity(i);
//            finish();
//        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_app_tutorial, menu);
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
