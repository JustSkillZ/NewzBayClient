package magshimim.newzbay;

import android.app.Application;

public class GlobalClass extends Application {

    private CategoriesHandler categoriesHandler;
    private Communication communication;
    private User user;

    public void initiateClass()
    {
        categoriesHandler= new CategoriesHandler(null, "", "", false);
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
}
