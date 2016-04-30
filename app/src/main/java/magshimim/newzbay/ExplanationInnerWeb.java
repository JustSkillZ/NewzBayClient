package magshimim.newzbay;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ExplanationInnerWeb extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explanation_innerweb);
        android.support.v7.widget.Toolbar toolbar_web = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_web);
        setSupportActionBar(toolbar_web);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public void closeExplanation(View v)
    {
        finish();
    }

    @Override
    public void onBackPressed() {
    }
}
