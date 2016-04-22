package magshimim.newzbay;

import java.util.HashMap;
import java.util.Vector;

public class PriorityHandler {
    private Vector<CategorySite> categorySites;
    private String currentPrioritySubject;
    private Vector<String> sitesOfCurrentSubject;
    private Vector<String> clientsPriority;
    private Vector<String> removedSitesOfCurrentSubject;

    public PriorityHandler() {
        categorySites = new Vector<>();
        sitesOfCurrentSubject = new Vector<>();
        removedSitesOfCurrentSubject = new Vector<>();
    }

    private HashMap<String, String> Subjects = new HashMap<String, String>() // Key - subjectToServer, Value - subjectToShow
    {{
            put("israelNewz","חדשות בארץ");
            put("worldNewz","חדשות בעולם");
            put("politics","פוליטיקה");
            put("economy","כלכלה");
            put("sport","ספורט");
            put("culture","תרבות");
            put("celebs","סלבס");
            put("technology","טכנולוגיה");
            put("science","מדע");
        }};

    public HashMap<String, String> getSubjects() {
        return Subjects;
    }

    public void setSubjects(HashMap<String, String> subjects) {
        Subjects = subjects;
    }

    public Vector<CategorySite> getCategorySites() {
        return categorySites;
    }

    public void setCategorySites(Vector<CategorySite> categorySites) {
        this.categorySites = categorySites;
    }

    public String getCurrentPrioritySubject() {
        return currentPrioritySubject;
    }

    public void setCurrentPrioritySubject(String currentPrioritySubject) {
        this.currentPrioritySubject = currentPrioritySubject;
        sitesOfCurrentSubject.clear();
        for(int i = 0; i < categorySites.size(); i++)
        {
            if(categorySites.get(i).getSubject().equals(currentPrioritySubject))
            {
                sitesOfCurrentSubject.add(categorySites.get(i).getSite());
            }
        }
        clientsPriority = new Vector<>(sitesOfCurrentSubject);
    }

    public Vector<String> getSitesOfCurrentSubject() {
        return sitesOfCurrentSubject;
    }

    public void setSitesOfCurrentSubject(Vector<String> sitesOfCurrentSubject) {
        this.sitesOfCurrentSubject = sitesOfCurrentSubject;
    }

    public Vector<String> getClientsPriority() {
        return clientsPriority;
    }

    public void setClientsPriority(Vector<String> clientsPriority) {
        this.clientsPriority = clientsPriority;
    }

    public Vector<String> getRemovedSitesOfCurrentSubject() {
        return removedSitesOfCurrentSubject;
    }

    public void setRemovedSitesOfCurrentSubject(Vector<String> removedSitesOfCurrentSubject) {
        this.removedSitesOfCurrentSubject = removedSitesOfCurrentSubject;
    }
}