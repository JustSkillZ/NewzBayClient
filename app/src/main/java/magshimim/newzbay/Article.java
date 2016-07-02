package magshimim.newzbay;

import android.graphics.Bitmap;

import java.util.Date;

public class Article
{
    private String subject;
    private String mainHeadline;
    private String secondHeadline;
    private Bitmap picture;
    private String picURL;
    private Date date;
    private String siteName;
    private String url;
    private int numberOfLikes;
    private int numberOfComments;
    private boolean liked;

    public Article(String subject, String mainHeadline, String secondHeadline, String picURL, Date date, String siteName, String url, int numberOfLikes, int numberOfComments, boolean liked, GlobalClass globalClass)
    {
        this.subject = subject;
        this.mainHeadline = mainHeadline;
        this.secondHeadline = secondHeadline;
        this.picURL = picURL;
        this.date = date;
        this.siteName = siteName;
        this.picture = globalClass.getCategoriesHandler().getSiteLogo().get(getSiteName());
        this.url = url;
        this.numberOfLikes = numberOfLikes;
        this.numberOfComments = numberOfComments;
        this.liked = liked;
    }

    public String getMainHeadline()
    {
        return this.mainHeadline;
    }

    public String getSecondHeadline()
    {
        return this.secondHeadline;
    }

    public Bitmap getPicture()
    {
        return this.picture;
    }

    public Date getDate()
    {
        return this.date;
    }

    public String getSiteName()
    {
        return this.siteName;
    }

    public String getUrl()
    {
        return this.url;
    }

    public int getNumberOfLikes()
    {
        return this.numberOfLikes;
    }

    public int getNumberOfComments()
    {
        return this.numberOfComments;
    }

    public boolean isLiked()
    {
        return this.liked;
    }

    public void setLiked(boolean liked)
    {
        this.liked = liked;
    }

    public void incNumberOfLikes()
    {
        numberOfLikes++;
    }

    public void decNumberOfLikes()
    {
        numberOfLikes--;
    }

    public void incNumberOfComments()
    {
        numberOfComments++;
    }

    public void decNumberOfComments()
    {
        numberOfComments--;
    }

    public String getPicURL()
    {
        return picURL;
    }

    public String getSubject()
    {
        return subject;
    }
}