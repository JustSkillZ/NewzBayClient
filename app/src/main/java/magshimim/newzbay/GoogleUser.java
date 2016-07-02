package magshimim.newzbay;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.model.people.Person;

public class GoogleUser extends User
{
    private Person googleProfile;
    private GoogleApiClient googleApiClient;

    public GoogleUser(String fullName, String picURL, Person googleProfile, GoogleApiClient googleApiClient)
    {
        super(fullName, picURL, "Google");
        this.googleProfile = googleProfile;
        this.googleApiClient = googleApiClient;
    }

    public Person getGoogleProfile()
    {
        return googleProfile;
    }

    public GoogleApiClient getGoogleApiClient()
    {
        return googleApiClient;
    }
}
