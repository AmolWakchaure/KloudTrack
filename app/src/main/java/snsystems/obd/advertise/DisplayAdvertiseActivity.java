package snsystems.obd.advertise;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;
import snsystems.obd.classes.T;
import snsystems.obd.classes.VolleyResponseClass;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.interfaces.Constants;
import snsystems.obd.interfaces.VolleyCallback;

public class DisplayAdvertiseActivity extends AnimationActivity {


    @Bind(R.id.expandableListviewForDisplayAdvertise)
    ExpandableListView expandableListviewForDisplayAdvertise;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.notificationHideRelativeLayout)
    RelativeLayout notificationHideRelativeLayout;

    @Bind(R.id.imgHideLayout)
    ImageView imgHideLayout;

    @Bind(R.id.textViewHideLayout)
    TextView textViewHideLayout;


    private DisplayAdvertiseAdapter
            productCategoryAdapter;

    ArrayList<String> PARRENT = new ArrayList<>();
    HashMap<String, List<String>> CHILD = new HashMap<String, List<String>>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_advertise);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        expandableListviewForDisplayAdvertise.setGroupIndicator(null);


        boolean c = T.checkConnection(DisplayAdvertiseActivity.this);

        if(c)
        {
            getAdvertise();
        }
        else
        {
            notificationHideRelativeLayout.setVisibility(View.VISIBLE);
            imgHideLayout.setImageResource(R.drawable.ic_cloud_off_black_48dp);
            textViewHideLayout.setText("No Connection");
        }
    }

    private void getAdvertise() {


        String emailData = S.getDeviceIdUserName(new DBHelper(DisplayAdvertiseActivity.this));

        String [] data = emailData.split("#");

        String [] parameters =
                {
                        "username"+"#"+data[1]
                };
        VolleyResponseClass.getResponseProgressDialog(
                new VolleyCallback() {
                    @Override
                    public void onSuccess(String result)
                    {
                        Log.e("VolleyResponse", "" + result);

                        //T.t(DisplayAdvertiseActivity.this,""+result);
                        //{"status":1,"advertise":[{"advertise_title":"ddd","advertise_sub_title":"sss","advertise_description":"eeee","start_date":"2017-02-01","end_date":"2017-03-18","file_link":0}]}

                        parseResponse(result);


                    }
                },
                DisplayAdvertiseActivity.this,
                getResources().getString(R.string.webUrl)+""+getResources().getString(R.string.displayAdvertise),
                expandableListviewForDisplayAdvertise,
                parameters,
                "Loading advertise...");

    }

    private void parseResponse(String result) {


        ArrayList<String> MENU_PARRENT = new ArrayList<>();


        String advertise_title = Constants.NA;
        String advertise_sub_title = Constants.NA;
        String advertise_description = Constants.NA;
        String file_link = Constants.NA;

        try
        {
            if(result != null || result.length() > 0)
            {
                Object json = new JSONTokener(result).nextValue();
                if (json instanceof JSONObject)
                {
                    JSONObject adJsonObject = new JSONObject(result);
                    String status = adJsonObject.getString("status");

                    if(status.equals("1"))
                    {
                        JSONArray adJsonArray = adJsonObject.getJSONArray("advertise");

                        for(int i = 0; i < adJsonArray.length(); i++)
                        {
                            List<String> su_items = new ArrayList<>();

                            JSONObject addJsonObject = adJsonArray.getJSONObject(i);

                            if(addJsonObject.has("advertise_title") && !addJsonObject.isNull("advertise_title"))
                            {
                                advertise_title = addJsonObject.getString("advertise_title");
                            }

                            if(addJsonObject.has("advertise_sub_title") && !addJsonObject.isNull("advertise_sub_title"))
                            {
                                advertise_sub_title = addJsonObject.getString("advertise_sub_title");
                            }
                            if(addJsonObject.has("advertise_description") && !addJsonObject.isNull("advertise_description"))
                            {
                                advertise_description = addJsonObject.getString("advertise_description");
                            }
                            if(addJsonObject.has("file_link") && !addJsonObject.isNull("file_link"))
                            {
                                file_link = addJsonObject.getString("file_link");
                            }

                            MENU_PARRENT.add(advertise_title+"#"+advertise_sub_title);
                            su_items.add(advertise_description+"#"+file_link);

                            PARRENT.add(MENU_PARRENT.get(i));
                            CHILD.put(PARRENT.get(i), su_items);

                        }

                        productCategoryAdapter = new DisplayAdvertiseAdapter(DisplayAdvertiseActivity.this, PARRENT, CHILD);
                        expandableListviewForDisplayAdvertise.setAdapter(productCategoryAdapter);


                    }
                    else
                    {

                        notificationHideRelativeLayout.setVisibility(View.VISIBLE);
                        imgHideLayout.setImageResource(R.drawable.ic_loyalty_black_48dp);
                        textViewHideLayout.setText("No Advertise Found");

                    }
                }
                else
                {
                    T.t(DisplayAdvertiseActivity.this, "incorect json");
                }
            }
            else
            {
                T.t(DisplayAdvertiseActivity.this,"0 or null json");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view

        return super.onPrepareOptionsMenu(menu);
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
