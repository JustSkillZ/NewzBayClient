package magshimim.newzbay;

import android.support.v7.widget.RecyclerView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class PriorityHandler
{
    private Vector<RSS> rssSites;
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
        rssSites = new Vector<>();
        sitesOfCurrentSubject = new Vector<>();
        removedSitesOfCurrentSubject = new Vector<>();
    }

    public HashMap<String, String> getSubjects()
    {
        return Subjects;
    }

    public Vector<RSS> getRssSites()
    {
        return rssSites;
    }

    public String getCurrentPrioritySubject()
    {
        return currentPrioritySubject;
    }

    public void setCurrentPrioritySubject(String currentPrioritySubject)
    {
        this.currentPrioritySubject = currentPrioritySubject;
        sitesOfCurrentSubject.clear();
        for (int i = 0; i < rssSites.size(); i++)
        {
            if (rssSites.get(i).getSubject().equals(currentPrioritySubject))
            {
                sitesOfCurrentSubject.add(rssSites.get(i).getWebsite());
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
        for (int i = 0; i < rssSites.size(); i++) //Put all sites from current category
        {
            if (rssSites.get(i).getSubject().equals(currentPrioritySubject))
            {
                removedSitesOfCurrentSubject.add(rssSites.get(i).getWebsite());
            }
        }
        for (int i = 0; i < rssSites.size(); i++) //Delete from removedSites the sites that exist in client's priority
        {
            if (rssSites.get(i).getSubject().equals(currentPrioritySubject))
            {
                for (int j = 0; j < clientPriority.size(); j++)
                {
                    if (rssSites.get(i).getWebsite().equals(clientPriority.get(j)))
                    {
                        removedSitesOfCurrentSubject.remove(clientPriority.get(j));
                    }
                }
            }
        }
    }

    public String getWebsiteByID(String id)
    {
        for(int i = 0; i < rssSites.size(); i++)
        {
            if(rssSites.get(i).getID().equals(id))
            {
                return rssSites.get(i).getWebsite();
            }
        }
        return "";
    }

    public String getSubjectByID(String id)
    {
        for(int i = 0; i < rssSites.size(); i++)
        {
            if(rssSites.get(i).getID().equals(id))
            {
                return rssSites.get(i).getSubject();
            }
        }
        return "";
    }
}

class Priority
{
    @SerializedName("ID")
    @Expose
    private String ID;
    @SerializedName("priority")
    @Expose
    private Integer priority;

    public Priority(String ID, Integer priority)
    {
        this.ID = ID;
        this.priority = priority;
    }

    public String getID()
    {
        return ID;
    }

    public void setID(String ID)
    {
        this.ID = ID;
    }

    public Integer getPriority()
    {
        return priority;
    }

    public void setPriority(Integer priority)
    {
        this.priority = priority;
    }
}

class UserPriority
{
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("priority")
    @Expose
    private List<RSS> priority = new ArrayList<RSS>();
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public List<RSS> getPriority()
    {
        return priority;
    }

    public void setPriority(List<RSS> message)
    {
        this.priority = message;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}

class SubWeb
{
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("subWeb")
    @Expose
    private List<RSS> subWeb = new ArrayList<RSS>();
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public List<RSS> getSubWeb()
    {
        return subWeb;
    }

    public void setSubWeb(List<RSS> subWeb)
    {
        this.subWeb = subWeb;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}