package magshimim.newzbay;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

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
