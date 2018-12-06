package snsystems.obd.howtouse;


import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.Locale;

import butterknife.Bind;
import snsystems.obd.R;

/**
 * Created by snsystem_amol on 02-May-17.
 */

public class CustomSlideOne extends Fragment implements TextToSpeech.OnInitListener
{

    private TextToSpeech textToSpeech;

    String infoData = "Sn Systems pvt ltd";

    View rootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {


        rootView = inflater.inflate(R.layout.fragment_custom_slide_one, container, false);
        initialize();
        speakOut();
        return rootView;

    }

    private void initialize() {

        textToSpeech = new TextToSpeech(rootView.getContext(), this);


    }
    @Override
    public void onDestroy()
    {
// Don't forget to shutdown tts!
        if (textToSpeech != null)
        {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status)
    {


        if (status == TextToSpeech.SUCCESS)
        {

            int result = textToSpeech.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
            {
                Log.e("TTS", "This Language is not supported");
            }
            else
            {
                speakOut();
            }

        }
        else
        {
            Log.e("TTS", "Initilization Failed!");
        }
    }
    private void speakOut()
    {

        textToSpeech.speak(infoData, TextToSpeech.QUEUE_FLUSH, null);
    }
}
