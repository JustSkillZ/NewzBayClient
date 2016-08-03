package magshimim.newzbay;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.ads.AdRequest;

public class GlobalClass extends Application
{
    private CategoriesHandler categoriesHandler;
    private CommentsHandler commentsHandler;
    private Communication communication;
    private PriorityHandler priorityHandler;
    private User user;
    private Context currentActivity;

    public void initiateClass() //Init the fields of the class
    {
        communication = new Communication(this);
        categoriesHandler = new CategoriesHandler(null, "", "", false, this);
        commentsHandler = new CommentsHandler();
        user = null;
        priorityHandler = new PriorityHandler();
    }

    public void endClass() //Reset the fields of the class
    {
        categoriesHandler = null;
        user = null;
        priorityHandler = null;
        communication = null;
        commentsHandler = null;
    }

    public CategoriesHandler getCategoriesHandler()
    {

        return categoriesHandler;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Communication getCommunication()
    {
        return communication;
    }

    public PriorityHandler getPriorityHandler()
    {
        return priorityHandler;
    }

    public Context getCurrentActivity()
    {
        return currentActivity;
    }

    public void setCurrentActivity(Context currentActivity)
    {
        this.currentActivity = currentActivity;
    }

    public CommentsHandler getCommentsHandler()
    {
        return commentsHandler;
    }
}
