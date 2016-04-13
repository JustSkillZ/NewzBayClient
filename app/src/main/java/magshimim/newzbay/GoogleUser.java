package magshimim.newzbay;

import android.graphics.Bitmap;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.model.people.Person;

public class GoogleUser extends User{

    private Person googleProfile;
    private GoogleApiClient mGoogleApiClient;

    public GoogleUser(String fullName, String picURL, Bitmap profilePic, Person googleProfile, GoogleApiClient mGoogleApiClient, GlobalClass globalClass) {
        super(fullName, picURL, profilePic, "Google", globalClass);
        this.googleProfile = googleProfile;
        this.mGoogleApiClient = mGoogleApiClient;
    }

    public Person getGoogleProfile() {
        return googleProfile;
    }

    public void setGoogleProfile(Person googleProfile) {
        this.googleProfile = googleProfile;
    }

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void setmGoogleApiClient(GoogleApiClient mGoogleApiClient) {
        this.mGoogleApiClient = mGoogleApiClient;
    }
}
