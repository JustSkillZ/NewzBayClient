package magshimim.newzbay;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageButton;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class User {
    private String fullName;
    private String picURL;
    private Bitmap profilePic;
    private String connectedVia;
    private GlobalClass globalClass;

    public User(String fullName, String picURL, Bitmap profilePic, String connectedVia, GlobalClass globalClass)
    {
        this.fullName = fullName;
        this.picURL = picURL;
        this.profilePic = profilePic;
        this.connectedVia = connectedVia;
        this.globalClass = globalClass;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPicURL() {
        return picURL;
    }

    public void setPicURL(String picURL) {
        this.picURL = picURL;
    }

    public Bitmap getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Bitmap profilePic) {
        this.profilePic = profilePic;
    }

    public String getConnectedVia() {
        return connectedVia;
    }

    public void setConnectedVia(String connectedVia) {
        this.connectedVia = connectedVia;
    }
}
