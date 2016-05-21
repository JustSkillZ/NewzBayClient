package magshimim.newzbay;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.Time;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ActivityChoosePriority extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener
{

    private GlobalClass globalClass;
    private PriorityHandler priorityHandler;
    private RecyclerView recyclerViewSites;
    private RecyclerView.Adapter recyclerAdapter;
    private android.support.v7.widget.LinearLayoutManager recyclerLayoutManager;
    private PopupMenu addRemoved;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_priority);
        globalClass = (GlobalClass) getApplicationContext();
        priorityHandler = globalClass.getPriorityHandler();
        priorityHandler.getRemovedSitesOfCurrentSubject().clear();
        priorityHandler.getClientPriority().clear();
        globalClass.getCommunication().clientSend("128◘" + priorityHandler.getCurrentPrioritySubject() + "#");
        TextView subject = (TextView) findViewById(R.id.tv_subject);
        subject.setText(priorityHandler.getSubjects().get(priorityHandler.getCurrentPrioritySubject()));
        Time now = new Time();
        now.setToNow();
        if (now.hour >= 19 || now.hour >= 0 && now.hour <= 5) //Change Theme if the time is after 7 PM or before 6 AM
        {
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_choosePriority);
            layout.setBackground(getResources().getDrawable(R.drawable.main_background_night));
            subject.setTextColor(getResources().getColor(R.color.orange));
            ImageButton addSite = (ImageButton) findViewById(R.id.btn_add);
            addSite.getBackground().setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_ATOP);
            ImageButton help = (ImageButton) findViewById(R.id.btn_help);
            help.getBackground().setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_ATOP);
            Button sendPriority = (Button) findViewById(R.id.btn_sendPriority);
            sendPriority.setBackground(getResources().getDrawable(R.drawable.button_rounded_corners));
            ImageView recyclerViewStroke = (ImageView) findViewById(R.id.rv_orderSites_stroke);
            recyclerViewStroke.setBackground(getResources().getDrawable(R.drawable.rounded_stroke_orange));
        }
        //***********************************Init RecyclerView*************************************
        recyclerViewSites = (RecyclerView) findViewById(R.id.rv_orderSites);
        recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerViewSites.setLayoutManager(recyclerLayoutManager);
        recyclerAdapter = new SitesAdapter((GlobalClass) getApplicationContext());
        priorityHandler.setRecyclerAdapter(recyclerAdapter);
        recyclerViewSites.setAdapter(recyclerAdapter);
        ItemTouchHelper.Callback callback = new DragNSwipeHelper((SitesAdapter) recyclerAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerViewSites);
        //*****************************************************************************************
    }

    public void sendPriority(View v)
    {
        globalClass.getCommunication().clientSend("194◘" + priorityHandler.getCurrentPrioritySubject() + "#"); //Delete previous priority in current subject
        String priority = "104◘";
        for (int i = 0; i < priorityHandler.getClientPriority().size(); i++) //Make the priority string
        {
            for (int j = 0; j < priorityHandler.getCategorySites().size(); j++)
            {
                if (priorityHandler.getCategorySites().get(j).getSubject().equals(priorityHandler.getCurrentPrioritySubject())) //Get the id of each site that in user's priority
                {
                    if (priorityHandler.getCategorySites().get(j).getSite().equals(priorityHandler.getClientPriority().get(i)))
                    {
                        priority = priority + priorityHandler.getCategorySites().get(j).getId() + "○" + (i + 1) + "◘";
                    }
                }
            }
        }
        priority = priority.substring(0, priority.length() - 1);
        priority = priority + "#";
        if (priorityHandler.getClientPriority().size() != 0) //If the priority has sites
        {
            globalClass.getCommunication().clientSend(priority);
        }
        finish();
    }

    public void addRemovedSites(View v) //Open PopupMenu with the sites that the user removed from his priority
    {
        addRemoved = new PopupMenu(this, v);
        addRemoved.setOnMenuItemClickListener(this);
        for (int i = 0; i < priorityHandler.getRemovedSitesOfCurrentSubject().size(); i++)
        {
            addRemoved.getMenu().add(priorityHandler.getRemovedSitesOfCurrentSubject().get(i));
        }
        addRemoved.show();
    }

    public void showWhatTODO(View v) //Information Dialog
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String msg = getResources().getString(R.string.PriorityHelp1) + ": " + priorityHandler.getSubjects().get(priorityHandler.getCurrentPrioritySubject()) + ".\n\n";
        msg = msg + getResources().getString(R.string.PriorityHelp2) + "\n\n" + getResources().getString(R.string.PriorityHelp3) + "\n\n";
        msg = msg + getResources().getString(R.string.PriorityHelp4) + "\n\n" + getResources().getString(R.string.PriorityHelp5);
        builder.setMessage(msg).setNeutralButton("הבנתי", dialogClickListener).show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) //Return the chosen site to client's priority
    {
        int position = -1;
        for (int i = 0; i < priorityHandler.getRemovedSitesOfCurrentSubject().size(); i++)
        {
            if (priorityHandler.getRemovedSitesOfCurrentSubject().get(i).equals(item.getTitle()))
            {
                position = i;
            }
        }
        priorityHandler.getClientPriority().add(priorityHandler.getRemovedSitesOfCurrentSubject().get(position));
        priorityHandler.getRemovedSitesOfCurrentSubject().remove(position);
        addRemoved.getMenu().removeItem(item.getItemId());
        recyclerAdapter.notifyDataSetChanged();
        return true;
    }
}