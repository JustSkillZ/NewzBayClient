package magshimim.newzbay;

import android.app.Application;

public class GlobalClass extends Application {

    private CategoriesHandler categoriesHandler;
    private Communication communication;
    private ErrorHandler errorHandler;
    private PriorityHandler priorityHandler;
    private User user;

    public void initiateClass()
    {
        categoriesHandler= new CategoriesHandler(null, "", "", false);
        errorHandler = new ErrorHandler();
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

    public void setCategoriesHandler(CategoriesHandler categoriesHandler) {
        this.categoriesHandler = categoriesHandler;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
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

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public PriorityHandler getPriorityHandler() {
        return priorityHandler;
    }

    public void setPriorityHandler(PriorityHandler priorityHandler) {
        this.priorityHandler = priorityHandler;
    }
}
