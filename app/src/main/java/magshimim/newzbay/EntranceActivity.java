package magshimim.newzbay;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.google.android.gms.plus.Plus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class EntranceActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private LoginButton facebookLoginButton;
    private SignInButton googleSignInButton;
    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private GoogleApiAvailability mGoogleApiAvailability;
    private ConnectionResult mConnectionResult;
    private int requestCode;
    private ProgressDialog mProgressDialog;
    private static final String TAG = "check";
    private Communication communication;
    private static final int SIGN_IN_CODE = 0;
    private boolean isIntentInprogress;
    private boolean isSignInBtnClicked;
    private GlobalClass globalClass;
    private User user;

    private static final String prefsConnection = "magshimim.newzbay.ConnectionPrefs";
    private static final String facebookEmail = "facebookEmail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((GlobalClass) getApplicationContext()).initiateClass(getResources());
        globalClass = ((GlobalClass) getApplicationContext());
        globalClass.setCurrentActivity(EntranceActivity.this);
        globalClass.setCurrentLayout(R.id.entrance_layout);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        globalClass.getErrorHandler().setPopupWindowViewNoConnectionWithServer(inflater.inflate(R.layout.popup_no_connection, null, false));
        globalClass.getErrorHandler().handleNoConnectionToTheServer();
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_entrance);

        Button guestLoginBtn = (Button) findViewById(R.id.btn_NB);
        guestLoginBtn.setBackground(getResources().getDrawable(R.drawable.disabled_button_rounded_corners));
        guestLoginBtn.setTextColor(getResources().getColor(R.color.grey));
        guestLoginBtn.setAlpha((float) 0.1);
        guestLoginBtn.setEnabled(false);
        Drawable logo = getResources().getDrawable(R.drawable.anchor_logo);
        logo.setBounds(0, 0, (int)(logo.getIntrinsicWidth()*0.5),(int)(logo.getIntrinsicHeight()*0.5));
        guestLoginBtn.setCompoundDrawables(logo, null, null, null);

        LoginButton facebookLoginBtn = (LoginButton) findViewById(R.id.btn_Facebook);
        facebookLoginBtn.setBackground(getResources().getDrawable(R.drawable.disabled_button_rounded_corners));
        facebookLoginBtn.setTextColor(getResources().getColor(R.color.grey));
        facebookLoginBtn.setAlpha((float) 0.1);
        facebookLoginBtn.setEnabled(false);

        SignInButton googleLoginBtn = (SignInButton) findViewById(R.id.btn_Google);
        googleLoginBtn.setEnabled(false);


        Time now = new Time();
        now.setToNow();
        if (now.hour >= 19 || now.hour >= 0 && now.hour <= 5) {
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.entrance_layout);
            layout.setBackground(getResources().getDrawable(R.drawable.main_background_night));
            TextView newzBay = (TextView) findViewById(R.id.tv_newzbay);
            newzBay.setTextColor(getResources().getColor(R.color.white));
            TextView slogen = (TextView) findViewById(R.id.tv_slogen);
            slogen.setTextColor(getResources().getColor(R.color.white));
        }

        communication = new Communication((GlobalClass) getApplicationContext());
        globalClass.setCommunication(communication);
        Thread t = new Thread(communication);
        t.start();

//        Intent priority = new Intent(this,Priority.class);
//        startActivity(priority);
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
        user = new User("Guest", "", "Guest");
        globalClass.setUser(user);
        String send = "102○guest@guest.com○guest○#";
        globalClass.getErrorHandler().setConnectingClientMsg(send);
        globalClass.getCommunication().clientSend(send);
        moveToNewsFeed();
    }

    public void moveToNewsFeed() {
        if(user.getConnectedVia().equals("Google"))
        {
            String send = "102○" + Plus.AccountApi.getAccountName(mGoogleApiClient) + "○" + ((GoogleUser) user).getGoogleProfile().getName().getGivenName() + "○" + user.getPicURL() + "#";
            globalClass.getErrorHandler().setConnectingClientMsg(send);
            globalClass.getCommunication().clientSend(send);
            Log.d("Server", "102○" + Plus.AccountApi.getAccountName(mGoogleApiClient) + "○" + ((GoogleUser) user).getGoogleProfile().getName().getGivenName() + "○" + user.getPicURL() + "#");
        }
        Intent nfScreen = new Intent(this, NewsFeedActivity.class);
        startActivity(nfScreen);
        finish();
    }

    public void facebookLogin() {
        facebookLoginButton = (LoginButton) findViewById(R.id.btn_Facebook);
        facebookLoginButton.setReadPermissions(Arrays.asList("email", "user_friends"));
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            private ProfileTracker mProfileTracker;

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook login");
                final GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());
                                try {
                                    String email = object.getString("email");
                                    ((FacebookUser)user).setFacebookUserEmail(email);
                                    String send = "102○" + ((FacebookUser) user).getFacebookUserEmail() + "○" + ((FacebookUser) user).getFacebookProfile().getFirstName() + "○" + user.getPicURL() + "#";
                                    globalClass.getErrorHandler().setConnectingClientMsg(send);
                                    globalClass.getCommunication().clientSend(send);
                                    SharedPreferences sharedpreferences = getSharedPreferences(prefsConnection, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString(facebookEmail, email);
                                    editor.commit();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                if(Profile.getCurrentProfile() == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                            user = new FacebookUser(Profile.getCurrentProfile().getName(), Profile.getCurrentProfile().getProfilePictureUri(500, 500).toString(), Profile.getCurrentProfile(), "");
                            globalClass.setUser(user);
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,email,gender, birthday");
                            request.setParameters(parameters);
                            request.executeAsync();
                            moveToNewsFeed();
                            mProfileTracker.stopTracking();
                        }
                    };
                    mProfileTracker.startTracking();
                }
                else
                {
                    user = new FacebookUser(Profile.getCurrentProfile().getName(), Profile.getCurrentProfile().getProfilePictureUri(500, 500).toString(), Profile.getCurrentProfile(), "");
                    globalClass.setUser(user);
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email,gender, birthday");
                    request.setParameters(parameters);
                    request.executeAsync();
                    moveToNewsFeed();
                }
            }

            @Override
            public void onCancel() {
                globalClass.setUser(null);
            }

            @Override
            public void onError(FacebookException e) {
                globalClass.setUser(null);
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

        googleSignInButton = (SignInButton) findViewById(R.id.btn_Google);
        googleSignInButton.setSize(SignInButton.SIZE_STANDARD);
        googleSignInButton.setColorScheme(SignInButton.COLOR_DARK);
        googleSignInButton.setScopes(new Scope[]{Plus.SCOPE_PLUS_LOGIN});
        setGooglePlusButtonText(googleSignInButton);

    }

    /*
      Set on click Listeners on the sign-in sign-out and disconnect buttons
     */

    private void setBtnClickListeners(){
        // Button listeners
        googleSignInButton.setOnClickListener(this);
    }

    protected void onStart() {
        super.onStart();
//        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            mGoogleApiAvailability.getErrorDialog(this, result.getErrorCode(), requestCode).show();
            return;
        }

        if (!isIntentInprogress) {

            mConnectionResult = result;

            if (isSignInBtnClicked) {

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
            this.requestCode = requestCode;
            if (responseCode != RESULT_OK) {
                isSignInBtnClicked = false;
                mProgressDialog.dismiss();

            }
            isIntentInprogress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        isSignInBtnClicked = false;
        // Get user's information and set it into the layout
        user = new GoogleUser(Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getDisplayName(), Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getImage().getUrl(), Plus.PeopleApi.getCurrentPerson(mGoogleApiClient), mGoogleApiClient);
        globalClass.setUser(user);
        // Update the UI after signin
        moveToNewsFeed();

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
        user = new GoogleUser(Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getDisplayName(), Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getImage().getUrl(), Plus.PeopleApi.getCurrentPerson(mGoogleApiClient), mGoogleApiClient);
        globalClass.setUser(user);
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
            isSignInBtnClicked = true;
//            mProgressDialog.show();
            resolveSignInError();

        }
    }

    /*
      Method to resolve any signin errors
     */

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                isIntentInprogress = true;
                mConnectionResult.startResolutionForResult(this, SIGN_IN_CODE);
                Log.d("resolve error", "sign in error resolved");
            } catch (IntentSender.SendIntentException e) {
                isIntentInprogress = false;
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

    public void connectToSocialNets()
    {
        facebookLogin();
        if (Profile.getCurrentProfile() != null) {
            Log.d(TAG, "facebook login");
            SharedPreferences sharedpreferences = getSharedPreferences(prefsConnection, Context.MODE_PRIVATE);
            user = new FacebookUser(Profile.getCurrentProfile().getName(), Profile.getCurrentProfile().getProfilePictureUri(500, 500).toString(), Profile.getCurrentProfile(), sharedpreferences.getString(facebookEmail, ""));
            globalClass.setUser(user);
            String send = "102○" + ((FacebookUser) user).getFacebookUserEmail() + "○" + ((FacebookUser) user).getFacebookProfile().getFirstName() + "○" + user.getPicURL() + "#";
            globalClass.getErrorHandler().setConnectingClientMsg(send);
            globalClass.getCommunication().clientSend(send);
            moveToNewsFeed();
        }
        else
        {
            buildNewGoogleApiClient();
            customizeSignBtn();
            setBtnClickListeners();
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading...");
            mGoogleApiClient.connect();
        }
    }
}