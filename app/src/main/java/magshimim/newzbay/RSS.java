package magshimim.newzbay;

public class RSS
{
    private String id;
    private String subject;
    private String site;

    public RSS(String id, String subject, String site)
    {
        this.id = id;
        this.subject = subject;
        this.site = site;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getSubject()
    {
        return subject;
    }

    public String getSite()
    {
        return site;
    }
}
