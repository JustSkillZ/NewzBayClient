package magshimim.newzbay;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Date;

public class Comments extends AppCompatActivity {

    private RecyclerView recyclerView_comments;
    private android.support.v7.widget.LinearLayoutManager recyclerLayoutManager;
    private RecyclerView.Adapter recyclerAdapter;
    private GlobalClass globalClass;
    private CommentsHandler commentsHandler;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        globalClass = (GlobalClass) getApplicationContext();
        commentsHandler = globalClass.getCommentsHandler();
        user = globalClass.getUser();

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(commentsHandler.getArticle().getMainHeadline());
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        ((TextView) findViewById(R.id.tv_mainHeadline)).setText(commentsHandler.getArticle().getMainHeadline());
        ((TextView) findViewById(R.id.tv_secondHeadline)).setText(commentsHandler.getArticle().getSecondHeadline());
        ((TextView) findViewById(R.id.tv_site)).setText(commentsHandler.getArticle().getSiteName());
        if (commentsHandler.getArticle().getDate() != null) {
            Date d = new Date();
            ((TextView) findViewById(R.id.tv_date)).setText((String) DateUtils.getRelativeTimeSpanString(commentsHandler.getArticle().getDate().getTime(), d.getTime(), 0));
        }
        if (!commentsHandler.getArticle().getPicURL().equals("null")) {
            Picasso.with(this).load(commentsHandler.getArticle().getPicURL()).into(((ImageButton) findViewById(R.id.ib_picture)));
        } else {
            ((ImageButton) findViewById(R.id.ib_picture)).setImageBitmap(commentsHandler.getArticle().getPicture());
        }
        ((TextView) findViewById(R.id.tv_likes)).setText(commentsHandler.getArticle().getNumberOfLikes() + "");
        ((TextView) findViewById(R.id.tv_comments)).setText(commentsHandler.getArticle().getNumberOfComments() + "");

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(globalClass.getResources().getDrawable(R.drawable.ic_arrow_back_white_48dp));

        recyclerView_comments = (RecyclerView) findViewById(R.id.rv_comments);
        recyclerLayoutManager = new CommentsLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView_comments.setLayoutManager(recyclerLayoutManager);
        recyclerAdapter = new CommentsAdapter(globalClass, Comments.this);
        commentsHandler.setRecyclerAdapter(recyclerAdapter);
        recyclerView_comments.setAdapter(recyclerAdapter);
        globalClass.getCommunication().clientSend("120◘" + commentsHandler.getArticle().getUrl() + "#");
    }

    public void sendComment(View v)
    {
        EditText commentText = (EditText) findViewById(R.id.commentText);
        if(!commentText.getText().toString().equals(""))
        {
            globalClass.getCommunication().clientSend("122◘" + commentsHandler.getArticle().getUrl() + "○" + commentText.getText().toString() + "#");
            commentsHandler.getCommentsofCurrentArticle().addElement(new Comment(user.getFullName(), user.getPicURL(), commentText.getText().toString()));
            recyclerAdapter.notifyDataSetChanged();
            commentText.setText("");
            commentsHandler.getArticle().incNumberOfComments();
            ViewGroup parent = (ViewGroup)v.getParent().getParent().getParent();
            ((TextView)parent.findViewById(R.id.tv_comments)).setText(commentsHandler.getArticle().getNumberOfComments() + "");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            this.onStop();
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
