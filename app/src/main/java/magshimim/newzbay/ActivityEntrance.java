package magshimim.newzbay;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Permission;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class ActivityEntrance extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener
{

    private static final String TAG = "check";
    private static final int SIGN_IN_CODE = 0;
    private static final String prefsConnection = "magshimim.newzbay.ConnectionPrefs";
    private static final String facebookEmail = "facebookEmail";
    private LoginButton facebookLoginButton;
    private SignInButton googleSignInButton;
    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private GoogleApiAvailability mGoogleApiAvailability;
    private ConnectionResult mConnectionResult;
    private int requestCode;
    private ProgressDialog mProgressDialog;
    private boolean isIntentInprogress;
    private boolean isSignInBtnClicked;
    private GlobalClass globalClass;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //******************************Init GlobalClass************************
        //getApplicationContext() set for returning GlobalClass
        ((GlobalClass) getApplicationContext()).initiateClass();
        globalClass = ((GlobalClass) getApplicationContext());
        globalClass.setCurrentActivity(ActivityEntrance.this);
        //**********************************************************************

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_entrance);

        disableButtons();

        Time now = new Time();
        now.setToNow();
        if (now.hour >= 19 || now.hour >= 0 && now.hour <= 5) //Change Theme if the time is after 7 PM or before 6 AM
        {
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.entrance_layout);
            layout.setBackground(getResources().getDrawable(R.drawable.main_background_night));
            TextView newzBay = (TextView) findViewById(R.id.tv_newzbay);
            newzBay.setTextColor(getResources().getColor(R.color.white));
            TextView slogan = (TextView) findViewById(R.id.tv_slogan);
            slogan.setTextColor(getResources().getColor(R.color.white));
        }

        checkConnectionWithNet();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    public void signInAsGuest(View v)
    {
        user = new User("Guest", "", "Guest", "guest@guest.com");
        globalClass.setUser(user);
        globalClass.getCommunication().authenticate("guest@guest.com", "Guest", "Guest", globalClass); //Connect as guest
    }

    public void moveToNewsFeed() //Connect and open NewsFeed activity
    {
        Intent nfScreen = new Intent(this, ActivityNewsFeed.class);
        startActivity(nfScreen);
        finish();
    }

    public void facebookLogin() //Login via Facebook
    {
        facebookLoginButton = (LoginButton) findViewById(R.id.btn_Facebook);
        facebookLoginButton.setReadPermissions(Arrays.asList("email", "user_friends"));
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
        {

            private ProfileTracker mProfileTracker;

            @Override
            public void onSuccess(LoginResult loginResult) //Logged to Facebook successfully
            {
                Log.d(TAG, "facebook login");
                final GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback()
                        {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response)
                            {
                                Log.v("LoginActivity", response.toString());
                                try
                                {
                                    String email = object.getString("email");
                                    user.setEmail(email);
                                    globalClass.getCommunication().authenticate(user.getEmail(),user.getPicURL(), ((FacebookUser) user).getFacebookProfile().getFirstName(), globalClass); //Connect via Facebook
                                    SharedPreferences sharedpreferences = getSharedPreferences(prefsConnection, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString(facebookEmail, email);
                                    editor.commit();
                                }
                                catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        });
                if (Profile.getCurrentProfile() == null) //While first logging to facebook connected app, getCurrentProfile() returns null
                {
                    mProfileTracker = new ProfileTracker()
                    {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile, Profile profile2)
                        {
                            user = new FacebookUser(Profile.getCurrentProfile().getFirstName(), Profile.getCurrentProfile().getProfilePictureUri(500, 500).toString(), Profile.getCurrentProfile(), "");
                            globalClass.setUser(user);
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,email,gender, birthday"); //Request extra information about the user
                            request.setParameters(parameters);
                            request.executeAsync();
                            mProfileTracker.stopTracking();
                        }
                    };
                    mProfileTracker.startTracking();
                }
                else
                {
                    user = new FacebookUser(Profile.getCurrentProfile().getFirstName(), Profile.getCurrentProfile().getProfilePictureUri(500, 500).toString(), Profile.getCurrentProfile(), "");
                    globalClass.setUser(user);
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email,gender, birthday"); //Request extra information about the user
                    request.setParameters(parameters);
                    request.executeAsync();
                }
            }

            @Override
            public void onCancel()
            {
                globalClass.setUser(null);
            }

            @Override
            public void onError(FacebookException e)
            {
                globalClass.setUser(null);
                checkConnectionWithNet();
            }
        });
    }


    /*create and  initialize GoogleApiClient object to use Google Plus Api.
      While initializing the GoogleApiClient object, request the Plus.SCOPE_PLUS_LOGIN scope. */
    private void buildNewGoogleApiClient()
    {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }


    private void customizeGoogleSignBtn()
    {
        googleSignInButton = (SignInButton) findViewById(R.id.btn_Google);
        if(googleSignInButton != null)
        {
            googleSignInButton.setSize(SignInButton.SIZE_STANDARD);
            googleSignInButton.setColorScheme(SignInButton.COLOR_DARK);
            googleSignInButton.setScopes(new Scope[]{Plus.SCOPE_PLUS_LOGIN});
            setGooglePlusButtonText(googleSignInButton);
        }
    }

    protected void onStart()
    {
        super.onStart();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) //if Google connection failed
    {
        if (!result.hasResolution())
        {
            mGoogleApiAvailability.getErrorDialog(this, result.getErrorCode(), requestCode).show();
            return;
        }

        if (!isIntentInprogress)
        {

            mConnectionResult = result;

            if (isSignInBtnClicked)
            {

                resolveSignInError();
            }
        }

    }

    @Override
    /*Will receive the activity result and check which request we are responding to
      Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...); */
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent)
    {
        super.onActivityResult(requestCode, responseCode, intent);
        callbackManager.onActivityResult(requestCode, responseCode, intent);
        // Check which request we're responding to
        if (requestCode == SIGN_IN_CODE)
        {
            this.requestCode = requestCode;
            if (responseCode != RESULT_OK)
            {
                isSignInBtnClicked = false;
                mProgressDialog.dismiss();

            }
            isIntentInprogress = false;

            if (!mGoogleApiClient.isConnecting())
            {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle arg0) //Google+ connected
    {
        Person p = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
        if(Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null)
        {
            isSignInBtnClicked = false;
            user = new GoogleUser(Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getName().getGivenName(), Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getImage().getUrl(), Plus.PeopleApi.getCurrentPerson(mGoogleApiClient), mGoogleApiClient, Plus.AccountApi.getAccountName(mGoogleApiClient));
            globalClass.setUser(user);
            globalClass.getCommunication().authenticate(Plus.AccountApi.getAccountName(mGoogleApiClient), user.getPicURL(), ((GoogleUser) user).getGoogleProfile().getName().getGivenName(), globalClass); //Connect via Google
        }
        else
        {
            checkConnectionWithNet();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) //Google connection suspended
    {
        mGoogleApiClient.connect();
        user = new GoogleUser(Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getName().getGivenName(), Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getImage().getUrl(), Plus.PeopleApi.getCurrentPerson(mGoogleApiClient), mGoogleApiClient, Plus.AccountApi.getAccountName(mGoogleApiClient));
        globalClass.setUser(user);
        globalClass.getCommunication().authenticate(Plus.AccountApi.getAccountName(mGoogleApiClient), user.getPicURL(), ((GoogleUser) user).getGoogleProfile().getName().getGivenName(), globalClass); //Connect via Google
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_Google:
                gPlusSignIn();
                break;
        }
    }

    private void gPlusSignIn() //Sign-in into the Google+ account
    {
        if (!mGoogleApiClient.isConnecting())
        {
            Log.d("user connected", "connected");
            isSignInBtnClicked = true;
            resolveSignInError();

        }
    }

    private void resolveSignInError() //Resolve any sign-in errors
    {
        if (mConnectionResult.hasResolution())
        {
            try
            {
                isIntentInprogress = true;
                mConnectionResult.startResolutionForResult(this, SIGN_IN_CODE);
                Log.d("resolve error", "sign in error resolved");
            }
            catch (IntentSender.SendIntentException e)
            {
                isIntentInprogress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    private void gPlusRevokeAccess() //Revoking access from Google+ account
    {
        if (mGoogleApiClient.isConnected())
        {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>()
                    {
                        @Override
                        public void onResult(Status arg0)
                        {
                            Log.d("MainActivity", "User access revoked!");
                            buildNewGoogleApiClient();
                            mGoogleApiClient.connect();
                        }

                    });
        }
    }

    protected void setGooglePlusButtonText(SignInButton signInButton)
    {
        for (int i = 0; i < signInButton.getChildCount(); i++)
        {
            View v = signInButton.getChildAt(i);
            if (v instanceof TextView && v != null)
            {
                TextView mTextView = (TextView) v;
                mTextView.setText(R.string.SignInWithGoogle);
                mTextView.setTypeface(mTextView.getTypeface(), Typeface.BOLD);
                return;
            }
        }
    }

    public void connectToSocialNets() //If connected successfully to NB server, now connect to social nets.
    {
        enableButtons();

        facebookLogin();
        if (Profile.getCurrentProfile() != null) //If connected once and still connected with Facebook, auto connect to Facebook.
        {
            Log.d(TAG, "facebook login");
            SharedPreferences sharedpreferences = getSharedPreferences(prefsConnection, Context.MODE_PRIVATE); //If auto-connect: take last saved Facebook email
            user = new FacebookUser(Profile.getCurrentProfile().getFirstName(), Profile.getCurrentProfile().getProfilePictureUri(500, 500).toString(), Profile.getCurrentProfile(), sharedpreferences.getString(facebookEmail, ""));
            globalClass.setUser(user);
            globalClass.getCommunication().authenticate(user.getEmail() ,user.getPicURL(), ((FacebookUser) user).getFacebookProfile().getFirstName(), globalClass); //Connect via Facebook
        }
        else //Connect via Google
        {
            buildNewGoogleApiClient();
            customizeGoogleSignBtn();
            googleSignInButton.setOnClickListener(this);
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading...");
            mGoogleApiClient.connect();
        }
    }

    void disableButtons()
    {
        Button guestLoginBtn = (Button) findViewById(R.id.btn_NB);
        guestLoginBtn.setBackground(getResources().getDrawable(R.drawable.disabled_button_rounded_corners));
        guestLoginBtn.setTextColor(getResources().getColor(R.color.grey));
        guestLoginBtn.setAlpha((float) 0.1);
        guestLoginBtn.setEnabled(false);
        Drawable logo = getResources().getDrawable(R.drawable.anchor_logo);
        logo.setBounds(0, 0, (int) (logo.getIntrinsicWidth() * 0.5), (int) (logo.getIntrinsicHeight() * 0.5));
        guestLoginBtn.setCompoundDrawables(logo, null, null, null);

        LoginButton facebookLoginBtn = (LoginButton) findViewById(R.id.btn_Facebook);
        facebookLoginBtn.setBackground(getResources().getDrawable(R.drawable.disabled_button_rounded_corners));
        facebookLoginBtn.setTextColor(getResources().getColor(R.color.grey));
        facebookLoginBtn.setAlpha((float) 0.1);
        facebookLoginBtn.setEnabled(false);

        SignInButton googleLoginBtn = (SignInButton) findViewById(R.id.btn_Google);
        googleLoginBtn.setEnabled(false);
    }

    void enableButtons()
    {
        //Enable Buttons
        Button guestLoginBtn = (Button) findViewById(R.id.btn_NB);
        if(guestLoginBtn != null)
        {
            guestLoginBtn.setBackground(globalClass.getCurrentActivity().getResources().getDrawable(R.drawable.button_rounded_corners));
            guestLoginBtn.setTextColor(globalClass.getCurrentActivity().getResources().getColor(R.color.white));
            guestLoginBtn.setAlpha((float) 1);
            guestLoginBtn.setEnabled(true);
        }

        LoginButton facebookLoginBtn = (LoginButton) findViewById(R.id.btn_Facebook);
        if(facebookLoginBtn != null)
        {
            facebookLoginBtn.setBackground(globalClass.getCurrentActivity().getResources().getDrawable(R.drawable.button_rounded_corners_facebook));
            facebookLoginBtn.setTextColor(globalClass.getCurrentActivity().getResources().getColor(R.color.white));
            facebookLoginBtn.setAlpha((float) 1);
            facebookLoginBtn.setEnabled(true);
        }

        SignInButton googleLoginBtn = (SignInButton) findViewById(R.id.btn_Google);
        if(googleLoginBtn != null)
        {
            googleLoginBtn.setEnabled(true);
        }
    }

    boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    void checkConnectionWithNet()
    {
        if(isOnline())
        {
            connectToSocialNets();
        }
        else
        {
            disableButtons();
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
}