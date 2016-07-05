package magshimim.newzbay;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Comment
{
    private String id;
    private String username;
    private String profilePicURL;
    private String commentText;
    private Boolean clientComment;

    public Comment(String username, String profilePicURL, String commentText, Boolean clientComment, String id)
    {
        this.username = username;
        this.profilePicURL = profilePicURL;
        this.commentText = commentText;
        this.clientComment = clientComment;
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }

    public String getProfilePicURL()
    {
        return profilePicURL;
    }

    public String getCommentText()
    {
        return commentText;
    }

    public Boolean getClientComment()
    {
        return clientComment;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }
}