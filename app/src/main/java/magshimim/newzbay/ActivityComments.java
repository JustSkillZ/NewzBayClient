package magshimim.newzbay;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class ActivityComments extends AppCompatActivity implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener
{
    private RecyclerView recyclerViewComments;
    private android.support.v7.widget.LinearLayoutManager recyclerLayoutManager;
    private RecyclerView.Adapter commentsRecyclerAdapter;
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
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        globalClass = (GlobalClass) getApplicationContext();
        globalClass.setCurrentActivity(ActivityComments.this);
        commentsHandler = globalClass.getCommentsHandler();
        user = globalClass.getUser();
        commentText = (EditText) findViewById(R.id.commentText);

        emojicons = getSupportFragmentManager().findFragmentById(R.id.fragment_emojicons);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction(); //Hide emojicons keyboard
        ft.hide(emojicons);
        ft.commit();
        emojiconsOpen = false;

        final View rootView = findViewById(R.id.comments_layout);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() //Get the height of virtual keyboard
        {
            @Override
            public void onGlobalLayout()
            {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();
                int heightDifference = screenHeight - (r.bottom - r.top);
                if (heightDifference > 150) //If the difference is bigger than 150+- it means that the keyboard is open
                {
                    if (!keyboardVisible && emojiconsOpen) //If the keyboard was closed and the emojicons fragment is open
                    {
                        changeFragmentVisibility(null); //Close the emojicons fragment (Because the keyboard is open now)
                    }
                    softKeyboardHeight = heightDifference - 75; //The real height is a bit smaller than the difference
                    keyboardVisible = true;
                }
                else
                {
                    keyboardVisible = false;
                }
            }
        });

        //*********************************Customize the Collapsing ToolbarLayout*******************************************
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(commentsHandler.getArticle().getMainHeadline());
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener()
        {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset)
            {
                if (verticalOffset == 0)
                {
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
        ((TextView) findViewById(R.id.tv_site)).setText(commentsHandler.getArticle().getWebSite());
        if (commentsHandler.getArticle().getDate() != null)
        {
            Date d = new Date();
            ((TextView) findViewById(R.id.tv_date)).setText((String) DateUtils.getRelativeTimeSpanString(commentsHandler.getArticle().getDate().getTime(), d.getTime(), 0));
        }
        if (!commentsHandler.getArticle().getPicURL().equals("null"))
        {
            Picasso.with(this).load(commentsHandler.getArticle().getPicURL()).into(((ImageButton) findViewById(R.id.ib_picture)));
        }
        else
        {
            ((ImageButton) findViewById(R.id.ib_picture)).setImageBitmap(commentsHandler.getArticle().getPicture());
        }
        ((TextView) findViewById(R.id.tv_likes)).setText(commentsHandler.getArticle().getNumberOfLikes() + "");
        ((TextView) findViewById(R.id.tv_comments)).setText(commentsHandler.getArticle().getNumberOfComments() + "");

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(globalClass.getResources().getDrawable(R.drawable.ic_arrow_back_white_48dp));
        //*****************************************************************************************************************

        //**************************************Init Comments RecyclerView*************************************************
        recyclerViewComments = (RecyclerView) findViewById(R.id.rv_comments);
        recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerViewComments.setLayoutManager(recyclerLayoutManager);
        commentsRecyclerAdapter = new CommentsAdapter(globalClass, ActivityComments.this);
        commentsHandler.setCommentsRecyclerAdapter(commentsRecyclerAdapter);
        recyclerViewComments.setAdapter(commentsRecyclerAdapter);
        globalClass.getNewCommunication().getComments(commentsHandler.getArticle().getUrl(), globalClass);
        //*****************************************************************************************************************

        commentsHandler.setCommentActivity(this);

        if(!globalClass.getUser().getConnectedVia().equals("Guest"))
        {
            ((Button) findViewById(R.id.btn_sendComment)).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    sendComment(v);
                }
            });
        }
        else
        {
            ((Button) findViewById(R.id.btn_sendComment)).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Toast toast = Toast.makeText(ActivityComments.this, "רק משתמשים מחוברים יכולים להגיב", Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        }

        ((ImageButton) findViewById(R.id.ib_picture)).setOnClickListener(new View.OnClickListener() //Go to web activity
        {
            @Override
            public void onClick(View v)
            {
                globalClass.getCategoriesHandler().setCurrentlyOpenURL(commentsHandler.getArticle().getUrl());
                Intent web = new Intent(ActivityComments.this, ActivityInnerWeb.class);
                ActivityComments.this.startActivity(web);
            }
        });
    }

    public void sendComment(View v) //Send Comment and update the recyclerView
    {
        if (!commentText.getText().toString().equals(""))
        {
            globalClass.getNewCommunication().addComment(commentsHandler.getArticle().getUrl(), commentText.getText().toString(), globalClass, commentsHandler, user, commentText.getText().toString());
            commentText.setText("");
            ViewGroup parent = (ViewGroup) v.getParent().getParent().getParent();
            ((TextView) parent.findViewById(R.id.tv_comments)).setText(commentsHandler.getArticle().getNumberOfComments() + "");
            if(emojiconsOpen)
            {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.hide(emojicons);
                emojiconsOpen = false;
                ft.commit();
            }
            else if(keyboardVisible)
            {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(commentText.getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);
                keyboardVisible = false;
            }
            appBarLayout.setExpanded(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) //Return to NewsFeed
    {
        if (menuItem.getItemId() == android.R.id.home)
        {
            this.onStop();
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onStop()
    {
        commentsHandler.getCommentsOfCurrentArticle().clear();
        commentsHandler.setCommentActivity(null);
        super.onStop();
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) //Fragment's backspace works in the editText view too
    {
        EmojiconsFragment.backspace(commentText);
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) //When emoji is selected, add it to editText view
    {
        EmojiconsFragment.input(commentText, emojicon);
    }

    public void changeFragmentVisibility(View v)
    {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (softKeyboardHeight != -1) //If the height of the keyboard set
        {
            ViewGroup.LayoutParams params = emojicons.getView().getLayoutParams();
            params.height = softKeyboardHeight;
            emojicons.getView().setLayoutParams(params);
        }
        if (emojicons.isHidden()) //Show fragment
        {
            if (keyboardVisible) //If the keyboard is visible, hide it
            {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(commentText.getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);
            }
            ft.show(emojicons);
            emojiconsOpen = true;
        }
        else //Hide fragment
        {
            ft.hide(emojicons);
            emojiconsOpen = false;
        }
        ft.commit();
    }

    @Override
    public void onBackPressed()
    {
        if (emojiconsOpen) //If emojicon fragment is open, close it
        {
            changeFragmentVisibility(null);
        }
        else
        {
            globalClass.getCategoriesHandler().getArticlesRecyclerAdapter().notifyDataSetChanged();
            super.onBackPressed();
        }
    }
}