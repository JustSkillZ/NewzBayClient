package magshimim.newzbay;

import com.facebook.Profile;

public class FacebookUser extends User
{

    private Profile facebookProfile;
    private String facebookUserEmail;

    public FacebookUser(String fullName, String picURL, Profile facebookProfile, String facebookUserEmail)
    {
        super(fullName, picURL, "Facebook");
        this.facebookProfile = facebookProfile;
        this.facebookUserEmail = facebookUserEmail;
    }

    public Profile getFacebookProfile()
    {
        return facebookProfile;
    }

    public String getFacebookUserEmail()
    {
        return facebookUserEmail;
    }

    public void setFacebookUserEmail(String facebookUserEmail)
    {
        this.facebookUserEmail = facebookUserEmail;
    }
}
