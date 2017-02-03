package today.comeet.android.comeet.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


import java.util.Objects;

import today.comeet.android.comeet.R;
import today.comeet.android.comeet.activity.CreatingAdressHomeActivity;
import today.comeet.android.comeet.activity.HomeActivity;
import today.comeet.android.comeet.activity.LoginActivity;
import today.comeet.android.comeet.helper.ServeurApiHelper;

/**
 * A placeholder fragment containing a simple view.
 */
public class FbLoginFragment extends Fragment {

    private CallbackManager callbackManager;
    private AccessTokenTracker tokenTracker;
    private ProfileTracker profileTracker;
    private String[] userPermission = {"public_profile", "email", "user_birthday", "user_location", "user_friends"};

    private FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            final AccessToken accessToken = loginResult.getAccessToken();
            Log.d("FBLogin", "User ID: "
                    + accessToken.getUserId()
                    + "\n" +
                    "Auth Token: "
                    + accessToken.getToken()
                    + "\n" +
                    "Permissions"
                    + accessToken.getPermissions());
            Profile profile = Profile.getCurrentProfile();

            if (accessToken.getPermissions().size() == userPermission.length) {
                Log.d("FBLogin", "testPermissionsWorking");
            }

            // Envoie du token au serveur.
            ServeurApiHelper apihelper = new ServeurApiHelper(getContext());
            apihelper.sendFbTokenAndCheckNewUser(accessToken.getToken(), new ServeurApiHelper.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d("store", "retour dans fblogin: "+result);

                    /**If new user */
                    if (result== "true") {
                        Profile profile = Profile.getCurrentProfile();
                        if (profile != null) {
                            Intent intent = new Intent(getActivity(), CreatingAdressHomeActivity.class);
                            startActivity(intent);
                        }
                    }

                    /** Old user so they're going to the homepage */
                    else {
                        Profile profile = Profile.getCurrentProfile();
                        if (profile != null) {
                            Intent intent = new Intent(getActivity(), HomeActivity.class);
                            startActivity(intent);
                        }
                    }
                }
            });
        }

        @Override
        public void onCancel() {
            Log.d("FBLogin", "onCancel");
        }

        @Override
        public void onError(FacebookException error) {
            Log.d("FBLogin", "onError");
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Vous avez besoin d'une connexion internet pour vous connecter à Facebook. Veuillez activer les données mobiles ou le wifi.")
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
        }
    };

    public FbLoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTokenTracker();
        setupProfileTracker();

        tokenTracker.startTracking();
        profileTracker.startTracking();
        callbackManager = CallbackManager.Factory.create();
    }

    private void setupTokenTracker() {
        tokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                Log.d("FBLogin", "CurrentAccessToken" + currentAccessToken);
                // If user is logging out
                if (currentAccessToken == null) {
                    Log.d("connexion", "current token" + currentAccessToken);
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        };
    }

    private void setupProfileTracker() {
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                Log.d("CurrentProfile", "" + currentProfile);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_facebook, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions(userPermission);
        // If using in a fragment
        loginButton.setFragment(this);
        // Other app specific specialization
        loginButton.registerCallback(callbackManager, facebookCallback);

        // Load HomeActivity if user is logged in
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        /** Redirecting the user if he is correctly registered or not*/
        // Check that we are on LogginActivity
        if (getActivity().getClass().toString().equals("class today.comeet.android.comeet.activity.LoginActivity"))  {
            if (accessToken != null) {
                ServeurApiHelper apihelper = new ServeurApiHelper(getContext());
                apihelper.isItNewUser(new ServeurApiHelper.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                       // Log.d("store","result "+result);

                        if (Objects.equals(result, "true")) {
                            //Log.d("store","ici");
                            Intent intent = new Intent(getActivity(), CreatingAdressHomeActivity.class);
                            startActivity(intent);
                        } else {
                            //Log.d("store","la");
                            Intent intent = new Intent(getActivity(), HomeActivity.class);
                            startActivity(intent);
                        }

                    }
                });



            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();
    }
}
