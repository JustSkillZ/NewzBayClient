package magshimim.newzbay;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ListAdapter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class CategoriesHandler {
    private Vector<Article> currentlyInUse;
    private String currentlyInUseCategory;
    private String currentlyInUseCategoryServer;
    private int currentCategoryID;
    private String currentlyOpenURL;

    //-----------------Priority---------------------
    private Vector<String> idOfRSS;
    private Vector<String> subject;
    private Vector<String> site;
    //----------------------------------------------

    private HashMap<String, Bitmap> downloadedPics = new HashMap<>();
    private boolean loadingArticles = false;
    private List<String> categoriesForServer = Arrays.asList("", "israelNewz", "worldNewz", "politics", "economy", "sport", "culture", "celebs", "technology");
    private ListAdapter listAdapter;
    private Activity newsfeed;

    public CategoriesHandler(Vector<Article> currentlyInUse, String currentlyInUseCategory, String currentlyInUseCategoryServer, boolean loadingArticles)
    {
        if(currentlyInUse == null)
        {
            this.currentlyInUse = new Vector<Article>();
        }
        else
        {
            this.currentlyInUse = currentlyInUse;
        }
        this.currentlyInUseCategory = currentlyInUseCategory;
        this.currentlyInUseCategoryServer = currentlyInUseCategoryServer;
        this.loadingArticles = loadingArticles;
    }

    public String getCurrentlyOpenURL() {
        return currentlyOpenURL;
    }

    public void setCurrentlyOpenURL(String currentlyOpenURL) {
        this.currentlyOpenURL = currentlyOpenURL;
    }

    public void setCurrentlyInUseCategory(String currentlyInUseCategory) {
        this.currentlyInUseCategory = currentlyInUseCategory;
    }

    public int getCurrentCategoryID() {
        return currentCategoryID;
    }

    public void setCurrentCategoryID(int currentCategoryID) {
        this.currentCategoryID = currentCategoryID;
    }

    public String getCurrentlyInUseCategory(User user) {
        if (currentlyInUseCategory.equals(""))
        {
            if(user.getConnectedVia() == "Google")
            {
                switch(((GoogleUser) user).getGoogleProfile().getGender())
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

    public void setCurrentlyInUseCategory(int categoryID, Context context) {
        currentCategoryID = categoryID;
        switch (categoryID)
        {
            case 0:
                currentlyInUseCategory = context.getResources().getString(R.string.hotNews);
                break;
            case 1:
                currentlyInUseCategory = context.getResources().getString(R.string.news);
                currentlyInUseCategoryServer = "israelNewz";
                break;
            case 2:
                currentlyInUseCategory = context.getResources().getString(R.string.globalNews);
                currentlyInUseCategoryServer = "worldNewz";
                break;
            case 3:
                currentlyInUseCategory = context.getResources().getString(R.string.politics);
                currentlyInUseCategoryServer = "politics";
                break;
            case 4:
                currentlyInUseCategory = context.getResources().getString(R.string.economy);
                currentlyInUseCategoryServer = "economy";
                break;
            case 5:
                currentlyInUseCategory = context.getResources().getString(R.string.sport);
                currentlyInUseCategoryServer = "sport";
                break;
            case 6:
                currentlyInUseCategory = context.getResources().getString(R.string.culture);
                currentlyInUseCategoryServer = "culture";
                break;
            case 7:
                currentlyInUseCategory = context.getResources().getString(R.string.celebrities);
                currentlyInUseCategoryServer = "celebs";
                break;
            case 8:
                currentlyInUseCategory = context.getResources().getString(R.string.technology);
                currentlyInUseCategoryServer = "technology";
                break;
            case 9:
                currentlyInUseCategory = context.getResources().getString(R.string.science);
                currentlyInUseCategoryServer = "science";
                break;
        }

    }

    public Vector<Article> getCurrentlyInUse() {
        return currentlyInUse;
    }

    public void setCurrentlyInUse(Vector<Article> currentlyInUse) {
        this.currentlyInUse = currentlyInUse;
    }

    public Vector<String> getIdOfRSS() {
        return idOfRSS;
    }

    public void setIdOfRSS(Vector<String> idOfRSS) {
        this.idOfRSS = idOfRSS;
    }

    public Vector<String> getSubject() {
        return subject;
    }

    public void setSubject(Vector<String> subject) {
        this.subject = subject;
    }

    public Vector<String> getSite() {
        return site;
    }

    public void setSite(Vector<String> site) {
        this.site = site;
    }

    public String getCurrentlyInUseCategoryServer() {
        return currentlyInUseCategoryServer;
    }

    public void setCurrentlyInUseCategoryServer(String currentlyInUseCategoryServer) {
        this.currentlyInUseCategoryServer = currentlyInUseCategoryServer;
    }

    public HashMap<String, Bitmap> getDownloadedPics() {
        return downloadedPics;
    }

    public void setDownloadedPics(HashMap<String, Bitmap> downloadedPics) {
        this.downloadedPics = downloadedPics;
    }

    public boolean isLoading() {
        return loadingArticles;
    }

    public void setLoading(boolean loading) {
        this.loadingArticles = loading;
    }

    public String getCurrentlyInUseCategory() {
        return currentlyInUseCategory;
    }

    public List<String> getCategoriesForServer() {
        return categoriesForServer;
    }

    public void setCategoriesForServer(List<String> categoriesForServer) {
        this.categoriesForServer = categoriesForServer;
    }

    public ListAdapter getListAdapter() {
        return listAdapter;
    }

    public void setListAdapter(ListAdapter listAdapter) {
        this.listAdapter = listAdapter;
    }

    public Activity getNewsfeed() {
        return newsfeed;
    }

    public void setNewsfeed(Activity newsfeed) {
        this.newsfeed = newsfeed;
    }
}
