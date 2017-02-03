package today.comeet.android.comeet.activity;

import android.app.NotificationManager;
import android.content.ContentValues;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Profile;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import today.comeet.android.comeet.R;
import today.comeet.android.comeet.helper.ConverterHelper;
import today.comeet.android.comeet.helper.DBHelper;
import today.comeet.android.comeet.helper.GoogleApiHelper;
import today.comeet.android.comeet.helper.ServeurApiHelper;
import today.comeet.android.comeet.modules.ImageLoadTask;
import today.comeet.android.comeet.provider.EventContentProvider;

public class ChooseBarActivity extends AppCompatActivity {
    private final String TAG = "bar";
    private TextView txtgetbar;
    private JSONArray listebar;
    private int indextoShow;
    private String dateEtHeure;
    private String eventName;
    private String eventDescription;
    private ImageView img_bar;
    private TextView numberResult;
    private String participants;
    private LatLng latlng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_pub);

        // Get extra
        Bundle extras = getIntent().getExtras();
        if (null != extras) {
            // Get origine and destination adress
            dateEtHeure = extras.getString("DateetHeureEvent");
            eventName = extras.getString("nameEvent");
            eventDescription = extras.getString("descriptionEvent");
        }

        /* We are converting the ArrayString into String to put it in Database*/
        ArrayList<String> participant_recieve = getIntent().getStringArrayListExtra("participants");

        /*
        * We have to take care, it could be an ArrayList<String> or a String
        * */
        if (participant_recieve != null) {
            ConverterHelper converterHelper = new ConverterHelper();
            participants = converterHelper.convertArrayToString(participant_recieve);
        } else {
            participants = getIntent().getExtras().getString("participants");
        }

        txtgetbar = (TextView) findViewById(R.id.getbar);
        img_bar = (ImageView) findViewById(R.id.img_bar);
        numberResult = (TextView) findViewById(R.id.numberResult);
        indextoShow = 0;
    }

    @Override
    public void onResume() {
        super.onResume();

        /**Cherching best pubs arround this LatLng including all participants */
        ServeurApiHelper apihelper = new ServeurApiHelper(getApplicationContext());
        Log.d("participant", "participants: " + participants);
        if (!participants.equals("aucun amis")) {
            Log.d("participant", "des amis");

            apihelper.setParticipantsEvent(participants, new ServeurApiHelper.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject jsonresult = new JSONObject(result);
                        latlng = new LatLng(jsonresult.getDouble("latitude"), jsonresult.getDouble("longitude"));
                        /**Searching Place arround the specified Latlng */
                        searchingBarByGooglePlace(latlng);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {

            /**Getting home location from the connected user */
            Log.d("participant", "aucun amis");

            apihelper.getUserDetail(new ServeurApiHelper.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject jsonuser = new JSONObject(result);
                        latlng = new LatLng((Double) jsonuser.getJSONObject("homeLocation").get("latitude"), (Double) jsonuser.getJSONObject("homeLocation").get("longitude"));
                        /**Searching Place arround the specified Latlng */
                        searchingBarByGooglePlace(latlng);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /* Load  retrieve data to Textview, imageview etc */
    private void LoadContentbyIndex(int index) {
        try {

            txtgetbar.setText("Nom: " + listebar.getJSONObject(index).getString("name") + "\nLocalisation: " + listebar.getJSONObject(index).getString("vicinity"));
            numberResult.setText("resultat " + (indextoShow + 1) + " sur " + listebar.length());

            // Check if any photo is existing
            if (!listebar.getJSONObject(index).isNull("photos")) {
                // Get photo reference from google api
                Log.d("photo", "photo ref: " + listebar.getJSONObject(index).getJSONArray("photos").getJSONObject(0).getString("photo_reference"));
                String URLimg = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + listebar.getJSONObject(index).getJSONArray("photos").getJSONObject(0).getString("photo_reference") + "&key=AIzaSyD7PnqYzH87nWyRlfdYR94O8nFLsq3Y-ik";
                Log.d("photo", "URL MAIN JAVA: " + "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&" + listebar.getJSONObject(index).getJSONArray("photos").getJSONObject(0).getString("photo_reference") + "&key=AIzaSyD7PnqYzH87nWyRlfdYR94O8nFLsq3Y-ik");
                Log.d("photo", "URL MAIN JAVA: " + URLimg);

                // Load picture from URL
                new ImageLoadTask(URLimg, img_bar).execute();
            } else {
                // No picture so transparent
                img_bar.setImageResource(android.R.color.transparent);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void searchingBarByGooglePlace (LatLng latlng) {
        GoogleApiHelper googleapihelper = new GoogleApiHelper(this);
        // retrieve bar from google nearby shearch
        googleapihelper.retrieveNearbyPlaceData(latlng, 1500, "bar", new GoogleApiHelper.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject resultat = new JSONObject(result);
                    Log.i("googleplace", "result json status: " + resultat.getString("status"));

                    // Recupère les réponses
                    listebar = resultat.getJSONArray("results");
                    Log.i("googleplace", "nombre de retour: " + listebar.length());

                    for (int i = 0; i < listebar.length(); i++) {
                        Log.d("test", "bar " + i + " geoloc : " + listebar.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getString("lat"));
                        Log.i("bar", "bar " + i + " name : " + listebar.getJSONObject(i).getString("name"));
                        Log.i("bar", "bar " + i + " adress : " + listebar.getJSONObject(i).getString("vicinity"));

                    }

                    /* Load  retrieve data to Textview, imageview etc */
                    LoadContentbyIndex(indextoShow);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void btn_next_bar(View v) {
        // Check that indextoShow < listebar size
        if (indextoShow < listebar.length()) {
            indextoShow++;
        }
        LoadContentbyIndex(indextoShow);
    }

    // Get data from CreationEventActivity and put bar data too (localisation)
    public void btn_save_bar_and_event(View v) {

        // Initialise contentvalue representing valus to put in database
        ContentValues contentValues = new ContentValues();

        // Get Event detail
        contentValues.put(DBHelper.COL_2, eventName);
        contentValues.put(DBHelper.COL_3, eventDescription);
        contentValues.put(DBHelper.COL_5, dateEtHeure);

        // Get Bar LatLng and address
        try {
            contentValues.put(DBHelper.COL_4, listebar.getJSONObject(indextoShow).getString("vicinity"));
            contentValues.put(DBHelper.COL_6, listebar.getJSONObject(indextoShow).getJSONObject("geometry").getJSONObject("location").getString("lat"));
            contentValues.put(DBHelper.COL_7, listebar.getJSONObject(indextoShow).getJSONObject("geometry").getJSONObject("location").getString("lng"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Participants
        contentValues.put(DBHelper.COL_8, participants);

        Uri result = getContentResolver().insert(EventContentProvider.CONTENT_URL, contentValues);

        /**Enregistrement de l'événement coté serveur*/
        ServeurApiHelper apihelper = new ServeurApiHelper(getApplicationContext());


        notification();

        //Back to HomeActivity
        setResult(1);
        finish();
    }

    // Fonction qui permet d'effectuer une notification (qui vibre)
    private void notification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Comeet")
                .setContentText("Création événement");

        builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, builder.build());

    }
}
