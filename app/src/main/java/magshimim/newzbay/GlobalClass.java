package magshimim.newzbay;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

public class GlobalClass extends Application
{
    private CategoriesHandler categoriesHandler;
    private CommentsHandler commentsHandler;
    private NewCommunication newCommunication;
    private ErrorHandler errorHandler;
    private PriorityHandler priorityHandler;
    private User user;
    private Resources resources;
    private Context currentActivity;
    private int currentLayout;

    public void initiateClass(Resources resources) //Init the fields of the class
    {
        this.resources = resources;
        newCommunication = new NewCommunication();
        categoriesHandler = new CategoriesHandler(null, "", "", false, this);
        commentsHandler = new CommentsHandler();
        errorHandler = new ErrorHandler(this);
        user = null;
        priorityHandler = new PriorityHandler();
    }

    public void endClass() //Reset the fields of the class
    {
        categoriesHandler = null;
        errorHandler = null;
        user = null;
        priorityHandler = null;
        newCommunication = null;
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

    public NewCommunication getNewCommunication()
    {
        return newCommunication;
    }

    public void setNewCommunication(NewCommunication newCommunication)
    {
        this.newCommunication = newCommunication;
    }

    public ErrorHandler getErrorHandler()
    {
        return errorHandler;
    }

    public PriorityHandler getPriorityHandler()
    {
        return priorityHandler;
    }

    public Resources getAppResources()
    {
        return resources;
    }

    public Context getCurrentActivity()
    {
        return currentActivity;
    }

    public void setCurrentActivity(Context currentActivity)
    {
        this.currentActivity = currentActivity;
    }

    public int getCurrentLayout()
    {
        return currentLayout;
    }

    public void setCurrentLayout(int currentLayout)
    {
        this.currentLayout = currentLayout;
    }

    public CommentsHandler getCommentsHandler()
    {
        return commentsHandler;
    }
}
