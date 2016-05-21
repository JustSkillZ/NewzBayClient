package magshimim.newzbay;

public class Comment
{
    private String username;
    private String profilePicURL;
    private String commentText;
    private Boolean clientComment;

    public Comment(String username, String profilePicURL, String commentText, Boolean clientComment)
    {
        this.username = username;
        this.profilePicURL = profilePicURL;
        this.commentText = commentText;
        this.clientComment = clientComment;
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
}
