package magshimim.newzbay;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class OpeningActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);
        Intent entrance = new Intent(OpeningActivity.this, ActivityEntrance.class);
        this.startActivity(entrance);
    }
}
