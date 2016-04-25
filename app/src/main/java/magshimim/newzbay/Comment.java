package magshimim.newzbay;

public class Comment {
    String username;
    String profilePicURL;
    String commentText;

    public Comment(String username, String profilePicURL, String commentText) {
        this.username = username;
        this.profilePicURL = profilePicURL;
        this.commentText = commentText;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePicURL() {
        return profilePicURL;
    }

    public void setProfilePicURL(String profilePicURL) {
        this.profilePicURL = profilePicURL;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }
}
