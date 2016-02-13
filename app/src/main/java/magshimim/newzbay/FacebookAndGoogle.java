package magshimim.newzbay;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

public class FacebookAndGoogle {
    private static boolean loggedWithFacebook = false;
    private static boolean loggedWithGoogle = false;
    private static Profile currentFacebookProfile = null;
    private static GoogleSignInAccount currentGoogleProfile = null;
    private static GoogleApiClient mGoogleApiClient = null;
    private static Bitmap profilePic = null;
    private static String picURL = "";
    private static String fullName = "Guest";

    public static void reset(Bitmap bm)
    {
        loggedWithFacebook = false;
        loggedWithGoogle = false;
        currentFacebookProfile = null;
        currentGoogleProfile = null;
        mGoogleApiClient = null;
        profilePic = bm;
        picURL = "";
        Random r = new Random();
        fullName = "Guest" + Integer.toString(r.nextInt(999999999));
    }

    public static GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    public static void setmGoogleApiClient(GoogleApiClient mGoogleApiClient) {
        FacebookAndGoogle.mGoogleApiClient = mGoogleApiClient;
    }

    public static GoogleSignInAccount getCurrentGoogleProfile() {
        return currentGoogleProfile;
    }

    public static void setCurrentGoogleProfile(GoogleSignInAccount currentGoogleProfile) {
        FacebookAndGoogle.currentGoogleProfile = currentGoogleProfile;
    }

    public static String getFullName() {
        return fullName;
    }

    public static void setFullName(String fullName) {
        FacebookAndGoogle.fullName = fullName;
    }

    public static Bitmap getProfilePic() {
        return profilePic;
    }

    public static void setProfilePic(Bitmap profilePic) {
        FacebookAndGoogle.profilePic = profilePic;
    }

    public static boolean isLoggedWithFacebook()
    {
        return loggedWithFacebook;
    }

    public static boolean isLoggedWithGoogle()
    {
        return loggedWithGoogle;
    }

    public static void setLoggedWithFacebook(boolean loggedWithFacebook1)
    {
        loggedWithFacebook = loggedWithFacebook1;
    }

    public static void setLoggedWithGoogle(boolean loggedWithGoogle1)
    {
        loggedWithGoogle = loggedWithGoogle1;
    }

    public static Profile getCurrentFacebookProfile() {
        return currentFacebookProfile;
    }

    public static void setCurrentFacebookProfile(Profile currentFacebookProfile1)
    {
        currentFacebookProfile = currentFacebookProfile1;
    }
    public static Bitmap getBitmapFromURL(String src) {
        picURL = src;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(picURL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    profilePic = BitmapFactory.decodeStream(input);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread t = new Thread(runnable);
        t.start();
        return profilePic;
    }
}
