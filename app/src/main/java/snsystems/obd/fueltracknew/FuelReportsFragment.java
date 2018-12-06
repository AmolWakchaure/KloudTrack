package snsystems.obd.fueltracknew;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import snsystems.obd.R;
import snsystems.obd.classes.MyApplication;
import snsystems.obd.classes.T;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.interfaces.Constants;

/**
 * Created by snsystem_amol on 3/17/2017.
 */

public class FuelReportsFragment extends Fragment
{


    private View view;

    @Bind(R.id.vehicleNameButton)
    Button vehicleNameButton;


    @Bind(R.id.lastFilledOnDateTextView)
    TextView lastFilledOnDateTextView;

    @Bind(R.id.quantityTextView)
    TextView quantityTextView;

    @Bind(R.id.fuelEconomyAverageTextView)
    TextView fuelEconomyAverageTextView;

    @Bind(R.id.fuelEconomyLastTextView)
    TextView fuelEconomyLastTextView;

    @Bind(R.id.fuelEconomyBestTextView)
    TextView fuelEconomyBestTextView;

    @Bind(R.id.fuelEconomyWorstTextView)
    TextView fuelEconomyWorstTextView;

    @Bind(R.id.fuelPricesAverageTextView)
    TextView fuelPricesAverageTextView;

    @Bind(R.id.fuelPriceslastTextView)
    TextView fuelPriceslastTextView;

    @Bind(R.id.fuelPricesMaxTextView)
    TextView fuelPricesMaxTextView;

    @Bind(R.id.fuelPricesMinTextView)
    TextView fuelPricesMinTextView;

    @Bind(R.id.totalsTotalTextView)
    TextView totalsTotalTextView;

    @Bind(R.id.totalsTotalFuelCostTextView)
    TextView totalsTotalFuelCostTextView;

    @Bind(R.id.totalsTotalFuelTextView)
    TextView totalsTotalFuelTextView;

    @Bind(R.id.totalsTotalFillupsTextView)
    TextView totalsTotalFillupsTextView;


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
        view =  inflater.inflate(R.layout.fuel_reports, container, false);

        ButterKnife.bind(this, view);


        VEHICLE_NAMES = S.getVehicleName(MyApplication.db);
        setClickListner();

        return view;
    }

    private void setClickListner()
    {


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

                                    getVnameLastFillDateQty(text.toString());
                                    getTotalsData(text.toString());
                                    setOdometerReading(text.toString());
                                    getFuelEconomyFuelPriceValues(text.toString());

                                    getMaxMinFuelEconimyPrice(text.toString());

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

    private void getMaxMinFuelEconimyPrice(String vName)
    {

        String FuelEconomyPrices = S.getMaxMinFuelEconimyPrice(MyApplication.db,vName);

        if(FuelEconomyPrices.equals(Constants.NA))
        {
            fuelEconomyBestTextView.setText("0 KmPerLtr");
            fuelEconomyWorstTextView.setText("0 KmPerLtr");
            fuelPricesMaxTextView.setText("0");
            fuelPricesMinTextView.setText("0");
        }
        else
        {

            String [] data = FuelEconomyPrices.split("#");

            fuelEconomyBestTextView.setText(data[0]+" KmPerLtr");
            fuelEconomyWorstTextView.setText(data[1]+" KmPerLtr");
            fuelPricesMaxTextView.setText(data[2]+" ₹");
            fuelPricesMinTextView.setText(data[3]+" ₹");

        }

    }
    private void getFuelEconomyFuelPriceValues(String vName)
    {

        String FuelEconomyPrices = S.getFuelEconomyFuelPriceValues(MyApplication.db,vName);

        if(FuelEconomyPrices.equals(Constants.NA))
        {
            fuelEconomyAverageTextView.setText("0");
        }
        else
        {

            String [] data = FuelEconomyPrices.split("#");

            //Log.e("Total_km",""+data[0]);

            fuelEconomyAverageTextView.setText(Float.valueOf(data[0])/Float.valueOf(data[2])+" KmPerLtr");//fuel economy = total odo reading / toatal fuel quantity
            fuelPricesAverageTextView.setText(data[1]+" ₹");

        }

    }

    private void setOdometerReading(String vName)
    {
        String odo_reading = S.checkOdoAvailable(MyApplication.db,vName);

        if(odo_reading.equals(Constants.NA))
        {
            totalsTotalTextView.setText("0 KM");
            fuelEconomyLastTextView.setText("0 KmPerLtr");
            fuelPriceslastTextView.setText("0 ₹");
        }
        else
        {
            String [] data  = odo_reading.split("#");
            totalsTotalTextView.setText(data[0]+ " KM");
            fuelEconomyLastTextView.setText(data[1]+" KmPerLtr");
            fuelPriceslastTextView.setText(data[2]+" ₹");
        }
    }

    private void getTotalsData(String vName)
    {

        String fueldata = S.getTotalsData(MyApplication.db,vName);

        if(fueldata.equals(Constants.NA))
        {
//            lastFilledOnDateTextView.setText("0");
//            quantityTextView.setText("0");
//            fuelEconomyAverageTextView.setText("0");
//            fuelEconomyLastTextView.setText("0");
//            fuelEconomyBestTextView.setText("0");
//            fuelEconomyWorstTextView.setText("0");
//            fuelPricesAverageTextView.setText("0");
//            fuelPriceslastTextView.setText("0");
//            fuelPricesMaxTextView.setText("0");
//            fuelPricesMinTextView.setText("0");
//            totalsTotalTextView.setText("0");
//            totalsTotalFuelTextView.setText("0");
//            totalsTotalFuelCostTextView.setText("0");
//            totalsTotalFillupsTextView.setText("0");
        }
        else
        {

            String [] data = fueldata.split("#");

            totalsTotalFuelTextView.setText(data[0]+" Ltr");
            totalsTotalFuelCostTextView.setText(data[1]+" ₹");
            totalsTotalFillupsTextView.setText(data[2]);

            String total_dist_sqlite  = data[3];

          //  totalsTotalTextView.setText(""+(Float.valueOf(total_dist_sqlite) - Float.valueOf(totalDistPref))+" KM");
        }
    }

    private void getVnameLastFillDateQty(String vName)
    {
        String fueldata = S.getVnameLastFillDateQty(MyApplication.db,vName);

        if(fueldata.equals(Constants.NA))
        {
            lastFilledOnDateTextView.setText("0");
            quantityTextView.setText("0");
            fuelEconomyAverageTextView.setText("0");
            fuelEconomyLastTextView.setText("0");
            fuelEconomyBestTextView.setText("0");
            fuelEconomyWorstTextView.setText("0");
            fuelPricesAverageTextView.setText("0");
            fuelPriceslastTextView.setText("0");
            fuelPricesMaxTextView.setText("0");
            fuelPricesMinTextView.setText("0");
            totalsTotalTextView.setText("0");
            totalsTotalFuelTextView.setText("0");
            totalsTotalFuelCostTextView.setText("0");
            totalsTotalFillupsTextView.setText("0");
        }
        else
        {
            String [] data = fueldata.split("#");

            lastFilledOnDateTextView.setText(data[0]);
            quantityTextView.setText(data[1]+" Ltr");
        }
    }
}
