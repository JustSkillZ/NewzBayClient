package magshimim.newzbay;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebBackForwardList;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.plus.Plus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class newsfeed_activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private android.support.v7.widget.Toolbar toolbar_main;
    private ListAdapter listadapter;
    private SwipeRefreshLayout refreshList;
    private ListView listView_article;
    private Handler handler = new Handler();
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //--------------------------------------------------------------------------------------------------TEST-------------------------------------------------------------------------------------------------------------------------------------------------
        Vector<Article> articles = new Vector<Article>();
        articles.add(new Article("Subject", "כותרת ראשית 1", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "ynet", "http://www.ynet.co.il", 605, 24, true));
        Categories.setNews(articles);
        articles = new Vector<Article>();
        articles.add(new Article("Subject", "כותרת ראשית 1", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "ynet", "http://www.ynet.co.il", 605, 24, true));
        articles.add(new Article("Subject", "כותרת ראשית 2", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "ONE", "http://www.one.co.il", 524, 53, false));
        Categories.setGlobalNews(articles);
        articles = new Vector<Article>();
        articles.add(new Article("Subject", "כותרת ראשית 1", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "ynet", "http://www.ynet.co.il", 605, 24, true));
        articles.add(new Article("Subject", "כותרת ראשית 2", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "ONE", "http://www.one.co.il", 524, 53, false));
        articles.add(new Article("Subject", "כותרת ראשית 3", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "mako", "http://www.mako.co.il/", 106, 40, true));
        Categories.setPolitics(articles);
        articles = new Vector<Article>();
        articles.add(new Article("Subject", "כותרת ראשית 1", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "ynet", "http://www.ynet.co.il", 605, 24, true));
        articles.add(new Article("Subject", "כותרת ראשית 2", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "ONE", "http://www.one.co.il", 524, 53, false));
        articles.add(new Article("Subject", "כותרת ראשית 3", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "mako", "http://www.mako.co.il/", 106, 40, true));
        articles.add(new Article("Subject", "כותרת ראשית 4", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "9 канал", "http://9tv.co.il/", 106, 40, true));
        Categories.setEconomy(articles);
        articles = new Vector<Article>();
        articles.add(new Article("Subject", "כותרת ראשית 1", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "ynet", "http://www.ynet.co.il", 605, 24, true));
        articles.add(new Article("Subject", "כותרת ראשית 2", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "ONE", "http://www.one.co.il", 524, 53, false));
        articles.add(new Article("Subject", "כותרת ראשית 3", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "mako", "http://www.mako.co.il/", 106, 40, true));
        articles.add(new Article("Subject", "כותרת ראשית 4", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "9 канал", "http://9tv.co.il/", 106, 40, true));
        articles.add(new Article("Subject", "כותרת ראשית 5", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "נענע 10", "http://www.nana10.co.il/", 106, 40, true));
        Categories.setSport(articles);
        articles = new Vector<Article>();
        articles.add(new Article("Subject", "כותרת ראשית 1", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "ynet", "http://www.ynet.co.il", 605, 24, true));
        articles.add(new Article("Subject", "כותרת ראשית 2", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "ONE", "http://www.one.co.il", 524, 53, false));
        articles.add(new Article("Subject", "כותרת ראשית 3", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "mako", "http://www.mako.co.il/", 106, 40, true));
        articles.add(new Article("Subject", "כותרת ראשית 4", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "9 канал", "http://9tv.co.il/", 106, 40, true));
        articles.add(new Article("Subject", "כותרת ראשית 5", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "נענע 10", "http://www.nana10.co.il/", 106, 40, true));
        articles.add(new Article("Subject", "כותרת ראשית 6", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "Geektime", "http://www.geektime.co.il/", 106, 40, true));
        Categories.setCulture(articles);
        articles = new Vector<Article>();
        articles.add(new Article("Subject", "כותרת ראשית 1", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "ynet", "http://www.ynet.co.il", 605, 24, true));
        articles.add(new Article("Subject", "כותרת ראשית 2", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "ONE", "http://www.one.co.il", 524, 53, false));
        articles.add(new Article("Subject", "כותרת ראשית 3", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "mako", "http://www.mako.co.il/", 106, 40, true));
        articles.add(new Article("Subject", "כותרת ראשית 4", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "9 канал", "http://9tv.co.il/", 106, 40, true));
        articles.add(new Article("Subject", "כותרת ראשית 5", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "נענע 10", "http://www.nana10.co.il/", 106, 40, true));
        articles.add(new Article("Subject", "כותרת ראשית 6", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "Geektime", "http://www.geektime.co.il/", 106, 40, true));
        articles.add(new Article("Subject", "כותרת ראשית 7", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "NEXTER", "http://www.mako.co.il/nexter", 106, 40, true));
        Categories.setCelebrities(articles);
        articles = new Vector<Article>();
        articles.add(new Article("Subject", "כותרת ראשית 1", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "ynet", "http://www.ynet.co.il", 605, 24, true));
        articles.add(new Article("Subject", "כותרת ראשית 2", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "ONE", "http://www.one.co.il", 524, 53, false));
        articles.add(new Article("Subject", "כותרת ראשית 3", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "mako", "http://www.mako.co.il/", 106, 40, true));
        articles.add(new Article("Subject", "כותרת ראשית 4", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "9 канал", "http://9tv.co.il/", 106, 40, true));
        articles.add(new Article("Subject", "כותרת ראשית 5", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "נענע 10", "http://www.nana10.co.il/", 106, 40, true));
        articles.add(new Article("Subject", "כותרת ראשית 6", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "Geektime", "http://www.geektime.co.il/", 106, 40, true));
        articles.add(new Article("Subject", "כותרת ראשית 7", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "NEXTER", "http://www.mako.co.il/nexter", 106, 40, true));
        articles.add(new Article("Subject", "כותרת ראשית 8", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "ישראל היום", "http://www.israelhayom.co.il/", 106, 40, true));
        Categories.setTechnology(articles);
        articles = new Vector<Article>();
        articles.add(new Article("Subject", "כותרת ראשית 1", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "ynet", "http://www.ynet.co.il", 605, 24, true));
        articles.add(new Article("Subject", "כותרת ראשית 2", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "ONE", "http://www.one.co.il", 524, 53, false));
        articles.add(new Article("Subject", "כותרת ראשית 3", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "mako", "http://www.mako.co.il/", 106, 40, true));
        articles.add(new Article("Subject", "כותרת ראשית 4", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "9 канал", "http://9tv.co.il/", 106, 40, true));
        articles.add(new Article("Subject", "כותרת ראשית 5", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "נענע 10", "http://www.nana10.co.il/", 106, 40, true));
        articles.add(new Article("Subject", "כותרת ראשית 6", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "Geektime", "http://www.geektime.co.il/", 106, 40, true));
        articles.add(new Article("Subject", "כותרת ראשית 7", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "NEXTER", "http://www.mako.co.il/nexter", 106, 40, true));
        articles.add(new Article("Subject", "כותרת ראשית 8", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "ישראל היום", "http://www.israelhayom.co.il/", 106, 40, true));
        articles.add(new Article("Subject", "כותרת ראשית 9", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "כלכליסט", "http://www.calcalist.co.il/", 106, 40, true));
        Categories.setScience(articles);
        articles = new Vector<Article>();
        articles.add(new Article("Subject", "כותרת ראשית 1", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "ynet", "http://www.ynet.co.il", 605, 24, true));
        articles.add(new Article("Subject", "כותרת ראשית 2", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "ONE", "http://www.one.co.il", 524, 53, false));
        articles.add(new Article("Subject", "כותרת ראשית 3", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "mako", "http://www.mako.co.il/", 106, 40, true));
        articles.add(new Article("Subject", "כותרת ראשית 4", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "9 канал", "http://9tv.co.il/", 106, 40, true));
        articles.add(new Article("Subject", "כותרת ראשית 5", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "נענע 10", "http://www.nana10.co.il/", 106, 40, true));
        articles.add(new Article("Subject", "כותרת ראשית 6", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "Geektime", "http://www.geektime.co.il/", 106, 40, true));
        articles.add(new Article("Subject", "כותרת ראשית 7", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "NEXTER", "http://www.mako.co.il/nexter", 106, 40, true));
        articles.add(new Article("Subject", "כותרת ראשית 8", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "ישראל היום", "http://www.israelhayom.co.il/", 106, 40, true));
        articles.add(new Article("Subject", "כותרת ראשית 9", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "כלכליסט", "http://www.calcalist.co.il/", 106, 40, true));
        articles.add(new Article("Subject", "כותרת ראשית 10", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "וואלה!", "http://www.walla.co.il/", 106, 40, true));
        Categories.setHotNews(articles);
        //--------------------------------------------------------------------------------------------------TEST-------------------------------------------------------------------------------------------------------------------------------------------------
        Categories.setCurrentlyInUseCategory(1, getApplicationContext());

        setContentView(R.layout.newsfeed_activity);
        Intent welcome = new Intent(this,Explanation.class);
        startActivity(welcome);
        if(FacebookAndGoogle.isLoggedWithFacebook())
        {
            FacebookAndGoogle.getBitmapFromURL(FacebookAndGoogle.getCurrentFacebookProfile().getProfilePictureUri(500, 500).toString());
            FacebookAndGoogle.setFullName(FacebookAndGoogle.getCurrentFacebookProfile().getName());
        }
        else if(FacebookAndGoogle.isLoggedWithGoogle())
        {
            FacebookAndGoogle.getBitmapFromURL(FacebookAndGoogle.getCurrentGoogleProfile().getImage().getUrl().replace("sz=50", "sz=500").toString());
            FacebookAndGoogle.setFullName(FacebookAndGoogle.getCurrentGoogleProfile().getDisplayName());
        }
        toolbar_main = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar_main);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        for (int i = 0; i <= toolbar_main.getChildCount(); i++) {
            View v = toolbar_main.getChildAt(i);
            if (v instanceof TextView) {
                ((TextView) v).setText(Categories.getCurrentlyInUseCategory());
            }
        }

        saveInInternalFolder("NewzBay", "check");

        listView_article = (ListView) findViewById(R.id.listView_articles);
        listadapter = new ArticleAdapter(this);
        listView_article.setAdapter(listadapter);
        createSwipeRefreshLayout();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_hot_news) {
            Intent intent = new Intent(this, ExploreArticles.class);
            startActivity(intent);
            this.onStop();
        } else if (id == R.id.nav_news) {
            Categories.setCurrentlyInUseCategory(1, getApplicationContext());
            listadapter = new ArticleAdapter(newsfeed_activity.this);
            listView_article.setAdapter(listadapter);

        } else if (id == R.id.nav_global_news) {
            Categories.setCurrentlyInUseCategory(2, getApplicationContext());
            listadapter = new ArticleAdapter(newsfeed_activity.this);
            listView_article.setAdapter(listadapter);

        } else if (id == R.id.nav_politics) {
            Categories.setCurrentlyInUseCategory(3, getApplicationContext());
            listadapter = new ArticleAdapter(newsfeed_activity.this);
            listView_article.setAdapter(listadapter);

        } else if (id == R.id.nav_economy) {
            Categories.setCurrentlyInUseCategory(4, getApplicationContext());
            listadapter = new ArticleAdapter(newsfeed_activity.this);
            listView_article.setAdapter(listadapter);

        } else if (id == R.id.nav_sport) {
            Categories.setCurrentlyInUseCategory(5, getApplicationContext());
            listadapter = new ArticleAdapter(newsfeed_activity.this);
            listView_article.setAdapter(listadapter);

        } else if (id == R.id.nav_culture) {
            Categories.setCurrentlyInUseCategory(6, getApplicationContext());
            listadapter = new ArticleAdapter(newsfeed_activity.this);
            listView_article.setAdapter(listadapter);

        } else if (id == R.id.nav_celebrities) {
            Categories.setCurrentlyInUseCategory(7, getApplicationContext());
            listadapter = new ArticleAdapter(newsfeed_activity.this);
            listView_article.setAdapter(listadapter);

        } else if (id == R.id.nav_technology) {
            Categories.setCurrentlyInUseCategory(8, getApplicationContext());
            listadapter = new ArticleAdapter(newsfeed_activity.this);
            listView_article.setAdapter(listadapter);

        } else if (id == R.id.nav_science) {
            Categories.setCurrentlyInUseCategory(9, getApplicationContext());
            listadapter = new ArticleAdapter(newsfeed_activity.this);
            listView_article.setAdapter(listadapter);

        } else if (id == R.id.nav_settings) {
            Intent settings = new Intent(this, settings_activity.class);
            startActivity(settings);
            this.onStop();
        }
        else if (id == R.id.nav_discconect) {
            if (FacebookAndGoogle.isLoggedWithGoogle()) {
                Plus.AccountApi.clearDefaultAccount(FacebookAndGoogle.getmGoogleApiClient());
                FacebookAndGoogle.getmGoogleApiClient().disconnect();
                FacebookAndGoogle.getmGoogleApiClient().connect();
            }
            else if(FacebookAndGoogle.isLoggedWithFacebook())
            {
                FacebookAndGoogle.reset(BitmapFactory.decodeResource(getResources(), R.drawable.user_icon));
                LoginManager.getInstance().logOut();
            }
            FacebookAndGoogle.reset(BitmapFactory.decodeResource(getResources(), R.drawable.user_icon));
            Intent intent = new Intent(this, entrance.class);
            startActivity(intent);
            finish();
        }
        for (int i = 0; i <= toolbar_main.getChildCount(); i++) {
            View v = toolbar_main.getChildAt(i);
            if (v instanceof TextView) {
                ((TextView) v).setText(Categories.getCurrentlyInUseCategory());
            }
        }
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
            userFullName.setText(FacebookAndGoogle.getFullName());
        }
        ImageButton userPic = (ImageButton) drawer.findViewById(R.id.ib_userPic);
        if(userPic != null)
        {
            userPic.setImageBitmap(RoundedImageView.getCroppedBitmap(FacebookAndGoogle.getProfilePic(), 240));
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
            Vector<Article> articles = new Vector<Article>();
            articles.add(new Article("Subject", "כותרת ראשית 1", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "ynet", "http://www.ynet.co.il", 605, 24, true));
            articles.add(new Article("Subject", "כותרת ראשית 2", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "ONE", "http://www.one.co.il", 524, 53, false));
            articles.add(new Article("Subject", "כותרת ראשית 3", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "mako", "http://www.mako.co.il/", 106, 40, true));
            articles.add(new Article("Subject", "כותרת ראשית 4", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.anchor), null, "9 канал", "http://9tv.co.il/", 106, 40, true));

            Categories.setNews(articles);
            Categories.setCurrentlyInUseCategory(Categories.getCurrentCategoryID(), getApplicationContext());

            listadapter = new ArticleAdapter(newsfeed_activity.this);
            listView_article.setAdapter(listadapter);
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
}
