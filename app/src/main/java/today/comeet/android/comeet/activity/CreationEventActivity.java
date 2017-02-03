package today.comeet.android.comeet.activity;

import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import today.comeet.android.comeet.helper.ConverterHelper;
import today.comeet.android.comeet.helper.DBHelper;
import today.comeet.android.comeet.R;
import today.comeet.android.comeet.helper.ServeurApiHelper;
import today.comeet.android.comeet.provider.EventContentProvider;


public class CreationEventActivity extends AppCompatActivity {

    private DatePickerDialog dpd;
    private TimePickerDialog timepicker;
    private EditText eventName;
    private EditText eventDescription;
    private String date;
    private String heure;
    private Place place;
    private Button friends;
    private JSONArray JsonArraylistFriends;
    private ArrayList<String> listFriends;
    private Calendar calendar;
    private ArrayList<String> participant;
    private EditText eventDate;
    private String participants;
    private EditText eventHour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_event);
        eventName = (EditText) findViewById(R.id.event_name);
        eventDescription = (EditText) findViewById(R.id.event_description);
        friends = (Button) findViewById(R.id.add_friends);

        /* Using Calendar to get current date & hour*/
        calendar = Calendar.getInstance();

        /* Get EditText from hour and date*/
        eventDate = (EditText) findViewById(R.id.event_date);
        eventDate.setText(DateFormat.format("dd/MM/yyyy",calendar));

        eventHour = (EditText) findViewById(R.id.event_hour);
        eventHour.setText(DateFormat.format("HH:mm", calendar));

    }

    public void btn_launch_choose_bar(View v) {

        String dateEtHeure = "";
        if (date== null) {
            date=eventDate.getText().toString();
        }

        if (heure==null) {
            heure=eventHour.getText().toString();
        }
        dateEtHeure+= date + " à " +heure;

        // Launch ChooseBarActivity
        Intent intent = new Intent(getApplicationContext(), ChooseBarActivity.class);

        // Send Data to create new event
        if (!eventName.getText().toString().isEmpty())
            intent.putExtra("nameEvent", eventName.getText().toString());
        else
            intent.putExtra("nameEvent", "Non définit");

        if (!eventDescription.getText().toString().isEmpty())
            intent.putExtra("descriptionEvent", eventDescription.getText().toString());
        else
            intent.putExtra("descriptionEvent", "Non définit");

        intent.putExtra("DateetHeureEvent", dateEtHeure);

        /*
        * 2 cases :
        * participant == null ==> No friends, so we send a string
        * participant != null ==> We send, an array of friends (so ArrayList<String> )
        *
        * */
        if (participant == null)
            intent.putExtra("participants", "aucun amis");
        else
            intent.putExtra("participants", participant);

        startActivityForResult(intent, 1000);
    }

    /**
     * Method to get friends and launch the "ChooseFriendsActivity"
     *
     * @param v
     */

    public void btn_friends(View v) {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo == null) {
            Log.d("internet", "pas connexion");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Vous avez besoin d'une connexion internet pour charger cette page. Veuillez activer les données mobiles ou le wifi, et recharger la page.")
                    .setTitle("Impossible de se connecter")
                    .setCancelable(false)
                    .setPositiveButton("Paramètres",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                                    startActivity(i);
                                }
                            }
                    )
                    .setNegativeButton("Retour",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // ChooseBarActivity.this.finish();
                                }
                            }
                    );

            AlertDialog alert = builder.create();
            alert.show();
            return;
        } else {
            Log.d("internet", "connexion");

        }
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            Log.d("friend", "profile != null");
            Log.d("friend", "profile name: " + profile.getName());

            // we will query the name with the openGraph API
            GraphRequest request = GraphRequest.newMeRequest(
                    AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            // this code is executed when the response from the API is received (async)
                            try {
                                /* Retrieving friends list*/
                                JsonArraylistFriends = object.getJSONObject("friends").getJSONArray("data");
                                Log.d("friend", "list ami retrieve: " + JsonArraylistFriends);

                                listFriends = new ArrayList<>();
                                for (int i = 0; i < JsonArraylistFriends.length(); i++)
                                    listFriends.add(JsonArraylistFriends.getJSONObject(i).getString("name"));

                                Log.d("friend", "liste ami final: " + listFriends);

                                /*Launch ChooseFriends Activity*/
                                Intent intent_friends = new Intent(getApplicationContext(), ChooseFriendsActivity.class);
                                intent_friends.putExtra("friends_list", listFriends);
                                startActivityForResult(intent_friends, 2000);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            // we pass the right parameters for the query : fields=name
            Bundle parameters = new Bundle();
            parameters.putString("fields", "friends");
            request.setParameters(parameters);
            request.executeAsync(); // execute the query
        } else {
            Log.d("friend", "profile == null");

        }
    }


    /**
     * Method permit to back to Home (with the result code send by ChooseBarActivity)
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // on récupère le statut de retour de l'activité 2 c'est à dire l'activité numéro 1000
        if (requestCode == 1000) {
            // si le code de retour est égal à 1 on stoppe l'activité 1
            if (resultCode == 1) {
                // ferme l'actviité
                finish();
            }
        }
        if (requestCode == 2000) {
            /*  Récupère la liste des participants à l'événements*/
            if (resultCode == RESULT_OK) {
                participant = data.getStringArrayListExtra("liste_participant");
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void ChooseDate(View v) {
        dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date = year + "-" + (monthOfYear) + "-" + dayOfMonth;

                /*Priting result in Edittexet */
                Calendar caltemp = Calendar.getInstance();
                caltemp.set(year, monthOfYear, dayOfMonth);
                eventDate.setText(DateFormat.format("dd/MM/yyyy",caltemp));

            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dpd.show();
    }

    public void ChooseHour(View v) {
        timepicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Log.d("heure", "hour: " + hourOfDay + ", minute: " + minute);
                heure = hourOfDay + ":" + minute + ":00";

                /* Printing in the EditText */
                Calendar caltemp = Calendar.getInstance();
                caltemp.set(0, 0, 0, hourOfDay, minute);
                eventHour.setText(DateFormat.format("HH:mm",caltemp));

            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timepicker.setTitle("");
        timepicker.show();

    }

}
