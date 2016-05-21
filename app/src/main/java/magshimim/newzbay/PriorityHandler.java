package magshimim.newzbay;

import android.support.v7.widget.RecyclerView;

import java.util.HashMap;
import java.util.Vector;

public class PriorityHandler
{
    private Vector<CategorySite> categorySites;
    private String currentPrioritySubject;
    private Vector<String> sitesOfCurrentSubject;
    private Vector<String> clientPriority;
    private Vector<String> removedSitesOfCurrentSubject;
    private RecyclerView.Adapter recyclerAdapter;
    private HashMap<String, String> Subjects = new HashMap<String, String>() // Key - subjectToServer, Value - subjectToShow
    {{
        put("israelNewz", "חדשות בארץ");
        put("worldNewz", "חדשות בעולם");
        put("politics", "פוליטיקה");
        put("economy", "כלכלה");
        put("sport", "ספורט");
        put("culture", "תרבות");
        put("celebs", "סלבס");
        put("technology", "טכנולוגיה");
        put("science", "מדע");
    }};

    public PriorityHandler()
    {
        categorySites = new Vector<>();
        sitesOfCurrentSubject = new Vector<>();
        removedSitesOfCurrentSubject = new Vector<>();
    }

    public HashMap<String, String> getSubjects()
    {
        return Subjects;
    }

    public Vector<CategorySite> getCategorySites()
    {
        return categorySites;
    }

    public String getCurrentPrioritySubject()
    {
        return currentPrioritySubject;
    }

    public void setCurrentPrioritySubject(String currentPrioritySubject)
    {
        this.currentPrioritySubject = currentPrioritySubject;
        sitesOfCurrentSubject.clear();
        for (int i = 0; i < categorySites.size(); i++)
        {
            if (categorySites.get(i).getSubject().equals(currentPrioritySubject))
            {
                sitesOfCurrentSubject.add(categorySites.get(i).getSite());
            }
        }
        clientPriority = new Vector<>(sitesOfCurrentSubject);
    }

    public Vector<String> getClientPriority()
    {
        return clientPriority;
    }

    public Vector<String> getRemovedSitesOfCurrentSubject()
    {
        return removedSitesOfCurrentSubject;
    }

    public RecyclerView.Adapter getRecyclerAdapter()
    {
        return recyclerAdapter;
    }

    public void setRecyclerAdapter(RecyclerView.Adapter recyclerAdapter)
    {
        this.recyclerAdapter = recyclerAdapter;
    }

    public void createRemovedSitesList()
    {
        for(int i = 0; i < categorySites.size(); i++) //Put all sites from current category
        {
            if (categorySites.get(i).getSubject().equals(currentPrioritySubject))
            {
                removedSitesOfCurrentSubject.add(categorySites.get(i).getSite());
            }
        }
        for (int i = 0; i < categorySites.size(); i++) //Delete from removedSites the sites that exist in client's priority
        {
            if (categorySites.get(i).getSubject().equals(currentPrioritySubject))
            {
                for(int j = 0; j < clientPriority.size(); j++)
                {
                    if (categorySites.get(i).getSite().equals(clientPriority.get(j)))
                    {
                        removedSitesOfCurrentSubject.remove(clientPriority.get(j));
                    }
                }
            }
        }
    }
}