package snsystems.obd.maintenance;

/**
 * Created by snsystem_amol on 3/19/2017.


public class DatePickerFragmentMaintenance
{
}*/

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by snsystem_amol on 3/17/2017.
 */

public class DatePickerFragmentMaintenance extends DialogFragment {
    DatePickerDialog.OnDateSetListener ondateSet;
    private int year, month, day;

    public DatePickerFragmentMaintenance() {

    }

    public void setCallBack(DatePickerDialog.OnDateSetListener ondate) {
        ondateSet = ondate;
    }

    @SuppressLint("NewApi")
    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        year = args.getInt("year");
        month = args.getInt("month");
        day = args.getInt("day");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

//        final Calendar c = Calendar.getInstance();
//        int year = c.get(Calendar.YEAR);
//        int month = c.get(Calendar.MONTH);
//        int day = c.get(Calendar.DAY_OF_MONTH);
//
//
//
//        DatePickerDialog dialog = new DatePickerDialog(getActivity(), ondateSet, year, month, day);
//
//        dialog.getDatePicker().setMaxDate(2017-04-24);
//        dialog.getDatePicker().setMinDate(2017-03-24);

        DatePickerDialog datePickerDialog;
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        c.add(Calendar.DAY_OF_YEAR, -30);

        datePickerDialog = new DatePickerDialog(getActivity(), ondateSet, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());


        return datePickerDialog;
    }
}

