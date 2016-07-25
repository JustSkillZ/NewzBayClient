package magshimim.newzbay;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthenticationSend
{
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("picURL")
    @Expose
    private String picURL;
    @SerializedName("name")
    @Expose
    private String name;

    public AuthenticationSend(String email, String picURL, String name)
    {
        this.email = email;
        this.picURL = picURL;
        this.name = name;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPicURL()
    {
        return picURL;
    }

    public void setPicURL(String picURL)
    {
        this.picURL = picURL;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}