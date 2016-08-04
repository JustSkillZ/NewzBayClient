package magshimim.newzbay;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

public class ActivityInnerWeb extends AppCompatActivity
{

    private static final String prefsConnection = "magshimim.newzbay.ConnectionPrefs";
    private static final String isExplanation2 = "isExplanation2";
    private WebView web;
    private WebBackForwardList webBackForwardList;
    private android.support.v7.widget.Toolbar toolbarWeb;
    private CategoriesHandler categoriesHandler;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_web);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(100);
        progressBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.white), android.graphics.PorterDuff.Mode.SRC_IN));
        categoriesHandler = ((GlobalClass) getApplicationContext()).getCategoriesHandler();
        ((GlobalClass) getApplicationContext()).setCurrentActivity(ActivityInnerWeb.this);
        if (!getSharedPreferences(prefsConnection, Context.MODE_PRIVATE).getBoolean(isExplanation2, false)) //If opened this screen first time, open explanation activity.
        {
            Intent explanation2 = new Intent(this, ActivityExplanationInnerWeb.class);
            startActivity(explanation2);

            SharedPreferences sharedpreferences = getSharedPreferences(prefsConnection, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(isExplanation2, true);
            editor.commit();
        }
        createWebView();
        toolbarWeb = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_web);
        setSupportActionBar(toolbarWeb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        web.loadUrl(categoriesHandler.getCurrentlyOpenURL()); //Load article's web page
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        web.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        web.onResume();
    }

    @Override
    public void onBackPressed()
    {

        if (web.canGoBack()) //If there are any pages before
        {
            webBackForwardList = web.copyBackForwardList();
            if (webBackForwardList.getCurrentIndex() == 1)  //If there is 1 page before
            {
                closeWeb(null); //Close it, because the page on index 0 is a white page...
            }
            else //Go to previous page
            {
                web.goBack();
            }
        }
        else //Close it
        {
            closeWeb(null);
        }
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if (web.getVisibility() != View.GONE)
            {
                closeWeb(null);
            }
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    private void createWebView() //Init the WebView and customize its settings
    {
        web = (WebView) findViewById(R.id.wv_article);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setDisplayZoomControls(false);
        web.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress)
            {
                progressBar.setProgress(progress);
                if(progress == 100)
                {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        web.setWebViewClient(new WebViewClient()
        {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                super.onPageFinished(view, url);
                if (web.getVisibility() == View.GONE)
                {
                    web.clearHistory();
                }
                treatPageControllers();
            }

            @Override
            public void onLoadResource(WebView view, String url)
            {
                super.onLoadResource(view, url);
                treatPageControllers();
            }
        });
        web.loadUrl("about:blank");
    }


    public void closeWeb(View v) //Close the activity
    {
        categoriesHandler.setCurrentlyOpenURL("");
        finish();
    }

    public void treatPageControllers() //Handle forward and backward controllers
    {
        webBackForwardList = web.copyBackForwardList();
        Button previousPage = (Button) toolbarWeb.findViewById(R.id.btn_previousPage);
        Button nextPage = (Button) toolbarWeb.findViewById(R.id.btn_nextPage);
        if (webBackForwardList.getCurrentIndex() <= 1) //If you cant go backwards
        {
            previousPage.setAlpha((float) 0.65);
            previousPage.getBackground().setColorFilter(getResources().getColor(R.color.disabledButton), PorterDuff.Mode.MULTIPLY);
        }
        else
        {
            previousPage.setAlpha((float) 1);
            previousPage.getBackground().setColorFilter(null);
        }

        if (webBackForwardList.getCurrentIndex() == webBackForwardList.getSize() - 1) //If you cant go forward
        {
            nextPage.setAlpha((float) 0.65);
            nextPage.getBackground().setColorFilter(getResources().getColor(R.color.disabledButton), PorterDuff.Mode.MULTIPLY);
        }
        else
        {
            nextPage.setAlpha((float) 1);
            nextPage.getBackground().setColorFilter(null);
        }
    }

    public void reloadWebPage(View v) //Refresh page
    {
        web.reload();
    }

    public void previousPage(View v) //Go backward (previous page)
    {
        if (web.canGoBack())
        {
            webBackForwardList = web.copyBackForwardList();
            if (webBackForwardList.getCurrentIndex() > 1) //First page is blank
            {
                web.goBack();
            }
        }
    }

    public void nextPage(View v) //Go forward (next page)
    {
        if (web.canGoForward())
        {
            web.goForward();
        }
    }
}