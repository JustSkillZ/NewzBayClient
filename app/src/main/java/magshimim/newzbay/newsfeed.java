package magshimim.newzbay;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;
import android.os.Handler;
import android.widget.Toast;

public class newsfeed extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar_main;
    private ListAdapter listadapter;
    private SwipeRefreshLayout refreshList;
    private ListView listView_article;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);
        toolbar_main = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar_main);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        saveInInternalFolder("NewzBay", "check");
        Vector<Article> articles = new Vector<Article>();
        articles.add(new Article("Subject", "כותרת ראשית 1", "Second Headline", null, null, "Site", "http://www.ynet.co.il/articles/0,7340,L-4740637,00.html"));
        articles.add(new Article("Subject", "כותרת ראשית 2", "Second Headline", null, null, "Site", "http://www.google.co.il"));
        articles.add(new Article("Subject", "כותרת ראשית 3", "Second Headline", null, null, "Site", "http://www.facebook.com"));
        listadapter = new ArticleAdapter(this, articles);
        listView_article = (ListView) findViewById(R.id.listView_articles);
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
            articles.add(new Article("Subject", "כותרת ראשית 1", "Second Headline", null, null, "Site", "http://www.ynet.co.il/articles/0,7340,L-4740637,00.html"));
            articles.add(new Article("Subject", "כותרת ראשית 2", "Second Headline", null, null, "Site", "http://www.google.co.il"));
            articles.add(new Article("Subject", "כותרת ראשית 3", "Second Headline", null, null, "Site", "http://www.facebook.com"));
            articles.add(new Article("Subject", "כותרת ראשית 4", "Second Headline", null, null, "Site", "http://www.youtube.com"));

            listadapter = new ArticleAdapter(newsfeed.this, articles);
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
