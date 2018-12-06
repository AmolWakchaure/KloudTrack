package snsystems.obd.maintenance;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import snsystems.obd.R;
import snsystems.obd.activity.CustomViewsActivity;
import snsystems.obd.activity.FeedbackActivity;
import snsystems.obd.classes.MyApplication;
import snsystems.obd.classes.T;
import snsystems.obd.classes.Validations;
import snsystems.obd.classes.VolleyResponseClass;
import snsystems.obd.dashboard.DashboardActivity;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.docupload.DocumentUploadActivity;
import snsystems.obd.fueltracknew.DatePickerFragment;
import snsystems.obd.interfaces.Constants;
import snsystems.obd.interfaces.ReturnHistory;
import snsystems.obd.interfaces.VolleyCallback;
import snsystems.obd.interfaces.VolleyErrorCallback;

/**
 * Created by snsystem_amol on 3/18/2017.
 */

public class FillupMaintenanceFragment extends Fragment
{

    private View view;

    @Bind(R.id.selectVehicleRelativeLayout)
    RelativeLayout selectVehicleRelativeLayout;

    @Bind(R.id.serviceTypeRelativeLayout)
    RelativeLayout serviceTypeRelativeLayout;

    @Bind(R.id.serviceStatusRelativeLayout)
    RelativeLayout serviceStatusRelativeLayout;

    @Bind(R.id.otherLovRelativeLayout)
    RelativeLayout otherLovRelativeLayout;

    @Bind(R.id.vehicleNameSpinner)
    TextView vehicleNameSpinner;

    @Bind(R.id.maintenanceTypeSpinner)
    TextView maintenanceTypeSpinner;

    @Bind(R.id.lovServiceStatus)
    TextView lovServiceStatus;

    @Bind(R.id.lovAlertsSpinner)
    TextView lovAlertsSpinner;

    @Bind(R.id.maintenanceDateTextView)
    TextView maintenanceDateTextView;

//    @Bind(R.id.maintenanceDueDateTextView)
//    TextView maintenanceDueDateTextView;

    @Bind(R.id.selectServiceDateLinearLayout)
    LinearLayout selectServiceDateLinearLayout;

//    @Bind(R.id.selectServiceDueDateLinearLayout)
//    LinearLayout selectServiceDueDateLinearLayout;

    @Bind(R.id.submitFillupMaintFloatingActionButton)
    FloatingActionButton submitFillupMaintFloatingActionButton;


    @Bind(R.id.hideRelativeLayout)
    RelativeLayout hideRelativeLayout;

    @Bind(R.id.remarkEditextgdfg)
    EditText remarkEditextgdfg;







    private ArrayList<String> VEHICLE_NAMES = new ArrayList<>();

    private String serviceRemark,serviceStatus,serviceOtherLov;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fillup_maintenance, container, false);

        ButterKnife.bind(this, view);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        VEHICLE_NAMES = S.getVehicleName(MyApplication.db);

        setClickListner();

        getDAta();



        return view;
    }

    private void getDAta()
    {


    }

    private void setClickListner()
    {

        submitFillupMaintFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                if(!Validations.validateTextViewEmpty(vehicleNameSpinner,"Select Vehicle",getActivity(),"Select Vehicle"))
                {
                    return;
                }
                if(!Validations.validateTextViewEmpty(maintenanceDateTextView,"Select service date",getActivity(),"yyyy-mm-dd"))
                {
                    return;
                }

                if(!Validations.validateTextViewEmpty(maintenanceTypeSpinner,"Select Vehicle",getActivity(),"Select Service Type"))
                {
                    return;
                }

                if(T.checkConnection(getActivity()))
                {

//                    if(maintenanceDueDateTextView.getText().toString().equals("yyyy-mm-dd"))
//                    {
//                        serviceDueDate = "0000-00-00";
//                    }
//                    else
//                    {
//                        serviceDueDate = maintenanceDueDateTextView.getText().toString();
//                    }

                    if(remarkEditextgdfg.getText().toString().trim().isEmpty())
                    {
                        serviceRemark = Constants.NA;
                    }
                    else
                    {
                        serviceRemark = remarkEditextgdfg.getText().toString();
                    }

                    if(lovServiceStatus.getText().toString().equals("Status"))
                    {
                        serviceStatus = Constants.NA;
                    }
                    else
                    {
                        serviceStatus = lovServiceStatus.getText().toString();
                    }

                    if(lovServiceStatus.getText().toString().equals("Lov"))
                    {
                        serviceOtherLov = Constants.NA;
                    }
                    else
                    {
                        serviceOtherLov = lovAlertsSpinner.getText().toString();
                    }

                    //private String serviceDueDate,serviceRemark,serviceStatus,serviceOtherLov;

                    String serviceDate1 = maintenanceDateTextView.getText().toString();

                    String serviceDatee = T.parseDate(serviceDate1);

                    submitServiceData(

                            vehicleNameSpinner.getText().toString(),
                            serviceDatee,
                            maintenanceTypeSpinner.getText().toString(),
                            serviceRemark,
                            serviceStatus,
                            serviceOtherLov

                    );

                }
                else
                {
                    T.t(getActivity(),"Network connection off");
                }



            }
        });
        selectServiceDateLinearLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                showDatePickerServiceDate();

            }
        });
//        selectServiceDueDateLinearLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
//
//
//                showDatePickerServiceDueDate();
//
//            }
//        });
        serviceStatusRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new MaterialDialog.Builder(getActivity())
                        .title("Select Service Status")
                        .items(R.array.service_status)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice()
                        {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                try
                                {

                                    lovServiceStatus.setText(text.toString());

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
        });

        otherLovRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(getActivity())
                        .title("Select Other Problems")
                        .items(R.array.service_other_break_down)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice()
                        {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                try
                                {

                                    lovAlertsSpinner.setText(text.toString());

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
        });


        serviceTypeRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                new MaterialDialog.Builder(getActivity())
                        .title("Select Service Type")
                        .items(R.array.service_type)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice()
                        {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                try
                                {

                                    String service_type = text.toString();

                                    maintenanceTypeSpinner.setText(service_type);

                                    if(service_type.equals("Break down") || service_type.equals("Other"))
                                    {
                                        hideRelativeLayout.setVisibility(View.VISIBLE);
                                    }
                                    else
                                    {
                                        hideRelativeLayout.setVisibility(View.GONE);
                                    }



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
        });
        selectVehicleRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(getActivity())
                        .title("Select Vehicle")
                        .items(VEHICLE_NAMES)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice()
                        {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                try
                                {

                                    vehicleNameSpinner.setText(text.toString());

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
        });
    }

    private void submitServiceData(final String vehicleName,
                                   final String maintenanceDate,
                                   final String maintenanceType,

                                   final String serviceRemark,
                                   final String serviceStatus,
                                   final String serviceOtherLov)
    {


        final String device_id = S.returnDeviceIdByVehicleName(MyApplication.db,vehicleName);





        String [] parameters =
                {
                        "device_id"+"#"+device_id,
                        "vehicleName"+"#"+vehicleName,
                        "service_date"+"#"+maintenanceDate,
                        "service_type"+"#"+maintenanceType,
                        "remark"+"#"+serviceRemark,
                        "service_status"+"#"+serviceStatus,
                        "service_other_lov"+"#"+serviceOtherLov,
                        "service_charges"+"#"+"0"


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
                    public void onError(final VolleyError result)
                    {

                        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
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

                                        handleError(result,vehicleName,
                                                maintenanceDate,
                                                maintenanceType,
                                                serviceRemark,
                                                serviceStatus,
                                                serviceOtherLov);
                                    }
                                })
                                .show();

                    }
                },
                getActivity(),
                getResources().getString(R.string.webUrl) + "" + getResources().getString(R.string.serviceSchedule),
                remarkEditextgdfg,
                parameters,
                "Submiting Service Details...");


    }
    private void handleError(VolleyError error,String vehicleName,
                             String maintenanceDate,
                             String maintenanceType,

                             String serviceRemark,
                             String serviceStatus,
                             String serviceOtherLov)
    {
        try
        {

            if(error instanceof TimeoutError || error instanceof NoConnectionError)
            {

                displayError("TimeoutError/NoConnectionError","Server not responding or no connection.",vehicleName,
                        maintenanceDate,
                        maintenanceType,

                        serviceRemark,
                        serviceStatus,
                        serviceOtherLov);


            }
            else if(error instanceof AuthFailureError)
            {

                displayError("AuthFailureError","Remote server returns (401) Unauthorized?.",vehicleName,
                        maintenanceDate,
                        maintenanceType,

                        serviceRemark,
                        serviceStatus,
                        serviceOtherLov);

            }
            else if(error instanceof ServerError)
            {


                displayError("ServerError","Wrong webservice call or wrong webservice url.",vehicleName,
                        maintenanceDate,
                        maintenanceType,
                        serviceRemark,
                        serviceStatus,
                        serviceOtherLov);

            }
            else if (error instanceof NetworkError)
            {
                displayError("NetworkError","you doesn't have a data connection and wi-fi Connection.",vehicleName,
                        maintenanceDate,
                        maintenanceType,

                        serviceRemark,
                        serviceStatus,
                        serviceOtherLov);

            }
            else if(error instanceof ParseError)
            {

                displayError("NetworkError","Incorrect json response.",vehicleName,
                        maintenanceDate,
                        maintenanceType,
                        serviceRemark,
                        serviceStatus,
                        serviceOtherLov);
            }



        }
        catch (Exception e)
        {

        }

    }
    private void displayError(String title,
                              String error,
                              final String vehicleName,
                              final String maintenanceDate,
                              final String maintenanceType,
                              final String serviceRemark,
                              final String serviceStatus,
                              final String serviceOtherLov)
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
                        submitServiceData(vehicleName,
                                maintenanceDate,
                                maintenanceType,
                                serviceRemark,
                                serviceStatus,
                                serviceOtherLov);
                    }
                })
                .show();

    }

    private void parseResponse(String result) {

        //{"status":1,"success":"Success! Record added successfully."}
        try
        {
            if(result != null || result.length() > 0)
            {
                Object json = new JSONTokener(result).nextValue();
                if (json instanceof JSONObject)
                {
                    JSONObject tripsJsonObject = new JSONObject(result);
                    String status = tripsJsonObject.getString("status");

                    if(status.equals("0"))
                    {
                        T.displayErrorMessage(getActivity(), "Fail", "Cancel", "Fail to submitService details. Try again...");
                    }
                    else if(status.equals("1"))
                    {
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Success")
                                .setConfirmText("OK")
                                .setContentText("Service details successfully submited.")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();


                                        getActivity().finish();
                                    }
                                })
                                .show();


                    }
                    else if(status.equals("2"))
                    {
                        T.displayErrorMessage(getActivity(),"Oops...","OK","Service details already submited.");
                    }
                }
                else
                {
                    T.t(getActivity(),"incorrect json");
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

    private void showDatePickerServiceDate()
    {
        DatePickerFragmentMaintenance date = new DatePickerFragmentMaintenance();
        /**
         * Set Up Current Date Into dialog
         */
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);

        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(ondateServiceDate);
        date.show(getFragmentManager(), "Date Picker");

    }
    android.app.DatePickerDialog.OnDateSetListener ondateServiceDate = new android.app.DatePickerDialog.OnDateSetListener() {



        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {

            maintenanceDateTextView.setText(String.valueOf(year) + "-" + String.valueOf(monthOfYear+1) + "-" + String.valueOf(dayOfMonth));
            //maintenanceDateTextView.setText(String.valueOf(dayOfMonth) + "-" + String.valueOf(monthOfYear+1) + "-" + String.valueOf(year));
        }
    };


//    private void showDatePickerServiceDueDate()
//    {
//        DatePickerFragmentMaintenance date = new DatePickerFragmentMaintenance();
//        /**
//         * Set Up Current Date Into dialog
//         */
//        Calendar calender = Calendar.getInstance();
//        Bundle args = new Bundle();
//        args.putInt("year", calender.get(Calendar.YEAR));
//        args.putInt("month", calender.get(Calendar.MONTH));
//        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
//        date.setArguments(args);
//
//        /**
//         * Set Call back to capture selected date
//         */
//        date.setCallBack(ondateServiceueDate);
//        date.show(getFragmentManager(), "Date Picker");
//
//    }
//    android.app.DatePickerDialog.OnDateSetListener ondateServiceueDate = new android.app.DatePickerDialog.OnDateSetListener() {
//
//
//
//        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
//        {
//
//            maintenanceDueDateTextView.setText(String.valueOf(year) + "-" + String.valueOf(monthOfYear+1) + "-" + String.valueOf(dayOfMonth));
//            //maintenanceDateTextView.setText(String.valueOf(dayOfMonth) + "-" + String.valueOf(monthOfYear+1) + "-" + String.valueOf(year));
//        }
//    };

//    @Override
//    public void onSuccess(String result) {
//
//        T.t(getActivity(),""+result);
//
//    }
}
