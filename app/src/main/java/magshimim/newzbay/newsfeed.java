package magshimim.newzbay;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class newsfeed extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);
        toolbar_main = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar_main);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        String[] abc = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p"};
        ListAdapter listadapter = new ArticleAdapter(this, abc);
        ListView listView_article = (ListView) findViewById(R.id.listView_articles);
        listView_article.setAdapter(listadapter);
        listView_article.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedArticle = "You selected " + String.valueOf(adapterView.getItemAtPosition(position));
                Toast.makeText(newsfeed.this, selectedArticle, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void likeClickHandler(View v)
    {
            //get the row the clicked button is in
        LinearLayout vwParentRow = (LinearLayout)v.getParent();

        Button button = (Button)vwParentRow.getChildAt(1);
        if(button.getText().equals("Like"))
        {
            button.setText("Unlike");
        }
        if(button.getText().equals("UnLike"))
        {
            button.setText("Like");
        }
        int c = getResources().getColor(R.color.nb);
        vwParentRow.setBackgroundColor(c);
        vwParentRow.refreshDrawableState();
    }
}
