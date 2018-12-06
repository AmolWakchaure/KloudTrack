package snsystems.obd.classes;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by shree on 1/2/17.
 */
public class T
{


public static double calculateDistanceUsingLatLong(ArrayList<LatLng> ROUTE_MARKERS)
    {
        double total_distance = 0;
        try
        {
            for (int i = 0; i < ROUTE_MARKERS.size() - 1; i++)
            {
                double total_distance1 = distanceBetween(ROUTE_MARKERS.get(i), ROUTE_MARKERS.get(i + 1));

                total_distance = total_distance + total_distance1;
            }
        }
        catch (Exception e)
        {

        }
        return total_distance;
    }
    public static Double distanceBetween(LatLng point1, LatLng point2) {
        if (point1 == null || point2 == null) {
            return null;
        }

        return SphericalUtil.computeDistanceBetween(point1, point2);
    }











    public static void s(View view, String message)
    {

        Snackbar snackbar = Snackbar.make(view, "" + message, Snackbar.LENGTH_LONG);
        snackbar.show();

    }

    public static void t(Context context,String message)
    {

        Toast.makeText(context,""+message,Toast.LENGTH_LONG).show();

    }
    public static void tTop(Context context,String message)
    {

        Toast toast= Toast.makeText(context, ""+message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();

    }
    public static boolean checkConnection(Context context)
    {

//        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//        android.net.NetworkInfo datac = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//        if ((wifi != null & datac != null) && (wifi.isConnected() | datac.isConnected()))
//        {
//            return true;
//        }
//        else
//        {
//            return false;
//        }

        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;

    }
    public static boolean sdkLevel()
    {
        boolean status = false;
        try
        {
            int currentapiVersion = Build.VERSION.SDK_INT;

            if (currentapiVersion >= Build.VERSION_CODES.M)
            {

                status = true;
            }
            else
            {
                status = false;
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return status;

    }

    public static void displayInfoDialog(Context context,
                                             String titleText,
                                             String confirmText,
                                             String contentText)
    {
        new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText(titleText)
                .setConfirmText(confirmText)
                .setContentText(contentText)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();


                    }
                })
                .show();
    }

    public static void displaySuccessMessage(Context context,
                                             String titleText,
                                             String confirmText,
                                             String contentText)
    {
        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(titleText)
                .setConfirmText(confirmText)
                .setContentText(contentText)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();


                    }
                })
                .show();
    }
    public static void displayErrorMessage(Context context,
                                             String titleText,
                                             String confirmText,
                                             String contentText)
    {
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(titleText)
                .setConfirmText(confirmText)
                .setContentText(contentText)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog)
                    {
                        sDialog.dismissWithAnimation();


                    }
                })
                .show();
    }
    public static void displayException(Context context,String exception)
    {
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setConfirmText("OK")
                .setContentText(exception)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog)
                    {
                        sDialog.dismissWithAnimation();


                    }
                })
                .show();
    }
    public static void displaySuccessMessageUpdate(Context context,
                                                   String titleText,
                                                   String confirmText,
                                                   String contentText,
                                                   final SweetAlertDialog fghf)
    {
        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(titleText)
                .setConfirmText(confirmText)
                .setContentText(contentText)

                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        fghf.dismissWithAnimation();

                    }
                })
                .show();
    }

    public static String getSystemTime()
    {

        String systemTime = null;

        try
        {
            DateFormat df = new SimpleDateFormat("HH:mm");
            systemTime = df.format(Calendar.getInstance().getTime());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return systemTime;

    }

    public static boolean  handleVolleyerror(com.android.volley.VolleyError error,View view)
    {
        if(error instanceof TimeoutError || error instanceof NoConnectionError)
        {

            /*relativeLayout.setVisibility(View.GONE);
            Snackbar snackbar = Snackbar.make(view, "Oops!Server Timeout Please refresh", Snackbar.LENGTH_LONG);
            View vieww = snackbar.getView();
            vieww.setBackgroundColor(Color.parseColor("#0097A7"));
            snackbar.show();*/
            return true;


        }
        else if(error instanceof AuthFailureError) {


            Snackbar snackbar = Snackbar.make(view, "AuthFailureError", Snackbar.LENGTH_LONG);

            snackbar.show();

            return true;


        }
        else if(error instanceof ServerError)
        {


            Snackbar snackbar = Snackbar.make(view, "ServerError", Snackbar.LENGTH_LONG);
            snackbar.show();
            return true;


        } else if (error instanceof NetworkError) {



            Snackbar snackbar = Snackbar.make(view, "NetworkError", Snackbar.LENGTH_LONG);
            snackbar.show();
            return true;

        }
        else if(error instanceof ParseError)
        {

            Snackbar snackbar = Snackbar.make(view, "ParseError", Snackbar.LENGTH_LONG);

            snackbar.show();
            return true;


        }
        else
        {
            return false;
        }



    }

    public static String  parseDate(String dateSet)
    {

        String  parseDate = null;
        try
        {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Date convertDate = dateFormat.parse(dateSet);

            parseDate  = simpleDateFormat.format(convertDate);





        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return  parseDate;


    }

    public static boolean checkPermission(Context context,String permission)
    {

        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED );

    }

    public static void askPermission(Context context,String permission)
    {

        ActivityCompat.requestPermissions(
                (Activity) context,
                new String[]{permission},
                12
        );
    }

    public static String changeDateFormat(String strCurrentDate)
    {
        String datte = null;
        String monthName = null;
        try
        {
            String [] data = strCurrentDate.split("-");

            String year = data[0];
            String month = data[1];
            String date = data[2];

            if(month.equals("01"))
            {
                monthName = "Jan";
            }
            else if(month.equals("02"))
            {
                monthName = "Feb";
            }
            else if(month.equals("03"))
            {
                monthName = "Mar";
            }
            else if(month.equals("04"))
            {
                monthName = "Apr";
            }
            else if(month.equals("05"))
            {
                monthName = "May";
            }
            else if(month.equals("06"))
            {
                monthName = "Jun";
            }
            else if(month.equals("07"))
            {
                monthName = "Jul";
            }
            else if(month.equals("08"))
            {
                monthName = "Aug";
            }
            else if(month.equals("09"))
            {
                monthName = "Sep";
            }
            else if(month.equals("10"))
            {
                monthName = "Oct";
            }
            else if(month.equals("11"))
            {
                monthName = "Nov";
            }
            else if(month.equals("12"))
            {
                monthName = "Dec";
            }

            datte = monthName+" "+date+","+year;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return datte;
    }
    public static String changeDateFormatMonth(String strCurrentDate)
    {
        String datte = null;
        String monthName = null;
        try
        {
            String [] data = strCurrentDate.split(" ");

            String year = data[2];
            String month = data[1];
            String date = data[0];

            if(month.equals("Jan"))
            {
                monthName = "01";
            }
            else if(month.equals("Feb"))
            {
                monthName = "02";
            }
            else if(month.equals("Mar"))
            {
                monthName = "03";
            }
            else if(month.equals("Apr"))
            {
                monthName = "04";
            }
            else if(month.equals("May"))
            {
                monthName = "05";
            }
            else if(month.equals("Jun"))
            {
                monthName = "06";
            }
            else if(month.equals("Jul"))
            {
                monthName = "07";
            }
            else if(month.equals("Aug"))
            {
                monthName = "08";
            }
            else if(month.equals("Sep"))
            {
                monthName = "09";
            }
            else if(month.equals("Oct"))
            {
                monthName = "10";
            }
            else if(month.equals("Nov"))
            {
                monthName = "11";
            }
            else if(month.equals("Dec"))
            {
                monthName = "12";
            }

            datte = year+"-"+monthName+"-"+date;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return datte;
    }
    public static String changeDateFormatDdMmYyyy(String strCurrentDate)
    {
        String datte = null;
        String monthName = null;
        try
        {
            String [] data = strCurrentDate.split("-");

            String year = data[0];
            String month = data[1];
            String date = data[2];

            if(month.equals("01"))
            {
                monthName = "Jan";
            }
            else if(month.equals("02"))
            {
                monthName = "Feb";
            }
            else if(month.equals("03"))
            {
                monthName = "Mar";
            }
            else if(month.equals("04"))
            {
                monthName = "Apr";
            }
            else if(month.equals("05"))
            {
                monthName = "May";
            }
            else if(month.equals("06"))
            {
                monthName = "Jun";
            }
            else if(month.equals("07"))
            {
                monthName = "Jul";
            }
            else if(month.equals("08"))
            {
                monthName = "Aug";
            }
            else if(month.equals("09"))
            {
                monthName = "Sep";
            }
            else if(month.equals("10"))
            {
                monthName = "Oct";
            }
            else if(month.equals("11"))
            {
                monthName = "Nov";
            }
            else if(month.equals("12"))
            {
                monthName = "Dec";
            }

            datte = date+" "+monthName+" "+year;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return datte;
    }
    public static String changeTimeFormat(String strCurrentTime)
    {
        String timee = null;

        try
        {
            String [] data = strCurrentTime.split(":");

            String hrs = data[0];
            String min = data[1];
            String sec = data[2];



            timee = hrs+":"+min;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return timee;
    }

    //check location services
    public static ArrayList<Boolean> checkLocationServices(Context context)
    {
        ArrayList<Boolean> gpsNetworkEnableStatus = new ArrayList<>();
        try
        {

            LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
            boolean gps_enabled = false;
            boolean network_enabled = false;

            try
            {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

                gpsNetworkEnableStatus.add(gps_enabled);
            }
            catch(Exception ex)
            {

            }

            try
            {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                gpsNetworkEnableStatus.add(network_enabled);
            }
            catch(Exception ex)
            {

            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return gpsNetworkEnableStatus;
    }

    public static String getSystemDateTime()
    {

        String systemTime = null;

        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            systemTime = df.format(Calendar.getInstance().getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return systemTime;

    }
    public static String getSystemDateTimeTwelvehrs()
    {

        String systemTime = null;

        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd H:mm");
            systemTime = df.format(Calendar.getInstance().getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return systemTime;

    }
    public static String getSystemDate()
    {

        String systemTime = null;

        try {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            systemTime = df.format(Calendar.getInstance().getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return systemTime;

    }

    public static String formatHoursAndMinutes(int totalMinutes)
    {
        String minutes = Integer.toString(totalMinutes % 60);
        minutes = minutes.length() == 1 ? "0" + minutes : minutes;
        return (totalMinutes / 60) + ":" + minutes;
    }

    public static String timeConvert(int time){
        String t = "";

        int j = time/(24*60);
        int h= (time%(24*60)) / 60;
        int m = (time%(24*60)) % 60;



        t =j + ":" + h + ":" + m;
        return t;
    }

    public static String getAddress(Context context,Double lat, Double landi) {


        String locationAddress = null;
        try
        {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(context, Locale.getDefault());

            addresses = geocoder.getFromLocation(lat, landi, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            locationAddress = address + ", " + city + ", " + state + ", " + country + ","+postalCode+".";
        }
        catch (Exception e)
        {

        }

        return locationAddress;
    }


    public static String getWeekDates()
    {
        String weekDates = null;

        ArrayList<String> dates = new ArrayList<>();

        try
        {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE yyyy-MM-dd");

            for (int i = 0; i < 7; i++)
            {
                //Log.e("fromToDates", sdf.format(cal.getTime()));
                dates.add(sdf.format(cal.getTime()));
                cal.add(Calendar.DAY_OF_WEEK, 1);
            }

            weekDates = dates.get(0)+"#"+dates.get(6);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return weekDates;
    }

    public static String getMonthDates()
    {
        String weekDates = null;

        ArrayList<String> dates = new ArrayList<>();

        try
        {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE yyyy-MM-dd");

            for (int i = 0; i < 30; i++)
            {
                Log.e("fromToDates", sdf.format(cal.getTime()));
                dates.add(sdf.format(cal.getTime()));
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }

            weekDates = dates.get(0)+"#"+dates.get(6);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return weekDates;
    }

    public static void displayInfoDialogg(Context context,
                                             String titleText,
                                             String confirmText,
                                             String contentText)
    {
        new SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText(titleText)
                .setConfirmText(confirmText)
                .setContentText(contentText)

                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();

                    }
                })
                .show();
    }


    public static String getDurationString(int seconds)
    {

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        return twoDigitString(hours) + ":" + twoDigitString(minutes) + ":" + twoDigitString(seconds);
    }

    public static String twoDigitString(int number) {

        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }

    //check sim available or not in device
    public static boolean isSimSupport(Context context)
    {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);  //gets the current TelephonyManager
        return !(tm.getSimState() == TelephonyManager.SIM_STATE_ABSENT);

    }


    public static String convertSecondsToTimeFormat(int seconds)
    {
        int hours = Integer.valueOf(seconds) / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        String formatTime = String.valueOf(hours)+"."+String.valueOf(minutes);

        return formatTime;


    }
    

    public static ArrayList<String> getMonthNamesCurrent(String[] monthNames)
    {

        ArrayList<String> MONTH_NAMES = new ArrayList<>();

        try
        {
            String monthname=(String)android.text.format.DateFormat.format("MMMM", new Date());

            int  monthIndex = Arrays.asList(monthNames).indexOf(monthname);

            for(int i = 0; i <= monthIndex; i++)
            {
                MONTH_NAMES.add(monthNames[i]);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return MONTH_NAMES;
    }

    public static boolean exists(String URLName){
        try
        {
            HttpURLConnection.setFollowRedirects(false);
            // note : you may also need
            //        HttpURLConnection.setInstanceFollowRedirects(false)
            HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }


}
