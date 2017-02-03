package today.comeet.android.comeet.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.Profile;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import today.comeet.android.comeet.R;
import today.comeet.android.comeet.helper.ServeurApiHelper;

public class CreatingAdressHomeActivity extends AppCompatActivity {
    private Place placeSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating_adress_home);
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

/*
* The following code example shows setting an AutocompleteFilter on a PlaceAutocompleteFragment to
* set a filter returning only results with a precise address.
*/
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build();
        autocompleteFragment.setFilter(typeFilter);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("googleplace", "Place: " + place.getName());//get place details here
                Log.i("googleplace", "Place: " + place.getLatLng());//get place details here
                placeSelected = place;
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("googleplace", "An error occurred: " + status);
            }
        });
    }

    public void btn_save_address(View v) {
        Log.d("googleplace", "Place recieved: " + placeSelected.getName());
        Log.d("googleplace", "Place recieved: " + placeSelected.getLatLng());

        ServeurApiHelper apihelper = new ServeurApiHelper(this);
        apihelper.setHomeLocation(placeSelected.getLatLng(), new ServeurApiHelper.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

            }
        }, (String) placeSelected.getName());

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);

    }
}
