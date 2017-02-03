package today.comeet.android.comeet.fragment;

/**
 * Created by Vincent on 11/10/2016.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import today.comeet.android.comeet.R;
import today.comeet.android.comeet.activity.CreationEventActivity;

public class FirstFragment extends Fragment {
    FloatingActionButton buttonEventCreate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static FirstFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        FirstFragment firstFragment = new FirstFragment();
        firstFragment.setArguments(args);
        return firstFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onStart() {
        super.onStart();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.Events_Fragment, new EventRecyclerViewFragment()).commit();
        fm.beginTransaction().replace(R.id.Profile_Fragment, new ProfileFragment()).commit();
    }
}