package magshimim.newzbay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebBackForwardList;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class InnerWeb extends AppCompatActivity {

    private WebView web;
    private WebBackForwardList webBackForwardList;
    private android.support.v7.widget.Toolbar toolbar_web;
    private CategoriesHandler categoriesHandler;

    private static final String prefsConnection = "magshimim.newzbay.ConnectionPrefs";
    private static final String isExplanation2 = "isExplanation2";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_web);

        categoriesHandler = ((GlobalClass) getApplicationContext()).getCategoriesHandler();

        if (!getSharedPreferences(prefsConnection, Context.MODE_PRIVATE).getBoolean(isExplanation2, false))
        {
            Intent explanation2 = new Intent(this,ExplanationInnerWeb.class);
            startActivity(explanation2);

            SharedPreferences sharedpreferences = getSharedPreferences(prefsConnection, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(isExplanation2, true);
            editor.commit();
        }
        createWebView();
        toolbar_web = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_web);
        setSupportActionBar(toolbar_web);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        web.loadUrl(categoriesHandler.getCurrentlyOpenURL());
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

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if(web.getVisibility() != View.GONE)
            {
                closeWeb(null);
            }
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    private void createWebView()
    {
        web = (WebView) findViewById(R.id.wv_article);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setDisplayZoomControls(false);
        web.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

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
        categoriesHandler.setCurrentlyOpenURL("");
        finish();
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