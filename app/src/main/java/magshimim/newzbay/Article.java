package magshimim.newzbay;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.util.Date;

public class Article {
    private String subject;
    private String mainHeadline;
    private String secondHeadline;
    private Drawable picture;
    private Date date;
    private String siteName;
    private String url;

    public Article(String subject, String mainHeadline, String secondHeadline, Drawable picture, Date date, String siteName, String url)
    {
        this.subject = subject;
        this.mainHeadline = mainHeadline;
        this.secondHeadline = secondHeadline;
        this.picture = picture;
        this.date = date;
        this.siteName = siteName;
        this.url = url;
    }

    public String getSubject()
    {
        return this.subject;
    }

    public String getMainHeadline()
    {
        return this.mainHeadline;
    }

    public String getSecondHeadline()
    {
        return this.secondHeadline;
    }

    public Drawable getPicture()
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
}