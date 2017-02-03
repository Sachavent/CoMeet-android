package today.comeet.android.comeet.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import today.comeet.android.comeet.R;
import today.comeet.android.comeet.listener.LocationListener;

public class EventDetailActivity extends AppCompatActivity {
    private int idEventToPrint;
    /* Attributs de l'événement */
    private String eventName;
    private String eventDescription;
    private String eventLocalisation;
    private String eventDate;
    private String participants;

    /* Affichage des attributs de l'événement*/
    private TextView txtEventDescription;
    private TextView txtEventLocalisation;
    private TextView txtEventDate;
    private Button btnitineraire;
    private boolean permissionsEnabled;
    private LatLng latLng;
    private TextView txtEventParticipants;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // Check permissions
        ActivatePermissions();
        latLng = getPosition();

        Bundle extras = getIntent().getExtras();
        if (null != extras) {
            idEventToPrint = extras.getInt("id");
            // On incrémente l'id de 1 car il y a une différence de 1 entre la position dans le recyclerView et dans la bdd
            idEventToPrint++;
        }

        txtEventDate = (TextView) findViewById(R.id.eventDate);
        txtEventDescription = (TextView) findViewById(R.id.descriptionEvent);
        txtEventLocalisation = (TextView) findViewById(R.id.eventlocalisation);
        btnitineraire = (Button) findViewById(R.id.btn_itineraire);
        txtEventParticipants = (TextView) findViewById(R.id.eventparticipant);

        // Loading Choosen Event
        Cursor cursor = getContentResolver().query(Uri.parse("content://today.comeet.android.comeet/elements/" + idEventToPrint), null, null, null, null);
        while (cursor.moveToNext()) {
            eventName = cursor.getString(1);
            eventDescription = cursor.getString(2);
            eventLocalisation = cursor.getString(3);
            eventDate = cursor.getString(4);
            participants = cursor.getString(7);
        }
        setTitle(eventName);
        // Printing in TextView
        txtEventDate.append(eventDate);
        txtEventDescription.append(eventDescription);
        if (eventLocalisation != null)
            txtEventLocalisation.append(eventLocalisation);
        else
            txtEventLocalisation.append("Non définit");
        txtEventParticipants.append(participants);
    }

    public void btnItineraireOnclick(View v) {
        Intent intent = new Intent(getApplicationContext(), ItineraireActivity.class);
        // send origin and destination adress
        intent.putExtra("destination", eventLocalisation.toString());
        if (latLng != null) {
            Log.i("position", "latitude: " + latLng.latitude);
            Log.i("position", "longitude: " + latLng.longitude);

            // We need to get adress from latitude and longitude
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            try {
                addresses= geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                address += " "+city;
                Log.i("position", "adresse origine: " +address);
                intent.putExtra("origine", address);

                startActivity(intent);

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            // Localisation null , tell to the user the error
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Unexpected problem with your geolocalisation. Refresh the page")
                    .setTitle("Unexpeted error")
                    .setCancelable(false)
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    EventDetailActivity.this.finish();
                                }
                            }
                    );
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public LatLng getPosition() {
        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Location location = null;
        if (network_enabled && locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) {
            Log.d("test ", "position activée");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }

            location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
                return latlng;

            } else
                Log.d("test ", "location null");
        } else {
            Log.d("test", "position non activé");
            locManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener(this, null), null);
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionsEnabled = true;
                } else {
                    Log.d("test ", "position refusée");
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setMessage("Activating position permissions is mandatory");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            ActivatePermissions();
                        }
                    });
                    AlertDialog dialog = alert.create();
                    dialog.show();
                }

        }
    }

    private void ActivatePermissions() {
        if (permissionsEnabled != true) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE
            }, 10);
        }
    }
}
