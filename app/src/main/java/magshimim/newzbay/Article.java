package magshimim.newzbay;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Date;

public class Article{
    private String subject;
    private String mainHeadline;
    private String secondHeadline;
    private Bitmap picture;
    private boolean pictureIsDawnloaded;
    private String picURL;
    private Date date;
    private String siteName;
    private String url;
    private int numberOfLikes;
    private int numberOfComments;
    private boolean liked;

    public Article(String subject, String mainHeadline, String secondHeadline, String picURL, Date date, String siteName, String url, int numberOfLikes, int numberOfComments, boolean liked, GlobalClass globalClass)
    {
        this.pictureIsDawnloaded = false;
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
//        if(globalClass.getCategoriesHandler().getDownloadedPics().containsKey(picURL))
//        {
//            picture = globalClass.getCategoriesHandler().getDownloadedPics().get(picURL);
//        }
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

    public boolean getLiked()
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

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setMainHeadline(String mainHeadline) {
        this.mainHeadline = mainHeadline;
    }

    public void setSecondHeadline(String secondHeadline) {
        this.secondHeadline = secondHeadline;
    }

    public void setPicture(Bitmap picture) { this.picture = picture; }

    public String getPicURL() {
        return picURL;
    }

    public void setPicURL(String picURL) {
        this.picURL = picURL;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setNumberOfLikes(int numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    public void setNumberOfComments(int numberOfComments) {
        this.numberOfComments = numberOfComments;
    }

    public boolean isLiked() {
        return liked;
    }

    public boolean isPictureIsDawnloaded() {
        return pictureIsDawnloaded;
    }

    public void setPictureIsDawnloaded(boolean pictureIsDawnloaded) {
        this.pictureIsDawnloaded = pictureIsDawnloaded;
    }
}