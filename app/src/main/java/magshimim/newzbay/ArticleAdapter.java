package magshimim.newzbay;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private Context context;
    private CategoriesHandler categoriesHandler;
    private Communication communication;
    private User user;
    private LruCache <String, Bitmap> downloadedPictures;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mainHeadline;
        public TextView secondHeadline;
        public TextView site;
        public TextView date;
        public ImageButton picture;
        public TextView countLikes;
        public TextView countComments;
        public Button like;
        public Button comment;

        public ViewHolder(View itemView, TextView mainHeadline, TextView secondHeadline, TextView site, TextView date, ImageButton picture, TextView countLikes, TextView countComments, Button like, Button comment) {
            super(itemView);
            this.mainHeadline = mainHeadline;
            this.secondHeadline = secondHeadline;
            this.site = site;
            this.date = date;
            this.picture = picture;
            this.countLikes = countLikes;
            this.countComments = countComments;
            this.like = like;
            this.comment = comment;
        }
    }

    public ArticleAdapter(Context context, GlobalClass globalClass)
    {
        this.context = context;
        categoriesHandler = globalClass.getCategoriesHandler();
        user = globalClass.getUser();
        this.communication = globalClass.getCommunication();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int maxKb = am.getMemoryClass() * 1024;
        int limitKb = maxKb / 8;
        downloadedPictures = new LruCache<String, Bitmap>(limitKb);
    }

    @Override
    public ArticleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_article, parent, false);
        TextView mainHeadline = (TextView) view.findViewById(R.id.tv_mainHeadline);
        TextView secondHeadline = (TextView) view.findViewById(R.id.tv_secondHeadline);
        TextView site = (TextView) view.findViewById(R.id.tv_site);
        TextView date = (TextView) view.findViewById(R.id.tv_date);
        ImageButton picture = (ImageButton) view.findViewById(R.id.ib_picture);
        TextView countLikes = (TextView) view.findViewById(R.id.tv_likes);
        TextView countComments = (TextView) view.findViewById(R.id.tv_comments);
        Button like = (Button) view.findViewById(R.id.btn_like);
        Button comment = (Button) view.findViewById(R.id.btn_comment);
        ViewHolder vh = new ViewHolder(view, mainHeadline, secondHeadline, site, date, picture, countLikes, countComments, like, comment);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int tempPosition = position;
        if(categoriesHandler.getCurrentlyInUse().size() != 0) {
            if (user.getConnectedVia().equals("Guest")) {

                holder.like.setBackgroundResource(R.drawable.buttonborder_disabled);
                holder.like.setAlpha((float) 0.3);
                holder.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast toast = Toast.makeText(context, "רק משתמשים מחוברים יכולים לעשות לייק", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });

                holder.comment.setBackgroundResource(R.drawable.buttonborder_disabled);
                holder.comment.setAlpha((float) 0.3);
                holder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast toast = Toast.makeText(context, "רק משתמשים מחוברים יכולים להגיב", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });

            } else {
                if (categoriesHandler.getCurrentlyInUse().get(position).getLiked()) {
                    holder.like.setText("Unlike");
                } else {
                    holder.like.setText("Like");
                }
                holder.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Button like = (Button) v.findViewById(R.id.btn_like);
                        ViewGroup item = (ViewGroup) v.getParent().getParent();
                        TextView countLikes = (TextView) item.findViewById(R.id.tv_likes);
                        if (categoriesHandler.getCurrentlyInUse().get(tempPosition).getLiked()) {
                            like.setText("Like");
                            categoriesHandler.getCurrentlyInUse().get(tempPosition).setLiked(false);
                            categoriesHandler.getCurrentlyInUse().get(tempPosition).decNumberOfLikes();
                            countLikes.setText(categoriesHandler.getCurrentlyInUse().get(tempPosition).getNumberOfLikes() + " Likes");
                            communication.clientSend("110&" + categoriesHandler.getCurrentlyInUse().get(tempPosition).getUrl() + "#");
                        } else {
                            like.setText("Unlike");
                            categoriesHandler.getCurrentlyInUse().get(tempPosition).setLiked(true);
                            categoriesHandler.getCurrentlyInUse().get(tempPosition).incNumberOfLikes();
                            countLikes.setText(categoriesHandler.getCurrentlyInUse().get(tempPosition).getNumberOfLikes() + " Likes");
                            communication.clientSend("110&" + categoriesHandler.getCurrentlyInUse().get(tempPosition).getUrl() + "#");
                        }
                    }
                });
            }
            holder.mainHeadline.setText(categoriesHandler.getCurrentlyInUse().elementAt(position).getMainHeadline());
            holder.secondHeadline.setText(categoriesHandler.getCurrentlyInUse().elementAt(position).getSecondHeadline());
            holder.site.setText(categoriesHandler.getCurrentlyInUse().elementAt(position).getSiteName());
            if (categoriesHandler.getCurrentlyInUse().elementAt(position).getDate() != null) {
                Date d = new Date();
                holder.date.setText((String) DateUtils.getRelativeTimeSpanString(categoriesHandler.getCurrentlyInUse().elementAt(position).getDate().getTime(), d.getTime(), 0));
            }
            holder.countLikes.setText(categoriesHandler.getCurrentlyInUse().get(position).getNumberOfLikes() + " Likes");
            holder.countComments.setText("    " + categoriesHandler.getCurrentlyInUse().get(position).getNumberOfComments() + " Comments");
            if (!categoriesHandler.getCurrentlyInUse().get(position).getPicURL().equals("null")) {
                Bitmap pic = downloadedPictures.get(categoriesHandler.getCurrentlyInUse().get(position).getPicURL());
                if (pic != null) {
                    holder.picture.setImageBitmap(pic);
                }
                else {
                    holder.picture.setImageBitmap(categoriesHandler.getCurrentlyInUse().get(position).getPicture());
                    getBitmapFromURL(holder.picture, position);
                }
            }
            holder.picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    categoriesHandler.setCurrentlyOpenURL(categoriesHandler.getCurrentlyInUse().elementAt(tempPosition).getUrl());
                    Intent web = new Intent(context, InnerWeb.class);
                    context.startActivity(web);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return categoriesHandler.getCurrentlyInUse().size();
    }

    public void getBitmapFromURL(ImageButton ib, int position) {
        final int position1 = position;
        final ImageButton tempImageButton = ib;
        Runnable runnable = new Runnable() {
            @Override
            public void run()
            {
                try {
                    URL url = new URL((categoriesHandler.getCurrentlyInUse().get(position1).getPicURL()));
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    final Bitmap b = BitmapFactory.decodeStream(input);
                    if(categoriesHandler.getCurrentlyInUse().size() != 0 && b != null)
                    {
                        downloadedPictures.put(categoriesHandler.getCurrentlyInUse().get(position1).getPicURL(), b);
                        while(categoriesHandler.isLoading()){}
                        categoriesHandler.getNewsfeed().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tempImageButton.setImageBitmap(b);
                            }
                        });
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread t = new Thread(runnable);
        t.start();
    }


}