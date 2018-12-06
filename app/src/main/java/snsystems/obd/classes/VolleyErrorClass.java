package snsystems.obd.classes;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;

/**
 * Created by Admin on 1/14/2017.
 */
public class VolleyErrorClass {

    Context context;

    public static void handleVolleyerrorProgressNew(Context context, com.android.volley.VolleyError error) {
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {

            Toast.makeText(context, "Oops!Server Timeout Please refresh", Toast.LENGTH_LONG).show();

        } else if (error instanceof AuthFailureError) {

            Toast.makeText(context, "AuthFailureError", Toast.LENGTH_LONG).show();

        } else if (error instanceof ServerError) {

            Toast.makeText(context, "ServerError", Toast.LENGTH_LONG).show();

        } else if (error instanceof NetworkError) {

            Toast.makeText(context, "NetworkError", Toast.LENGTH_LONG).show();

        } else if (error instanceof ParseError) {

            Toast.makeText(context, "ParseError", Toast.LENGTH_LONG).show();

        }
    }
}
