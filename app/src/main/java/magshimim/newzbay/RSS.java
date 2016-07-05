package magshimim.newzbay;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RSS
{
    @SerializedName("ID")
    @Expose
    private String id;
    @SerializedName("subject")
    @Expose
    private String subject;
    @SerializedName("website")
    @Expose
    private String website;

    public RSS(String ID, String subject, String site)
    {
        this.id = ID;
        this.subject = subject;
        this.website = site;
    }

    public String getID()
    {
        return id;
    }

    public void setID(String id)
    {
        this.id = id;
    }

    public String getSubject()
    {
        return subject;
    }

    public String getWebsite()
    {
        return website;
    }
}
