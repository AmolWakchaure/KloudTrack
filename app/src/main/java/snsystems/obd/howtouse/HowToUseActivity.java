package snsystems.obd.howtouse;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.vlonjatg.android.apptourlibrary.AppTour;
import com.vlonjatg.android.apptourlibrary.MaterialSlide;

import snsystems.obd.R;
import snsystems.obd.activity.AnimationActivity;

public class HowToUseActivity extends AppTour {



    @Override
    public void init(Bundle savedInstanceState) {


        int customSlideColor = Color.parseColor("#0066B3");

//        //Create pre-created fragments
//        Fragment firstSlide = MaterialSlide.newInstance(R.drawable.blue_button_background, "Presentations on the go",
//                "Get stuff done with or without an internet connection.", Color.WHITE, Color.WHITE);
//
//        Fragment secondSlide = MaterialSlide.newInstance(R.drawable.blue_button_background, "Share and edit together",
//                "Write on your own or invite more people to contribute.", Color.WHITE, Color.WHITE);
//
//        //Add slides
//        addSlide(firstSlide, firstColor);
//        addSlide(secondSlide, secondColor);

        //Custom slide
        addSlide(new CustomSlideOne(), customSlideColor);
        addSlide(new CustomSlideTwo(), customSlideColor);

        //Customize tour
        setSkipButtonTextColor(Color.WHITE);
        setNextButtonColorToWhite();
        setDoneButtonTextColor(Color.WHITE);
    }

    @Override
    public void onSkipPressed()
    {
        //Do something after clicking Skip button.
        setCurrentSlide(2);
    }

    @Override
    public void onDonePressed()
    {
        //Do something after clicking Done button.
        finish();
    }
}
