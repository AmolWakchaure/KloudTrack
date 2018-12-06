package snsystems.obd.interfaces;

import java.util.List;

import snsystems.obd.trips.Route;

/**
 * Created by shree on 14-Jan-17.
 */
public interface DirectionFinderListener {

    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
