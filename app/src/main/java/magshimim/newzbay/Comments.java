package magshimim.newzbay;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class Comments extends AppCompatActivity implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {



    private RecyclerView recyclerViewComments;
    private android.support.v7.widget.LinearLayoutManager recyclerLayoutManager;
    private RecyclerView.Adapter recyclerAdapter;
    private GlobalClass globalClass;
    private CommentsHandler commentsHandler;
    private User user;
    private EditText commentText;
    private Fragment emojicons;
    private Boolean emojiconsOpen;
    private AppBarLayout appBarLayout;

    private int softKeyboardHeight = -1;
    private boolean keyboardVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        globalClass = (GlobalClass) getApplicationContext();
        commentsHandler = globalClass.getCommentsHandler();
        user = globalClass.getUser();
        emojiconsOpen = false;
        commentText = (EditText) findViewById(R.id.commentText);
        emojicons = getSupportFragmentManager().findFragmentById(R.id.fragment_emojicons);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.hide(emojicons);
        ft.commit();

        final View rootView = findViewById(R.id.comments_layout);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();
                int heightDifference = screenHeight - (r.bottom - r.top);
                if(heightDifference > 150)
                {
                    if(!keyboardVisible && emojiconsOpen)
                    {
                        setFragmentVisibility(null);
                    }
                    softKeyboardHeight = heightDifference - 75;
                    keyboardVisible = true;
                }
                else
                {
                    keyboardVisible = false;
                }
            }
        });

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(commentsHandler.getArticle().getMainHeadline());
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
                else
                {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            }
        });

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
        getSupportActionBar().setHomeAsUpIndicator(globalClass.getResources().getDrawable(R.drawable.ic_arrow_back_white_48dp));

        recyclerViewComments = (RecyclerView) findViewById(R.id.rv_comments);
        recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerViewComments.setLayoutManager(recyclerLayoutManager);
        recyclerAdapter = new CommentsAdapter(globalClass, Comments.this);
        commentsHandler.setRecyclerAdapter(recyclerAdapter);
        recyclerViewComments.setAdapter(recyclerAdapter);
        globalClass.getCommunication().clientSend("120◘" + commentsHandler.getArticle().getUrl() + "#");
    }

    public void sendComment(View v)
    {
        if(!commentText.getText().toString().equals(""))
        {
            globalClass.getCommunication().clientSend("122◘" + commentsHandler.getArticle().getUrl() + "○" + commentText.getText().toString() + "#");
            commentsHandler.getCommentsofCurrentArticle().addElement(new Comment(user.getFullName(), user.getPicURL(), commentText.getText().toString()));
            recyclerAdapter.notifyDataSetChanged();
            commentText.setText("");
            commentsHandler.getArticle().incNumberOfComments();
            ViewGroup parent = (ViewGroup)v.getParent().getParent().getParent();
            ((TextView)parent.findViewById(R.id.tv_comments)).setText(commentsHandler.getArticle().getNumberOfComments() + "");
            appBarLayout.setExpanded(false);
            recyclerViewComments.scrollToPosition(commentsHandler.getCommentsofCurrentArticle().size()-1);
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

    @Override
    protected void onStop() {
        commentsHandler.getCommentsofCurrentArticle().clear();
        super.onStop();
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(commentText);
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(commentText, emojicon);
    }

    public void setFragmentVisibility(View v){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if(softKeyboardHeight != -1)
        {
            ViewGroup.LayoutParams params = emojicons.getView().getLayoutParams();
            params.height = softKeyboardHeight;
            emojicons.getView().setLayoutParams(params);
        }
        if (emojicons.isHidden()) {
            if(keyboardVisible)
            {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(commentText.getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);
            }
            ft.show(emojicons);
            emojiconsOpen = true;
        } else {
            ft.hide(emojicons);
            emojiconsOpen = false;
        }
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if(emojiconsOpen)
        {
            setFragmentVisibility(null);
        }
        else
        {
            globalClass.getCategoriesHandler().getRecyclerAdapter().notifyDataSetChanged();
            super.onBackPressed();
        }
    }
}