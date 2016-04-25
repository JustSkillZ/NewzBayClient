package magshimim.newzbay;

import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.Time;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChoosePriority extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private GlobalClass globalClass;
    private PriorityHandler priorityHandler;
    private RecyclerView recyclerView_sites;
    private RecyclerView.Adapter recyclerAdapter;
    private android.support.v7.widget.LinearLayoutManager recyclerLayoutManager;
    private PopupMenu addRemoved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_priority);
        globalClass = (GlobalClass) getApplicationContext();
        priorityHandler = globalClass.getPriorityHandler();
        priorityHandler.getRemovedSitesOfCurrentSubject().clear();
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
            ImageView recyclerViewStroke = (ImageView) findViewById(R.id.rv_orderSites_stroke);
            recyclerViewStroke.setBackground(getResources().getDrawable(R.drawable.rounded_stroke_orange));
        }

        recyclerView_sites = (RecyclerView) findViewById(R.id.rv_orderSites);
        recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerView_sites.setLayoutManager(recyclerLayoutManager);
        recyclerAdapter =  new SitesAdapter((GlobalClass)getApplicationContext());
        recyclerView_sites.setAdapter(recyclerAdapter);
        ItemTouchHelper.Callback callback = new DragNSwipeHelper((SitesAdapter)recyclerAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView_sites);


    }

    public void sendPriority(View v)
    {
        globalClass.getCommunication().clientSend("194◘" + priorityHandler.getCurrentPrioritySubject() + "#");
        String priority = "104◘";
        for(int i = 0; i < priorityHandler.getClientsPriority().size(); i++)
        {
            for(int j = 0; j < priorityHandler.getCategorySites().size(); j++)
            {
                if(priorityHandler.getCategorySites().get(j).getSubject().equals(priorityHandler.getCurrentPrioritySubject()))
                {
                    if(priorityHandler.getCategorySites().get(j).getSite().equals(priorityHandler.getClientsPriority().get(i)))
                    {
                        priority = priority + priorityHandler.getCategorySites().get(j).getId() + "○" + (i + 1) + "◘";
                    }
                }
            }
        }
        priority = priority.substring(0, priority.length() - 1);
        priority = priority + "#";
        if(priorityHandler.getClientsPriority().size()!= 0)
        {
            globalClass.getCommunication().clientSend(priority);
        }
        finish();
    }

    public void addRemovedSites(View v) {
        addRemoved = new PopupMenu(this, v);
        addRemoved.setOnMenuItemClickListener(this);
        for(int i = 0; i < priorityHandler.getRemovedSitesOfCurrentSubject().size(); i++)
        {
            addRemoved.getMenu().add( priorityHandler.getRemovedSitesOfCurrentSubject().get(i));
        }
//        MenuInflater inflater = addRemoved.getMenuInflater();
//        inflater.inflate(R.menu.newsfeed_activity_drawer, addRemoved.getMenu());
        addRemoved.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int position = -1;
        for(int i = 0; i < priorityHandler.getRemovedSitesOfCurrentSubject().size(); i++)
        {
            if(priorityHandler.getRemovedSitesOfCurrentSubject().get(i).equals(item.getTitle()))
            {
                position = i;
            }
        }
        priorityHandler.getClientsPriority().add(priorityHandler.getRemovedSitesOfCurrentSubject().get(position));
        priorityHandler.getRemovedSitesOfCurrentSubject().remove(position);
        addRemoved.getMenu().removeItem(item.getItemId());
        recyclerAdapter.notifyDataSetChanged();
        return true;
    }
}