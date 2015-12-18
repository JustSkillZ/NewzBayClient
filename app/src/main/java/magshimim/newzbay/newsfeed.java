package magshimim.newzbay;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.util.Vector;
import android.os.Handler;

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
}
