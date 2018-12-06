package snsystems.obd.carlog;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Calendar;

import snsystems.obd.R;
import snsystems.obd.activity.FeedbackActivity;
import snsystems.obd.alerts.*;
import snsystems.obd.classes.MyApplication;
import snsystems.obd.classes.T;
import snsystems.obd.classes.VolleyResponseClass;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.docupload.DocumentUploadActivity;
import snsystems.obd.interfaces.Constants;
import snsystems.obd.interfaces.VolleyCallback;

/**
 * Created by snsystem_amol on 2/24/2017.
 */

public class CarLogReport extends Fragment implements DatePickerDialog.OnDateSetListener{

    private View view;


    private RecyclerView
            carLogRecyclerView;


    private CarLogAdapterrr
            carLogAdapter;

    private LinearLayoutManager
            linearLayoutManager;

    private TextView selectDateTextView;

    private ArrayList<CarLogInfo> CAR_LOG_INFORMATION = new ArrayList<>();

    private RelativeLayout selectDateRelativeLayout;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.car_log_report, container, false);

        initializeWidgets();

        setClickListner();

        boolean c = T.checkConnection(getActivity());

        if(c)
        {
            getCarLogDetails();
        }
        else
        {
            T.t(getActivity(),"Network connection off");
        }

        return view;

    }

    private void setClickListner() {

        selectDateRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                Calendar now = Calendar.getInstance();

                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        (DatePickerDialog.OnDateSetListener)getActivity(),
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );

                dpd.setMinDate(Calendar.getInstance());
                dpd.setAccentColor(Color.parseColor("#0066B3"));
                dpd.show(getActivity().getFragmentManager(), "Pick Date");

                dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int month, int date)
                    {
                        //String dateSet = "" + date + "-" + (++month) + "-" + year;
                        String dateSet = "" + year + "-" + (++month) + "-" + date;
                        selectDateTextView.setText(dateSet);




//                        _device_id = "2345";
//                        _expiry_date = T.parseDate(dateSet);
//                        _document_type = "Driving Licence";
//
//                        uploadDocumentImage();
                    }
                });


            }
        });
    }

    private void getCarLogDetails()
    {


        String device_id_mail = S.getDeviceIdUserName(MyApplication.db);

        String [] data = device_id_mail.split("#");

        String [] parameters =
                {
                        "device_id"+"#"+"9000",
                        "log_date"+"#"+"2017-02-09"


                };
        VolleyResponseClass.getResponseProgressDialog(
                new VolleyCallback() {
                    @Override
                    public void onSuccess(String result)
                    {


                       // Log.e("RESPONSE",""+result);
                        CAR_LOG_INFORMATION = parseFeedback(result);
                        carLogAdapter.setCarLogData(CAR_LOG_INFORMATION);
                        carLogAdapter.notifyDataSetChanged();

                    }
                },
                getActivity(),
                getResources().getString(R.string.getCarLog),
                carLogRecyclerView,
                parameters,
                "Submiting feedback...");

    }

    private ArrayList<CarLogInfo> parseFeedback(String result) {


        String avg_speed = Constants.NA;
        String max_speed = Constants.NA;
        String no_of_halts = Constants.NA;
        String total_km = Constants.NA;
        String vehicle_on_time = Constants.NA;
        String vehicle_off_time = Constants.NA;


        ArrayList<CarLogInfo> CAR  = new ArrayList<>();
        try
        {
            if(result != null || result.length() > 0)
            {
                Object json = new JSONTokener(result).nextValue();
                if (json instanceof JSONObject)
                {
                    JSONObject tripsJsonObject = new JSONObject(result);
                    String status = tripsJsonObject.getString("status");

                    if(status.equals("1"))
                    {
                        JSONArray logJsonArray = tripsJsonObject.getJSONArray("car_log_data");

                        for (int i = 0; i < logJsonArray.length(); i++)
                        {
                            JSONObject logJsonObject = logJsonArray.getJSONObject(i);

                            if(logJsonObject.has("avg_speed") && !logJsonObject.isNull("avg_speed"))
                            {
                                avg_speed = logJsonObject.getString("avg_speed");
                            }
                            if(logJsonObject.has("max_speed") && !logJsonObject.isNull("max_speed"))
                            {
                                max_speed = logJsonObject.getString("max_speed");
                            }
                            if(logJsonObject.has("no_of_halts") && !logJsonObject.isNull("no_of_halts"))
                            {
                                no_of_halts = logJsonObject.getString("no_of_halts");
                            }
                            if(logJsonObject.has("total_km") && !logJsonObject.isNull("total_km"))
                            {
                                total_km = logJsonObject.getString("total_km");
                            }
                            if(logJsonObject.has("vehicle_on_time") && !logJsonObject.isNull("vehicle_on_time"))
                            {
                                vehicle_on_time = logJsonObject.getString("vehicle_on_time");
                            }
                            if(logJsonObject.has("vehicle_off_time") && !logJsonObject.isNull("vehicle_off_time"))
                            {
                                vehicle_off_time = logJsonObject.getString("vehicle_off_time");
                            }

                            CarLogInfo carLogInfo = new CarLogInfo();

                            carLogInfo.setAvgSpeed(avg_speed);
                            carLogInfo.setMax_speed(max_speed);
                            carLogInfo.setNo_of_halts(no_of_halts);
                            carLogInfo.setTotal_km(total_km);
                            carLogInfo.setVehicle_on_time(vehicle_on_time);
                            carLogInfo.setVehicle_off_time(vehicle_off_time);

                            CAR.add(carLogInfo);


                        }
                    }
                    else
                    {
                            T.t(getActivity(),"No data found");

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
            e.printStackTrace();

        }

        return CAR;

    }

    private void initializeWidgets()
    {


        carLogRecyclerView = (RecyclerView)view.findViewById(R.id.carLogRecyclerView);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        carLogRecyclerView.setLayoutManager(linearLayoutManager);
        carLogAdapter = new CarLogAdapterrr(getActivity());
        carLogRecyclerView.setAdapter(carLogAdapter);

        selectDateRelativeLayout = (RelativeLayout) view.findViewById(R.id.selectDateRelativeLayout);

        selectDateTextView = (TextView)view.findViewById(R.id.selectDateTextView);

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

    }
}
