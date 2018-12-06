package snsystems.obd.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;

import org.json.JSONObject;
import org.json.JSONTokener;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import snsystems.obd.R;
import snsystems.obd.classes.T;
import snsystems.obd.classes.Validations;
import snsystems.obd.classes.VolleyResponseClass;
import snsystems.obd.database.DBHelper;
import snsystems.obd.database.S;
import snsystems.obd.interfaces.Constants;
import snsystems.obd.interfaces.VolleyCallback;
import snsystems.obd.interfaces.VolleyErrorCallback;
import snsystems.obd.performancedash.PerformanceDashboardActivity;


public class FeedbackActivity extends AnimationActivity {

    @Bind(R.id.your_feedback_editext)
    EditText your_feedback_editext;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

//    @Bind(R.id.lovProblemTextView)
//    TextView lovProblemTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void submitFeedback(View view)
    {
        if(!Validations.validateEmptyEditext(FeedbackActivity.this,your_feedback_editext,"enter your feedback"))
        {
            return;
        }
        if(!Validations.validateFeedbackLength(FeedbackActivity.this, your_feedback_editext, "Feedback must between 25 and 1000 characters!"))
        {
            return;
        }

        //T.t(FeedbackActivity.this,"Success");
        boolean c = T.checkConnection(FeedbackActivity.this);
        if(c)
        {
            sendFeedback();
        }
        else
        {
            T.t(FeedbackActivity.this,"Network connection off");
        }

    }

    private void sendFeedback()
    {


            String device_id_mail = S.getDeviceIdUserName(new DBHelper(FeedbackActivity.this));

            String [] data = device_id_mail.split("#");

            String [] parameters =
                    {
                            "device_id"+"#"+data[0],
                        "email_id"+"#"+data[1],
                        "feedback_details"+"#"+your_feedback_editext.getText().toString(),
                        "problem_name"+"#"+"NA"

                };
        VolleyResponseClass.getResponseProgressDialogError(
                new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {

                        parseFeedback(result);

                    }
                },
                new VolleyErrorCallback()
                {
                    @Override
                    public void onError(VolleyError result)
                    {

                        new SweetAlertDialog(FeedbackActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setConfirmText("Try again")
                                .setContentText(""+result)
                                .setCancelText("Cancel")
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

                                        sendFeedback();
                                    }
                                })
                                .show();

                    }
                },
                FeedbackActivity.this,
                getResources().getString(R.string.webUrl) + "" + getResources().getString(R.string.sendFeedback),
                your_feedback_editext,
                parameters,
                "Submiting feedback...");

    }

    private void parseFeedback(String result) {

        try
        {
            if(result != null || result.length() > 0)
            {
                Object json = new JSONTokener(result).nextValue();
                if (json instanceof JSONObject)
                {
                    JSONObject tripsJsonObject = new JSONObject(result);
                    String status = tripsJsonObject.getString("success");

                    if(status.equals("1"))
                    {
                        new SweetAlertDialog(FeedbackActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Success")
                                .setConfirmText("OK")
                                .setContentText("Your feedback successfully send.")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();


                                        finish();
                                    }
                                })
                                .show();
                        //T.displaySuccessMessage(FeedbackActivity.this,"Success","OK","Your feedback successfully send.");
                    }
                    else if(status.equals("0"))
                    {
                        T.displayErrorMessage(FeedbackActivity.this,"Oops...","Cancel","Fail to send feedback.");
                    }
                }
                else
                {
                    T.t(FeedbackActivity.this,"incorect json");
                }
            }
            else
            {
                T.t(FeedbackActivity.this,"0 or null json");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feedback, menu);
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

        return super.onOptionsItemSelected(item);
    }


    public void selectLovProblem(View view)
    {


//        new MaterialDialog.Builder(FeedbackActivity.this)
//                .title("Vehicle Speed Alert")
//                .items(R.array.feedback_problem)
//                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice()
//                {
//                    @Override
//                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
//                        try
//                        {
//
//                            lovProblemTextView.setText(text.toString());
//
//                        }
//                        catch (Exception e)
//                        {
//                            e.printStackTrace();
//                        }
//
//                        return true;
//                    }
//                })
//                .show();

    }
}
