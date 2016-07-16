package magshimim.newzbay;

public class User
{
    private String fullName;
    private String picURL;
    private String connectedVia;
    private String email;

    public User(String fullName, String picURL, String connectedVia, String email)
    {
        this.fullName = fullName;
        this.picURL = picURL;
        this.connectedVia = connectedVia;
        this.email = email;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public String getPicURL()
    {
        return picURL;
    }

    public String getConnectedVia()
    {
        return connectedVia;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }
}
