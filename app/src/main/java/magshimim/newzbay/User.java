package magshimim.newzbay;

public class User {
    private String fullName;
    private String picURL;
    private String connectedVia;

    public User(String fullName, String picURL, String connectedVia)
    {
        this.fullName = fullName;
        this.picURL = picURL;
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

    public String getConnectedVia() {
        return connectedVia;
    }
}
