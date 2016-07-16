package magshimim.newzbay;

import com.facebook.Profile;

public class FacebookUser extends User
{

    private Profile facebookProfile;

    public FacebookUser(String fullName, String picURL, Profile facebookProfile, String email)
    {
        super(fullName, picURL, "Facebook", email);
        this.facebookProfile = facebookProfile;
    }

    public Profile getFacebookProfile()
    {
        return facebookProfile;
    }
}
