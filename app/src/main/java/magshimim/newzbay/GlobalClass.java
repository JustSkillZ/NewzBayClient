package magshimim.newzbay;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

public class GlobalClass extends Application {

    private CategoriesHandler categoriesHandler;
    private CommentsHandler commentsHandler;
    private Communication communication;
    private ErrorHandler errorHandler;
    private PriorityHandler priorityHandler;
    private User user;
    private Resources resources;
    private Context currentActivity;
    private int currentLayout;

    public void initiateClass(Resources resources)
    {
        this.resources = resources;
        categoriesHandler= new CategoriesHandler(null, "", "", false, this);
        commentsHandler = new CommentsHandler();
        errorHandler = new ErrorHandler(this);
        user = null;
        priorityHandler = new PriorityHandler();
    }

    public void endClass()
    {
        categoriesHandler= null;
        errorHandler = null;
        user = null;
    }

    public CategoriesHandler getCategoriesHandler() {

        return categoriesHandler;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Communication getCommunication() {
        return communication;
    }

    public void setCommunication(Communication communication) {
        this.communication = communication;
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public PriorityHandler getPriorityHandler() {
        return priorityHandler;
    }

    public Resources getAppResources() {
        return resources;
    }

    public Context getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(Context currentActivity) {
        this.currentActivity = currentActivity;
    }

    public int getCurrentLayout() {
        return currentLayout;
    }

    public void setCurrentLayout(int currentLayout) {
        this.currentLayout = currentLayout;
    }

    public CommentsHandler getCommentsHandler() {
        return commentsHandler;
    }
}
