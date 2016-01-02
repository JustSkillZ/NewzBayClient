package magshimim.newzbay;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebBackForwardList;
import android.webkit.WebView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class newsfeed extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar_main;
    private android.support.v7.widget.Toolbar toolbar_web;
    private ListAdapter listadapter;
    private SwipeRefreshLayout refreshList;
    private ListView listView_article;
    private Handler handler = new Handler();
    private WebView web;
    private WebBackForwardList webBackForwardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);

        toolbar_web = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_web);
        setSupportActionBar(toolbar_web);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_web.setVisibility(View.GONE);
        web = (WebView) findViewById(R.id.wv_article);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setDisplayZoomControls(false);
        toolbar_main = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar_main);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        saveInInternalFolder("NewzBay", "check");
        Vector<Article> articles = new Vector<Article>();
        articles.add(new Article("Subject", "כותרת ראשית 1", "Second Headline", null, null, "Site", "http://www.ynet.co.il/articles/0,7340,L-4740637,00.html", 605, 24, true));
        articles.add(new Article("Subject", "כותרת ראשית 2", "Second Headline", null, null, "Site", "http://www.google.co.il", 524, 53, false));
        articles.add(new Article("Subject", "כותרת ראשית 3", "Second Headline", null, null, "Site", "http://www.facebook.com", 106, 40, true));
        listView_article = (ListView) findViewById(R.id.listView_articles);
        listadapter = new ArticleAdapter(this, articles, web, toolbar_main, toolbar_web);
        listView_article.setAdapter(listadapter);
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
        Toast.makeText(this,("the string is: " + readInInternalFolder("check")), Toast.LENGTH_SHORT).show();
    }

    private final Runnable refreshListView = new Runnable(){
        public void run(){
            Vector<Article> articles = new Vector<Article>();
            articles.add(new Article("Subject", "כותרת ראשית 1", "Second Headline", null, null, "Site", "http://www.ynet.co.il/articles/0,7340,L-4740637,00.html", 605, 24, true));
            articles.add(new Article("Subject", "כותרת ראשית 2", "Second Headline", null, null, "Site", "http://www.google.co.il", 524, 53, false));
            articles.add(new Article("Subject", "כותרת ראשית 3", "Second Headline", null, null, "Site", "http://www.facebook.com", 106, 40, true));
            articles.add(new Article("Subject", "כותרת ראשית 4", "Second Headline", null, null, "Site", "http://www.youtube.com", 275, 404, true));

            listadapter = new ArticleAdapter(newsfeed.this, articles, web, toolbar_main, toolbar_web);
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

    @Override
    public void onBackPressed() {
        if(web.getVisibility() != View.GONE) {
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

    public void closeWeb(View v)
    {
        toolbar_web.setVisibility(View.GONE);
        toolbar_main.setVisibility(View.VISIBLE);
        web.setVisibility(View.GONE);
        web.loadUrl("about:blank");
    }

    public void reloadWebPage(View v)
    {
        web.reload();
        v.setAlpha((float)0.65);
        v.getBackground().setColorFilter(getResources().getColor(R.color.disabledButton), PorterDuff.Mode.MULTIPLY);
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
