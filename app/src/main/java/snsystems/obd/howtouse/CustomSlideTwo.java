package snsystems.obd.howtouse;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import snsystems.obd.R;

/**
 * Created by snsystem_amol on 02-May-17.
 */

public class CustomSlideTwo  extends Fragment
{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {


        View rootView = inflater.inflate(R.layout.fragment_custom_slide_one, container, false);

        return rootView;

    }
}