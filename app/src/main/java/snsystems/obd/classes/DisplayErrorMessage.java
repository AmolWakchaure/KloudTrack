package snsystems.obd.classes;

import android.content.Context;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Admin on 1/13/2017.
 */
public class DisplayErrorMessage {

    Context context;

    DisplayErrorMessage(Context context) {
        this.context = context;
    }

    public static void displayErrorMessage(Context context, String title, String content)
    {

        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(title)
                .setContentText(content)
                .show();
    }
}
