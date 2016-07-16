package magshimim.newzbay;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class CommentsHandler
{
    private Vector<Comment> commentsOfCurrentArticle;
    private RecyclerView.Adapter commentsRecyclerAdapter;
    private Article article;
    private Context commentActivity;

    public CommentsHandler()
    {
        this.commentsOfCurrentArticle = new Vector<>();
    }

    public Vector<Comment> getCommentsOfCurrentArticle()
    {
        return commentsOfCurrentArticle;
    }

    public RecyclerView.Adapter getCommentsRecyclerAdapter()
    {
        return commentsRecyclerAdapter;
    }

    public void setCommentsRecyclerAdapter(RecyclerView.Adapter commentsRecyclerAdapter)
    {
        this.commentsRecyclerAdapter = commentsRecyclerAdapter;
    }

    public Article getArticle()
    {
        return article;
    }

    public void setArticle(Article article)
    {
        this.article = article;
    }

    public Context getCommentActivity()
    {
        return commentActivity;
    }

    public void setCommentActivity(Context commentActivity)
    {
        this.commentActivity = commentActivity;
    }
}

class JsonRecievedComments
{
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("comments")
    @Expose
    private List<JsonComment> comments = new ArrayList<JsonComment>();
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

    public List<JsonComment> getComments()
    {
        return comments;
    }

    public void setComments(List<JsonComment> articles)
    {
        this.comments = articles;
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

class JsonComment
{
    @SerializedName("ID")
    @Expose
    private String id;
    @SerializedName("picURL")
    @Expose
    private String picURL;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("name")
    @Expose
    private String name;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getPicURL()
    {
        return picURL;
    }

    public void setPicURL(String picURL)
    {
        this.picURL = picURL;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}

