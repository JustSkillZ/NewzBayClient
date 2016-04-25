package magshimim.newzbay;

import android.support.v7.widget.RecyclerView;

import java.util.Vector;

public class CommentsHandler {

    private Vector<Comment> commentsofCurrentArticle;
    private RecyclerView.Adapter recyclerAdapter;
    private Article article;

    public CommentsHandler() {
        this.commentsofCurrentArticle = new Vector<>();
    }

    public Vector<Comment> getCommentsofCurrentArticle() {
        return commentsofCurrentArticle;
    }

    public void setCommentsofCurrentArticle(Vector<Comment> commentsofCurrentArticle) {
        this.commentsofCurrentArticle = commentsofCurrentArticle;
    }

    public RecyclerView.Adapter getRecyclerAdapter() {
        return recyclerAdapter;
    }

    public void setRecyclerAdapter(RecyclerView.Adapter recyclerAdapter) {
        this.recyclerAdapter = recyclerAdapter;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }
}
