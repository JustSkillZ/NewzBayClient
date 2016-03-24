package magshimim.newzbay;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Explanation2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explanation2);
        android.support.v7.widget.Toolbar toolbar_web = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_web);
        setSupportActionBar(toolbar_web);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public void closeExplanation(View v)
    {
        finish();
    }

    public void closeWeb(View v)
    {

    }

    public void previousPage(View v)
    {

    }

    public void reloadWebPage(View v)
    {

    }

    public void nextPage(View v)
    {

    }

    @Override
    public void onBackPressed() {
    }
}
