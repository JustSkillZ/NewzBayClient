package magshimim.newzbay;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Vector;

public class ArticleAdapter  extends ArrayAdapter<Article>{

    private ListView listView;
    private Vector<Article> articles;
    private Context context;
    private Button like;
    private TextView countLikes;
    private Button comment;
    private TextView countComments;
    private ImageButton picture;
    private WebView web;
    private android.support.v7.widget.Toolbar toolbar_main;
    private android.support.v7.widget.Toolbar toolbar_web;

    public ArticleAdapter(Context context, Vector<Article> values, WebView web,
                          android.support.v7.widget.Toolbar toolbar_main,
                          android.support.v7.widget.Toolbar toolbar_web)
    {
        super(context, R.layout.listview_articles, values);
        articles = values;
        this.context = context;
        this.web = web;
        this.toolbar_main = toolbar_main;
        this.toolbar_web = toolbar_web;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.listview_articles, parent, false);
        TextView mainHeadline = (TextView) view.findViewById(R.id.tv_mainHeadline);
        mainHeadline.setText(articles.elementAt(position).getMainHeadline());
        countLikes = (TextView) view.findViewById(R.id.tv_likes);
        countLikes.setText(articles.get(position).getNumberOfLikes() + " Likes");
        countComments = (TextView) view.findViewById(R.id.tv_comments);
        countComments.setText("    " + articles.get(position).getNumberOfComments() + " Comments");
        like = (Button) view.findViewById(R.id.btn_like);
        if(articles.get(position).getLiked())
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
                if (articles.get(position).getLiked()) {
                    like.setText("Like");
                    articles.get(position).setLiked(false);
                    articles.get(position).decNumberOfLikes();
                    countLikes.setText(articles.get(position).getNumberOfLikes() + " Likes");

                }
                else {
                    like.setText("Unlike");
                    articles.get(position).setLiked(true);
                    articles.get(position).incNumberOfLikes();
                    countLikes.setText(articles.get(position).getNumberOfLikes() + " Likes");
                }
            }
        });
        picture = (ImageButton) view.findViewById(R.id.ib_picture);
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View parentRow = (View) v.getParent();
                listView = (ListView) parentRow.getParent();
                int position = listView.getPositionForView(parentRow);
                web.setWebViewClient(new WebViewClient());
                web.clearHistory();
                web.loadUrl(articles.elementAt(position).getUrl());
                toolbar_main.setVisibility(View.GONE);
                toolbar_web.setVisibility(View.VISIBLE);
                web.setVisibility(View.VISIBLE);
            }
        });
        return view;
    }
}