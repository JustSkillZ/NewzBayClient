package magshimim.newzbay;

import android.graphics.Bitmap;

import com.facebook.Profile;

public class FacebookUser extends User{

    private Profile facebookProfile;
    private String facebookUserEmail;

    public FacebookUser(String fullName, String picURL, Bitmap profilePic, Profile facebookProfile, String facebookUserEmail) {
        super(fullName, picURL, profilePic, "Facebook");
        this.facebookProfile = facebookProfile;
        this.facebookUserEmail = facebookUserEmail;
    }

    public Profile getFacebookProfile() {
        return facebookProfile;
    }

    public void setFacebookProfile(Profile facebookProfile) {
        this.facebookProfile = facebookProfile;
    }

    public String getFacebookUserEmail() {
        return facebookUserEmail;
    }

    public void setFacebookUserEmail(String facebookUserEmail) {
        this.facebookUserEmail = facebookUserEmail;
    }
}
