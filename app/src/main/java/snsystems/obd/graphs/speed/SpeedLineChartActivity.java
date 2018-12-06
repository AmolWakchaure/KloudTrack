package snsystems.obd.graphs.speed;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;
import snsystems.obd.activity.FeedbackActivity;
import snsystems.obd.classes.T;
import snsystems.obd.classes.VolleyResponseClass;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.devicemgt.ActivityUpdateDevice;
import snsystems.obd.docupload.DocumentUploadActivity;
import snsystems.obd.interfaces.Constants;
import snsystems.obd.interfaces.VolleyCallback;
import snsystems.obd.interfaces.VolleyErrorCallback;

public class SpeedLineChartActivity extends AnimationActivity implements  OnChartGestureListener, OnChartValueSelectedListener {


    private LineChart mChart;
    //private SeekBar mSeekBarX, mSeekBarY;
    //private TextView tvX, tvY;




    @Bind(R.id.toolbar)
    Toolbar toolbar;

    XAxis xAxis;
    LimitLine llXAxis;
    YAxis leftAxis;

    ArrayList<Entry> valuesForSpeedWeekly = new ArrayList<Entry>();
    ArrayList<Entry> valuesForSpeedMonthly = new ArrayList<Entry>();

    String graphName,actionBarTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);
        ButterKnife.bind(this);


        Bundle bundleGraph = getIntent().getExtras();

        graphName = bundleGraph.getString("graphName");


        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        manageGraph();
        manageAxis();
        //weekly,monthly


        if(graphName.equals("speed"))
        {
            actionBarTitle = "Speed Graph";
            getSupportActionBar().setTitle(actionBarTitle);
            getSpeedWeeklyMonthlyData("weekly","speed","Jan");
        }
        else if(graphName.equals("rpm"))
        {
            actionBarTitle = "RPM Graph";
            getSupportActionBar().setTitle(actionBarTitle);
            getSpeedWeeklyMonthlyData("weekly","rpm","Jan");
        }
        else if(graphName.equals("ontime"))
        {
            actionBarTitle = "Vehicle On Time Graph";
            getSupportActionBar().setTitle(actionBarTitle);
            getSpeedWeeklyMonthlyData("weekly","ontime","Jan");
        }
        else if(graphName.equals("battery_voltage"))
        {
            actionBarTitle = "Battery Voltage Graph";
            getSupportActionBar().setTitle(actionBarTitle);
            getSpeedWeeklyMonthlyData("weekly","battery_voltage","Jan");
        }
        else if(graphName.equals("coolant_temp"))
        {
            actionBarTitle = "Coolant Temperature Graph";
            getSupportActionBar().setTitle(actionBarTitle);
            getSpeedWeeklyMonthlyData("weekly","coolant_temp","Jan");
        }

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);


    }
    public void getMonthlyGraph(View view)
    {

        DateFormatSymbols symbols = new DateFormatSymbols();
        String[] monthNames = symbols.getMonths();

        ArrayList<String> MONTH_NAMES = T.getMonthNamesCurrent(monthNames);

        new MaterialDialog.Builder(SpeedLineChartActivity.this)
                .title("Select Month")
                .items(MONTH_NAMES)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice()
                {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text)
                    {
                        try
                        {

                            StringBuffer buffer = new StringBuffer();
                            String monthName = text.toString();

                            for(int i = 0; i < 3; i++)
                            {
                                char c = monthName.charAt(i);

                                buffer.append(c);
                            }

                            getMonthWiseGraphDetails(buffer.toString());

                           // T.t(SpeedLineChartActivity.this,""+text.toString());
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        return true;
                    }
                })
                .show();





    }

    private void getMonthWiseGraphDetails(String monthName)
    {

        if(graphName.equals("speed"))
        {
            getSpeedWeeklyMonthlyData("monthly","speed",monthName);
        }
        else if(graphName.equals("rpm"))
        {
            getSpeedWeeklyMonthlyData("monthly","rpm",monthName);
        }
        else if(graphName.equals("ontime"))
        {
            getSpeedWeeklyMonthlyData("monthly","ontime",monthName);
        }
        else if(graphName.equals("battery_voltage"))
        {
            getSpeedWeeklyMonthlyData("monthly","battery_voltage",monthName);
        }
        else if(graphName.equals("coolant_temp"))
        {
            getSpeedWeeklyMonthlyData("monthly","coolant_temp",monthName);
        }

        mChart.invalidate();


    }

    public void getDailyGraph(View view)
    {

        if(graphName.equals("speed"))
        {
            getSpeedWeeklyMonthlyData("weekly","speed","Jan");
        }
        else if(graphName.equals("rpm"))
        {
            getSpeedWeeklyMonthlyData("weekly","rpm","Jan");
        }
        else if(graphName.equals("ontime"))
        {
            getSpeedWeeklyMonthlyData("weekly","ontime","Jan");
        }
        else if(graphName.equals("battery_voltage"))
        {
            getSpeedWeeklyMonthlyData("weekly","battery_voltage","Jan");
        }
        else if(graphName.equals("coolant_temp"))
        {
            getSpeedWeeklyMonthlyData("weekly","coolant_temp","Jan");
        }

        mChart.invalidate();

    }

    private void manageAxis()
    {


        // x-axis limit line
        llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(2f);
        llXAxis.enableDashedLine(3f, 3f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(3f, 3f, 0f);//enable grid
        //xAxis.setValueFormatter(new MyCustomXAxisValueFormatter());
        //xAxis.addLimitLine(llXAxis); // add x-axis limit line
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // xAxis.setAxisMinValue(1);
//        xAxis.setAxisMaxValue(6);
//        xAxis.setAxisMinValue(0);


        xAxis.setAxisMaxValue(30);
        xAxis.setAxisMinValue(0);

//        final String[] mMonths = new String[] {
//                "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
//        };
//
//        xAxis.setValueFormatter(new IAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, AxisBase axis)
//            {
//                return mMonths[(int) value % mMonths.length];
//            }
//
//            @Override
//            public int getDecimalDigits() {
//                return 0;
//            }
//        })





//        LimitLine ll1 = new LimitLine(150f, "Upper Limit");
//        ll1.setLineWidth(4f);
//        ll1.enableDashedLine(10f, 10f, 0f);
//        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
//        ll1.setTextSize(10f);
//
//
//        LimitLine ll2 = new LimitLine(-30f, "Lower Limit");
//        ll2.setLineWidth(4f);
//        ll2.enableDashedLine(10f, 10f, 0f);
//        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
//        ll2.setTextSize(10f);


        leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
//        leftAxis.addLimitLine(ll1);
//        leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaximum(200f);
        leftAxis.setAxisMinimum(-50f);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(3f, 3f, 0f); //display grid here
        leftAxis.setDrawZeroLine(true);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);
//        leftAxis.setAxisMaxValue(200);
//        leftAxis.setAxisMinValue(0);

        leftAxis.setAxisMaxValue(200);
        leftAxis.setAxisMinValue(0);


        mChart.getAxisRight().setEnabled(false);

        //mChart.getViewPortHandler().setMaximumScaleY(2f);
        //mChart.getViewPortHandler().setMaximumScaleX(2f);

        // add data
        //setWeeklyData();
    }

    private void manageGraph()
    {

        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);

        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);

        // set an alternative background color
        // mChart.setBackgroundColor(Color.GRAY);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart

    }

    private void setAnimationToGraph() {


        List<ILineDataSet> sets = mChart.getData()
                .getDataSets();

        for (ILineDataSet iSet : sets) {

            LineDataSet set = (LineDataSet) iSet;

            set.setDrawCircles(true);


            set.setMode(set.getMode() == LineDataSet.Mode.CUBIC_BEZIER
                    ? LineDataSet.Mode.LINEAR
                    :  LineDataSet.Mode.CUBIC_BEZIER);

//            if (set.isDrawCirclesEnabled())
//                set.setDrawCircles(false);
//            else
//                set.setDrawCircles(true);
        }
        mChart.invalidate();
        mChart.animateXY(3000, 3000);
    }
    private void getSpeedWeeklyMonthlyData(String weeklyOrMonthlyStatus,String graphName,String monthName)
    {

        if(T.checkConnection(SpeedLineChartActivity.this))
        {
            getSpeedGraphData(weeklyOrMonthlyStatus,graphName,monthName);
        }
        else
        {
            T.displayErrorMessage(SpeedLineChartActivity.this, Constants.OOPS,Constants.OKK,Constants.NETWORK_CONNECTION_OFF);
        }

    }

    private void getSpeedGraphData(final String weeklyOrMonthlyStatus, final String graphName, final String monthName)
    {
        
        String weeklyMonthlyKeyword = null;
        String graphWebserViceName = null;

        if(graphName.equals("speed"))
        {
            graphWebserViceName = getResources().getString(R.string.getSpeedGraphWeeklyMonthly);
        }
        else if(graphName.equals("ontime"))
        {
            graphWebserViceName = getResources().getString(R.string.getOnTimeGraphWeeklyMonthly);
        }
        else if(graphName.equals("rpm"))
        {
            graphWebserViceName = getResources().getString(R.string.getRpmGraphWeeklyMonthly);
        }
        else if(graphName.equals("battery_voltage"))
        {
            graphWebserViceName = getResources().getString(R.string.getBatteryVoltageGraphWeeklyMonthly);
        }
        else if(graphName.equals("coolant_temp"))
        {
            graphWebserViceName = getResources().getString(R.string.getCoolantTempGraphWeeklyMonthly);
        }

        if(weeklyOrMonthlyStatus.equals("weekly"))
        {
            weeklyMonthlyKeyword = weeklyOrMonthlyStatus;

        }
        else if(weeklyOrMonthlyStatus.equals("monthly"))
        {
            weeklyMonthlyKeyword = weeklyOrMonthlyStatus;

        }

        String device_id_mail = S.getDeviceIdUserName(new DBHelper(SpeedLineChartActivity.this));

        String [] data = device_id_mail.split("#");

        String [] parameters =
                {
                        "device_id"+"#"+data[0],
                        "keyword"+"#"+weeklyMonthlyKeyword,
                        "month"+"#"+monthName
                };

//        String [] parameters =
//                {
//                        "device_id"+"#"+"9000",
//                        "keyword"+"#"+weeklyMonthlyKeyword,
//                        "month"+"#"+monthName
//
//
//                };
        VolleyResponseClass.getResponse(
                new VolleyCallback()
                {
                    @Override
                    public void onSuccess(String result)
                    {

                        parseSpeedGraph(result,weeklyOrMonthlyStatus,graphName);

                    }
                },
                new VolleyErrorCallback()
                {
                    @Override
                    public void onError(final VolleyError result) {

                        new SweetAlertDialog(SpeedLineChartActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setConfirmText("Try again")
                                .setContentText(""+result)
                                .setCancelText("Cancel")
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                                        sweetAlertDialog.dismissWithAnimation();
                                    }
                                })
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                                {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog)
                                    {
                                        sDialog.dismissWithAnimation();

                                        handleError(result,weeklyOrMonthlyStatus,graphName,monthName);
                                    }
                                })
                                .show();

                    }
                },
                SpeedLineChartActivity.this,
                getResources().getString(R.string.webUrl) + "" + graphWebserViceName,
                new EditText(SpeedLineChartActivity.this),
                parameters,
                "Please wait...");

    }


    private void parseSpeedGraph(String result,String weeklyOrMonthlyStatus,String graphName) {

        String parseArrayNameFieldName = null;
        int plusGraphValue = 0;
        String graphLineName = null;
        try
        {
            if(result != null || result.length() > 0)
            {

                valuesForSpeedWeekly.clear();
                Object json = new JSONTokener(result).nextValue();
                if (json instanceof JSONObject)
                {
                    ArrayList<Float> list = new ArrayList<Float>();

                    JSONObject speedGarphJsonObject = new JSONObject(result);

                    if(graphName.equals("speed"))
                    {
                        parseArrayNameFieldName = "speed";
                        plusGraphValue = 10;
                        graphLineName = "Speed";
                    }
                    else if(graphName.equals("rpm"))
                    {
                        parseArrayNameFieldName = "rpm";
                        plusGraphValue = 100;
                        graphLineName = "RPM";
                    }
                    else if(graphName.equals("ontime"))
                    {
                        parseArrayNameFieldName = "on_time";
                        plusGraphValue = 10;
                        graphLineName = "Vehicle On Time";
                    }
                    else if(graphName.equals("battery_voltage"))
                    {
                        parseArrayNameFieldName = "battery_voltage";
                        plusGraphValue = 10;
                        graphLineName = "Battery Voltage";
                    }
                    else if(graphName.equals("coolant_temp"))
                    {
                        parseArrayNameFieldName = "coolant_temp";
                        plusGraphValue = 10;
                        graphLineName = "Coolant Temperature";
                    }
                    JSONArray speedJsonArray = speedGarphJsonObject.getJSONArray(parseArrayNameFieldName);

                    //Log.e("SPEED_LENGTH",""+speedJsonArray.length());

                   // valuesForSpeedWeekly.add(new Entry(0, 0.0f));

                    for(int i = 0; i < speedJsonArray.length(); i++)
                    {
                        JSONObject sspeedGarphJsonObject = speedJsonArray.getJSONObject(i);

                        String speed = sspeedGarphJsonObject.getString(parseArrayNameFieldName);
                        String date = sspeedGarphJsonObject.getString("date");

                        if(graphName.equals("ontime"))
                        {
                            list.add(Float.valueOf(T.convertSecondsToTimeFormat(Integer.valueOf(speed))));
                            valuesForSpeedWeekly.add(new Entry(i+1, Float.valueOf(speed)));
                        }
                        else
                        {
                            list.add(Float.valueOf(speed));
                            valuesForSpeedWeekly.add(new Entry(i+1, Float.valueOf(speed)));
                        }


                    }
                    if(weeklyOrMonthlyStatus.equals("weekly"))
                    {

                        xAxis.setAxisMaxValue(speedJsonArray.length());
                        xAxis.setAxisMinValue(1);

                        float max = list.get(0);

                        for(Float i: list) {

                            if(i > max) max = i;
                        }

                       // System.out.println("max = " + max);

                        leftAxis.setAxisMaxValue(Math.round(max) + plusGraphValue);

                    }
                    else if(weeklyOrMonthlyStatus.equals("monthly"))
                    {

                        xAxis.setAxisMaxValue(speedJsonArray.length());
                        xAxis.setAxisMinValue(1);


                        float max = list.get(0);

                        for(Float i: list) {

                            if(i > max) max = i;
                        }

                        // System.out.println("max = " + max);

                        leftAxis.setAxisMaxValue(Math.round(max) + plusGraphValue);

                    }

                    setWeeklyData(graphLineName);

                }
                else
                {
                    T.displayErrorMessage(SpeedLineChartActivity.this,Constants.OOPS,Constants.OKK,Constants.INCORRECT_JSON);
                }
            }
            else
            {
                T.displayErrorMessage(SpeedLineChartActivity.this,Constants.OOPS,Constants.OKK,Constants.NULL_JSON);
                //T.t(SpeedLineChartActivity.this,"0 or null json");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
    }
    private void setWeeklyData(String graphLineName)
    {
//
//
//        valuesForSpeedWeekly.add(new Entry(0, 10.5f));
//        values.add(new Entry(1, 20.5f));
//        values.add(new Entry(2, 30.5f));
//        values.add(new Entry(3, 230.0f));
//        values.add(new Entry(4, 40.5f));
//        values.add(new Entry(5, 55.5f));
//        values.add(new Entry(6, 80.5f));
//        values.add(new Entry(7, 100.5f));

        mChart.notifyDataSetChanged();
        LineDataSet set1;

        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0)
        {
            set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setValues(valuesForSpeedWeekly);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        }
        else
        {
            // create a dataset and give it a type
            set1 = new LineDataSet(valuesForSpeedWeekly, graphLineName);

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
                set1.setFillDrawable(drawable);
            }
            else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            mChart.setData(data);
        }

        setAnimationToGraph();
    }

    private void handleError(VolleyError error,String weeklyOrMonthlyStatus,String graphName,String monthName)
    {
        try
        {

            if(error instanceof TimeoutError || error instanceof NoConnectionError)
            {

                displayError("TimeoutError/NoConnectionError","Server not responding or no connection.",weeklyOrMonthlyStatus,graphName,monthName);


            }
            else if(error instanceof AuthFailureError)
            {

                displayError("AuthFailureError","Remote server returns (401) Unauthorized?.",weeklyOrMonthlyStatus,graphName,monthName);

            }
            else if(error instanceof ServerError)
            {


                displayError("ServerError","Wrong webservice call or wrong webservice url.",weeklyOrMonthlyStatus,graphName,monthName);

            }
            else if (error instanceof NetworkError)
            {
                displayError("NetworkError","you doesn't have a data connection and wi-fi Connection.",weeklyOrMonthlyStatus,graphName,monthName);

            }
            else if(error instanceof ParseError)
            {

                displayError("NetworkError","Incorrect json response.",weeklyOrMonthlyStatus,graphName,monthName);
            }



        }
        catch (Exception e)
        {

        }

    }
    private void displayError(String title, String error, final String weeklyOrMonthlyStatus, final String graphName, final String monthName)
    {

        new SweetAlertDialog(SpeedLineChartActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setConfirmText("Try again")
                .setContentText(error)
                .setCancelText("Cancel")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        sweetAlertDialog.dismissWithAnimation();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                {
                    @Override
                    public void onClick(SweetAlertDialog sDialog)
                    {
                        sDialog.dismissWithAnimation();
                        getSpeedGraphData(weeklyOrMonthlyStatus,graphName,monthName);
                    }
                })
                .show();

    }

    private void setMonthlyData()
    {

        ArrayList<Entry> values = new ArrayList<Entry>();


        values.add(new Entry(0, 10.5f));
        values.add(new Entry(1, 20.5f));
        values.add(new Entry(2, 0.5f));
        values.add(new Entry(3, 10.5f));
        values.add(new Entry(4, 40.5f));
        values.add(new Entry(5, 55.5f));
        values.add(new Entry(6, 80.5f));
        values.add(new Entry(7, 60.5f));
        values.add(new Entry(8, 70.5f));
        values.add(new Entry(9, 80.5f));
        values.add(new Entry(10, 80.5f));
        values.add(new Entry(11, 55.5f));
        values.add(new Entry(12, 60.5f));
        values.add(new Entry(13, 65.5f));
        values.add(new Entry(14, 70.5f));
        values.add(new Entry(15, 75.5f));
        values.add(new Entry(16, 80.5f));
        values.add(new Entry(17, 80.5f));
        values.add(new Entry(18, 80.5f));
        values.add(new Entry(19, 80.5f));
        values.add(new Entry(20, 80.5f));
        values.add(new Entry(21, 55.5f));
        values.add(new Entry(22, 60.5f));
        values.add(new Entry(23, 65.5f));
        values.add(new Entry(24, 70.5f));
        values.add(new Entry(25, 75.5f));
        values.add(new Entry(26, 80.5f));
        values.add(new Entry(27, 80.5f));
        values.add(new Entry(28, 80.5f));
        values.add(new Entry(29, 80.5f));
        LineDataSet set1;

        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0)
        {
            set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        }
        else
        {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "Speed");

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
                set1.setFillDrawable(drawable);
            }
            else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            mChart.setData(data);
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.line_chart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {



        switch (item.getItemId()) {

            case android.R.id.home: {
                finish();
                break;
            }

//            case R.id.actionToggleValues: {
//                List<ILineDataSet> sets = mChart.getData()
//                        .getDataSets();
//
//                for (ILineDataSet iSet : sets) {
//
//                    LineDataSet set = (LineDataSet) iSet;
//                    set.setDrawValues(!set.isDrawValuesEnabled());
//                }
//
//                mChart.invalidate();
//                break;
//            }
//            case R.id.actionToggleHighlight: {
//                if(mChart.getData() != null) {
//                    mChart.getData().setHighlightEnabled(!mChart.getData().isHighlightEnabled());
//                    mChart.invalidate();
//                }
//                break;
//            }
//            case R.id.actionToggleFilled: {
//
//                List<ILineDataSet> sets = mChart.getData()
//                        .getDataSets();
//
//                for (ILineDataSet iSet : sets) {
//
//                    LineDataSet set = (LineDataSet) iSet;
//                    if (set.isDrawFilledEnabled())
//                        set.setDrawFilled(false);
//                    else
//                        set.setDrawFilled(true);
//                }
//                mChart.invalidate();
//                break;
//            }
//            case R.id.actionToggleCircles: {
//                List<ILineDataSet> sets = mChart.getData()
//                        .getDataSets();
//
//                for (ILineDataSet iSet : sets) {
//
//                    LineDataSet set = (LineDataSet) iSet;
//                    if (set.isDrawCirclesEnabled())
//                        set.setDrawCircles(false);
//                    else
//                        set.setDrawCircles(true);
//                }
//                mChart.invalidate();
//                break;
//            }
//            case R.id.actionToggleCubic: {
//                List<ILineDataSet> sets = mChart.getData()
//                        .getDataSets();
//
//                for (ILineDataSet iSet : sets) {
//
//                    LineDataSet set = (LineDataSet) iSet;
//                    set.setMode(set.getMode() == LineDataSet.Mode.CUBIC_BEZIER
//                            ? LineDataSet.Mode.LINEAR
//                            :  LineDataSet.Mode.CUBIC_BEZIER);
//                }
//                mChart.invalidate();
//                break;
//            }
//            case R.id.actionToggleStepped: {
//                List<ILineDataSet> sets = mChart.getData()
//                        .getDataSets();
//
//                for (ILineDataSet iSet : sets) {
//
//                    LineDataSet set = (LineDataSet) iSet;
//                    set.setMode(set.getMode() == LineDataSet.Mode.STEPPED
//                            ? LineDataSet.Mode.LINEAR
//                            :  LineDataSet.Mode.STEPPED);
//                }
//                mChart.invalidate();
//                break;
//            }
//            case R.id.actionToggleHorizontalCubic: {
//                List<ILineDataSet> sets = mChart.getData()
//                        .getDataSets();
//
//                for (ILineDataSet iSet : sets) {
//
//                    LineDataSet set = (LineDataSet) iSet;
//                    set.setMode(set.getMode() == LineDataSet.Mode.HORIZONTAL_BEZIER
//                            ? LineDataSet.Mode.LINEAR
//                            :  LineDataSet.Mode.HORIZONTAL_BEZIER);
//                }
//                mChart.invalidate();
//                break;
//            }
////            case R.id.actionTogglePinch: {
////                if (mChart.isPinchZoomEnabled())
////                    mChart.setPinchZoom(false);
////                else
////                    mChart.setPinchZoom(true);
////
////                mChart.invalidate();
////                break;
////            }
////            case R.id.actionToggleAutoScaleMinMax: {
////                mChart.setAutoScaleMinMaxEnabled(!mChart.isAutoScaleMinMaxEnabled());
////                mChart.notifyDataSetChanged();
////                break;
////            }
//            case R.id.animateX: {
//                mChart.animateX(3000);
//                break;
//            }
//            case R.id.animateY: {
//                mChart.animateY(3000, Easing.EasingOption.EaseInCubic);
//                break;
//            }
//            case R.id.animateXY: {
//                mChart.animateXY(3000, 3000);
//                break;
//            }
//            case R.id.actionSave: {
//                if (mChart.saveToPath("title" + System.currentTimeMillis(), "")) {
//                    Toast.makeText(getApplicationContext(), "Saving SUCCESSFUL!",
//                            Toast.LENGTH_SHORT).show();
//                } else
//                    Toast.makeText(getApplicationContext(), "Saving FAILED!", Toast.LENGTH_SHORT)
//                            .show();
//
//                // mChart.saveToGallery("title"+System.currentTimeMillis())
//                break;
//            }
        }
        return true;
    }

//    @Override
//    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
////        tvX.setText("" + (mSeekBarX.getProgress() + 1));
////        tvY.setText("" + (mSeekBarY.getProgress()));
////
////        setData(mSeekBarX.getProgress() + 1, mSeekBarY.getProgress());
////
////        // redraw
////        mChart.invalidate();
//    }
//
//    @Override
//    public void onStartTrackingTouch(SeekBar seekBar) {
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public void onStopTrackingTouch(SeekBar seekBar) {
//        // TODO Auto-generated method stub
//
//    }



    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if(lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            mChart.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: " + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
        Log.i("LOWHIGH", "low: " + mChart.getLowestVisibleX() + ", high: " + mChart.getHighestVisibleX());
        Log.i("MIN MAX", "xmin: " + mChart.getXChartMin() + ", xmax: " + mChart.getXChartMax() + ", ymin: " + mChart.getYChartMin() + ", ymax: " + mChart.getYChartMax());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }
}
