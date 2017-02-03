package today.comeet.android.comeet.helper;

import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sachavent on 20/10/2016.
 */
public class ServeurApiHelper {

    private RequestQueue queue;
    private String url = "https://comeet-server.herokuapp.com";
    private Context contexte;


    public ServeurApiHelper(Context context) {
        queue = Volley.newRequestQueue(context);
        contexte = context;
    }

    public void sendFbTokenAndCheckNewUser(final String token, final VolleyCallback callback) {
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, this.url + "/login", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                //Log.d("test", "reponse: " + response.toString());
                try {
                    JSONObject jsontoken = new JSONObject(response);
                    String serveurToken = jsontoken.getString("token");

                    /**Saving token */
                    savingToken(serveurToken);

                    /**Is it a new user?*/
                    isItNewUser(new VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {
                            //Log.d("store", "retour is it new user: "+result);
                            callback.onSuccess("true");
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            } //Create an error listener to handle errors appropriately.

        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("fbToken", token); //Add the data you'd like to send to the server.
                return MyData;
            }
        };

        queue.add(MyStringRequest);
    }

    public void setHomeLocation(final LatLng home, final VolleyCallback callback, final String adresse) {
        Log.d("test", "token: "+gettingToken());
        final StringRequest MyStringRequest = new StringRequest(Request.Method.PUT, this.url + "/me?token=" + gettingToken(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("test", "reponse: " + response.toString());
                callback.onSuccess(response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("test", "error home"+error.getMessage());
            } //Create an error listener to handle errors appropriately.

        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("latitude",  String.valueOf(home.latitude));
                MyData.put("longitude",  String.valueOf(home.longitude));
                MyData.put("adresse",  adresse);
                return MyData;
            }
        };

        queue.add(MyStringRequest);

    }

    public void setParticipantsEvent( final String participantsEvent, final VolleyCallback callback) {
        Log.d("test", "token: "+gettingToken());
        Log.d("test", "participants: "+participantsEvent);

        final StringRequest MyStringRequest = new StringRequest(Request.Method.POST, this.url + "/location?token=" + gettingToken(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("test", "reponse: " + response.toString());
                callback.onSuccess(response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("test", "error participants "+error.getMessage());
            } //Create an error listener to handle errors appropriately.

        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("usersName",  participantsEvent);
                return MyData;
            }
        };

        queue.add(MyStringRequest);

    }




    public void isItNewUser(final VolleyCallback callback) {
        getUserDetail(new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonuser = new JSONObject(result);
                    if (jsonuser.isNull("homeLocation")) {
                       // Log.d("store", "nouvel utilisateur ");
                        callback.onSuccess("true");
                    } else {
                        callback.onSuccess("false");
                       // Log.d("store","pas nouvel utilisateur ");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getUserDetail(final VolleyCallback callback) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, this.url + "/me?token=" + gettingToken(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.d("store","result getuserdetail: "+response);
                        /**Prevent that we got the information*/
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    public void savingToken(String token) {

        /* Makes a textfile in the absolute cache directory  */
        File tempFile = new File(contexte.getCacheDir(), "cacheFile.txt");

        /* Writing into the created textfile */
        FileWriter writer = null;
        try {
            writer = new FileWriter(tempFile);
            writer.write(token);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String gettingToken() {

        /* Reading from the Created File */
        String strLine = "";

        /* Makes a textfile in the absolute cache directory  */
        StringBuilder text = new StringBuilder();
        try {
            FileReader fReader = new FileReader(new File(contexte.getCacheDir(), "cacheFile.txt").getAbsoluteFile());
            BufferedReader bReader = new BufferedReader(fReader);

            while ((strLine = bReader.readLine()) != null) {
                Log.d("stored retour", strLine);
                return strLine;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public interface VolleyCallback {
        void onSuccess(String result);
    }


}
