package magshimim.newzbay;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebBackForwardList;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class newsfeed_activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private android.support.v7.widget.Toolbar toolbar_main;
    private android.support.v7.widget.Toolbar toolbar_web;
    private ListAdapter listadapter;
    private SwipeRefreshLayout refreshList;
    private ListView listView_article;
    private Handler handler = new Handler();
    private WebView web;
    private WebBackForwardList webBackForwardList;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newsfeed_activity);
        createWebView();
        toolbar_web = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_web);
        setSupportActionBar(toolbar_web);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_web.setVisibility(View.GONE);
        toolbar_main = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar_main);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        saveInInternalFolder("NewzBay", "check");
        Vector<Article> articles = new Vector<Article>();
        articles.add(new Article("Subject", "כותרת ראשית 1", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.nb_white), null, "Site", "http://www.ynet.co.il/articles/0,7340,L-4740637,00.html", 605, 24, true));
        articles.add(new Article("Subject", "כותרת ראשית 2", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.nb_white), null, "Site", "http://www.google.co.il", 524, 53, false));
        articles.add(new Article("Subject", "כותרת ראשית 3", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.nb_white), null, "Site", "http://www.facebook.com", 106, 40, true));
        listView_article = (ListView) findViewById(R.id.listView_articles);
        listadapter = new ArticleAdapter(this, articles, web, toolbar_main, toolbar_web);
        listView_article.setAdapter(listadapter);
        createSwipeRefreshLayout();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar_main, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);
        toolbar_main.setNavigationIcon(R.drawable.nb_white);
        toolbar_main.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        web.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        web.onResume();
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        else if(web.getVisibility() != View.GONE) {
            if(web.canGoBack()) {
                webBackForwardList = web.copyBackForwardList();
                if(webBackForwardList.getCurrentIndex() == 1)
                {
                    closeWeb(null);
                }
                else
                {
                    web.goBack();
                }
            }
            else
            {
                closeWeb(null);
            }
        }

        else {
            super.onBackPressed();
        }
    }

/** NOT NEEDED FOR NOW
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.newsfeed_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
**/
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_news) {
            // Handle the camera action
        } else if (id == R.id.nav_economy) {

        } else if (id == R.id.nav_sport) {

        } else if (id == R.id.nav_settings) {
            Intent settings = new Intent(this, settings_activity.class);
            startActivity(settings);
            this.onStop();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
            articles.add(new Article("Subject", "כותרת ראשית 1", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.nb_white), null, "Site", "http://www.ynet.co.il/articles/0,7340,L-4740637,00.html", 605, 24, true));
            articles.add(new Article("Subject", "כותרת ראשית 2", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.nb_white), null, "Site", "http://www.google.co.il", 524, 53, false));
            articles.add(new Article("Subject", "כותרת ראשית 3", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.nb_white), null, "Site", "http://www.facebook.com", 106, 40, true));
            articles.add(new Article("Subject", "כותרת ראשית 4", "Second Headline", BitmapFactory.decodeResource(getResources(), R.drawable.nb_white), null, "Site", "http://www.youtube.com", 275, 404, true));

            listadapter = new ArticleAdapter(newsfeed_activity.this, articles, web, toolbar_main, toolbar_web);
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

    private void createWebView()
    {
        web = (WebView) findViewById(R.id.wv_article);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setDisplayZoomControls(false);
        web.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (web.getVisibility() == View.GONE) {
                    web.clearHistory();
                }
                treatPageControllers();
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                treatPageControllers();
            }
        });
        web.loadUrl("about:blank");
    }


    public void closeWeb(View v)
    {
        toolbar_web.setVisibility(View.GONE);
        toolbar_main.setVisibility(View.VISIBLE);
        web.setVisibility(View.GONE);
        web.loadUrl("about:blank");
    }

    public void treatPageControllers()
    {
        webBackForwardList = web.copyBackForwardList();
        Button previousPage = (Button) toolbar_web.findViewById(R.id.btn_previousPage);
        Button nextPage = (Button) toolbar_web.findViewById(R.id.btn_nextPage);
        if (webBackForwardList.getCurrentIndex() <= 1) {
            previousPage.setAlpha((float) 0.65);
            previousPage.getBackground().setColorFilter(getResources().getColor(R.color.disabledButton), PorterDuff.Mode.MULTIPLY);
        } else {
            previousPage.setAlpha((float) 1);
            previousPage.getBackground().setColorFilter(null);
        }

        if (webBackForwardList.getCurrentIndex() == webBackForwardList.getSize() - 1) {
            nextPage.setAlpha((float) 0.65);
            nextPage.getBackground().setColorFilter(getResources().getColor(R.color.disabledButton), PorterDuff.Mode.MULTIPLY);
        } else {
            nextPage.setAlpha((float) 1);
            nextPage.getBackground().setColorFilter(null);
        }
    }

    public void reloadWebPage(View v)
    {
        web.reload();
    }

    public void previousPage(View v)
    {
        if(web.canGoBack())
        {
            webBackForwardList = web.copyBackForwardList();
            if(webBackForwardList.getCurrentIndex() > 1)
            {
                web.goBack();
            }
        }
    }

    public void nextPage(View v)
    {
        if(web.canGoForward())
        {
            web.goForward();
        }
    }
}

