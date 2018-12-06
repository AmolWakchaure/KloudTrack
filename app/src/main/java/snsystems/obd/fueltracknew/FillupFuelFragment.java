package snsystems.obd.fueltracknew;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import snsystems.obd.R;
import snsystems.obd.activity.FeedbackActivity;
import snsystems.obd.classes.MyApplication;
import snsystems.obd.classes.T;
import snsystems.obd.classes.Validations;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.docupload.DocumentUploadActivity;
import snsystems.obd.interfaces.Constants;

/**
 * Created by snsystem_amol on 3/17/2017.
 */

public class FillupFuelFragment extends Fragment
{


    private View view;

    private String notes;

    @Bind(R.id.vehicleNameButton)
    Button vehicleNameButton;

    @Bind(R.id.dateButton)
    Button dateButton;

    @Bind(R.id.saveButton)
    Button saveButton;

    @Bind(R.id.clearButton)
    Button clearButton;

    @Bind(R.id.odoReqadingEditText)
    EditText odoReqadingEditText;

    @Bind(R.id.quantityLtrEditText)
    EditText quantityLtrEditText;

    @Bind(R.id.costEditText)
    EditText costEditText;

    @Bind(R.id.notesEditText)
    EditText notesEditText;


    private String Odoreading;

    private ArrayList<String> VEHICLE_NAMES = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fillup_fuel, container, false);

        ButterKnife.bind(this, view);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //String status = preferences.getString("status","0");

        VEHICLE_NAMES = S.getVehicleName(MyApplication.db);

        setClickListner();

        return view;
    }

    private void setClickListner() {


        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDatePicker();


//                Calendar now = Calendar.getInstance();
//
//                DatePickerDialog dpd = DatePickerDialog.newInstance(
//                        getActivity(),
//                        now.get(Calendar.YEAR),
//                        now.get(Calendar.MONTH),
//                        now.get(Calendar.DAY_OF_MONTH)
//                );
//
//                dpd.setMinDate(Calendar.getInstance());
//                dpd.setAccentColor(Color.parseColor("#0066B3"));
//                dpd.show(getActivity().getFragmentManager(), "Pick Date");
//
//                dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener()
//                {
//                    @Override
//                    public void onDateSet(DatePickerDialog view, int year, int month, int date)
//                    {
//
//
//                        String dateSet = "" + year + "-" + (++month) + "-" + date;
//
//                    }
//                });

            }
        });


        vehicleNameButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                new MaterialDialog.Builder(getActivity())
                        .title("Vehicle Name")
                        .items(VEHICLE_NAMES)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice()
                        {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text)
                            {
                                try
                                {

                                    vehicleNameButton.setText(text.toString());

                                    setOdometerReading(text.toString());

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


        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!Validations.validateButtnEmpty(vehicleNameButton,"Select vehicle name",getActivity(),"Vehicle Name"))
                {
                    return;
                }
                if(!Validations.validateButtnEmpty(dateButton,"Select date",getActivity(),"Date"))
                {
                    return;
                }
                if(!Validations.validateEmptyEditext(getActivity(),odoReqadingEditText,"Enter odo reading."))
                {
                    return;
                }
                if(!Validations.validateEmptyEditext(getActivity(),quantityLtrEditText,"Enter quantity in ltr."))
                {
                    return;
                }
                if(!Validations.validateEmptyEditext(getActivity(),costEditText,"Enter cost per quantity in ₹."))
                {
                    return;
                }

                if(!checkOdoReading())
                {
                    return;
                }
                if(!checkOdoReadingSame())
                {
                    return;
                }


                Float costT = Float.valueOf(costEditText.getText().toString()) * Float.valueOf(quantityLtrEditText.getText().toString());


               MyApplication.db.insertFuelNewData(
                       vehicleNameButton.getText().toString(),
                       dateButton.getText().toString(),
                       odoReqadingEditText.getText().toString(),
                       quantityLtrEditText.getText().toString(),
                       String.valueOf(costT),
                       "na",
                       T.getSystemDate());

                vehicleNameButton.setText("Vehicle Name");
                dateButton.setText("Date");
                odoReqadingEditText.setText("");
                quantityLtrEditText.setText("");
                costEditText.setText("");
                notesEditText.setText("");




               T.displaySuccessMessage(getActivity(),"Success","Ok","Successfully inserted.");



            }
        });

        clearButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                vehicleNameButton.setText("Vehicle Name");
                dateButton.setText("Date");
                odoReqadingEditText.setText("");
                quantityLtrEditText.setText("");
                costEditText.setText("");
                notesEditText.setText("");

            }
        });
    }

    public boolean checkOdoReading()
    {
        if(Odoreading.equals(Constants.NA))
        {
            return true;
        }
        else
        {
            if (Float.valueOf(odoReqadingEditText.getText().toString()) < Float.valueOf(Odoreading))
            {
                T.tTop(getActivity(), "Odometer reading should be greater from previous");
                //requestFocus(editText);
                return false;
            }
            else
            {
                return true;
            }
        }


    }
    public boolean checkOdoReadingSame()
    {

            if (odoReqadingEditText.getText().toString().equals(Odoreading))
            {
                T.tTop(getActivity(), "Odometer reading not be same from previous.");
                //requestFocus(editText);
                return false;
            }
            else
            {
                return true;
            }



    }

    private void setOdometerReading(String vName)
    {
        String odo_reading = S.checkOdoAvailable(MyApplication.db,vName);

        if(odo_reading.equals(Constants.NA))
        {
            odoReqadingEditText.setText("");

            Odoreading = Constants.NA;
        }
        else
        {
            String [] data  = odo_reading.split("#");
            odoReqadingEditText.setText(data[0]);
            Odoreading = data[0];
        }
    }

    private void showDatePicker()
    {
        DatePickerFragment date = new DatePickerFragment();
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
        date.setCallBack(ondate);
        date.show(getFragmentManager(), "Date Picker");

    }
    android.app.DatePickerDialog.OnDateSetListener ondate = new android.app.DatePickerDialog.OnDateSetListener() {



        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {

            dateButton.setText(String.valueOf(dayOfMonth) + "-" + String.valueOf(monthOfYear+1) + "-" + String.valueOf(year));
        }
    };


}
