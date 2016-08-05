package magshimim.newzbay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class CategoriesHandler
{
    private Vector<Article> currentlyInUse; //Articles of current subject
    private Vector<Article> hotNewsArticles;
    private String currentlyInUseCategory; //Category to show
    private String currentlyInUseCategoryServer; //Category to server
    private int currentCategoryID;
    private String currentlyOpenURL; //URL for ActivityInnerWeb

    private HashMap<String, Bitmap> siteLogo; //Map: Key - Site name | Value - Site default pic
    private boolean loadingArticles;
    private List<String> categoriesForServer = Arrays.asList("hotNewz", "israelNewz", "worldNewz", "politics", "economy", "sport", "culture", "celebs", "technology", "science"); //Sites for server
    private Vector<Bitmap> categoryIcon;
    private Vector<Integer> categoryColor;

    private RecyclerView.Adapter articlesRecyclerAdapter; //Article Adapter - notifyDataSetChanged in ClientRead Thread
    private PagerAdapter hotNewsPageAdapter; //Hot News Adapter - notifyDataSetChanged in ClientRead Thread

    public CategoriesHandler(Vector<Article> currentlyInUse, String currentlyInUseCategory, String currentlyInUseCategoryServer, boolean loadingArticles, GlobalClass globalClass)
    {
        if (currentlyInUse == null)
        {
            this.currentlyInUse = new Vector<>();
        }
        if (hotNewsArticles == null)
        {
            this.hotNewsArticles = new Vector<>();
        }
        else
        {
            this.currentlyInUse = currentlyInUse;
        }
        this.currentlyInUseCategory = currentlyInUseCategory;
        this.currentlyInUseCategoryServer = currentlyInUseCategoryServer;
        this.loadingArticles = loadingArticles;
        siteLogo = new HashMap<>(); //Map: Key - Site name | Value - Site default pic
        siteLogo.put("ynet", BitmapFactory.decodeResource(globalClass.getResources(), R.drawable.site_ynet));
        siteLogo.put("one", BitmapFactory.decodeResource(globalClass.getResources(), R.drawable.site_one));
        siteLogo.put("walla", BitmapFactory.decodeResource(globalClass.getResources(), R.drawable.site_walla));
        siteLogo.put("nrg", BitmapFactory.decodeResource(globalClass.getResources(), R.drawable.site_nrg));
        siteLogo.put("mako", BitmapFactory.decodeResource(globalClass.getResources(), R.drawable.site_mako));
        siteLogo.put("nana10", BitmapFactory.decodeResource(globalClass.getResources(), R.drawable.site_nana10));
        siteLogo.put("9TV", BitmapFactory.decodeResource(globalClass.getResources(), R.drawable.site_9tv_new));
        siteLogo.put("calcalist", BitmapFactory.decodeResource(globalClass.getResources(), R.drawable.site_calcalist));
        siteLogo.put("0404", BitmapFactory.decodeResource(globalClass.getResources(), R.drawable.site_0404));
        siteLogo.put("TheMarker", BitmapFactory.decodeResource(globalClass.getResources(), R.drawable.site_themarker_new));
        siteLogo.put("Globes", BitmapFactory.decodeResource(globalClass.getResources(), R.drawable.site_globes));
        siteLogo.put("Haaretz", BitmapFactory.decodeResource(globalClass.getResources(), R.drawable.site_haaretz));
        siteLogo.put("Sport 5", BitmapFactory.decodeResource(globalClass.getResources(), R.drawable.site_sport5));
        siteLogo.put("Israel Hayom", BitmapFactory.decodeResource(globalClass.getResources(), R.drawable.site_israel_hayom));
        siteLogo.put("Rotter", BitmapFactory.decodeResource(globalClass.getResources(), R.drawable.site_rotter));

        categoryIcon = new Vector<>();
        categoryIcon.add(BitmapFactory.decodeResource(globalClass.getResources(), R.drawable.hot_news));
        categoryIcon.add(BitmapFactory.decodeResource(globalClass.getResources(), R.drawable.news_category));
        categoryIcon.add(BitmapFactory.decodeResource(globalClass.getResources(), R.drawable.global_news_category));
        categoryIcon.add(BitmapFactory.decodeResource(globalClass.getResources(), R.drawable.politics_category));
        categoryIcon.add(BitmapFactory.decodeResource(globalClass.getResources(), R.drawable.economy_category));
        categoryIcon.add(BitmapFactory.decodeResource(globalClass.getResources(), R.drawable.sport_category));
        categoryIcon.add(BitmapFactory.decodeResource(globalClass.getResources(), R.drawable.culture_category));
        categoryIcon.add(BitmapFactory.decodeResource(globalClass.getResources(), R.drawable.celebrities_category));
        categoryIcon.add(BitmapFactory.decodeResource(globalClass.getResources(), R.drawable.technology_category));
        categoryIcon.add(BitmapFactory.decodeResource(globalClass.getResources(), R.drawable.science_category));

        categoryColor = new Vector<>();
        categoryColor.add(globalClass.getResources().getColor(R.color.orange));
        categoryColor.add(globalClass.getResources().getColor(R.color.blueCategory));
        categoryColor.add(globalClass.getResources().getColor(R.color.lightGreen));
        categoryColor.add(globalClass.getResources().getColor(R.color.deepOrange));
        categoryColor.add(globalClass.getResources().getColor(R.color.brown));
        categoryColor.add(globalClass.getResources().getColor(R.color.red));
        categoryColor.add(globalClass.getResources().getColor(R.color.purpleCategory));
        categoryColor.add(globalClass.getResources().getColor(R.color.pink));
        categoryColor.add(globalClass.getResources().getColor(R.color.indigo));
        categoryColor.add(globalClass.getResources().getColor(R.color.green));
    }

    public String getCurrentlyOpenURL()
    {
        return currentlyOpenURL;
    }

    public void setCurrentlyOpenURL(String currentlyOpenURL)
    {
        this.currentlyOpenURL = currentlyOpenURL;
    }

    public int getCurrentCategoryID()
    {
        return currentCategoryID;
    }

    public String getCurrentlyInUseCategory(User user)
    {
        if (currentlyInUseCategory.equals("")) //When welcome screen
        {
            if (user.getConnectedVia().equals("Google"))
            {
                switch (((GoogleUser) user).getGoogleProfile().getGender())
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

    public void setCurrentlyInUseCategory(int categoryID, Context context)
    {
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

    public Vector<Article> getCurrentlyInUse()
    {
        return currentlyInUse;
    }

    public String getCurrentlyInUseCategoryServer()
    {
        return currentlyInUseCategoryServer;
    }

    public boolean isLoading()
    {
        return loadingArticles;
    }

    public void setLoading(boolean loading)
    {
        this.loadingArticles = loading;
    }

    public List<String> getCategoriesForServer()
    {
        return categoriesForServer;
    }

    public RecyclerView.Adapter getArticlesRecyclerAdapter()
    {
        return articlesRecyclerAdapter;
    }

    public void setArticlesRecyclerAdapter(RecyclerView.Adapter articlesRecyclerAdapter)
    {
        this.articlesRecyclerAdapter = articlesRecyclerAdapter;
    }

    public HashMap<String, Bitmap> getSiteLogo()
    {
        return siteLogo;
    }

    public PagerAdapter getHotNewsPageAdapter()
    {
        return hotNewsPageAdapter;
    }

    public void setHotNewsPageAdapter(PagerAdapter hotNewsPageAdapter)
    {
        this.hotNewsPageAdapter = hotNewsPageAdapter;
    }

    public void setCurrentlyInUseCategoryServer(String currentlyInUseCategoryServer)
    {
        this.currentlyInUseCategoryServer = currentlyInUseCategoryServer;
    }

    public void setCurrentlyInUseCategory(String currentlyInUseCategory)
    {
        this.currentlyInUseCategory = currentlyInUseCategory;
    }

    public Vector<Article> getHotNewsArticles()
    {
        return hotNewsArticles;
    }

    public Vector<Bitmap> getCategoryIcon()
    {
        return categoryIcon;
    }

    public Vector<Integer> getCategoryColor()
    {
        return categoryColor;
    }
}
