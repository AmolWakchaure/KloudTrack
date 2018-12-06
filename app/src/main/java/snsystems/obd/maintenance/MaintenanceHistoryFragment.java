package snsystems.obd.maintenance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import snsystems.obd.R;
import snsystems.obd.activity.FeedbackActivity;
import snsystems.obd.classes.MyApplication;
import snsystems.obd.classes.T;
import snsystems.obd.classes.VolleyResponseClass;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.interfaces.Constants;
import snsystems.obd.interfaces.ReturnHistory;
import snsystems.obd.interfaces.VolleyCallback;
import snsystems.obd.interfaces.VolleyErrorCallback;

/**
 * Created by snsystem_amol on 3/18/2017.
 */

public class MaintenanceHistoryFragment extends Fragment
{

    private View view;

    @Bind(R.id.serviceHistoryRecyclerView)
    RecyclerView serviceHistoryRecyclerView;

    @Bind(R.id.serviceCountTextView)
    TextView serviceCountTextView;

    @Bind(R.id.serviceExpencesTextView)
    TextView serviceExpencesTextView;

    @Bind(R.id.fromDateTextView)
    TextView fromDateTextView;

    @Bind(R.id.textView7Hide)
    TextView textView7Hide;


    private ServiceHistoryAdapterrr serviceHistoryAdapterrr;


    private String [] dates = {

            "11.3.2017",
            "12.3.2017",
            "13.3.2017",
            "14.3.2017",
            "15.3.2017"
    };

    private String [] charges = {

            "10000",
            "20000",
            "30000",
            "40000",
            "50000"
    };

    private String [] remarks = {

            "remark remarkremarkremarkremark",
            "remark remarkremarkremarkremark",
            "remark remarkremarkremarkremark",
            "remark remarkremarkremarkremark",
            "remark remarkremarkremarkremark"
    };


    private ArrayList<ServiceHistoryInformationn> HISTORY_DETAILS = new ArrayList<>();

    ViewPager viewPager;

    ReturnHistory returnHistory;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.service_history, container, false);

        ButterKnife.bind(this, view);


        initialize();

        boolean c = T.checkConnection(getActivity());

        if(c)
        {
            HISTORY_DETAILS.clear();
            getHistory();
        }
        else
        {
            textView7Hide.setVisibility(View.VISIBLE);
            textView7Hide.setText("Network connection off");
        }

        //setHistoryData();


        return view;
    }

    private void getHistory() {


        String device_id_mail = S.getDeviceIdUserName(MyApplication.db);

        String [] data = device_id_mail.split("#");

        String [] parameters =
                {
                        "device_id"+"#"+data[0]

                };
        VolleyResponseClass.getResponseProgressDialogError(
                new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {

                        parseResponse(result);

                    }
                },
                new VolleyErrorCallback()
                {
                    @Override
                    public void onError(VolleyError result)
                    {
                        handleError(result);
                    }
                },
                getActivity(),
                getResources().getString(R.string.webUrl) + "" + getResources().getString(R.string.serviceHistory),
                serviceExpencesTextView,
                parameters,
                "Getting history...");
    }
    private void handleError(VolleyError error)
    {
        try
        {

            if(error instanceof TimeoutError || error instanceof NoConnectionError)
            {

                displayError("TimeoutError/NoConnectionError","Server not responding or no connection.");


            }
            else if(error instanceof AuthFailureError)
            {

                displayError("AuthFailureError","Remote server returns (401) Unauthorized?.");

            }
            else if(error instanceof ServerError)
            {


                displayError("ServerError","Wrong webservice call or wrong webservice url.");

            }
            else if (error instanceof NetworkError)
            {
                displayError("NetworkError","you doesn't have a data connection and wi-fi Connection.");

            }
            else if(error instanceof ParseError)
            {

                displayError("NetworkError","Incorrect json response.");
            }



        }
        catch (Exception e)
        {

        }

    }
    private void displayError(String title,
                              String error)
    {

        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setConfirmText("Try again")
                .setCancelText("Cancel")
                .setContentText(error)
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
                        getHistory();
                    }
                })
                .show();

    }
    private void parseResponse(String result) {

        String from_date = Constants.NA;
        String service_count = Constants.NA;
        String service_charges = Constants.NA;

        String device_id = Constants.NA;
        String service_schedule_master_id = Constants.NA;
        String service_date = Constants.NA;
        String service_charges_ind = Constants.NA;
        String remark = Constants.NA;
        String service_status = Constants.NA;

        String service_type = Constants.NA;
        String service_other_lov = Constants.NA;

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

                        JSONArray historyJsonArray = tripsJsonObject.getJSONArray("history");

                        if(tripsJsonObject.has("service_count") && !tripsJsonObject.isNull("service_count"))
                        {
                            service_count = tripsJsonObject.getString("service_count");
                        }

                        if(tripsJsonObject.has("service_charges") && !tripsJsonObject.isNull("service_charges"))
                        {
                            service_charges = tripsJsonObject.getString("service_charges");
                        }

                        if(tripsJsonObject.has("from_date") && !tripsJsonObject.isNull("from_date"))
                        {
                            from_date = tripsJsonObject.getString("from_date");
                        }

                        for (int i = 0; i < historyJsonArray.length(); i++)
                        {

                            JSONObject hhistoryJsonObject = historyJsonArray.getJSONObject(i);

                            if(hhistoryJsonObject.has("service_schedule_master_id") && !hhistoryJsonObject.isNull("service_schedule_master_id"))
                            {
                                service_schedule_master_id = hhistoryJsonObject.getString("service_schedule_master_id");
                            }
                            if(hhistoryJsonObject.has("service_date") && !hhistoryJsonObject.isNull("service_date"))
                            {
                                service_date = hhistoryJsonObject.getString("service_date");
                            }
                            if(hhistoryJsonObject.has("service_charges") && !hhistoryJsonObject.isNull("service_charges"))
                            {
                                service_charges_ind = hhistoryJsonObject.getString("service_charges");
                            }
                            if(hhistoryJsonObject.has("remark") && !hhistoryJsonObject.isNull("remark"))
                            {
                                remark = hhistoryJsonObject.getString("remark");
                            }
                            if(hhistoryJsonObject.has("service_status") && !hhistoryJsonObject.isNull("service_status"))
                            {
                                service_status = hhistoryJsonObject.getString("service_status");
                            }
                            if(hhistoryJsonObject.has("device_id") && !hhistoryJsonObject.isNull("device_id"))
                            {
                                device_id = hhistoryJsonObject.getString("device_id");


                            }

                            if(hhistoryJsonObject.has("service_type") && !hhistoryJsonObject.isNull("service_type"))
                            {
                                service_type = hhistoryJsonObject.getString("service_type");
                            }
                            if(hhistoryJsonObject.has("service_other_lov") && !hhistoryJsonObject.isNull("service_other_lov"))
                            {
                                service_other_lov = hhistoryJsonObject.getString("service_other_lov");
                            }


                            ServiceHistoryInformationn sdInformationn = new ServiceHistoryInformationn();

                            sdInformationn.setId(service_schedule_master_id);
                            sdInformationn.setMaintenanceDate(service_date);
                            sdInformationn.setServiceCost(service_charges_ind);
                            sdInformationn.setServiceRemark(remark);
                            sdInformationn.setServiceStatus(service_status);
                            sdInformationn.setDeviceId(device_id);

                            sdInformationn.setMaintenanceType(service_type);
                            sdInformationn.setServiceOtherLov(service_other_lov);


                            HISTORY_DETAILS.add(sdInformationn);
                        }

                        String [] fDate = from_date.split(" ");

                        fromDateTextView.setText("From Date: "+T.changeDateFormat(fDate[0]));
                        serviceCountTextView.setText(service_count);
                        serviceExpencesTextView.setText("₹ "+service_charges);
                        serviceHistoryAdapterrr.setHistoryData(HISTORY_DETAILS);
                        serviceHistoryAdapterrr.notifyDataSetChanged();

                    }
                    else
                    {
                        textView7Hide.setVisibility(View.VISIBLE);
                        textView7Hide.setText("History not found.");
                    }
                }
                else
                {
                    textView7Hide.setVisibility(View.VISIBLE);
                    textView7Hide.setText("History not found.");
                   // T.t(getActivity(),"incorect json");
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
    }

    private void setHistoryData()
    {

        for(int i = 0; i < dates.length; i++)
        {
            ServiceHistoryInformationn serviceHistoryInformationn = new ServiceHistoryInformationn();

            serviceHistoryInformationn.setMaintenanceDate(dates[i]);
            serviceHistoryInformationn.setServiceCost(charges[i]);
            serviceHistoryInformationn.setServiceRemark(remarks[i]);

            HISTORY_DETAILS.add(serviceHistoryInformationn);

        }

        serviceHistoryAdapterrr.setHistoryData(HISTORY_DETAILS);
        serviceHistoryAdapterrr.notifyDataSetChanged();

    }

    private void initialize()
    {

        viewPager = (ViewPager) getActivity().findViewById(R.id.tabanim_viewpager);

        serviceHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        serviceHistoryAdapterrr = new ServiceHistoryAdapterrr(getActivity());
        serviceHistoryRecyclerView.setAdapter(serviceHistoryAdapterrr);

    }



}
