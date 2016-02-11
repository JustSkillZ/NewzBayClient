package magshimim.newzbay;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class FacebookAndGoogle {
    public static boolean loggedWithFacebook;
    public static boolean loggedWithGoogle;
    public static Profile currentFacebookProfile = null;

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

    public static void setCurrentFacebookProfile(Profile currentFacebookProfile1)
    {
        currentFacebookProfile = currentFacebookProfile1;
    }
    public static Bitmap getBitmapFromURL(String src)
    {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
