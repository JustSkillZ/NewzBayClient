package magshimim.newzbay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.facebook.Profile;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.model.people.Person;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.Vector;

public class FacebookAndGoogle {
    private static boolean loggedWithFacebook = false;
    private static boolean loggedWithGoogle = false;
    private static Profile currentFacebookProfile = null;
    private static Person currentGoogleProfile = null;
    private static GoogleApiClient mGoogleApiClient = null;
    private static Bitmap profilePic = null;
    private static String picURL = "";
    private static String fullName = "Guest";
    private static String facebookUserEmail = "";
    private static Context appContext;

    private static Communication communication = null;

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
        facebookUserEmail = "";
        communication = null;
        Categories.reset();
    }

    public static GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    public static void setmGoogleApiClient(GoogleApiClient mGoogleApiClient) {
        FacebookAndGoogle.mGoogleApiClient = mGoogleApiClient;
    }

    public static Person getCurrentGoogleProfile() {
        return currentGoogleProfile;
    }

    public static void setCurrentGoogleProfile(Person currentGoogleProfile) {
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

    public static String getPicURL() {
        return picURL;
    }

    public static void setPicURL(String picURL) {
        FacebookAndGoogle.picURL = picURL;
    }

    public static String getFacebookUserEmail() {
        return facebookUserEmail;
    }

    public static void setFacebookUserEmail(String facebookUserEmail) {
        FacebookAndGoogle.facebookUserEmail = facebookUserEmail;
    }

    public static Communication getCommunication() {
        return communication;
    }

    public static void setCommunication(Communication communication) {
        FacebookAndGoogle.communication = communication;
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static void setAppContext(Context appContext) {
        FacebookAndGoogle.appContext = appContext;
    }
}
