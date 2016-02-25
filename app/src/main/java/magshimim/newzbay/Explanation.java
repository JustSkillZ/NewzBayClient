package magshimim.newzbay;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Explanation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explanation);
        android.support.v7.widget.Toolbar toolbar_main = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar_main);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_main.setNavigationIcon(R.drawable.nb_white);
    }

    public void closeExplanation(View v)
    {
        finish();
    }

    @Override
    public void onBackPressed() {
    }
}
