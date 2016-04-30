package magshimim.newzbay;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ExplanationInnerWeb extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explanation_innerweb);
        android.support.v7.widget.Toolbar toolbarWeb = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_web);
        setSupportActionBar(toolbarWeb);
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
