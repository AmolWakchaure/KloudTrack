package snsystems.obd.carlog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import snsystems.obd.R;

/**
 * Created by snsystem_amol on 2/24/2017.
 */

public class CarLogGraph extends Fragment {

    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.car_log_graph, container, false);


        return view;

    }
}