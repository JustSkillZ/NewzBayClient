package magshimim.newzbay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.plus.Plus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

public class newsfeed_activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private android.support.v7.widget.Toolbar toolbar_main;
    private SwipeRefreshLayout refreshList;
    private Handler handler = new Handler();
    private DrawerLayout drawer;
    private GlobalClass globalClass;
    private CategoriesHandler categoriesHandler;
    private Communication communication;
    private User user;
    private RecyclerView recyclerView_article;
    private RecyclerView.Adapter recyclerAdapter;
    private android.support.v7.widget.LinearLayoutManager recyclerLayoutManager;

    boolean doubleBackToExitPressedOnce = false;

    private final String explanationPref = "magshimim.newzbay.ExplanationPref" ;
    private final String prefsConnection = "magshimim.newzbay.ConnectionPrefs";
    private final String isExplanation1 = "isExplanation1";
    private final String isPrioritized = "isPrioritized";

    public newsfeed_activity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.newsfeed_activity);

        SharedPreferences sharedpreferences = getSharedPreferences(prefsConnection, Context.MODE_PRIVATE);
        if (!sharedpreferences.getBoolean(isPrioritized, false))
        {
            Intent priority = new Intent(this,Priority.class);
            startActivity(priority);

            sharedpreferences = getSharedPreferences(prefsConnection, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(isPrioritized, true);
            editor.commit();
        }

        Time now = new Time();
        now.setToNow();
        if(now.hour >= 19 || now.hour >= 0 && now.hour <= 5)
        {
            RelativeLayout layout =(RelativeLayout)findViewById(R.id.newsfeed_layout);
            layout.setBackground(getResources().getDrawable(R.drawable.main_background_night));
            TextView slogen = (TextView) findViewById(R.id.tv_hello1);
            slogen.setTextColor(getResources().getColor(R.color.white));
        }

        globalClass = ((GlobalClass) getApplicationContext());
        categoriesHandler = globalClass.getCategoriesHandler();
        communication = globalClass.getCommunication();
        user = globalClass.getUser();

//        final ImageView loading = (ImageView) findViewById(R.id.iv_nb_loading);
//        loading.setVisibility(View.VISIBLE);
//        final Animation an = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
//        final Animation an2 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.abc_fade_out);
//
//        loading.startAnimation(an);
//        an.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                loading.startAnimation(an2);
//                loading.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });

        if (!getSharedPreferences(explanationPref, Context.MODE_PRIVATE).getBoolean(isExplanation1, false))
        {
            Intent welcome = new Intent(this,Explanation.class);
            startActivity(welcome);

            sharedpreferences = getSharedPreferences(explanationPref, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(isExplanation1, true);
            editor.commit();
        }

        if(user.getConnectedVia().equals("Facebook"))
        {
            user.getBitmapFromURL(((FacebookUser) user).getFacebookProfile().getProfilePictureUri(500, 500).toString());
            user.setFullName(((FacebookUser) user).getFacebookProfile().getName());
        }
        else if(user.getConnectedVia().equals("Google")) {
            user.getBitmapFromURL(((GoogleUser) user).getGoogleProfile().getImage().getUrl().replace("sz=50", "sz=500").toString());
            user.setFullName(((GoogleUser) user).getGoogleProfile().getDisplayName());
        }

        toolbar_main = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar_main);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        for (int i = 0; i <= toolbar_main.getChildCount(); i++) {
            View v = toolbar_main.getChildAt(i);
            if (v instanceof TextView) {
                ((TextView) v).setText(categoriesHandler.getCurrentlyInUseCategory(user));
            }
        }

        saveInInternalFolder("NewzBay", "check");

        recyclerView_article = (RecyclerView) findViewById(R.id.recyclerView_articles);
        recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerView_article.setLayoutManager(recyclerLayoutManager);
        recyclerAdapter =  new ArticleAdapter(this, (GlobalClass)getApplicationContext());
        recyclerView_article.setAdapter(recyclerAdapter);
        recyclerView_article.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int pastVisiblesItems, visibleItemCount, totalItemCount;
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = recyclerLayoutManager.getChildCount();
                    totalItemCount = recyclerLayoutManager.getItemCount();
                    pastVisiblesItems = recyclerLayoutManager.findFirstVisibleItemPosition();

                    if (pastVisiblesItems + visibleItemCount == totalItemCount && totalItemCount != 0) {
                        if (!categoriesHandler.isLoading()) {
                            categoriesHandler.setLoading(true);
                            communication.clientSend("118&" + categoriesHandler.getCurrentlyInUseCategoryServer() + "&" + categoriesHandler.getCurrentlyInUse().lastElement().getUrl() + "#");
                        }
                    }
                }
            }
        });

        categoriesHandler.setRecyclerAdapter(recyclerAdapter);
        categoriesHandler.setNewsfeed(this);


//                int position = listView_article.getPositionForView(view);
//        Article current = categoriesHandler.getCurrentlyInUse().get(i);
//        if (current.getPicture() != null && current.isPictureIsDawnloaded()) {
//            if(!current.getPicture().isRecycled())
//            {
//                current.getPicture().recycle();
//                current.setPicture(globalClass.getCategoriesHandler().getSiteLogo().get(current.getSiteName()));
//                current.setPictureIsDawnloaded(false);
//            }
//        }

        createSwipeRefreshLayout();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Field mDragger = null;//mRightDragger for right obviously
        try {
            mDragger = drawer.getClass().getDeclaredField(
                    "mLeftDragger");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        mDragger.setAccessible(true);
        ViewDragHelper draggerObj = null;
        try {
            draggerObj = (ViewDragHelper) mDragger
                    .get(drawer);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        Field mEdgeSize = null;
        try {
            mEdgeSize = draggerObj.getClass().getDeclaredField(
                    "mEdgeSize");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        mEdgeSize.setAccessible(true);
        int edge = 0;
        try {
            edge = mEdgeSize.getInt(draggerObj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            mEdgeSize.setInt(draggerObj, edge * 5); //optimal value as for me, you may set any constant in dp
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar_main, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                drawerHandler(drawer);
            }
        };
        toggle.setDrawerIndicatorEnabled(false);
        toolbar_main.setNavigationIcon(R.drawable.anchor);
        toolbar_main.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerHandler(drawer);
            }

        });
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.nav_hot_news).getIcon().setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_ATOP);
        SpannableString spanString = new SpannableString(navigationView.getMenu().findItem(R.id.nav_hot_news).getTitle().toString());
        spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.orange)), 0, spanString.length(), 0); //fix the color to white
        navigationView.getMenu().findItem(R.id.nav_hot_news).setTitle(spanString);
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "יש ללחוץ פעם נוספת בשביל לצאת", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_hot_news) {
//            Intent intent = new Intent(this, ExploreArticles.class);
//            startActivity(intent);
//            this.onStop();
        } else if (id == R.id.nav_news) {
            changeCategory(1);

        } else if (id == R.id.nav_global_news) {
            changeCategory(2);

        } else if (id == R.id.nav_politics) {
            changeCategory(3);

        } else if (id == R.id.nav_economy) {
            changeCategory(4);
        } else if (id == R.id.nav_sport) {
            changeCategory(5);

        } else if (id == R.id.nav_culture) {
            changeCategory(6);

        } else if (id == R.id.nav_celebrities) {
            changeCategory(7);

        } else if (id == R.id.nav_technology) {
            changeCategory(8);

        } else if (id == R.id.nav_science) {
            //changeCategory(9);

        } else if (id == R.id.nav_settings) {
            Intent settings = new Intent(this, settings_activity.class);
            startActivity(settings);
            this.onStop();
        }
        else if (id == R.id.nav_discconect) {
            if (user.getConnectedVia().equals("Google")) {
                Plus.AccountApi.clearDefaultAccount(((GoogleUser) user).getmGoogleApiClient());
                ((GoogleUser) user).getmGoogleApiClient().disconnect();
                ((GoogleUser) user).getmGoogleApiClient().connect();
            }
            else if(user.getConnectedVia().equals("Facebook"))
            {
                LoginManager.getInstance().logOut();
            }
            communication.clientSend("500#"); //Disconnect from the server
            communication.setIsConnect(0);
            Log.d("Server", "500#");
            globalClass.endClass();
            Intent intent = new Intent(this, entrance.class);
            startActivity(intent);
            finish();
        }
        for (int i = 0; i <= toolbar_main.getChildCount(); i++) {
            View v = toolbar_main.getChildAt(i);
            if (v instanceof TextView) {
                ((TextView) v).setText(categoriesHandler.getCurrentlyInUseCategory(user));
            }
        }
        categoriesHandler.setLoading(false);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void drawerHandler(DrawerLayout drawer)
    {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            drawer.openDrawer(GravityCompat.START);
        }
        TextView userFullName = (TextView) drawer.findViewById(R.id.tv_userFullName);
        if(userFullName != null)
        {
            userFullName.setText(user.getFullName());
        }
        ImageButton userPic = (ImageButton) drawer.findViewById(R.id.ib_userPic);
        if(userPic != null)
        {
            if(user.getConnectedVia().equals("Guest")) {
                userPic.setImageBitmap(RoundedImageView.getCroppedBitmap(BitmapFactory.decodeResource(globalClass.getResources(), R.drawable.user_icon), 240));
            }
        }
    }

    private void createSwipeRefreshLayout()
    {
        refreshList = (SwipeRefreshLayout) findViewById(R.id.refresh_ListView);
        refreshList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.post(refreshListView);
            }
        });
        refreshList.setColorSchemeResources(R.color.nb,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light,
                android.R.color.holo_red_light);
    }

    private final Runnable refreshListView = new Runnable(){
        public void run(){
            if(!categoriesHandler.getCurrentlyInUseCategoryServer().equals(""))
            {
                updateArticles();
            }
            refreshList.setRefreshing(false);
        }
    };


    private void saveInInternalFolder(String str1, String fileName)
    {
        FileOutputStream fos = null;
        try
        {
            fos = openFileOutput(fileName, this.MODE_PRIVATE);
            fos.write(str1.getBytes());
            fos.close();
        }
        catch (IOException e)
        {
            Toast.makeText(this, "Error write internals", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private String readInInternalFolder(String fileName) {
        Context context = getApplicationContext();
        try {
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line + "\n");
            }
            fis.close();
            return (sb.toString());
        } catch (IOException e) {
            Toast.makeText(this, "Error read internals", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return "";
        }
    }

    private void updateArticles()
    {
        categoriesHandler.getCurrentlyInUse().clear();
        communication.clientSend("114&" + categoriesHandler.getCurrentlyInUseCategoryServer() + "#");
        categoriesHandler.setCurrentlyInUseCategory(categoriesHandler.getCurrentCategoryID(), this);
        while(categoriesHandler.getCurrentlyInUse().size() != 0);
    }

    private void changeCategory(int categoryID)
    {
        categoriesHandler.getCurrentlyInUse().clear();
        communication.clientSend("114&" + categoriesHandler.getCategoriesForServer().get(categoryID) + "#");
        categoriesHandler.setCurrentlyInUseCategory(categoryID, this);
        while(categoriesHandler.getCurrentlyInUse().size() != 0);
    }
}