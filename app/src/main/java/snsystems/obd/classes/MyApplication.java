package snsystems.obd.classes;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import snsystems.obd.database.DBHelper;

/**
 * Created by snsystem_amol on 4/25/2017.
 */

public class MyApplication extends Application
{

    public static DBHelper db = null;
    private static Context context;

    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate()
    {

        super.onCreate();
        context = getApplicationContext();

        if (db == null)
        {
            db = new DBHelper(context);
            db.getWritableDatabase();
        }

    }
}
