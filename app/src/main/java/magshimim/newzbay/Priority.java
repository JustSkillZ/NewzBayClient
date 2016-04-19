package magshimim.newzbay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Priority extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_priority);
        Time now = new Time();
        now.setToNow();
        if(now.hour >= 19 || now.hour >= 0 && now.hour <= 5)
        {
            LinearLayout layout =(LinearLayout)findViewById(R.id.activity_priority);
            layout.setBackground(getResources().getDrawable(R.drawable.main_background_night));
            for (int i = 0; i < layout.getChildCount(); i++) {
                View v = layout.getChildAt(i);
                for(int j = 0; j < ((ViewGroup)v).getChildCount(); j++)
                {
                    View nextChild = ((ViewGroup)v).getChildAt(j);
                    if (nextChild instanceof TextView) {
                        ((TextView) nextChild).setTextColor(getResources().getColor(R.color.white));
                    }
                }
            }
            Button sendPriority = (Button) findViewById(R.id.btn_endPriority);
            sendPriority.setBackground(getResources().getDrawable(R.drawable.button_rounded_corners));

            TextView mainHeadline = (TextView) findViewById(R.id.tv_priority_mainHeadLine);
            mainHeadline.setTextColor(getResources().getColor(R.color.orange));

            TextView explanation = (TextView) findViewById(R.id.tv_explanation);
            explanation.setTextColor(getResources().getColor(R.color.disabledButton));
        }
        Button sendPriority = (Button) findViewById(R.id.btn_endPriority);
        sendPriority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void choosePriorityBySubject(View v)
    {
        ((GlobalClass) getApplicationContext()).getPriorityHandler().setCurrentPrioritySubject(v.getContentDescription().toString());
        Intent priority = new Intent(this,ChoosePriority.class);
        startActivity(priority);
    }
}