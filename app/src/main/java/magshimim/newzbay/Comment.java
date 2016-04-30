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

    public String getProfilePicURL() {
        return profilePicURL;
    }

    public String getCommentText() {
        return commentText;
    }
}
