package magshimim.newzbay;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
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
import com.google.android.gms.plus.Plus;

public class entrance extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private LoginButton facebook_loginButton;
    private SignInButton google_signInButton;
    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private GoogleApiAvailability mGoogleApiAvailability;
    private ConnectionResult mConnectionResult;
    private int request_code;
    private ProgressDialog mProgressDialog;
    private static final String TAG = "check";
    private Communication communication;
    private static final int SIGN_IN_CODE = 0;
    private boolean is_intent_inprogress;
    private boolean is_signInBtn_clicked;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookAndGoogle.reset(BitmapFactory.decodeResource(getResources(), R.drawable.user_icon));
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_entrance);
//        communication = new Communication();
//        new Thread(communication).start();
//        while(!communication.getIsConnect())
//        {
//
//        }
        facebook_login();
        if (Profile.getCurrentProfile() != null) {
            Log.d(TAG, "facebook login");
            FacebookAndGoogle.setLoggedWithFacebook(true);
            FacebookAndGoogle.setCurrentFacebookProfile(Profile.getCurrentProfile());
            FacebookAndGoogle.setFullName(FacebookAndGoogle.getCurrentFacebookProfile().getName());
//            communication.clientSend("First name: " + Profile.getCurrentProfile().getFirstName());
            moveToNewsFeed();
        }
        else
        {
            buildNewGoogleApiClient();
            //Customize sign-in button.a red button may be displayed when Google+ scopes are requested
            customizeSignBtn();
            setBtnClickListeners();
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading...");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    public void signInAsGuest(View v) {
        Log.d(TAG, "guest login");
//        communication.clientSend("First name: guest");
        FacebookAndGoogle.reset(BitmapFactory.decodeResource(getResources(), R.drawable.user_icon));
        moveToNewsFeed();
    }

    public void moveToNewsFeed() {
        Intent nfScreen = new Intent(this, newsfeed_activity.class);
        startActivity(nfScreen);
        finish();
    }

    public void facebook_login()
    {
        facebook_loginButton = (LoginButton) findViewById(R.id.btn_Facebook);
        facebook_loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook login");
//                communication.clientSend("First name: " + Profile.getCurrentProfile().getFirstName());
                FacebookAndGoogle.setLoggedWithFacebook(true);
                FacebookAndGoogle.setCurrentFacebookProfile(Profile.getCurrentProfile());
                FacebookAndGoogle.setFullName(FacebookAndGoogle.getCurrentFacebookProfile().getName());
                moveToNewsFeed();
            }

            @Override
            public void onCancel() {
                FacebookAndGoogle.setLoggedWithFacebook(false);
            }

            @Override
            public void onError(FacebookException e) {
                FacebookAndGoogle.setLoggedWithFacebook(false);
            }

        });
    }

    /*
    create and  initialize GoogleApiClient object to use Google Plus Api.
    While initializing the GoogleApiClient object, request the Plus.SCOPE_PLUS_LOGIN scope.
    */

    private void buildNewGoogleApiClient(){

        mGoogleApiClient =  new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API,Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    /*
      Customize sign-in button. The sign-in button can be displayed in
      multiple sizes and color schemes. It can also be contextually
      rendered based on the requested scopes. For example. a red button may
      be displayed when Google+ scopes are requested, but a white button
      may be displayed when only basic profile is requested. Try adding the
      Plus.SCOPE_PLUS_LOGIN scope to see the  difference.
    */

    private void customizeSignBtn(){

        google_signInButton = (SignInButton) findViewById(R.id.btn_Google);
        google_signInButton.setSize(SignInButton.SIZE_STANDARD);
        google_signInButton.setColorScheme(SignInButton.COLOR_DARK);
        google_signInButton.setScopes(new Scope[]{Plus.SCOPE_PLUS_LOGIN});
        setGooglePlusButtonText(google_signInButton);

    }

    /*
      Set on click Listeners on the sign-in sign-out and disconnect buttons
     */

    private void setBtnClickListeners(){
        // Button listeners
        google_signInButton.setOnClickListener(this);
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            mGoogleApiAvailability.getErrorDialog(this, result.getErrorCode(),request_code).show();
            return;
        }

        if (!is_intent_inprogress) {

            mConnectionResult = result;

            if (is_signInBtn_clicked) {

                resolveSignInError();
            }
        }

    }

    /*
      Will receive the activity result and check which request we are responding to

     */
    @Override
    // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        callbackManager.onActivityResult(requestCode, responseCode, intent);

        // Check which request we're responding to

        if (requestCode == SIGN_IN_CODE) {
            request_code = requestCode;
            if (responseCode != RESULT_OK) {
                is_signInBtn_clicked = false;
                mProgressDialog.dismiss();

            }
            is_intent_inprogress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        is_signInBtn_clicked = false;
        // Get user's information and set it into the layout
        FacebookAndGoogle.setCurrentGoogleProfile(Plus.PeopleApi.getCurrentPerson(mGoogleApiClient));
        FacebookAndGoogle.setmGoogleApiClient(mGoogleApiClient);
        FacebookAndGoogle.setLoggedWithGoogle(true);
        FacebookAndGoogle.setFullName(Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getDisplayName());
        // Update the UI after signin
        moveToNewsFeed();

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
        FacebookAndGoogle.setCurrentGoogleProfile(Plus.PeopleApi.getCurrentPerson(mGoogleApiClient));
        FacebookAndGoogle.setmGoogleApiClient(mGoogleApiClient);
        FacebookAndGoogle.setLoggedWithGoogle(true);
        FacebookAndGoogle.setFullName(Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getDisplayName());
        moveToNewsFeed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_Google:
                gPlusSignIn();
                break;
        }
    }

    /*
      Sign-in into the Google + account
     */

    private void gPlusSignIn() {
        if (!mGoogleApiClient.isConnecting()) {
            Log.d("user connected","connected");
            is_signInBtn_clicked = true;
            mProgressDialog.show();
            resolveSignInError();

        }
    }

    /*
      Method to resolve any signin errors
     */

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                is_intent_inprogress = true;
                mConnectionResult.startResolutionForResult(this, SIGN_IN_CODE);
                Log.d("resolve error", "sign in error resolved");
            } catch (IntentSender.SendIntentException e) {
                is_intent_inprogress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    /*
     Revoking access from Google+ account
     */

    private void gPlusRevokeAccess() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            Log.d("MainActivity", "User access revoked!");
                            buildNewGoogleApiClient();
                            mGoogleApiClient.connect();
                        }

                    });
        }
    }

    protected void setGooglePlusButtonText(SignInButton signInButton) {
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);
            if (v instanceof TextView) {
                TextView mTextView = (TextView) v;
                mTextView.setText(R.string.SignInWithGoogle);
                mTextView.setTypeface(mTextView.getTypeface(), Typeface.BOLD);
                return;
            }
        }
    }
}