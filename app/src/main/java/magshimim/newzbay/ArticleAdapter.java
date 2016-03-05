package magshimim.newzbay;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebBackForwardList;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Vector;

public class ArticleAdapter  extends ArrayAdapter<Article>{

    private ListView listView;
    private Button like;
    private TextView countLikes;
    private Button comment;
    private TextView countComments;
    private ImageButton picture;
    private WebView web;
    private android.support.v7.widget.Toolbar toolbar_main;
    private android.support.v7.widget.Toolbar toolbar_web;
    private WebBackForwardList webBackForwardList;
    private Context context;

    public ArticleAdapter(Context context, WebView web,
                          android.support.v7.widget.Toolbar toolbar_main,
                          android.support.v7.widget.Toolbar toolbar_web)
    {
        super(context, R.layout.listview_articles, Categories.getCurrentlyInUse());
        this.web = web;
        this.toolbar_main = toolbar_main;
        this.toolbar_web = toolbar_web;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.listview_articles, parent, false);
        if(!FacebookAndGoogle.isLoggedWithGoogle() && !FacebookAndGoogle.isLoggedWithFacebook())
        {
            like = (Button) view.findViewById(R.id.btn_like);
            like.setBackgroundResource(R.drawable.buttonborder_disabled);
            like.setAlpha((float) 0.3);
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast toast = Toast.makeText(context, "רק משתמשים מחוברים יכולים לעשות לייק", Toast.LENGTH_LONG);
                    toast.show();
                }
            });

            comment = (Button) view.findViewById(R.id.btn_comment);
            comment.setBackgroundResource(R.drawable.buttonborder_disabled);
            comment.setAlpha((float) 0.3);
            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast toast = Toast.makeText(context, "רק משתמשים מחוברים יכולים להגיב", Toast.LENGTH_LONG);
                    toast.show();
                }
            });

        }
        else
        {
            like = (Button) view.findViewById(R.id.btn_like);
            if(Categories.getCurrentlyInUse().get(position).getLiked())
            {
                like.setText("Unlike");
            }
            else
            {
                like.setText("Like");
            }
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    like = (Button) v.findViewById(R.id.btn_like);
                    ViewGroup item = (ViewGroup)v.getParent().getParent();
                    countLikes = (TextView) item.findViewById(R.id.tv_likes);
                    if (Categories.getCurrentlyInUse().get(position).getLiked()) {
                        like.setText("Like");
                        Categories.getCurrentlyInUse().get(position).setLiked(false);
                        Categories.getCurrentlyInUse().get(position).decNumberOfLikes();
                        countLikes.setText(Categories.getCurrentlyInUse().get(position).getNumberOfLikes() + " Likes");

                    }
                    else {
                        like.setText("Unlike");
                        Categories.getCurrentlyInUse().get(position).setLiked(true);
                        Categories.getCurrentlyInUse().get(position).incNumberOfLikes();
                        countLikes.setText(Categories.getCurrentlyInUse().get(position).getNumberOfLikes() + " Likes");
                    }
                }
            });
        }
        TextView mainHeadline = (TextView) view.findViewById(R.id.tv_mainHeadline);
        mainHeadline.setText(Categories.getCurrentlyInUse().elementAt(position).getMainHeadline());
        TextView site = (TextView) view.findViewById(R.id.tv_site);
        site.setText(Categories.getCurrentlyInUse().elementAt(position).getSiteName());
        countLikes = (TextView) view.findViewById(R.id.tv_likes);
        countLikes.setText(Categories.getCurrentlyInUse().get(position).getNumberOfLikes() + " Likes");
        countComments = (TextView) view.findViewById(R.id.tv_comments);
        countComments.setText("    " + Categories.getCurrentlyInUse().get(position).getNumberOfComments() + " Comments");
        picture = (ImageButton) view.findViewById(R.id.ib_picture);
        picture.setImageBitmap(Categories.getCurrentlyInUse().get(position).getPicture());
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View parentRow = (View) v.getParent();
                listView = (ListView) parentRow.getParent();
                int position = listView.getPositionForView(parentRow);
                web.loadUrl(Categories.getCurrentlyInUse().elementAt(position).getUrl());
                toolbar_main.setVisibility(View.GONE);
                toolbar_web.setVisibility(View.VISIBLE);
                web.setVisibility(View.VISIBLE);
                Intent intent = new Intent(context,Explanation2.class);
                context.startActivity(intent);
            }
        });
        return view;
    }
}