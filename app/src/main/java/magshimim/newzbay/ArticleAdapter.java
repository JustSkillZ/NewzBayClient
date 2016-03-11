package magshimim.newzbay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebBackForwardList;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.wallet.wobs.TimeInterval;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

public class ArticleAdapter  extends ArrayAdapter<Article>{

    private ListView listView;
    private Button like;
    private TextView countLikes;
    private Button comment;
    private TextView countComments;
    private ImageButton picture;
    private Context context;
    private ListAdapter adapter;
    private Activity activity;

    public ArticleAdapter(Context context, Activity act)
    {
        super(context, R.layout.listview_articles, Categories.getCurrentlyInUse());
        this.context = context;
        adapter = this;
        activity = act;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
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
                        FacebookAndGoogle.getCommunication().clientSend("110&" + Categories.getCurrentlyInUse().get(position).getUrl() + "#");
                    }
                    else {
                        like.setText("Unlike");
                        Categories.getCurrentlyInUse().get(position).setLiked(true);
                        Categories.getCurrentlyInUse().get(position).incNumberOfLikes();
                        countLikes.setText(Categories.getCurrentlyInUse().get(position).getNumberOfLikes() + " Likes");
                        FacebookAndGoogle.getCommunication().clientSend("110&" + Categories.getCurrentlyInUse().get(position).getUrl() + "#");
                    }
                }
            });
        }
        TextView mainHeadline = (TextView) view.findViewById(R.id.tv_mainHeadline);
        mainHeadline.setText(Categories.getCurrentlyInUse().elementAt(position).getMainHeadline());
        TextView secondHeadLine = (TextView) view.findViewById(R.id.tv_secondHeadline);
        secondHeadLine.setText(Categories.getCurrentlyInUse().elementAt(position).getSecondHeadline());
        TextView site = (TextView) view.findViewById(R.id.tv_site);
        site.setText(Categories.getCurrentlyInUse().elementAt(position).getSiteName());
        TextView date = (TextView) view.findViewById(R.id.tv_date);
        if(Categories.getCurrentlyInUse().elementAt(position).getDate() != null)
        {
            Date d = new Date();
            date.setText((String) DateUtils.getRelativeTimeSpanString(Categories.getCurrentlyInUse().elementAt(position).getDate().getTime(), d.getTime(), 0));
        }
        countLikes = (TextView) view.findViewById(R.id.tv_likes);
        countLikes.setText(Categories.getCurrentlyInUse().get(position).getNumberOfLikes() + " Likes");
        countComments = (TextView) view.findViewById(R.id.tv_comments);
        countComments.setText("    " + Categories.getCurrentlyInUse().get(position).getNumberOfComments() + " Comments");
        picture = (ImageButton) view.findViewById(R.id.ib_picture);
        if(!Categories.getCurrentlyInUse().get(position).getPicURL().equals("null"))
        {
            if(Categories.getCurrentlyInUse().get(position).getPicture() == null)
            {
                getBitmapFromURL(picture, position);
            }
            else {
                picture.setImageBitmap(Categories.getCurrentlyInUse().get(position).getPicture());
            }
        }
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View parentRow = (View) v.getParent();
                listView = (ListView) parentRow.getParent();
                int position = listView.getPositionForView(parentRow);
                Categories.setCurrentlyOpenURL(Categories.getCurrentlyInUse().elementAt(position).getUrl());
                Intent web = new Intent(context,InnerWeb.class);
                context.startActivity(web);
            }
        });
        return view;
    }

    public void getBitmapFromURL(ImageButton ib, int position) {
        final int position1 = position;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL((Categories.getCurrentlyInUse().get(position1).getPicURL()));
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap b = BitmapFactory.decodeStream(input);
                    Categories.getCurrentlyInUse().get(position1).setPicture(b);
                    Categories.getDownloadedPics().put(Categories.getCurrentlyInUse().get(position1).getPicURL(), b);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((BaseAdapter) adapter).notifyDataSetChanged();
                        }
                    });


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        };
        if(!Categories.getCurrentlyInUse().get(position).getPicURL().equals("null"))
        {
            Thread t = new Thread(runnable);
            t.start();
        }
    }
}