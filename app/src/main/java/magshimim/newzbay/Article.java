package magshimim.newzbay;

import android.graphics.Bitmap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Article
{
    private String subject;
    private String webSite;
    private String mainHeadline;
    private String secondHeadline;
    private Bitmap picture;
    private String picURL;
    private Date date;
    private String url;
    private int numberOfLikes;
    private int numberOfComments;
    private boolean liked;

    public Article(String subject, String webSite, String mainHeadline, String secondHeadline, String picURL, Date date, String url, int numberOfLikes, int numberOfComments, boolean liked, GlobalClass globalClass)
    {
        this.subject = subject;
        this.webSite = webSite;
        this.mainHeadline = mainHeadline;
        this.secondHeadline = secondHeadline;
        this.picURL = picURL;
        this.date = date;
        this.picture = globalClass.getCategoriesHandler().getSiteLogo().get(webSite);
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

    public String getWebSite()
    {
        return webSite;
    }
}

class JsonRecievedArticles
{
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("articles")
    @Expose
    private List<JsonArticle> articles = new ArrayList<JsonArticle>();
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public List<JsonArticle> getArticles()
    {
        return articles;
    }

    public void setArticles(List<JsonArticle> articles)
    {
        this.articles = articles;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}

class JsonArticle
{
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("mainHeadline")
    @Expose
    private String mainHeadline;
    @SerializedName("secondHeadline")
    @Expose
    private String secondHeadline;
    @SerializedName("picURL")
    @Expose
    private String picURL;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("numberOfLikes")
    @Expose
    private int numberOfLikes;
    @SerializedName("numberOfComments")
    @Expose
    private int numberOfComments;
    @SerializedName("ID")
    @Expose
    private String id;
    @SerializedName("liked")
    @Expose
    private boolean liked;

    public String getID()
    {
        return id;
    }

    public void setID(String id)
    {
        this.id = id;
    }

    public String getMainHeadline()
    {
        return mainHeadline;
    }

    public void setMainHeadline(String mainHeadline)
    {
        this.mainHeadline = mainHeadline;
    }

    public String getSecondHeadline()
    {
        return secondHeadline;
    }

    public void setSecondHeadline(String secondHeadline)
    {
        this.secondHeadline = secondHeadline;
    }

    public String getPicURL()
    {
        return picURL;
    }

    public void setPicURL(String picURL)
    {
        this.picURL = picURL;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public int getNumberOfLikes()
    {
        return numberOfLikes;
    }

    public void setNumberOfLikes(int numberOfLikes)
    {
        this.numberOfLikes = numberOfLikes;
    }

    public int getNumberOfComments()
    {
        return numberOfComments;
    }

    public void setNumberOfComments(int numberOfComments)
    {
        this.numberOfComments = numberOfComments;
    }

    public boolean isLiked()
    {
        return liked;
    }

    public void setLiked(boolean liked)
    {
        this.liked = liked;
    }
}