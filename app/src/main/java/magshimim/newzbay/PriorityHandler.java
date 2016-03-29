package magshimim.newzbay;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Vector;

public class PriorityHandler {
    private Vector<String> idOfRSS;
    private Vector<String> subject;
    private Vector<String> site;
    private String subjectToShow;
    private String subjectToServer;

    public PriorityHandler() {
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

    public Vector<String> getIdOfRSS(){
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

    public String getSubjectToShow() {
        return subjectToShow;
    }

    public void setSubjectToShow(String subjectToShow) {
        this.subjectToShow = subjectToShow;
    }

    public String getSubjectToServer() {
        return subjectToServer;
    }

    public void setSubjectToServer(String subjectToServer) {
        this.subjectToServer = subjectToServer;
    }

    public HashMap<String, String> getSubjects() {
        return Subjects;
    }

    public void setSubjects(HashMap<String, String> subjects) {
        Subjects = subjects;
    }
}
