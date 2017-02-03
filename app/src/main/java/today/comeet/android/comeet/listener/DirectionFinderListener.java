package today.comeet.android.comeet.listener;


import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import today.comeet.android.comeet.modules.Route;

/**
 * Created by Annick on 05/11/2016.
 */

public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
