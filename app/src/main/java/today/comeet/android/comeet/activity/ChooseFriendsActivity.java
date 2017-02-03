package today.comeet.android.comeet.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import today.comeet.android.comeet.R;
import today.comeet.android.comeet.helper.ConverterHelper;

public class ChooseFriendsActivity extends AppCompatActivity {

    private ListView friendsListView;
    private ArrayList<String> friendsListName;
    private ArrayList<String> participants;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_friends);

        /* Get Facebook friends list*/
        if (null != getIntent().getStringArrayListExtra("friends_list")) {
            friendsListName = getIntent().getStringArrayListExtra("friends_list");
            Log.i("friend", "friend list recupéré dans l'activity: "+friendsListName);
        } else {
            Log.e ("friends_activity", "problème récupération liste d'ami");
        }

        friendsListView = (ListView) findViewById(R.id.listView_friends);
        participants = new ArrayList<>();

        /* Inflate the Listview with the friendsName*/
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(ChooseFriendsActivity.this, android.R.layout.simple_list_item_multiple_choice, friendsListName);
        friendsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        friendsListView.setAdapter(arrayAdapter);
    }

    /**
     * This method existing to be sure that the Activity is killed when backButton is pressed
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    /**
     * Button to save friends who are taking part of the event
     * @param v
     */
    public void btn_get_participants(View v) {
        SparseBooleanArray Arraychecked = friendsListView.getCheckedItemPositions();

        /*Warning, here is"<=" (and not "<" )
        * We use the listview and the checkbox to know which friends are taking part of this event.
        * */
        for (int i = 0 ; i <= Arraychecked.size(); i++) {
            if (Arraychecked.get(i)) {
                participants.add(friendsListName.get(i));
            }
        }
        Log.i("friend","liste participants: "+participants);

        /* Sending the list of participant to CreationEventActivity */
        Intent intent = new Intent();
        intent.putExtra("liste_participant", participants);
        setResult(RESULT_OK, intent);

        finish();
    }

    /**
     * Button to comeback to the previous Activity (here Creation Event Activity
     * @param v
     */
    public void btn_cancel(View v) {
        finish();
    }

}
