package magshimim.newzbay;

import android.content.Context;
import android.graphics.BitmapFactory;

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
    private static Vector<Article> currentlyInUse;
    private static String currentlyInUseCategory;
    private static int CurrentCategoryID;
    private static String currentlyOpenURL;

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
        return currentlyInUseCategory;
    }

    public static void setCurrentlyInUseCategory(int categoryID, Context context) {
        CurrentCategoryID = categoryID;
        switch (categoryID)
        {
            case 0:
                currentlyInUseCategory = context.getResources().getString(R.string.hotNews);
                currentlyInUse = hotNews;
                break;
            case 1:
                currentlyInUseCategory = context.getResources().getString(R.string.news);
                currentlyInUse = news;
                break;
            case 2:
                currentlyInUseCategory = context.getResources().getString(R.string.globalNews);
                currentlyInUse = globalNews;
                break;
            case 3:
                currentlyInUseCategory = context.getResources().getString(R.string.politics);
                currentlyInUse = politics;
                break;
            case 4:
                currentlyInUseCategory = context.getResources().getString(R.string.economy);
                currentlyInUse = economy;
                break;
            case 5:
                currentlyInUseCategory = context.getResources().getString(R.string.sport);
                currentlyInUse = sport;
                break;
            case 6:
                currentlyInUseCategory = context.getResources().getString(R.string.culture);
                currentlyInUse = culture;
                break;
            case 7:
                currentlyInUseCategory = context.getResources().getString(R.string.celebrities);
                currentlyInUse = celebrities;
                break;
            case 8:
                currentlyInUseCategory = context.getResources().getString(R.string.technology);
                currentlyInUse = technology;
                break;
            case 9:
                currentlyInUseCategory = context.getResources().getString(R.string.science);
                currentlyInUse = science;
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
}
