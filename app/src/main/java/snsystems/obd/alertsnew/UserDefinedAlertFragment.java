package snsystems.obd.alertsnew;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import snsystems.obd.R;
import snsystems.obd.activity.SplashScreen;
import snsystems.obd.classes.MyApplication;
import snsystems.obd.classes.T;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;

/**
 * Created by snsystem_amol on 2/21/2017.
 */

public class UserDefinedAlertFragment  extends Fragment
{

    private View view;


    private TextView
            textViewFuelLevel,
            textViewSpeed,
            textViewRpm,
            textViewMaintenance,
            textViewServiceDue,
            textViewSubscription;

    private SwitchCompat
            compatSwitchFuelLevel,
            compatSwitchSpeed,
            compatSwitchRpm,
            compatSwitchMaintenance,
            compatSwitchServiceDue,
            compatSwitchSubscription;

    private TextView
            spinnerFuelLevel,
            spinnerSpeed,
            spinnerRpm,
            spinnerMaintenance,
            spinnerServiceDue,
            spinnerSubscription;

    private RelativeLayout
            setFuelLevel,
            setSpeedLevel,
            setRpmLevel,
            setMaintenance,
            setServiceDue,
            setSubscription;



    @Bind(R.id.fuelInfoDialog)
    ImageView fuelInfoDialog;

    @Bind(R.id.speedInfoDialog)
    ImageView speedInfoDialog;

    @Bind(R.id.rpmInfoDialog)
    ImageView rpmInfoDialog;

    @Bind(R.id.maintenanceInfoDialog)
    ImageView maintenanceInfoDialog;

    @Bind(R.id.serviceDueInfoDialog)
    ImageView serviceDueInfoDialog;

    @Bind(R.id.subscriptionInfoDialog)
    ImageView subscriptionInfoDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.user_defined_alert_fragment, container, false);

        ButterKnife.bind(this, view);

        initializeWidgets();
        //initializeArrayAdapter();
        //setSelectedValueSpinner(spinnerFuelLevel,"80%");

        setListner();

        setUserAlertsData();

       // updateUserDefinedAlertsOnOffStatus();

        return view;
    }


    private void setListner() {

                fuelInfoDialog.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        T.displayInfoDialog(getActivity(),"Fuel Level","Ok","To set above fuel level to get alerts accordingly");

                    }
                });

        speedInfoDialog.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                T.displayInfoDialog(getActivity(),"Speed","Ok","To set above Speed to get alerts accordingly");
            }
        });

        rpmInfoDialog.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                T.displayInfoDialog(getActivity(),"RPM","Ok","To set above RPM to get alerts accordingly");
            }
        });

        maintenanceInfoDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                T.displayInfoDialog(getActivity(),"Maintenance","Ok","To set above Days to get alerts accordingly");
            }
        });

//        serviceDueInfoDialog.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                T.displayInfoDialog(getActivity(),"Fuel Level","Ok","To set above fuel level to get alerts accordingly");
//            }
//        });

        subscriptionInfoDialog.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                T.displayInfoDialog(getActivity(),"Subscription","Ok","To set above days to get alerts accordingly");
            }
        });

                setFuelLevel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {



                        if(compatSwitchFuelLevel.isChecked())
                        {
                            new MaterialDialog.Builder(getActivity())
                                    .title("Fuel level Alert")
                                    .items(R.array.fuel_level)
                                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice()
                                    {
                                        @Override
                                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                            try
                                            {

                                                spinnerFuelLevel.setText(text.toString()+" %");
                                                MyApplication.db.updateUserDefinedAlertsSetData(textViewFuelLevel.getText().toString(),text.toString());
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
                        else
                        {
                            T.t(getActivity(),"Please, activate Fuel Level");
                        }




                    }
                });
                setSpeedLevel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {



                        if(compatSwitchSpeed.isChecked())
                        {
                            new MaterialDialog.Builder(getActivity())
                                    .title("Vehicle Speed Alert")
                                    .items(R.array.vehicle_speed)
                                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice()
                                    {
                                        @Override
                                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                            try
                                            {

                                                spinnerSpeed.setText(text.toString()+ "Kmph");
                                                MyApplication.db.updateUserDefinedAlertsSetData(textViewSpeed.getText().toString(),text.toString());
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
                        else
                        {
                            T.t(getActivity(),"Please, activate Speed");
                        }


                    }
                });
                setRpmLevel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if(compatSwitchRpm.isChecked())
                        {
                            new MaterialDialog.Builder(getActivity())
                                .title("RPM Alert")
                                .items(R.array.rpm_speed)
                                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice()
                                {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                        try
                                        {

                                            spinnerRpm.setText(text.toString()+ "RPM");
                                            MyApplication.db.updateUserDefinedAlertsSetData(textViewRpm.getText().toString(),text.toString());
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
                        else
                        {
                            T.t(getActivity(),"Please, activate RPM");
                        }




                    }
                });
                setMaintenance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if(compatSwitchMaintenance.isChecked())
                        {
                            setDataInDays(textViewMaintenance,"Maintenance Alert", spinnerMaintenance);

                        }
                        else
                        {
                            T.t(getActivity(),"Please, activate Maintenance");
                        }



                    }
                });
                setServiceDue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if(compatSwitchServiceDue.isChecked())
                        {
                            setDataInDays(textViewServiceDue,"Service Due Alert", spinnerServiceDue);

                        }
                        else
                        {
                            T.t(getActivity(),"Please, activate Service Due");
                        }

                    }
                });
                setSubscription.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if(compatSwitchSubscription.isChecked())
                        {
                            setDataInDays(textViewSubscription,"Suscription Alert", spinnerSubscription);
                        }
                        else
                        {
                            T.t(getActivity(),"Please, activate Subscription");
                        }


                    }
                });


        compatSwitchFuelLevel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {

                String alertName = textViewFuelLevel.getText().toString();
                if(b)
                {
                    //update alarm true
                    MyApplication.db.updateUserDefinedAlertsOnOff(alertName,getActivity().getResources().getString(R.string.alert_on));
//                    ArrayAdapter<CharSequence> blankAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.fuel_level, android.R.layout.simple_spinner_item);
//                    spinnerFuelLevel.setAdapter(blankAdapter);
                }
                else
                {
                    //update alarm false
                    MyApplication.db.updateUserDefinedAlertsOnOff(alertName,getActivity().getResources().getString(R.string.alert_off));
//                    ArrayAdapter<CharSequence> blankAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.blank_adapter, android.R.layout.simple_spinner_item);
//                    spinnerFuelLevel.setAdapter(blankAdapter);

                }
            }
        });
        compatSwitchSpeed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {

                String alertName = textViewSpeed.getText().toString();
                if(b)
                {
                    //update alarm true
                    MyApplication.db.updateUserDefinedAlertsOnOff(alertName,getActivity().getResources().getString(R.string.alert_on));
//                    ArrayAdapter<CharSequence> blankAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.vehicle_speed, android.R.layout.simple_spinner_item);
//                    spinnerSpeed.setAdapter(blankAdapter);
                }
                else
                {
                    //update alarm false
                    MyApplication.db.updateUserDefinedAlertsOnOff(alertName,getActivity().getResources().getString(R.string.alert_off));
//                    ArrayAdapter<CharSequence> blankAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.blank_adapter, android.R.layout.simple_spinner_item);
//                    spinnerSpeed.setAdapter(blankAdapter);
                }
            }
        });
        compatSwitchRpm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {

                String alertName = textViewRpm.getText().toString();
                if(b)
                {
                    //update alarm true
                    MyApplication.db.updateUserDefinedAlertsOnOff(alertName,getActivity().getResources().getString(R.string.alert_on));
//                    ArrayAdapter<CharSequence> blankAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.rpm_speed, android.R.layout.simple_spinner_item);
//                    spinnerRpm.setAdapter(blankAdapter);
                }
                else
                {
                    //update alarm false
                    MyApplication.db.updateUserDefinedAlertsOnOff(alertName,getActivity().getResources().getString(R.string.alert_off));
//                    ArrayAdapter<CharSequence> blankAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.blank_adapter, android.R.layout.simple_spinner_item);
//                    spinnerRpm.setAdapter(blankAdapter);
                }
            }
        });
        compatSwitchMaintenance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {

                String alertName = textViewMaintenance.getText().toString();
                if(b)
                {
                    //update alarm true
                    MyApplication.db.updateUserDefinedAlertsOnOff(alertName,getActivity().getResources().getString(R.string.alert_on));
//                    ArrayAdapter<CharSequence> blankAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.alert_in_days, android.R.layout.simple_spinner_item);
//                    spinnerMaintenance.setAdapter(blankAdapter);
                }
                else
                {
                    //update alarm false
                    MyApplication.db.updateUserDefinedAlertsOnOff(alertName,getActivity().getResources().getString(R.string.alert_off));
//                    ArrayAdapter<CharSequence> blankAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.blank_adapter, android.R.layout.simple_spinner_item);
//                    spinnerMaintenance.setAdapter(blankAdapter);
                }
            }
        });
        compatSwitchServiceDue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {

                String alertName = textViewServiceDue.getText().toString();
                if(b)
                {
                    //update alarm true
                    MyApplication.db.updateUserDefinedAlertsOnOff(alertName,getActivity().getResources().getString(R.string.alert_on));
//                    ArrayAdapter<CharSequence> blankAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.alert_in_days, android.R.layout.simple_spinner_item);
//                    spinnerServiceDue.setAdapter(blankAdapter);
                }
                else
                {
                    //update alarm false
                    MyApplication.db.updateUserDefinedAlertsOnOff(alertName,getActivity().getResources().getString(R.string.alert_off));
//                    ArrayAdapter<CharSequence> blankAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.blank_adapter, android.R.layout.simple_spinner_item);
//                    spinnerServiceDue.setAdapter(blankAdapter);
                }
            }
        });
        compatSwitchSubscription.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {

                String alertName = textViewSubscription.getText().toString();
                if(b)
                {
                    //update alarm true
                    MyApplication.db.updateUserDefinedAlertsOnOff(alertName,getActivity().getResources().getString(R.string.alert_on));
//                    ArrayAdapter<CharSequence> blankAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.alert_in_days, android.R.layout.simple_spinner_item);
//                    spinnerSubscription.setAdapter(blankAdapter);
                }
                else
                {
                    //update alarm false
                    MyApplication.db.updateUserDefinedAlertsOnOff(alertName,getActivity().getResources().getString(R.string.alert_off));
//                    ArrayAdapter<CharSequence> blankAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.blank_adapter, android.R.layout.simple_spinner_item);
//                    spinnerSubscription.setAdapter(blankAdapter);
                }
            }
        });

//        spinnerFuelLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
//        {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
//            {
//                // your code here
//
//                TextView textView = (TextView)selectedItemView;
//
////                T.t(getActivity(),""+textView.getText().toString());
//
//                MyApplication.db.updateUserDefinedAlertsSetData(textView.getText().toString(),getActivity().getResources().getString(R.string.alert_on));
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView)
//            {
//                // your code here
//            }
//
//        });

    }

    private void setDataInDays(final TextView textAlertName, String tital, final TextView textView)
    {

        new MaterialDialog.Builder(getActivity())
                .title(tital)
                .items(R.array.alert_in_days)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice()
                {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        try
                        {

                            textView.setText(text.toString()+ "Days");
                            MyApplication.db.updateUserDefinedAlertsSetData(textAlertName.getText().toString(),text.toString());
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

    private void setUserAlertsData()
    {

        ArrayList<String> alerts = S.checkUserAlertsAvailable(MyApplication.db);

        if(alerts.isEmpty())
        {
            ArrayAdapter<CharSequence> blankAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.blank_adapter, android.R.layout.simple_spinner_item);
            setAdapter(blankAdapter);

        }
        else
        {
            ArrayAdapter<CharSequence> adapterFuelLevel = ArrayAdapter.createFromResource(getActivity(), R.array.fuel_level, android.R.layout.simple_spinner_item);
            ArrayAdapter<CharSequence> adapterSpeed = ArrayAdapter.createFromResource(getActivity(), R.array.vehicle_speed, android.R.layout.simple_spinner_item);
            ArrayAdapter<CharSequence> adapterRpm = ArrayAdapter.createFromResource(getActivity(), R.array.rpm_speed, android.R.layout.simple_spinner_item);
            ArrayAdapter<CharSequence> adapterMaintenanceServiceDueSubscription = ArrayAdapter.createFromResource(getActivity(), R.array.alert_in_days, android.R.layout.simple_spinner_item);

            //set data here

            compatSwitchFuelLevel.setClickable(true);
            compatSwitchSpeed.setClickable(true);
            compatSwitchRpm.setClickable(true);
            compatSwitchMaintenance.setClickable(true);
            compatSwitchServiceDue.setClickable(true);
            compatSwitchSubscription.setClickable(true);

//            spinnerFuelLevel.setAdapter(adapterFuelLevel);
//            spinnerSpeed.setAdapter(adapterSpeed);
//            spinnerRpm.setAdapter(adapterRpm);
//            spinnerMaintenance.setAdapter(adapterMaintenanceServiceDueSubscription);
//            spinnerServiceDue.setAdapter(adapterMaintenanceServiceDueSubscription);
//            spinnerSubscription.setAdapter(adapterMaintenanceServiceDueSubscription);

            String [] fuel_level_data = alerts.get(0).split("#");
            textViewFuelLevel.setText(fuel_level_data[0]);
            if(Boolean.valueOf(fuel_level_data[1]))
            {
                compatSwitchFuelLevel.setChecked(true);

                if(fuel_level_data[2].equals("NA"))
                {
                    spinnerFuelLevel.setText("Set Fuel Level");

                }
                else
                {
                    spinnerFuelLevel.setText(fuel_level_data[2]+" %");
                }


            }
            else
            {
                compatSwitchFuelLevel.setChecked(false);
                spinnerFuelLevel.setText("Set Fuel Level");

            }

            String [] speed_data = alerts.get(1).split("#");
            textViewSpeed.setText(speed_data[0]);
            if(Boolean.valueOf(speed_data[1]))
            {
                compatSwitchSpeed.setChecked(true);
                if(speed_data[2].equals("NA"))
                {
                    spinnerSpeed.setText("Set Speed");

                }
                else
                {
                    spinnerSpeed.setText(speed_data[2]+" Kmph");
                }

            }
            else
            {
                compatSwitchSpeed.setChecked(false);
                spinnerSpeed.setText("Set Speed");
            }
            String [] rpm_data = alerts.get(2).split("#");
            textViewRpm.setText(rpm_data[0]);
            if(Boolean.valueOf(rpm_data[1]))
            {
                compatSwitchRpm.setChecked(true);

                if(rpm_data[2].equals("NA"))
                {
                    spinnerRpm.setText("Set RPM");

                }
                else
                {
                    spinnerRpm.setText(rpm_data[2]+" RPM");
                }

            }
            else
            {
                compatSwitchRpm.setChecked(false);
                spinnerRpm.setText("Set RPM");

            }

            String [] maintenance_data = alerts.get(3).split("#");
            textViewMaintenance.setText(maintenance_data[0]);
            if(Boolean.valueOf(maintenance_data[1]))
            {
                compatSwitchMaintenance.setChecked(true);

                if(maintenance_data[2].equals("NA"))
                {
                    spinnerMaintenance.setText("Set Maintenance");

                }
                else
                {
                    spinnerMaintenance.setText(maintenance_data[2]+" Days");
                }

            }
            else
            {
                compatSwitchMaintenance.setChecked(false);
                spinnerMaintenance.setText("Set Maintenance");
            }

            String [] service_due_data = alerts.get(4).split("#");
            textViewServiceDue.setText(service_due_data[0]);
            if(Boolean.valueOf(service_due_data[1]))
            {
                compatSwitchServiceDue.setChecked(true);
                if(service_due_data[2].equals("NA"))
                {
                    spinnerServiceDue.setText("Set Service Due");

                }
                else
                {
                    spinnerServiceDue.setText(service_due_data[2]+" Days");
                }
            }
            else
            {
                compatSwitchServiceDue.setChecked(false);
                spinnerServiceDue.setText("Set Service Due");
            }

            String [] subscription_data = alerts.get(5).split("#");
            textViewSubscription.setText(subscription_data[0]);
            if(Boolean.valueOf(subscription_data[1]))
            {
                compatSwitchSubscription.setChecked(true);
                if(subscription_data[2].equals("NA"))
                {
                    spinnerSubscription.setText("Set Subscription");

                }
                else
                {
                    spinnerSubscription.setText(subscription_data[2]+" Days");
                }
            }
            else
            {
                spinnerSubscription.setText("Set Subscription");

            }


        }
    }

    private void setAdapter(ArrayAdapter<CharSequence> blankAdapter) {
//
//        spinnerFuelLevel.setAdapter(blankAdapter);
//                spinnerSpeed.setAdapter(blankAdapter);
//                spinnerRpm.setAdapter(blankAdapter);
//                spinnerMaintenance.setAdapter(blankAdapter);
//                spinnerServiceDue.setAdapter(blankAdapter);
//                spinnerSubscription.setAdapter(blankAdapter);


    }

    private void insertUserDefinedAlerts()
    {



    }

    private void initializeArrayAdapter()
    {

        ArrayAdapter<CharSequence> adapterFuelLevel = ArrayAdapter.createFromResource(getActivity(), R.array.fuel_level, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterSpeed = ArrayAdapter.createFromResource(getActivity(), R.array.vehicle_speed, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterRpm = ArrayAdapter.createFromResource(getActivity(), R.array.rpm_speed, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterMaintenanceServiceDueSubscription = ArrayAdapter.createFromResource(getActivity(), R.array.alert_in_days, android.R.layout.simple_spinner_item);


    }

    private void setSelectedValueSpinner(ArrayAdapter<CharSequence> adapter,Spinner spiner,String compareValue)
    {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiner.setAdapter(adapter);
        if (!compareValue.equals(null))
        {
            int spinnerPosition = adapter.getPosition(compareValue);
            spiner.setSelection(spinnerPosition);
        }
    }

    private void initializeWidgets() {

        textViewFuelLevel = (TextView)view.findViewById(R.id.textViewFuelLevel);
                textViewSpeed = (TextView)view.findViewById(R.id.textViewSpeed);
                textViewRpm = (TextView)view.findViewById(R.id.textViewRpm);
                textViewMaintenance = (TextView)view.findViewById(R.id.textViewMaintenance);
                textViewServiceDue = (TextView)view.findViewById(R.id.textViewServiceDue);
                textViewSubscription = (TextView)view.findViewById(R.id.textViewSubscription);

        compatSwitchFuelLevel = (SwitchCompat) view.findViewById(R.id.compatSwitchFuelLevel);
                compatSwitchSpeed = (SwitchCompat) view.findViewById(R.id.compatSwitchSpeed);
                compatSwitchRpm = (SwitchCompat) view.findViewById(R.id.compatSwitchRpm);
                compatSwitchMaintenance = (SwitchCompat) view.findViewById(R.id.compatSwitchMaintenance);
                compatSwitchServiceDue = (SwitchCompat) view.findViewById(R.id.compatSwitchServiceDue);
                compatSwitchSubscription = (SwitchCompat) view.findViewById(R.id.compatSwitchSubscription);

        spinnerFuelLevel = (TextView) view.findViewById(R.id.spinnerFuelLevel);
                spinnerSpeed = (TextView) view.findViewById(R.id.spinnerSpeed);
                spinnerRpm = (TextView) view.findViewById(R.id.spinnerRpm);
                spinnerMaintenance = (TextView) view.findViewById(R.id.spinnerMaintenance);
                spinnerServiceDue = (TextView) view.findViewById(R.id.spinnerServiceDue);
                spinnerSubscription = (TextView) view.findViewById(R.id.spinnerSubscription);

        setFuelLevel = (RelativeLayout) view.findViewById(R.id.setFuelLevel);
                setSpeedLevel = (RelativeLayout) view.findViewById(R.id.setSpeedLevel);
                setRpmLevel = (RelativeLayout) view.findViewById(R.id.setRpmLevel);
                setMaintenance = (RelativeLayout) view.findViewById(R.id.setMaintenance);
                setServiceDue = (RelativeLayout) view.findViewById(R.id.setServiceDue);
                setSubscription = (RelativeLayout) view.findViewById(R.id.setSubscription);
    }
}
