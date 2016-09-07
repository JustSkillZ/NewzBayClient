package magshimim.newzbay;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

public class OpeningActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener
{
    private final String prefsConnection = "magshimim.newzbay.ConnectionPrefs";
    private static final String facebookEmail = "facebookEmail";
    private final String signInAsGuest = "signInAsGuest";
    private final String signInViaGoogle = "signInViaGoogle";
    private final String firstOpening = "firstOpening";
    private GlobalClass globalClass;
    private User user;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);
        FacebookSdk.sdkInitialize(getApplicationContext());
        //******************************Init GlobalClass************************
        //getApplicationContext() set for returning GlobalClass
        ((GlobalClass) getApplicationContext()).initiateClass();
        globalClass = ((GlobalClass) getApplicationContext());
        globalClass.setCurrentActivity(OpeningActivity.this);
        //**********************************************************************
        checkConnectionWithNet();

    }

    public void movToSignInActivity()
    {
        Intent entrance = new Intent(OpeningActivity.this, ActivityEntrance.class);
        this.startActivity(entrance);
        finish();
    }

    public void moveToNewsFeed() //Open NewsFeed activity
    {
        Intent nfScreen = new Intent(this, ActivityNewsFeed.class);
        startActivity(nfScreen);
        finish();
    }

    boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    void checkConnectionWithNet()
    {

        if (isOnline())
        {
            SharedPreferences sharedpreferences; //If first time in this activity, show explanation activity.
            if(getSharedPreferences(prefsConnection, Context.MODE_PRIVATE).getBoolean(firstOpening, true))
            {
                new CountDownTimer(420000, 1000) { //420000 = 7 Minutes

                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        SharedPreferences sharedpreferences = getSharedPreferences(prefsConnection, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean(firstOpening, false);
                        editor.apply();

                        new AlertDialog.Builder(globalClass.getCurrentActivity(), R.style.NBAlertDialog)
                                .setTitle("טיפ:")
                                .setCancelable(false)
                                .setMessage(globalClass.getResources().getString(R.string.connectWithSocialNet1) + "\n"
                                        + globalClass.getResources().getString(R.string.connectWithSocialNet2) + "\n"
                                        + globalClass.getResources().getString(R.string.connectWithSocialNet3))
                                .setPositiveButton("התחבר באמצעות רשת חברתית", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        SharedPreferences sharedpreferences = globalClass.getCurrentActivity().getSharedPreferences(prefsConnection, Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                        editor.putBoolean(signInAsGuest, false);
                                        editor.commit();
                                        Context current = globalClass.getCurrentActivity();
                                        globalClass.endClass();
                                        Intent intent = new Intent(current, ActivityEntrance.class);
                                        current.startActivity(intent);
                                        ((Activity) current).finish();
                                    }
                                })
                                .setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .show();
                    }

                }.start();
            }
            if (getSharedPreferences(prefsConnection, Context.MODE_PRIVATE).getBoolean(signInAsGuest, true))
            {
                sharedpreferences = getSharedPreferences(prefsConnection, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(signInAsGuest, true);
                editor.apply();

                user = new User("Guest", "", "Guest", "guest@guest.com");
                globalClass.setUser(user);
                globalClass.getCommunication().authenticate("guest@guest.com", "Guest", "Guest", globalClass); //Connect as guest
            }
            else if (getSharedPreferences(prefsConnection, Context.MODE_PRIVATE).getBoolean(signInViaGoogle, false))
            {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(Plus.API, Plus.PlusOptions.builder().build())
                        .addScope(Plus.SCOPE_PLUS_LOGIN)
                        .build();
                mGoogleApiClient.connect();
            }
            else if (Profile.getCurrentProfile() != null) //If connected once and still connected with Facebook, auto connect to Facebook.
            {
                sharedpreferences = getSharedPreferences(prefsConnection, Context.MODE_PRIVATE); //If auto-connect: take last saved Facebook email
                user = new FacebookUser(Profile.getCurrentProfile().getFirstName(), Profile.getCurrentProfile().getProfilePictureUri(500, 500).toString(), Profile.getCurrentProfile(), sharedpreferences.getString(facebookEmail, ""));
                globalClass.setUser(user);
                globalClass.getCommunication().authenticate(user.getEmail() ,user.getPicURL(), ((FacebookUser) user).getFacebookProfile().getFirstName(), globalClass); //Connect via Facebook
            }
            else
            {
                movToSignInActivity();
            }
        }
        else
        {
            new AlertDialog.Builder(globalClass.getCurrentActivity(), R.style.NBAlertDialog)
                    .setTitle("אין גישה לרשת האינטרנט")
                    .setMessage("אנא תקן זאת ונסה שנית....")
                    .setCancelable(false)
                    .setPositiveButton("נסה שנית", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            checkConnectionWithNet();
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        if(Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null)
        {
            user = new GoogleUser(Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getName().getGivenName(), Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getImage().getUrl(), Plus.PeopleApi.getCurrentPerson(mGoogleApiClient), mGoogleApiClient, Plus.AccountApi.getAccountName(mGoogleApiClient));
            globalClass.setUser(user);
            globalClass.getCommunication().authenticate(Plus.AccountApi.getAccountName(mGoogleApiClient), user.getPicURL(), ((GoogleUser) user).getGoogleProfile().getName().getGivenName(), globalClass); //Connect via Google
        }
    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onClick(View view)
    {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }
}
