package snsystems.obd.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import snsystems.obd.R;

public class ActivityTermsConditions extends AnimationActivity {

    String checked;
    CheckBox accept_checkBox;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_n_conditions);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        accept_checkBox = (CheckBox) findViewById(R.id.accept_CheckBox);
//        Intent intent = new Intent(ActivityTermsConditions.this, CustomViewsActivity.class);
//        intent.putExtra("checked_box", accept_checkBox.isChecked());

//        Intent intent=new Intent();
//        intent.putExtra("Checked", accept_checkBox.isChecked());

        accept_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    checked = "true";
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("checked_box", checked);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else {
                    checked = "false";
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("checked_box", checked);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }

            }
        });


    }

    @Override
    public void onBackPressed() {
        if (accept_checkBox.isChecked()) {
            checked = "true";
            Intent returnIntent = new Intent();
            returnIntent.putExtra("checked_box", checked);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        } else {
            checked = "false";
            Intent returnIntent = new Intent();
            returnIntent.putExtra("checked_box", checked);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
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

            if (accept_checkBox.isChecked()) {
                checked = "true";
                Intent returnIntent = new Intent();
                returnIntent.putExtra("checked_box", checked);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            } else {
                checked = "false";
                Intent returnIntent = new Intent();
                returnIntent.putExtra("checked_box", checked);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

}
