package magshimim.newzbay;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class User {
    private String fullName;
    private String picURL;
    private Bitmap profilePic;
    private String connectedVia;

    public User(String fullName, String picURL, Bitmap profilePic, String connectedVia)
    {
        this.fullName = fullName;
        this.picURL = picURL;
        this.profilePic = profilePic;
        this.connectedVia = connectedVia;
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

    public void getBitmapFromURL(String src) {
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
    }

    public String getConnectedVia() {
        return connectedVia;
    }

    public void setConnectedVia(String connectedVia) {
        this.connectedVia = connectedVia;
    }
}
