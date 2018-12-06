package snsystems.obd.classes;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.HashMap;
import java.util.Map;

import snsystems.obd.interfaces.VolleyCallback;
import snsystems.obd.interfaces.VolleyErrorCallback;

/**
 * Created by shree on 03-Feb-17.
 */
public class VolleyResponseClass
{

    public static void getResponseWithoutProgress(final VolleyCallback callback,
                                      final Context context,
                                       final String url,
                                       final View view,
                                       final String [] parameters)
    {

        RequestQueue requestQueue1 = null;
        try
        {


            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response)
                        {

                            callback.onSuccess(response);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {

                            boolean c = T.handleVolleyerror(error, view);
                            if(c)
                            {

                                getResponseWithoutProgress(callback,context,url,view,parameters);
                               // T.t(view.getContext(),""+error);


                            }
                        }
                    }){
                @Override
                protected Map<String,String> getParams()
                {

                    Map<String,String> params = new HashMap<String, String>();

                    for(int i = 0; i < parameters.length; i++)
                    {
                        String [] dataParams = parameters[i].split("#");
                        params.put(dataParams[0],dataParams[1]);
                    }

                    return params;
                }
            };


            if(requestQueue1 == null)
            {
                requestQueue1 = Volley.newRequestQueue(context);
            }
            requestQueue1.getCache().clear();
            RetryPolicy policy = new DefaultRetryPolicy(40000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue1.add(stringRequest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    public static void getResponseProgressDialog(final VolleyCallback callback,
                                   final Context context,
                                   final String url,
                                   final View view,
                                   final String [] parameters,final String progressMessage)
    {

        try
        {
//            final ProgressDialog progressDialog = new ProgressDialog(view.getContext());
//            progressDialog.setMessage(progressMessage);
//            progressDialog.setCancelable(false);
//            progressDialog.show();

            final KProgressHUD progressDialog = KProgressHUD.create(context)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel(progressMessage)
                    //.setDetailsLabel("Downloading data")
                    .setCancellable(true)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();


            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response)
                        {
                            progressDialog.dismiss();
                            callback.onSuccess(response);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            progressDialog.dismiss();
                            boolean c = T.handleVolleyerror(error, view);
                            if(c)
                            {

                                getResponseProgressDialog(callback, context, url, view, parameters, progressMessage);
                                // T.t(view.getContext(),""+error);

                                Log.e("ERORRR",""+error);


                            }
                        }
                    }){
                @Override
                protected Map<String,String> getParams()
                {

                    Map<String,String> params = new HashMap<String, String>();

                    for(int i = 0; i < parameters.length; i++)
                    {
                        String [] dataParams = parameters[i].split("#");
                        params.put(dataParams[0],dataParams[1]);
                    }

                    return params;
                }
            };
            RequestQueue requestQueue1 = Volley.newRequestQueue(context);
            requestQueue1.getCache().clear();
            RetryPolicy policy = new DefaultRetryPolicy(40000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue1.add(stringRequest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    public static void getResponseProgressDialogError(final VolleyCallback callback,
                                                      final VolleyErrorCallback errorLog,
                                                 final Context context,
                                                 final String url,
                                                 final View view,
                                                 final String [] parameters,final String progressMessage)
    {

        RequestQueue requestQueue1 = null;
        try
        {


            final KProgressHUD progressDialog = KProgressHUD.create(context)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel(progressMessage)
                    //.setDetailsLabel("Downloading data")
                    .setCancellable(false)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();


            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response)
                        {
                            progressDialog.dismiss();
                            callback.onSuccess(response);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            progressDialog.dismiss();
                            errorLog.onError(error);

                        }
                    }){
                @Override
                protected Map<String,String> getParams()
                {

                    Map<String,String> params = new HashMap<String, String>();

                    for(int i = 0; i < parameters.length; i++)
                    {
                        String [] dataParams = parameters[i].split("#");
                        params.put(dataParams[0],dataParams[1]);
                    }

                    return params;
                }
            };
            if(requestQueue1 == null)
            {
                requestQueue1 = Volley.newRequestQueue(context);
            }

            requestQueue1.getCache().clear();
            RetryPolicy policy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue1.add(stringRequest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    public static void getResponseProgressDialogErrorWithoutProgress(final VolleyCallback callback,
                                                      final VolleyErrorCallback errorLog,
                                                      final Context context,
                                                      final String url,
                                                      final View view,
                                                      final String [] parameters)
    {

        RequestQueue requestQueue1 = null;
        try
        {





            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response)
                        {

                            callback.onSuccess(response);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {

                            errorLog.onError(error);

                        }
                    }){
                @Override
                protected Map<String,String> getParams()
                {

                    Map<String,String> params = new HashMap<String, String>();

                    for(int i = 0; i < parameters.length; i++)
                    {
                        String [] dataParams = parameters[i].split("#");
                        params.put(dataParams[0],dataParams[1]);
                    }

                    return params;
                }
            };
            if(requestQueue1 == null)
            {
                requestQueue1 = Volley.newRequestQueue(context);
            }

            requestQueue1.getCache().clear();
            RetryPolicy policy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue1.add(stringRequest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    public static void getResponseAlertsVollyError(final VolleyCallback callback,
                                                   final VolleyErrorCallback errorLog,
                                                   final Context context,
                                                   final String url,
                                                   final View view,
                                                   final String [] parameters)
    {

        RequestQueue requestQueue1 = null;

        try
        {


            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response)
                        {

                            callback.onSuccess(response);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {

                            errorLog.onError(error);
                        }
                    }){
                @Override
                protected Map<String,String> getParams()
                {

                    Map<String,String> params = new HashMap<String, String>();

                    for(int i = 0; i < parameters.length; i++)
                    {
                        String [] dataParams = parameters[i].split("#");
                        params.put(dataParams[0],dataParams[1]);
                    }

                    return params;
                }
            };


            if(requestQueue1 == null)
            {
                requestQueue1 = Volley.newRequestQueue(context);
            }
            requestQueue1.getCache().clear();
            RetryPolicy policy = new DefaultRetryPolicy(40000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue1.add(stringRequest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public static void getResponseAlerts(final VolleyCallback callback,
                                                  final Context context,
                                                  final String url,
                                                  final View view,
                                                  final String [] parameters)
    {

        RequestQueue requestQueue1 = null;

        try
        {


            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response)
                        {

                            callback.onSuccess(response);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {

                            boolean c = T.handleVolleyerror(error, view);
                            if(c)
                            {

                                getResponseWithoutProgress(callback,context,url,view,parameters);
                                // T.t(view.getContext(),""+error);


                            }
                        }
                    }){
                @Override
                protected Map<String,String> getParams()
                {

                    Map<String,String> params = new HashMap<String, String>();

                    for(int i = 0; i < parameters.length; i++)
                    {
                        String [] dataParams = parameters[i].split("#");
                        params.put(dataParams[0],dataParams[1]);
                    }

                    return params;
                }
            };
            
            
            if(requestQueue1 == null)
            {
                requestQueue1 = Volley.newRequestQueue(context);
            }
            requestQueue1.getCache().clear();
            RetryPolicy policy = new DefaultRetryPolicy(40000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue1.add(stringRequest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public static void getResponse(final VolleyCallback callback,
                                                      final VolleyErrorCallback errorLog,
                                                      final Context context,
                                                      final String url,
                                                      final View view,
                                                      final String [] parameters,final String progressMessage)
    {

        RequestQueue requestQueue1 = null;
        try
        {


            final KProgressHUD progressDialog = KProgressHUD.create(context)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel(progressMessage)
                    //.setDetailsLabel("Downloading data")
                    .setCancellable(false)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();


            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response)
                        {
                            progressDialog.dismiss();
                            callback.onSuccess(response);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            progressDialog.dismiss();
                            errorLog.onError(error);

                        }
                    }){
                @Override
                protected Map<String,String> getParams()
                {

                    Map<String,String> params = new HashMap<String, String>();

                    for(int i = 0; i < parameters.length; i++)
                    {
                        String [] dataParams = parameters[i].split("#");
                        params.put(dataParams[0],dataParams[1]);
                    }

                    return params;
                }
            };
            if(requestQueue1 == null)
            {
                requestQueue1 = Volley.newRequestQueue(context);
            }

            requestQueue1.getCache().clear();
            RetryPolicy policy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue1.add(stringRequest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public static void checkImageExistAtServer(final VolleyCallback callback,
                                                      final VolleyErrorCallback errorLog,
                                                      final Context context,
                                                      final String url)
    {

        RequestQueue requestQueue1 = null;
        try
        {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response)
                        {

                            callback.onSuccess(response);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {

                            errorLog.onError(error);

                        }
                    });
            if(requestQueue1 == null)
            {
                requestQueue1 = Volley.newRequestQueue(context);
            }

            requestQueue1.getCache().clear();
            RetryPolicy policy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue1.add(stringRequest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}
