package today.comeet.android.comeet.helper;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import today.comeet.android.comeet.R;

/**
 * Created by Annick on 06/11/2016.
 */

public class GoogleApiHelper {
    private RequestQueue queue;
    private String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";

    public GoogleApiHelper(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    public void retrieveNearbyPlaceData(LatLng location, int radius, String types, final VolleyCallback callback) {
        // Request a string response from the provided URL.
        Log.i("test", "url :" + url + "location=" + location.latitude + "," + location.longitude + "&radius=" + radius + "&types=" + types + "&key=AIzaSyD7PnqYzH87nWyRlfdYR94O8nFLsq3Y-ik");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, this.url += "location=" + location.latitude + "," + location.longitude + "&radius=" + radius + "&types=" + types + "&key=AIzaSyD7PnqYzH87nWyRlfdYR94O8nFLsq3Y-ik",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onSuccess(response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    // Interface de "callback" qui permet de notifier qu'on a finit de récupérer les informations du serveur
    public interface VolleyCallback {
        void onSuccess(String result);
    }

}
