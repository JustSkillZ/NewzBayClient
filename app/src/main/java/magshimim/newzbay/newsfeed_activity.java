package magshimim.newzbay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebBackForwardList;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

    private static final String explanationPref = "magshimim.newzbay.ExplanationPref" ;
    private static final String isExplanation1 = "isExplanation1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.newsfeed_activity);

        final ImageView loading = (ImageView) findViewById(R.id.iv_nb_loading);
        loading.setVisibility(View.VISIBLE);
        final Animation an = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
        final Animation an2 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.abc_fade_out);

        loading.startAnimation(an);
        an.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                loading.startAnimation(an2);
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        SharedPreferences sharedpreferences = getSharedPreferences(explanationPref, Context.MODE_PRIVATE);
        if (!getSharedPreferences(explanationPref, Context.MODE_PRIVATE).getBoolean(isExplanation1, false))
        {
            Intent welcome = new Intent(this,Explanation.class);
            startActivity(welcome);

            sharedpreferences = getSharedPreferences(explanationPref, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(isExplanation1, true);
            editor.commit();
        }

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
        listadapter = new ArticleAdapter(this, this);
        listView_article.setAdapter(listadapter);
        ((BaseAdapter)listadapter).notifyDataSetChanged();
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
            Categories.setCurrentlyInUse(null);
            FacebookAndGoogle.getCommunication().clientSend("114&israelNewz#");
            Categories.setCurrentlyInUseCategory(1, getApplicationContext());
            while(Categories.getCurrentlyInUse() == null)
            {
                final ImageView loading = (ImageView) findViewById(R.id.iv_nb_loading);
                loading.setVisibility(View.VISIBLE);
                final Animation an = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
                final Animation an2 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.abc_fade_out);

                loading.startAnimation(an);
                an.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        loading.startAnimation(an2);
                        loading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
            listadapter = new ArticleAdapter(newsfeed_activity.this, this);
            listView_article.setAdapter(listadapter);

        } else if (id == R.id.nav_global_news) {
            FacebookAndGoogle.getCommunication().clientSend("114&worldNewz#");
            Categories.setCurrentlyInUseCategory(2, getApplicationContext());

        } else if (id == R.id.nav_politics) {
            FacebookAndGoogle.getCommunication().clientSend("114&politics#");
            Categories.setCurrentlyInUseCategory(3, getApplicationContext());
            listadapter = new ArticleAdapter(newsfeed_activity.this, this);
            listView_article.setAdapter(listadapter);

        } else if (id == R.id.nav_economy) {
            FacebookAndGoogle.getCommunication().clientSend("114&economy#");
            Categories.setCurrentlyInUseCategory(4, getApplicationContext());
            listadapter = new ArticleAdapter(newsfeed_activity.this, this);
            listView_article.setAdapter(listadapter);

        } else if (id == R.id.nav_sport) {
            FacebookAndGoogle.getCommunication().clientSend("114&sport#");
            Categories.setCurrentlyInUseCategory(5, getApplicationContext());
            listadapter = new ArticleAdapter(newsfeed_activity.this, this);
            listView_article.setAdapter(listadapter);

        } else if (id == R.id.nav_culture) {
            FacebookAndGoogle.getCommunication().clientSend("114&culture#");
            Categories.setCurrentlyInUseCategory(6, getApplicationContext());
            listadapter = new ArticleAdapter(newsfeed_activity.this, this);
            listView_article.setAdapter(listadapter);

        } else if (id == R.id.nav_celebrities) {
            FacebookAndGoogle.getCommunication().clientSend("114&celebs#");
            Categories.setCurrentlyInUseCategory(7, getApplicationContext());
            listadapter = new ArticleAdapter(newsfeed_activity.this, this);
            listView_article.setAdapter(listadapter);

        } else if (id == R.id.nav_technology) {
            FacebookAndGoogle.getCommunication().clientSend("114&technology)#");
            Categories.setCurrentlyInUseCategory(8, getApplicationContext());
            listadapter = new ArticleAdapter(newsfeed_activity.this, this);
            listView_article.setAdapter(listadapter);

        } else if (id == R.id.nav_science) {
            FacebookAndGoogle.getCommunication().clientSend("114&technology)#");
            Categories.setCurrentlyInUseCategory(9, getApplicationContext());
            listadapter = new ArticleAdapter(newsfeed_activity.this, this);
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
            FacebookAndGoogle.getCommunication().clientSend("500#"); //Disconnect from the server
            Log.d("Server", "500#");
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

            ((BaseAdapter)listadapter).notifyDataSetChanged();
            listadapter = new ArticleAdapter(newsfeed_activity.this, newsfeed_activity.this);
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
