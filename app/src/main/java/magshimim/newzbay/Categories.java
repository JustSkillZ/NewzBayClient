package magshimim.newzbay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.ArraySet;

import com.facebook.Profile;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class Categories {
    private static Vector<Article> hotNews; //ID = 0
    private static Vector<Article> news; //ID = 1
    private static Vector<Article> globalNews; //ID = 2
    private static Vector<Article> politics; //ID = 3
    private static Vector<Article> economy; //ID = 4
    private static Vector<Article> sport; //ID = 5
    private static Vector<Article> culture; //ID = 6
    private static Vector<Article> celebrities; //ID = 7
    private static Vector<Article> technology; //ID = 8
    private static Vector<Article> science; //ID = 9
    private static Vector<Article> currentlyInUse = new Vector<Article>();
    private static String currentlyInUseCategory = "";
    private static String currentlyInUseCategoryServer = "";
    private static int CurrentCategoryID;
    private static String currentlyOpenURL;

    //-----------------Priority---------------------
    private static Vector<String> idOfRSS;
    private static Vector<String> subject;
    private static Vector<String> site;
    //----------------------------------------------

    private static HashMap<String, Bitmap> downloadedPics = new HashMap<>();


    public static String getCurrentlyOpenURL() {
        return currentlyOpenURL;
    }

    public static void setCurrentlyOpenURL(String currentlyOpenURL) {
        Categories.currentlyOpenURL = currentlyOpenURL;
    }

    public static void setCurrentlyInUseCategory(String currentlyInUseCategory) {
        Categories.currentlyInUseCategory = currentlyInUseCategory;
    }

    public static int getCurrentCategoryID() {
        return CurrentCategoryID;
    }

    public static void setCurrentCategoryID(int currentCategoryID) {
        CurrentCategoryID = currentCategoryID;
    }

    public static String getCurrentlyInUseCategory() {
        if (currentlyInUseCategory.equals(""))
        {
            if(FacebookAndGoogle.isLoggedWithGoogle())
            {
                switch(FacebookAndGoogle.getCurrentGoogleProfile().getGender())
                {
                    case 0:
                        return "ברוך הבא";
                    case 1:
                        return "ברוכה הבאה";
                    case 2:
                        return "ברוך/ה הבא/ה";
                }
            }
            else
            {
                return "ברוך/ה הבא/ה";
            }
        }
        return currentlyInUseCategory;
    }

    public static void setCurrentlyInUseCategory(int categoryID, Context context) {
        CurrentCategoryID = categoryID;
        switch (categoryID)
        {
            case 0:
                currentlyInUseCategory = context.getResources().getString(R.string.hotNews);
                hotNews = currentlyInUse;
                break;
            case 1:
                currentlyInUseCategory = context.getResources().getString(R.string.news);
                currentlyInUseCategoryServer = "israelNewz";
                news = currentlyInUse;
                break;
            case 2:
                currentlyInUseCategory = context.getResources().getString(R.string.globalNews);
                currentlyInUseCategoryServer = "worldNewz";
                globalNews = currentlyInUse;
                break;
            case 3:
                currentlyInUseCategory = context.getResources().getString(R.string.politics);
                currentlyInUseCategoryServer = "politics";
                politics = currentlyInUse;
                break;
            case 4:
                currentlyInUseCategory = context.getResources().getString(R.string.economy);
                currentlyInUseCategoryServer = "economy";
                economy = currentlyInUse;
                break;
            case 5:
                currentlyInUseCategory = context.getResources().getString(R.string.sport);
                currentlyInUseCategoryServer = "sport";
                sport = currentlyInUse;
                break;
            case 6:
                currentlyInUseCategory = context.getResources().getString(R.string.culture);
                currentlyInUseCategoryServer = "culture";
                culture = currentlyInUse;
                break;
            case 7:
                currentlyInUseCategory = context.getResources().getString(R.string.celebrities);
                currentlyInUseCategoryServer = "celebs";
                celebrities = currentlyInUse;
                break;
            case 8:
                currentlyInUseCategory = context.getResources().getString(R.string.technology);
                currentlyInUseCategoryServer = "technology";
                technology = currentlyInUse;
                break;
            case 9:
                currentlyInUseCategory = context.getResources().getString(R.string.science);
                currentlyInUseCategoryServer = "science";
                science = currentlyInUse;
                break;
        }

    }

    public static Vector<Article> getCurrentlyInUse() {
        return currentlyInUse;
    }

    public static void setCurrentlyInUse(Vector<Article> currentlyInUse) {
        Categories.currentlyInUse = currentlyInUse;
    }

    public static Vector<Article> getHotNews() {
        return hotNews;

    }

    public static void setHotNews(Vector<Article> hot_news) {
        Categories.hotNews = hot_news;
    }

    public static Vector<Article> getNews() {
        return news;
    }

    public static void setNews(Vector<Article> news) {
        Categories.news = news;
    }

    public static Vector<Article> getGlobalNews() {
        return globalNews;
    }

    public static void setGlobalNews(Vector<Article> global_news) {
        Categories.globalNews = global_news;
    }

    public static Vector<Article> getPolitics() {
        return politics;
    }

    public static void setPolitics(Vector<Article> politics) {
        Categories.politics = politics;
    }

    public static Vector<Article> getEconomy() {
        return economy;
    }

    public static void setEconomy(Vector<Article> economy) {
        Categories.economy = economy;
    }

    public static Vector<Article> getSport() {
        return sport;
    }

    public static void setSport(Vector<Article> sport) {
        Categories.sport = sport;
    }

    public static Vector<Article> getCulture() {
        return culture;
    }

    public static void setCulture(Vector<Article> culture) {
        Categories.culture = culture;
    }

    public static Vector<Article> getCelebrities() {
        return celebrities;
    }

    public static void setCelebrities(Vector<Article> celebrities) {
        Categories.celebrities = celebrities;
    }

    public static Vector<Article> getTechnology() {
        return technology;
    }

    public static void setTechnology(Vector<Article> technology) {
        Categories.technology = technology;
    }

    public static Vector<Article> getScience() {
        return science;
    }

    public static void setScience(Vector<Article> science) {
        Categories.science = science;
    }

    public static Vector<String> getIdOfRSS() {
        return idOfRSS;
    }

    public static void setIdOfRSS(Vector<String> idOfRSS) {
        Categories.idOfRSS = idOfRSS;
    }

    public static Vector<String> getSubject() {
        return subject;
    }

    public static void setSubject(Vector<String> subject) {
        Categories.subject = subject;
    }

    public static Vector<String> getSite() {
        return site;
    }

    public static void setSite(Vector<String> site) {
        Categories.site = site;
    }

    public static String getCurrentlyInUseCategoryServer() {
        return currentlyInUseCategoryServer;
    }

    public static void setCurrentlyInUseCategoryServer(String currentlyInUseCategoryServer) {
        Categories.currentlyInUseCategoryServer = currentlyInUseCategoryServer;
    }

    public static HashMap<String, Bitmap> getDownloadedPics() {
        return downloadedPics;
    }

    public static void setDownloadedPics(HashMap<String, Bitmap> downloadedPics) {
        Categories.downloadedPics = downloadedPics;
    }
}
