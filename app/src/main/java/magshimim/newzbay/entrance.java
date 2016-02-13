package magshimim.newzbay;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

public class entrance extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private LoginButton facebook_loginButton;
    private SignInButton google_signInButton;
    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions gso;
    private ProgressDialog mProgressDialog;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "check";
    private Communication communication;

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
            movToNewsFeed();
        }
        else
        {
            FacebookAndGoogle.setLoggedWithFacebook(false);
            google_login();
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
        movToNewsFeed();
    }

    public void movToNewsFeed() {
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
                movToNewsFeed();
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

    public void google_login() {
        findViewById(R.id.btn_Google).setOnClickListener(this);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        google_signInButton = (SignInButton) findViewById(R.id.btn_Google);
        google_signInButton.setSize(SignInButton.SIZE_STANDARD);
        google_signInButton.setScopes(gso.getScopeArray());
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            //Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            //Log.d(TAG, "not cached");
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }
    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        //Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            FacebookAndGoogle.setCurrentGoogleProfile(acct);
            FacebookAndGoogle.setmGoogleApiClient(mGoogleApiClient);
            Log.d(TAG, "google login");
            FacebookAndGoogle.setLoggedWithGoogle(true);
//            communication.clientSend("@101|" + acct.getDisplayName() + "|");
            movToNewsFeed();
        } else {
            // Signed out, show unauthenticated UI.
            FacebookAndGoogle.setLoggedWithGoogle(false);
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_Google:
                googleSignIn();
                break;

        }
    }
}