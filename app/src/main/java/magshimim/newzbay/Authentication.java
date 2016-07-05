package magshimim.newzbay;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class AuthenticationRecieve
{

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("token")
    @Expose
    private String token;


    public Integer getStatus()
    {
        return status;
    }


    public void setStatus(Integer status)
    {
        this.status = status;
    }


    public String getMessage()
    {
        return message;
    }


    public void setMessage(String message)
    {
        this.message = message;
    }


    public String getToken()
    {
        return token;
    }


    public void setToken(String token)
    {
        this.token = token;
    }

}

class AuthenticationSend
{
    private String email;
    private String picURL;
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
