package snsystems.obd.sos;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

import cn.pedant.SweetAlert.SweetAlertDialog;
import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;
import snsystems.obd.activity.CustomViewsActivity;
import snsystems.obd.activity.FeedbackActivity;
import snsystems.obd.classes.MyApplication;
import snsystems.obd.classes.T;
import snsystems.obd.classes.Validations;
import snsystems.obd.classes.VolleyResponseClass;
import snsystems.obd.dashboard.DashboardActivity;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.interfaces.Constants;
import snsystems.obd.interfaces.VolleyCallback;
import snsystems.obd.interfaces.VolleyErrorCallback;

public class SubmitSosContactActivity extends AnimationActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private SharedPreferences smstextPreferences;

    @Bind(R.id.first_name_EditText)
    EditText first_name_EditText;

    @Bind(R.id.first_number_EditText)
    EditText first_number_EditText;

    @Bind(R.id.second_name_EditText)
    EditText second_name_EditText;

    @Bind(R.id.second_number_EditText)
    EditText second_number_EditText;

    @Bind(R.id.third_number_EditText)
    EditText third_number_EditText;

    @Bind(R.id.third_name_EditText)
    EditText third_name_EditText;

    @Bind(R.id.first_name_TextInputLayout)
    TextInputLayout first_name_TextInputLayout;

    @Bind(R.id.second_name_TextInputLayout)
    TextInputLayout second_name_TextInputLayout;

    @Bind(R.id.third_name_TextInputLayout)
    TextInputLayout third_name_TextInputLayout;

    @Bind(R.id.first_number_TextInputLayout)
    TextInputLayout first_number_TextInputLayout;

    @Bind(R.id.second_number_TextInputLayout)
    TextInputLayout second_number_TextInputLayout;

    @Bind(R.id.third_number_TextInputLayout)
    TextInputLayout third_number_TextInputLayout;

    @Bind(R.id.special_name_EditText)
    EditText special_name_EditText;

    @Bind(R.id.special_number_EditText)
    EditText special_number_EditText;

    @Bind(R.id.special_name_TextInputLayout)
    TextInputLayout special_name_TextInputLayout;

    @Bind(R.id.special_number_TextInputLayout)
    TextInputLayout special_number_TextInputLayout;



//    @Bind(R.id.viewSendSOS)
//    ImageView viewSendSOS;


    private static final String TAG = PhoneContactsActivity.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;
    private String contactID;     // contacts unique ID



//    @Bind(R.id.submitButton)
//    Button submitButton;

    private String status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_sos_contact);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

       // setClickListner();

        checkPermission();

        Bundle bundle = getIntent().getExtras();

//        if(bundle != null)
//        {
//            //intent_user_profile.putExtra("STATUS","view");
//
//            if(bundle.getString("STATUS").equals("view"))
//            {
//                viewSendSOS.setVisibility(View.VISIBLE);
//            }
//            else
//            {
//                viewSendSOS.setVisibility(View.GONE);
//            }
//        }

        boolean c = T.checkConnection(SubmitSosContactActivity.this);

        if(c)
        {
            ArrayList<String> SOS_DETAILS = S.getSosContact(MyApplication.db);

            if(SOS_DETAILS.isEmpty())
            {

                String device_id_mail = S.getDeviceIdUserName(MyApplication.db);

                String [] data = device_id_mail.split("#");


                getAlreadySosContact(data[1]);
            }
        }
        else
        {
            T.t(SubmitSosContactActivity.this,"Network connection off");
        }


        checkAlreadySosContactAvailableForUpdate();
    }

    private void setClickListner() {


       // first_number_EditText.setEnabled(false);

       // second_name_EditText.setEnabled(false);
        second_number_EditText.setEnabled(false);

        third_name_EditText.setEnabled(false);
        third_number_EditText.setEnabled(false);

        special_name_EditText.setEnabled(false);
        special_number_EditText.setEnabled(false);


        first_name_EditText.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {

            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus)
                {

                    if (first_name_EditText.getText().toString().trim().isEmpty())
                    {
                        T.t(SubmitSosContactActivity.this,"Enter first name");
                    }
                    else
                    {
                        first_number_EditText.setEnabled(true);
//
//                        if(first_number_EditText.getText().toString().equals(second_number_EditText.getText().toString()))
//                        {
//                            T.t(SubmitSosContactActivity.this,"Contact number should not be same.");
//                        }
                    }
                }

            }
        });


        first_number_EditText.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {

            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus)
                {

                    if (first_number_EditText.getText().toString().trim().isEmpty())
                    {
                        T.t(SubmitSosContactActivity.this,"Enter first number");
                    }
                    else
                    {
                        second_name_EditText.setEnabled(true);
//
//                        if(first_number_EditText.getText().toString().equals(second_number_EditText.getText().toString()))
//                        {
//                            T.t(SubmitSosContactActivity.this,"Contact number should not be same.");
//                        }
                    }
                }

            }
        });

        second_name_EditText.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {

            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus)
                {

                    if (second_name_EditText.getText().toString().trim().isEmpty())
                    {
                        T.t(SubmitSosContactActivity.this,"Enter second name");
                    }
                    else
                    {
                        second_number_EditText.setEnabled(true);
//
//                        if(first_number_EditText.getText().toString().equals(second_number_EditText.getText().toString()))
//                        {
//                            T.t(SubmitSosContactActivity.this,"Contact number should not be same.");
//                        }
                    }
                }

            }
        });


        second_number_EditText.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {

            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus)
                {

                    if (second_number_EditText.getText().toString().trim().isEmpty())
                    {
                       T.t(SubmitSosContactActivity.this,"Enter second Number");
                    }
                    else
                    {

                        if(first_number_EditText.getText().toString().equals(second_number_EditText.getText().toString()))
                        {
                            T.t(SubmitSosContactActivity.this,"Contact number should not be same.");
                        }
                        else
                        {
                            third_name_EditText.setEnabled(true);
                        }
                    }
                }

            }
        });
        third_name_EditText.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {

            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus)
                {

                    if (third_name_EditText.getText().toString().trim().isEmpty())
                    {
                        T.t(SubmitSosContactActivity.this,"Enter third name");
                    }
                    else
                    {
                        third_number_EditText.setEnabled(true);
//
//                        if(first_number_EditText.getText().toString().equals(second_number_EditText.getText().toString()))
//                        {
//                            T.t(SubmitSosContactActivity.this,"Contact number should not be same.");
//                        }
                    }
                }

            }
        });
        third_number_EditText.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {

            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus)
                {

                    if (third_number_EditText.getText().toString().trim().isEmpty())
                    {
                        T.t(SubmitSosContactActivity.this,"Enter third Number");
                    }
                    else
                    {

                        if(second_number_EditText.getText().toString().equals(third_number_EditText.getText().toString()))
                        {
                            T.t(SubmitSosContactActivity.this,"Contact number should not be same.");
                        }
                        else
                        {
                            special_name_EditText.setEnabled(true);
                        }
                    }
                }

            }
        });
        special_name_EditText.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {

            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus)
                {

                    if (special_name_EditText.getText().toString().trim().isEmpty())
                    {
                        T.t(SubmitSosContactActivity.this,"Enter fourth name");
                    }
                    else
                    {
                        special_number_EditText.setEnabled(true);
//
//                        if(first_number_EditText.getText().toString().equals(second_number_EditText.getText().toString()))
//                        {
//                            T.t(SubmitSosContactActivity.this,"Contact number should not be same.");
//                        }
                    }
                }

            }
        });

        special_number_EditText.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {

            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus)
                {

                    if (special_number_EditText.getText().toString().trim().isEmpty())
                    {
                        T.t(SubmitSosContactActivity.this,"Enter third Number");
                    }
                    else
                    {

                        if(third_number_EditText.getText().toString().equals(special_number_EditText.getText().toString()))
                        {
                            T.t(SubmitSosContactActivity.this,"Contact number should not be same.");
                        }
                        else
                        {
                            special_number_EditText.setEnabled(true);
                        }
                    }
                }

            }
        });

    }

    private void checkPermission() {

        if ( T.checkPermission(SubmitSosContactActivity.this, Manifest.permission.SEND_SMS) )
        {

        }

        else T.askPermission(SubmitSosContactActivity.this,Manifest.permission.SEND_SMS);



    }

    private void getAlreadySosContact(String email)
    {

        String [] parameters =
                {
                        "email_id"+"#"+email


                };
        VolleyResponseClass.getResponseProgressDialog(
                new VolleyCallback() {
                    @Override
                    public void onSuccess(String result)
                    {

                       // parseFeedback(result);
                        parseResponsefg(result);

                    }
                },
                SubmitSosContactActivity.this,
                getResources().getString(R.string.webUrl)+""+getResources().getString(R.string.getSOSContacts),
                toolbar,
                parameters,
                "Checking SOS available...");



    }
    private void parseResponsefg(String result) {

        try
        {
            if(result != null || result.length() > 0)
            {
                Object json = new JSONTokener(result).nextValue();
                if (json instanceof JSONObject)
                {
                    JSONObject tripsJsonObject = new JSONObject(result);
                    String status = tripsJsonObject.getString("status");

                    if(status.equals("1"))
                    {

                        JSONArray sosJsonArray = tripsJsonObject.getJSONArray("sos_data");


                            JSONObject ssosJsonObject = sosJsonArray.getJSONObject(0);

                            String full_name1 = ssosJsonObject.getString("full_name1");
                            String contact_number1 = ssosJsonObject.getString("contact_number1");
                            String full_name2 = ssosJsonObject.getString("full_name2");
                            String contact_number2 = ssosJsonObject.getString("contact_number2");
                            String full_name3 = ssosJsonObject.getString("full_name3");
                            String contact_number3 = ssosJsonObject.getString("contact_number3");
                            String full_name4 = ssosJsonObject.getString("full_name4");
                            String contact_number4 = ssosJsonObject.getString("contact_number4");

                        setContactLocale("1",full_name1,contact_number1);
                        setContactLocale("2",full_name2,contact_number2);
                        setContactLocale("3",full_name3,contact_number3);
                        setContactLocale("4",full_name4,contact_number4);

                        first_name_EditText.setText(full_name1);
                        first_number_EditText.setText(contact_number1);

                        if(!full_name2.equals(Constants.NA))
                        {
                            second_name_EditText.setText(full_name2);
                        }

                        if(!contact_number2.equals(Constants.NA))
                        {
                            second_number_EditText.setText(contact_number2);
                        }

                        if(!full_name3.equals(Constants.NA))
                        {
                            third_name_EditText.setText(full_name3);
                        }


                        if(!contact_number3.equals(Constants.NA))
                        {
                            third_number_EditText.setText(contact_number3);
                        }


                        if(!full_name4.equals(Constants.NA))
                        {
                            special_name_EditText.setText(full_name4);
                        }

                        if(!contact_number4.equals(Constants.NA))
                        {
                            special_number_EditText.setText(contact_number4);
                        }


                    }
                    else
                    {

                    }
                }
                else
                {
                    T.t(SubmitSosContactActivity.this, "incorect json");
                }
            }
            else
            {
                T.t(SubmitSosContactActivity.this,"0 or null json");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
    }

    private void checkAlreadySosContactAvailableForUpdate() {


        try
        {
            ArrayList<String> SOS_DETAILS = S.getSosContact(MyApplication.db);

            if(!SOS_DETAILS.isEmpty())
            {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//                submitButton.setText("UPDATE");

                if(!SOS_DETAILS.get(0).equals(Constants.NA))
                {
                    first_name_EditText.setText(SOS_DETAILS.get(0));
                }

                if(!SOS_DETAILS.get(1).equals(Constants.NA))
                {
                    first_number_EditText.setText(SOS_DETAILS.get(1));

                }

                if(!SOS_DETAILS.get(2).equals(Constants.NA))
                {
                    second_name_EditText.setText(SOS_DETAILS.get(2));

                }

                if(!SOS_DETAILS.get(3).equals(Constants.NA))
                {
                    second_number_EditText.setText(SOS_DETAILS.get(3));

                }

                if(!SOS_DETAILS.get(4).equals(Constants.NA))
                {
                    third_name_EditText.setText(SOS_DETAILS.get(4));

                }

                if(!SOS_DETAILS.get(5).equals(Constants.NA))
                {
                    third_number_EditText.setText(SOS_DETAILS.get(5));

                }

                if(!SOS_DETAILS.get(6).equals(Constants.NA))
                {
                    special_name_EditText.setText(SOS_DETAILS.get(6));
                }

                if(!SOS_DETAILS.get(7).equals(Constants.NA))
                {
                    special_number_EditText.setText(SOS_DETAILS.get(7));
                }






            }
        }
        catch (Exception e)
        {
            //T.t(SubmitSosContactActivity.this,"SOS Contact:"+e);
        }

    }



    private void updateDetails() {


        if(!Validations.validateEmptyField(first_name_EditText, "enter first name", first_name_TextInputLayout))
        {
            return;
        }
        if(!Validations.validateEmptyField(first_number_EditText,"enter first number",first_number_TextInputLayout))
        {
            return;
        }
        if (!Validations.validateMobileLength(first_number_EditText,"Invalid first mobile number",first_number_TextInputLayout,SubmitSosContactActivity.this))
        {
            return;
        }
        if(!validateSecondMobile())
        {
            return;
        }
        if(!validateThirdMobile())
        {
            return;
        }
        if(!validateFourthMobile())
        {
            return;
        }
        if(!getUniqueContacts())
        {
            return;
        }



        //T.t(SubmitSosContactActivity.this,"Success");


        boolean c = T.checkConnection(SubmitSosContactActivity.this);

        if(c)
        {
            submitDetails();
        }
        else
        {
            T.tTop(SubmitSosContactActivity.this,"Network connection off");
        }

    }
    private boolean validateFourthMobile()
    {

        boolean returnStatus = false;
        try
        {

            if(special_name_EditText.getText().toString().trim().isEmpty())
            {
                //returnStatus = true;
                if(special_number_EditText.getText().toString().trim().isEmpty())
                {

                    returnStatus = true;
                }
                else
                {

                    T.t(SubmitSosContactActivity.this,"Enter Fourth Person Name");
                    special_name_EditText.setError("Enter Fourth Person Name");
                    returnStatus = false;

                }
            }
            else
            {
                if(special_number_EditText.getText().toString().trim().isEmpty())
                {

                    T.t(SubmitSosContactActivity.this,"Enter Fourth Person Mobile");
                    special_number_EditText.setError("Enter Fourth Person Mobile");
                    returnStatus = false;
                }
                else
                {
                    boolean statuss = Validations.validateSosMobile(special_number_EditText);

                    if(statuss)
                    {
                        returnStatus = true;
                    }
                    else
                    {
                        T.t(SubmitSosContactActivity.this,"Invalid mobile");
                        special_number_EditText.setError("Invalid mobile");
                        returnStatus = false;
                    }
                }

            }

            //returnStatus = true;

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return returnStatus;
    }
    private boolean validateThirdMobile()
    {

        boolean returnStatus = false;
        try
        {

            if(third_name_EditText.getText().toString().trim().isEmpty())
            {
                //returnStatus = true;
                if(third_number_EditText.getText().toString().trim().isEmpty())
                {

                    returnStatus = true;
                }
                else
                {

                    T.t(SubmitSosContactActivity.this,"Enter Third Person Name");
                    third_name_EditText.setError("Enter Third Person Name");
                    returnStatus = false;

                }
            }
            else
            {
                if(third_number_EditText.getText().toString().trim().isEmpty())
                {

                    T.t(SubmitSosContactActivity.this,"Enter Third Person Mobile");
                    third_number_EditText.setError("Enter Third Person Mobile");
                    returnStatus = false;
                }
                else
                {
                    boolean statuss = Validations.validateSosMobile(third_number_EditText);

                    if(statuss)
                    {
                        returnStatus = true;
                    }
                    else
                    {
                        T.t(SubmitSosContactActivity.this,"Invalid mobile");
                        third_number_EditText.setError("Invalid mobile");
                        returnStatus = false;
                    }
                }

            }

            //returnStatus = true;

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return returnStatus;
    }

    private boolean validateSecondMobile()
    {

        boolean returnStatus = false;
        try
        {

                if(second_name_EditText.getText().toString().trim().isEmpty())
                {
                    //returnStatus = true;
                    if(second_number_EditText.getText().toString().trim().isEmpty())
                    {

                        returnStatus = true;
                    }
                    else
                    {

                        T.t(SubmitSosContactActivity.this,"Enter Second Person Name");
                        second_name_EditText.setError("Enter Second Person Name");
                        returnStatus = false;

                    }
                }
                else
                {
                    if(second_number_EditText.getText().toString().trim().isEmpty())
                    {

                        T.t(SubmitSosContactActivity.this,"Enter Second Person Mobile Number");
                        second_number_EditText.setError("Enter Second Person Mobile Number");
                        returnStatus = false;
                    }
                    else
                    {
                        boolean statuss = Validations.validateSosMobile(second_number_EditText);

                        if(statuss)
                        {
                            returnStatus = true;
                        }
                        else
                        {
                            T.t(SubmitSosContactActivity.this,"Invalid mobile");
                            second_number_EditText.setError("Invalid mobile");
                            returnStatus = false;
                        }
                    }

                }

            //returnStatus = true;

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return returnStatus;
    }

    private boolean getUniqueContacts() {

        boolean returnStatus = false;
        try
        {
            ArrayList<String> contactList = new ArrayList<>();

            String contactOne = first_number_EditText.getText().toString();
            String contactTwo = second_number_EditText.getText().toString();
            String contactThree = third_number_EditText.getText().toString();
            String contactFour = special_number_EditText.getText().toString();

            //one
            if(!contactOne.trim().isEmpty())
            {
                String conOne = contactOne.substring(0, 3);

                if(conOne.equals("+91"))
                {
                    contactList.add(contactOne.substring(contactOne.lastIndexOf("+91") + 3));
                }
                else
                {
                    contactList.add(contactOne);
                }
            }
            //two
            if(!contactTwo.trim().isEmpty())
            {
                String conTwo = contactTwo.substring(0, 3);

                if(conTwo.equals("+91"))
                {
                    contactList.add(contactTwo.substring(contactTwo.lastIndexOf("+91") + 3));
                }
                else
                {
                    contactList.add(contactTwo);
                }
            }
            //three
            if(!contactThree.trim().isEmpty())
            {
                String conThree = contactThree.substring(0, 3);

                if(conThree.equals("+91"))
                {
                    contactList.add(contactThree.substring(contactThree.lastIndexOf("+91") + 3));
                }
                else
                {
                    contactList.add(contactThree);
                }
            }

            //four
            if(!contactFour.trim().isEmpty())
            {
                String conFour = contactFour.substring(0, 3);

                if(conFour.equals("+91"))
                {
                    contactList.add(contactFour.substring(contactFour.lastIndexOf("+91") + 3));
                }
                else
                {
                    contactList.add(contactFour);
                }
            }

            boolean returnStatusTemp = findDuplicates(contactList);

            if(returnStatusTemp)
            {
                T.t(SubmitSosContactActivity.this,"SoS contacts should be unique.");
                returnStatus = false;
            }
            else
            {
                returnStatus = true;
            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return returnStatus;
    }
    public static boolean findDuplicates(ArrayList<String> listContainingDuplicates)
    {

        final Set<String> set1 = new HashSet<String>();

        for (String yourCard : listContainingDuplicates)
        {
            if (!set1.add(yourCard))
            {
                return true;
            }
        }
        return false;
    }

    private void submitDetails()
    {

        String secondName,secondNumber,thirdName,thirdNumber,fourthName,fourthNumber;

        if(second_name_EditText.getText().toString().trim().isEmpty())
        {
            secondName = Constants.NA;
        }
        else
        {
            secondName = second_name_EditText.getText().toString();
        }

        if(second_number_EditText.getText().toString().trim().isEmpty())
        {
            secondNumber = Constants.NA;
        }
        else
        {
            secondNumber = second_number_EditText.getText().toString();
        }

        if(third_name_EditText.getText().toString().trim().isEmpty())
        {
            thirdName = Constants.NA;
        }
        else
        {
            thirdName = third_name_EditText.getText().toString();
        }

        if(third_number_EditText.getText().toString().trim().isEmpty())
        {
            thirdNumber = Constants.NA;
        }
        else
        {
            thirdNumber = third_number_EditText.getText().toString();
        }

        if(special_name_EditText.getText().toString().trim().isEmpty())
        {
            fourthName = Constants.NA;
        }
        else
        {
            fourthName = special_name_EditText.getText().toString();
        }

        if(special_number_EditText.getText().toString().trim().isEmpty())
        {
            fourthNumber = Constants.NA;
        }
        else
        {
            fourthNumber = special_number_EditText.getText().toString();
        }




        String deviceIDemail = S.getDeviceIdUserName(MyApplication.db);

        String [] data = deviceIDemail.split("#");
        String [] parameters =
                {
                        "device_id"+"#"+data[0],
                        "username"+"#"+data[1],
                        "name1"+"#"+first_name_EditText.getText().toString(),
                        "contact1"+"#"+first_number_EditText.getText().toString(),
                        "name2"+"#"+secondName,
                        "contact2"+"#"+secondNumber,
                        "name3"+"#"+thirdName,
                        "contact3"+"#"+thirdNumber,
                        "name4"+"#"+fourthName,
                        "contact4"+"#"+fourthNumber
                };
        VolleyResponseClass.getResponseProgressDialogError(
                new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        //Log.e("VolleyResponse", "" + result);
                        //{"status":1,"success":"Success! Record updated successfully."}

                        parseResponse(result);


                    }
                },
                new VolleyErrorCallback()
                {
                    @Override
                    public void onError(VolleyError result)
                    {
                        new SweetAlertDialog(SubmitSosContactActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setConfirmText("Try again")
                                .setCancelText("Cancel")
                                .setContentText(""+result)
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                                        sweetAlertDialog.dismissWithAnimation();
                                    }
                                })
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                                {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog)
                                    {
                                        sDialog.dismissWithAnimation();
                                        submitDetails();
                                    }
                                })
                                .show();
                    }
                },
                SubmitSosContactActivity.this,
                getResources().getString(R.string.webUrl) + "" + getResources().getString(R.string.submitSosCon),
                toolbar,
                parameters,
                "Submiting sos...");


    }

    private void parseResponse(String result) {

        try
        {
            if(result != null || result.length() > 0)
            {
                Object json = new JSONTokener(result).nextValue();
                if (json instanceof JSONObject)
                {
                    JSONObject tripsJsonObject = new JSONObject(result);
                    String status = tripsJsonObject.getString("status");

                    if(status.equals("1"))
                    {
                        storeContactLocale();
                        new SweetAlertDialog(SubmitSosContactActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Success")
                                .setConfirmText("Ok")
                                .setContentText("SOS contacts successfully submited.")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();

                                        Intent i = new Intent(SubmitSosContactActivity.this, DashboardActivity.class);
                                        startActivity(i);
                                        finish();


                                    }
                                })
                                .show();
                        //T.displaySuccessMessage(SubmitSosContactActivity.this,"Success","Ok","SOS contacts successfully submited.");
                    }
                    else
                    {
                        new SweetAlertDialog(SubmitSosContactActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Fail...")
                                .setConfirmText("Try again")
                                .setContentText("Fail to submit SOS contacts.")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();

                                        submitDetails();
                                    }
                                })
                                .show();
                       // T.displayErrorMessage(SubmitSosContactActivity.this, "Fail...", "Ok", " Fail to submit SOS contacts.");
                    }
                }
                else
                {
                    T.t(SubmitSosContactActivity.this, "incorect json");
                }
            }
            else
            {
                T.t(SubmitSosContactActivity.this,"0 or null json");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
    }

    private void storeContactLocale()
    {
        //clear prev data

        MyApplication.db.clearSosContacts();
        String firstName = first_name_EditText.getText().toString();
        String firstNumber = first_number_EditText.getText().toString();

        setContactLocale("1",firstName,firstNumber);

        String secondName = second_name_EditText.getText().toString();
        String secondNumber = second_number_EditText.getText().toString();

        setContactLocale("2",secondName,secondNumber);

        String thirdName = third_name_EditText.getText().toString();
        String thirdNumber = third_number_EditText.getText().toString();

        setContactLocale("3",thirdName,thirdNumber);

        String fourthName = special_name_EditText.getText().toString();
        String fourthNumber = special_number_EditText.getText().toString();

        setContactLocale("4",fourthName,fourthNumber);

    }

    private void setContactLocale(String id,String firstName, String firstNumber)
    {

        boolean status = S.alreadyEmergencyContacts(MyApplication.db, id);

        if(status)
        {

            MyApplication.db.updateContact(firstName,firstNumber,id);
        }
        else
        {
            MyApplication.db.addEmergencyCotacts(firstName, firstNumber);

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_submit_sos_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {

            finish();
        }
        //select_done

//        if (id == R.id.select_done) {
//
//            updateDetails();
//        }

        return super.onOptionsItemSelected(item);
    }


    public void addFirstContactPreference(View view)
    {

        if ( T.checkPermission(SubmitSosContactActivity.this, Manifest.permission.READ_CONTACTS) )
        {
            status = "first";
            startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
        }

        else T.askPermission(SubmitSosContactActivity.this,Manifest.permission.READ_CONTACTS);

    }

    public void addSecondContactPreference(View view)
    {
        if ( T.checkPermission(SubmitSosContactActivity.this, Manifest.permission.READ_CONTACTS) )
        {
            status = "second";
            startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
        }

        else T.askPermission(SubmitSosContactActivity.this,Manifest.permission.READ_CONTACTS);


    }
    public void addThirdContactPreference(View view)
    {
        if ( T.checkPermission(SubmitSosContactActivity.this, Manifest.permission.READ_CONTACTS) )
        {
            status = "third";
            startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
        }

        else T.askPermission(SubmitSosContactActivity.this,Manifest.permission.READ_CONTACTS);


    }
    public void addFourthContactPreference(View view)
    {
        if ( T.checkPermission(SubmitSosContactActivity.this, Manifest.permission.READ_CONTACTS) )
        {
            status = "fourth";
            startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
        }

        else T.askPermission(SubmitSosContactActivity.this,Manifest.permission.READ_CONTACTS);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {

            uriContact = data.getData();

            try
            {
                String name = retrieveContactName();
                String number = retrieveContactNumber();

                if(status.equals("first"))
                {
                    first_name_EditText.setText(name);
                    first_number_EditText.setText(number);
                }
                else if(status.equals("second"))
                {
                    second_name_EditText.setText(name);
                    second_number_EditText.setText(number);
                }
                else if(status.equals("third"))
                {
                    third_name_EditText.setText(name);
                    third_number_EditText.setText(number);
                }
                else if(status.equals("fourth"))
                {
                    special_name_EditText.setText(name);
                    special_number_EditText.setText(number);
                }
            }
            catch (Exception e)
            {

            }



        }
    }
    private String  retrieveContactNumber() {

        String contactNumber = null;
        try
        {

            // getting contacts ID
            Cursor cursorID = getContentResolver().query(uriContact,
                    new String[]{ContactsContract.Contacts._ID},
                    null, null, null);

            if (cursorID.moveToFirst()) {

                contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
            }

            cursorID.close();

            Log.d(TAG, "Contact ID: " + contactID);

            // Using the contact ID now we will get contact phone number
            Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                            ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                    new String[]{contactID},
                    null);

            if (cursorPhone.moveToFirst())
            {
                contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                //  contactName = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            }

            cursorPhone.close();

            //Log.d("Contact", "Number:" + contactNumber.replace(" ",""));
        }
        catch (Exception e)
        {
            T.t(SubmitSosContactActivity.this,"Contact Number Not Found");
        }

        return contactNumber.replace(" ","");
    }
    private String retrieveContactName() {

        String contactName = null;
        try
        {


            // querying contact data store
            Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);

            if (cursor.moveToFirst()) {

                // DISPLAY_NAME = The display name for the contact.
                // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.

                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            }

            cursor.close();

            //Log.d("Contact", "Name:" + contactName);
        }
        catch (Exception e)
        {
            T.t(SubmitSosContactActivity.this,"Name not found");
        }

        return contactName;

    }


    public void submitDetails(View view)
    {

        updateDetails();
    }

    public void sendSOS(View view)
    {
        boolean status  = S.checkLogin(MyApplication.db);

        if(status)
        {

            checkPermissionforsms();

            boolean c = T.checkConnection(SubmitSosContactActivity.this);
            if(c)
            {
                sendSignalToWebApp();
            }
            else
            {
                T.t(SubmitSosContactActivity.this,"Network connection off. Message will send from device wait");
            }

        }
        else
        {
            displayDialog(
                    "Oops...",
                    "Ok",
                    "Close",
                    "No Sos contacts found");
            //T.displaySuccessMessage(MessageActivity.this, "Success", "Close", "SMS successfully send");
        }
    }

    private void displayDialog(String title,String confirmText,String cancelText,String contentText)
    {
        //
        new SweetAlertDialog(SubmitSosContactActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setConfirmText(confirmText)
                .setCancelText(cancelText)
                .setContentText(contentText)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();

                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        finish();

                    }
                })
                .show();

    }
    private void sendSignalToWebApp()
    {


        String device_id_mail = S.getDeviceIdUserName(MyApplication.db);

        String [] data = device_id_mail.split("#");

        boolean c = T.checkConnection(SubmitSosContactActivity.this);

        if(c)
        {
            String [] parameters =
                    {
                            "device_id"+"#"+data[0],
                            "username"+"#"+data[1]
                    };
            VolleyResponseClass.getResponseProgressDialog(
                    new VolleyCallback() {
                        @Override
                        public void onSuccess(String result)
                        {
                            // Log.e("VolleyResponse", "" + result);
                            //{"status":1,"success":"Success! Alert added successfully."}

                            parseResponseSms(result);


                        }
                    },
                    SubmitSosContactActivity.this,
                    getResources().getString(R.string.webUrl)+""+getResources().getString(R.string.sendSosSignalTowebapp),
                    new EditText(SubmitSosContactActivity.this),
                    parameters,
                    "Sending sos...");
        }

    }
    private void parseResponseSms(String result) {

        try
        {
            if(result != null || result.length() > 0)
            {
                Object json = new JSONTokener(result).nextValue();
                if (json instanceof JSONObject)
                {
                    JSONObject tripsJsonObject = new JSONObject(result);
                    String status = tripsJsonObject.getString("status");

                    if(status.equals("1"))
                    {


                    }
                }
                else
                {
                    T.t(SubmitSosContactActivity.this,"incorect json");
                }
            }
            else
            {
                T.t(SubmitSosContactActivity.this,"0 or null json");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
    }
    private void checkPermissionforsms()
    {

        try
        {
            ArrayList<String> sosContacts = S.getSosContact(MyApplication.db);

            smstextPreferences = getSharedPreferences(Constants.SMS_TEXT, 0);
            SharedPreferences.Editor editor = smstextPreferences.edit();
            editor.commit();


            String vehicle_number = smstextPreferences.getString("vehicle_number","");
            String latitude = smstextPreferences.getString("latitude","");
            String longitude = smstextPreferences.getString("longitude","");
            String address = smstextPreferences.getString("address","");
            String name = smstextPreferences.getString("name","");
            String mobile = smstextPreferences.getString("mobile","");

            String smsText = "Vehicle Number : "+vehicle_number+",Mobile:"+mobile+",Person Name: "+name+",LatLong: "+latitude+","+longitude+","+address+"";

            if ( T.checkPermission(SubmitSosContactActivity.this, Manifest.permission.SEND_SMS) )
            {


                //Toast.makeText(getApplicationContext(), "Message Sent successfully!", Toast.LENGTH_LONG).show();

                for(int i = 0; i < sosContacts.size(); i++)
                {
                    Intent intent =new Intent();
                    PendingIntent pi=PendingIntent.getActivity(getApplicationContext(), 0, intent,0);

                    SmsManager sms= SmsManager.getDefault();
                    sms.sendTextMessage(sosContacts.get(i), null, smsText, pi,null);

                    Toast.makeText(getApplicationContext(), "Message Sent successfully!", Toast.LENGTH_LONG).show();
                }
            }

            else T.askPermission(SubmitSosContactActivity.this,Manifest.permission.SEND_SMS);
        }
        catch (Exception e)
        {

        }
    }
}
