package magshimim.newzbay;

import android.support.v7.widget.RecyclerView;

import java.util.Vector;

public class CommentsHandler
{

    private Vector<Comment> commentsofCurrentArticle;
    private RecyclerView.Adapter commentsRecyclerAdapter;
    private Article article;

    public CommentsHandler()
    {
        this.commentsofCurrentArticle = new Vector<>();
    }

    public Vector<Comment> getCommentsofCurrentArticle()
    {
        return commentsofCurrentArticle;
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
}
