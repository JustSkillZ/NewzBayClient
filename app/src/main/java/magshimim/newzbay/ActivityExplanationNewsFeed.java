package magshimim.newzbay;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ActivityExplanationNewsFeed extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explanation_newsfeed);
        android.support.v7.widget.Toolbar toolbarMain = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbarMain);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarMain.setNavigationIcon(R.drawable.anchor);
    }

    public void closeExplanation(View v)
    {
        finish();
    }

    @Override
    public void onBackPressed()
    {
    }
}
