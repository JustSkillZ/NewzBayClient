package magshimim.newzbay;

import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChoosePriority extends AppCompatActivity {

    private GlobalClass globalClass;
    private PriorityHandler priorityHandler;
    private RecyclerView recyclerView_sites;
    private RecyclerView.Adapter recyclerAdapter;
    private android.support.v7.widget.LinearLayoutManager recyclerLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_priority);
        globalClass = (GlobalClass) getApplicationContext();
        priorityHandler = globalClass.getPriorityHandler();

        TextView subject = (TextView) findViewById(R.id.tv_subject);
        subject.setText(priorityHandler.getSubjects().get(priorityHandler.getCurrentPrioritySubject()));
        Time now = new Time();
        now.setToNow();
        if(now.hour >= 19 || now.hour >= 0 && now.hour <= 5)
        {
            RelativeLayout layout =(RelativeLayout)findViewById(R.id.activity_choosePriority);
            layout.setBackground(getResources().getDrawable(R.drawable.main_background_night));
            subject.setTextColor(getResources().getColor(R.color.orange));
            ImageButton addSite = (ImageButton) findViewById(R.id.btn_add);
            addSite.getBackground().setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_ATOP);
            Button sendPriority = (Button) findViewById(R.id.btn_sendPriority);
            sendPriority.setBackground(getResources().getDrawable(R.drawable.button_rounded_corners));
        }

        recyclerView_sites = (RecyclerView) findViewById(R.id.rv_orderSites);
        recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerView_sites.setLayoutManager(recyclerLayoutManager);
        recyclerAdapter =  new SitesAdapter((GlobalClass)getApplicationContext());
        recyclerView_sites.setAdapter(recyclerAdapter);
    }
}
