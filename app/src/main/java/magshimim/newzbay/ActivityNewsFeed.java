package magshimim.newzbay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.format.Time;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.plus.Plus;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;

public class ActivityNewsFeed extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{

    private final String explanationPref = "magshimim.newzbay.ExplanationPref";
    private final String isExplanation1 = "isExplanation1";
    private android.support.v7.widget.Toolbar toolbarMain;
    private SwipeRefreshLayout refreshList;
    private Handler handler = new Handler();
    private DrawerLayout drawer;
    private GlobalClass globalClass;
    private CategoriesHandler categoriesHandler;
    private User user;
    private RecyclerView recyclerViewArticle;
    private RecyclerView.Adapter recyclerAdapterArticle;
    private android.support.v7.widget.LinearLayoutManager recyclerLayoutManager;
    private final Runnable refreshListView = new Runnable()
    {
        public void run()
        {
            if (!categoriesHandler.getCurrentlyInUseCategoryServer().equals(""))
            {
                updateArticles();
            }
            refreshList.setRefreshing(false);
        }
    };
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.newsfeed_activity);
        changeTheme();
        globalClass = ((GlobalClass) getApplicationContext());
        user = globalClass.getUser();
        globalClass.setCurrentActivity(ActivityNewsFeed.this);
        globalClass.setCurrentLayout(R.id.newsfeed_layout);
        categoriesHandler = globalClass.getCategoriesHandler();
        SharedPreferences sharedpreferences; //If first time in this activity, show explanation activity.
        if (!getSharedPreferences(explanationPref, Context.MODE_PRIVATE).getBoolean(isExplanation1, false))
        {
            Intent welcome = new Intent(this, ActivityExplanationNewsFeed.class);
            startActivity(welcome);

            sharedpreferences = getSharedPreferences(explanationPref, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(isExplanation1, true);
            editor.commit();
        }

        //********************************Init Main Toolbar*****************************************
        toolbarMain = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbarMain);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        updateToolbarTitle();
        //******************************************************************************************

        //**************************Init RecyclerView of Articles***********************************
        recyclerViewArticle = (RecyclerView) findViewById(R.id.recyclerView_articles);
        recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerViewArticle.setLayoutManager(recyclerLayoutManager);
        recyclerAdapterArticle = new ArticleAdapter(this, (GlobalClass) getApplicationContext());
        recyclerViewArticle.setAdapter(recyclerAdapterArticle);
        recyclerViewArticle.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            int pastVisibleItems, visibleItemCount, totalItemCount;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (dy > 0)
                {
                    visibleItemCount = recyclerLayoutManager.getChildCount();
                    totalItemCount = recyclerLayoutManager.getItemCount();
                    pastVisibleItems = recyclerLayoutManager.findFirstVisibleItemPosition();

                    if (pastVisibleItems + visibleItemCount == totalItemCount && totalItemCount != 0)
                    {
                        if (!categoriesHandler.isLoading()) //If not asking, ask for more articles from current subject
                        {
                            categoriesHandler.setLoading(true);
                            globalClass.getCommunication().send("118○" + categoriesHandler.getCurrentlyInUseCategoryServer() + "○" + categoriesHandler.getCurrentlyInUse().lastElement().getUrl() + "#");
                            findViewById(R.id.pb_loadingArticles).setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
        //******************************************************************************************

        categoriesHandler.setArticlesRecyclerAdapter(recyclerAdapterArticle);

        createSwipeRefreshLayout();

        //**Open the possibility to drag and open the NavigationView from the middle of the screen**
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Field mDragger = null;//mRightDragger for right obviously
        try
        {
            mDragger = drawer.getClass().getDeclaredField(
                    "mLeftDragger");
        }
        catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }
        assert mDragger != null;
        mDragger.setAccessible(true);
        ViewDragHelper draggerObj = null;
        try
        {
            draggerObj = (ViewDragHelper) mDragger.get(drawer);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }

        Field mEdgeSize = null;
        try
        {
            assert draggerObj != null;
            mEdgeSize = draggerObj.getClass().getDeclaredField("mEdgeSize");
        }
        catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }
        assert mEdgeSize != null;
        mEdgeSize.setAccessible(true);
        int edge = 0;
        try
        {
            edge = mEdgeSize.getInt(draggerObj);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }

        try
        {
            mEdgeSize.setInt(draggerObj, edge * 5); //optimal value as for me, you may set any constant in dp
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        //******************************************************************************************

        //*******************Init DrawerLayout in order to open NavigationView**********************
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbarMain, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset)
            {
                super.onDrawerSlide(drawerView, slideOffset);
                drawerHandler(drawer);
            }
        };
        toggle.setDrawerIndicatorEnabled(false);
        toolbarMain.setNavigationIcon(R.drawable.anchor);
        toolbarMain.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                drawerHandler(drawer);
            }

        });
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        //******************************************************************************************

        //****************************Customize NavigationView**************************************
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.nav_hot_news).getIcon().setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_ATOP);
        SpannableString spanString = new SpannableString(navigationView.getMenu().findItem(R.id.nav_hot_news).getTitle().toString());
        spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.orange)), 0, spanString.length(), 0); //fix the color to white
        navigationView.getMenu().findItem(R.id.nav_hot_news).setTitle(spanString);
        if (user.getConnectedVia().equals("Guest"))
        {
            navigationView.getMenu().findItem(R.id.nav_priority).getIcon().setColorFilter(getResources().getColor(R.color.com_facebook_button_background_color_disabled), PorterDuff.Mode.SRC_ATOP);
            spanString = new SpannableString(navigationView.getMenu().findItem(R.id.nav_priority).getTitle().toString());
            spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.com_facebook_button_background_color_disabled)), 0, spanString.length(), 0); //fix the color to white
            navigationView.getMenu().findItem(R.id.nav_priority).setTitle(spanString);
        }
        //******************************************************************************************
    }

    @Override
    public void onBackPressed()
    {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else //Double click on back in order to close application
        {
            categoriesHandler.setCurrentlyInUseCategoryServer("");
            categoriesHandler.setCurrentlyInUseCategory("");
            categoriesHandler.getCurrentlyInUse().clear();
            categoriesHandler.getArticlesRecyclerAdapter().notifyDataSetChanged();
            for (int i = 0; i <= toolbarMain.getChildCount(); i++)
            {
                View v = toolbarMain.getChildAt(i);
                if (v != null)
                {
                    if (v instanceof TextView)
                    {
                        try
                        {
                            ((TextView) v).setText(categoriesHandler.getCurrentlyInUseCategory(user));
                        }
                        catch (NullPointerException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
            if (doubleBackToExitPressedOnce)
            {
                super.onBackPressed();
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "יש ללחוץ פעם נוספת בשביל לצאת", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_hot_news)
        {
            categoriesHandler.getCurrentlyInUse().clear();
            Intent intent = new Intent(this, ActivityHotNews.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_news)
        {
            changeCategory(1);

        }
        else if (id == R.id.nav_global_news)
        {
            changeCategory(2);

        }
        else if (id == R.id.nav_politics)
        {
            changeCategory(3);

        }
        else if (id == R.id.nav_economy)
        {
            changeCategory(4);
        }
        else if (id == R.id.nav_sport)
        {
            changeCategory(5);

        }
        else if (id == R.id.nav_culture)
        {
            changeCategory(6);

        }
        else if (id == R.id.nav_celebrities)
        {
            changeCategory(7);

        }
        else if (id == R.id.nav_technology)
        {
            changeCategory(8);

        }
        else if (id == R.id.nav_science)
        {
            changeCategory(9);

        }
        else if (id == R.id.nav_priority)
        {
            if (!user.getConnectedVia().equals("Guest"))
            {
                Intent priority1 = new Intent(this, ActivityPriority.class);
                startActivity(priority1);
                this.onStop();
            }
            else
            {
                Toast.makeText(globalClass.getCurrentActivity(), "רק משתמשים מחוברים יכולים לעשות תיעדוף לאתרים", Toast.LENGTH_LONG).show();
            }
        }
        else if (id == R.id.nav_helpNB) //Contact with NB developers
        {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("plain/text");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"nnewzbay@gmail.com"});
            startActivity(Intent.createChooser(intent, ""));
        }
        else if (id == R.id.nav_discconect)
        {
            if (user.getConnectedVia().equals("Google"))
            {
                Plus.AccountApi.clearDefaultAccount(((GoogleUser) user).getGoogleApiClient());
                ((GoogleUser) user).getGoogleApiClient().disconnect();
                ((GoogleUser) user).getGoogleApiClient().connect();
            }
            else if (user.getConnectedVia().equals("Facebook"))
            {
                LoginManager.getInstance().logOut();
            }
            globalClass.getCommunication().send("500#"); //Disconnect from the server
            globalClass.endClass();
            Intent intent = new Intent(this, ActivityEntrance.class);
            startActivity(intent);
            finish();
        }
        updateToolbarTitle();
        categoriesHandler.setLoading(false);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void drawerHandler(DrawerLayout drawer)
    {
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            drawer.openDrawer(GravityCompat.START);
        }
        TextView userFullName = (TextView) drawer.findViewById(R.id.tv_userFullName);
        if (userFullName != null) //Set user's name
        {
            userFullName.setText(user.getFullName());
        }
        de.hdodenhof.circleimageview.CircleImageView userPic = (de.hdodenhof.circleimageview.CircleImageView) drawer.findViewById(R.id.ib_userPic);
        if (userPic != null) //Set user's profile pic
        {
            if (user.getConnectedVia().equals("Guest"))
            {
                Picasso.with(globalClass.getCurrentActivity()).load(R.drawable.user_icon).into(userPic);
            }
            else if (user.getConnectedVia().equals("Facebook"))
            {
                Picasso.with(globalClass.getCurrentActivity()).load(((FacebookUser) user).getFacebookProfile().getProfilePictureUri(500, 500)).into(userPic);
                user.setFullName(((FacebookUser) user).getFacebookProfile().getName());
            }
            else if (user.getConnectedVia().equals("Google"))
            {
                Picasso.with(globalClass.getCurrentActivity()).load(((GoogleUser) user).getGoogleProfile().getImage().getUrl().replace("sz=50", "sz=500")).into(userPic);
                user.setFullName(((GoogleUser) user).getGoogleProfile().getDisplayName());
            }
        }
    }

    private void createSwipeRefreshLayout()
    {
        refreshList = (SwipeRefreshLayout) findViewById(R.id.refresh_ListView);
        refreshList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                handler.post(refreshListView);
            }
        });
        refreshList.setColorSchemeResources(R.color.nb,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light,
                android.R.color.holo_red_light);
    }

    private void updateArticles()
    {
        globalClass.getCommunication().send("114○" + categoriesHandler.getCurrentlyInUseCategoryServer() + "#");
        findViewById(R.id.pb_loadingArticles).setVisibility(View.VISIBLE);
        categoriesHandler.setCurrentlyInUseCategory(categoriesHandler.getCurrentCategoryID(), this);
        recyclerLayoutManager.scrollToPositionWithOffset(0, 0);
    }

    private void changeCategory(int categoryID)
    {
        globalClass.getCommunication().send("114○" + categoriesHandler.getCategoriesForServer().get(categoryID) + "#");
        findViewById(R.id.pb_loadingArticles).setVisibility(View.VISIBLE);
        categoriesHandler.setCurrentlyInUseCategory(categoryID, this);
        recyclerLayoutManager.scrollToPositionWithOffset(0, 0);
    }

    private void changeTheme()
    {
        Time now = new Time();
        now.setToNow();
        if (now.hour >= 19 || now.hour >= 0 && now.hour <= 5)
        {
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.newsfeed_layout);
            layout.setBackground(getResources().getDrawable(R.drawable.main_background_night));
            TextView slogan = (TextView) findViewById(R.id.tv_hello1);
            slogan.setTextColor(getResources().getColor(R.color.white));
        }
    }

    public void updateToolbarTitle()
    {
        for (int i = 0; i <= toolbarMain.getChildCount(); i++)
        {
            View v = toolbarMain.getChildAt(i);
            if (v != null)
            {
                if (v instanceof TextView)
                {
                    try
                    {
                        ((TextView) v).setText(categoriesHandler.getCurrentlyInUseCategory(user));
                    }
                    catch (NullPointerException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        globalClass.setCurrentActivity(ActivityNewsFeed.this);
        changeTheme();
        updateToolbarTitle();
    }
}