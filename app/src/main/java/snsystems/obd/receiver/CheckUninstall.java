package snsystems.obd.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import snsystems.obd.classes.T;

/**
 * Created by snsystem_amol on 05-May-17.
 */

public class CheckUninstall extends BroadcastReceiver
{


    @Override
    public void onReceive(Context context, Intent intent)
    {

        // install call
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED"))
        {
            T.t(context,"Install");
        }

        // uninstall call
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED"))
        {
            //code here on uninstall
            //Log.i("Uninstalled:", intent.getDataString());
            T.displayErrorMessage(context,"Oops","ok","Ready to unistall");

        }
    }
}
