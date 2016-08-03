package magshimim.newzbay;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder>
{

    private Context context;
    private CategoriesHandler categoriesHandler;
    private User user;
    private GlobalClass globalClass;

    private final int AD_TYPE = 1;
    private final int CONTENT_TYPE = 0;
    private boolean firstTime;

    public ArticleAdapter(Context context, GlobalClass globalClass)
    {
        this.context = context;
        categoriesHandler = globalClass.getCategoriesHandler();
        user = globalClass.getUser();
        this.globalClass = globalClass;
        firstTime = true;
    }

    @Override
    public ArticleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = null;
        ArticleAdapter.ViewHolder vh = null;
        if(firstTime)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_admob, parent, false);
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            ((NativeExpressAdView) view.findViewById(R.id.adView)).loadAd(adRequest);
            firstTime = false;
        }
        if (viewType == AD_TYPE)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_admob, parent, false);
            vh = new ViewHolderAdMob(view);
        }
        else
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_article, parent, false);
            vh = new ViewHolder(view);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(ArticleAdapter.ViewHolder viewHolder, int position)
    {
        final int tempPosition = position; //Current position
        if (categoriesHandler.getCurrentlyInUse().size() != 0)
        {
            if(getItemViewType(position) == AD_TYPE)
            {
                ((ViewHolderAdMob) viewHolder).line.setBackgroundColor(categoriesHandler.getCategoryColor().get(categoriesHandler.getCurrentCategoryID()));
            }
            else
            {
                ViewHolder holder = (ViewHolder) viewHolder;
                holder.line.setBackgroundColor(categoriesHandler.getCategoryColor().get(categoriesHandler.getCurrentCategoryID()));
                if (user.getConnectedVia().equals("Guest")) //Guest cant like or comment
                {
                    holder.like.setBackgroundResource(R.drawable.buttonborder_disabled);
                    holder.like.setAlpha((float) 0.3);
                    holder.like.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Toast toast = Toast.makeText(context, "רק משתמשים מחוברים יכולים לעשות לייק", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
                    holder.comment.setBackgroundResource(R.drawable.buttonborder_disabled);
                    holder.comment.setAlpha((float) 0.3);
                    holder.comment.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Toast toast = Toast.makeText(context, "רק משתמשים מחוברים יכולים להגיב", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });

                }
                else //The rest can like or comment
                {
                    if (categoriesHandler.getCurrentlyInUse().get(position).isLiked())
                    {
                        holder.like.setTextColor(categoriesHandler.getCategoryColor().get(categoriesHandler.getCurrentCategoryID()));
                    }
                    else
                    {
                        holder.like.setTextColor(globalClass.getResources().getColor(R.color.grey));
                    }
                    holder.like.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Button like = (Button) v.findViewById(R.id.btn_like);
                            ViewGroup item = (ViewGroup) v.getParent().getParent();
                            TextView countLikes = (TextView) item.findViewById(R.id.tv_likes);
                            if (categoriesHandler.getCurrentlyInUse().get(tempPosition).isLiked()) //Dislike
                            {
                                like.setTextColor(globalClass.getResources().getColor(R.color.grey));
                                categoriesHandler.getCurrentlyInUse().get(tempPosition).setLiked(false);
                                categoriesHandler.getCurrentlyInUse().get(tempPosition).decNumberOfLikes();
                                globalClass.getCommunication().like(categoriesHandler.getCurrentlyInUse().get(tempPosition).getUrl(), globalClass);
                            }
                            else //Like
                            {
                                like.setTextColor(categoriesHandler.getCategoryColor().get(categoriesHandler.getCurrentCategoryID()));
                                categoriesHandler.getCurrentlyInUse().get(tempPosition).setLiked(true);
                                categoriesHandler.getCurrentlyInUse().get(tempPosition).incNumberOfLikes();
                                globalClass.getCommunication().like(categoriesHandler.getCurrentlyInUse().get(tempPosition).getUrl(), globalClass);
                            }
                            float numOfLikes = Integer.parseInt(String.valueOf(categoriesHandler.getCurrentlyInUse().get(tempPosition).getNumberOfLikes()));
                            if (numOfLikes >= 1000) //Nice format, if there is more than 1000 likes. Example: (1.5k)
                            {
                                countLikes.setText(String.format("%.1f", (numOfLikes / 1000)) + "k");
                            }
                            else
                            {
                                countLikes.setText(categoriesHandler.getCurrentlyInUse().get(tempPosition).getNumberOfLikes() + "");
                            }
                        }
                    });
                    holder.comment.setOnClickListener(new View.OnClickListener() //Go to comments activity
                    {
                        @Override
                        public void onClick(View v)
                        {
                            globalClass.getCommentsHandler().setArticle(categoriesHandler.getCurrentlyInUse().elementAt(tempPosition));
                            Intent comments = new Intent(context, ActivityComments.class);
                            context.startActivity(comments);
                        }
                    });
                }
                holder.mainHeadline.setText(categoriesHandler.getCurrentlyInUse().elementAt(position).getMainHeadline());
                holder.secondHeadline.setText(categoriesHandler.getCurrentlyInUse().elementAt(position).getSecondHeadline());
                holder.secondHeadline.setMaxLines(2);
                holder.secondHeadline.setEllipsize(TextUtils.TruncateAt.END);
                holder.secondHeadline.setOnClickListener(new View.OnClickListener() //Go to comments activity
                {
                    @Override
                    public void onClick(View v)
                    {
                        ((TextView)v).setMaxLines(Integer.MAX_VALUE);
                        ((TextView)v).setEllipsize(null);
                    }
                });
                holder.site.setText(categoriesHandler.getCurrentlyInUse().elementAt(position).getWebSite());
                if (categoriesHandler.getCurrentlyInUse().elementAt(position).getDate() != null)
                {
                    Date d = new Date();
                    holder.date.setText((String) DateUtils.getRelativeTimeSpanString(categoriesHandler.getCurrentlyInUse().elementAt(position).getDate().getTime(), d.getTime(), 0));
                }
                float numOfLikes = Integer.parseInt(String.valueOf(categoriesHandler.getCurrentlyInUse().get(position).getNumberOfLikes()));
                if (numOfLikes >= 1000) //Nice format, if there is more than 1000 likes. Example: (1.5k)
                {
                    holder.countLikes.setText(String.format("%.1f", (numOfLikes / 1000)) + "k");
                }
                else
                {
                    holder.countLikes.setText(categoriesHandler.getCurrentlyInUse().get(position).getNumberOfLikes() + "");
                }
                float numOfComments = Integer.parseInt(String.valueOf(categoriesHandler.getCurrentlyInUse().get(position).getNumberOfComments()));
                if (numOfComments >= 1000) //Nice format, if there is more than 1000 comments. Example: (1.5k)
                {
                    holder.countComments.setText(String.format("%.1f", (numOfComments / 1000)) + "k");
                }
                else
                {
                    holder.countComments.setText(categoriesHandler.getCurrentlyInUse().get(position).getNumberOfComments() + "");
                }
                if (categoriesHandler.getCurrentlyInUse().get(position).getPicURL() != null) //If there is a URL, load the pic.
                {
                    Picasso.with(context).load(categoriesHandler.getCurrentlyInUse().get(position).getPicURL()).placeholder(R.drawable.loading_article).into(holder.picture);
                }
                else //If there is no URL, set default image.
                {
                    holder.picture.setImageBitmap(categoriesHandler.getCurrentlyInUse().get(position).getPicture());
                }
                holder.picture.setOnClickListener(new View.OnClickListener() //Go to web activity
                {
                    @Override
                    public void onClick(View v)
                    {
                        categoriesHandler.setCurrentlyOpenURL(categoriesHandler.getCurrentlyInUse().elementAt(tempPosition).getUrl());
                        Intent web = new Intent(context, ActivityInnerWeb.class);
                        context.startActivity(web);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return categoriesHandler.getCurrentlyInUse().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView mainHeadline;
        public TextView secondHeadline;
        public TextView site;
        public TextView date;
        public ImageButton picture;
        public TextView countLikes;
        public TextView countComments;
        public Button like;
        public Button comment;
        public ImageView line;

        public ViewHolder(View itemView)
        {
            super(itemView);
            this.mainHeadline = (TextView) itemView.findViewById(R.id.tv_mainHeadline);
            this.secondHeadline = (TextView) itemView.findViewById(R.id.tv_secondHeadline);
            this.site = (TextView) itemView.findViewById(R.id.tv_site);
            this.date = (TextView) itemView.findViewById(R.id.tv_date);
            this.picture = (ImageButton) itemView.findViewById(R.id.ib_picture);
            this.countLikes = (TextView) itemView.findViewById(R.id.tv_likes);
            this.countComments = (TextView) itemView.findViewById(R.id.tv_comments);
            this.like = (Button) itemView.findViewById(R.id.btn_like);
            this.comment = (Button) itemView.findViewById(R.id.btn_comment);
            this.line = (ImageView) itemView.findViewById(R.id.iv_line);
        }
    }

    public static class ViewHolderAdMob extends ArticleAdapter.ViewHolder {
        public NativeExpressAdView mAdView;
        public View line;
        public ViewHolderAdMob(View view) {
            super(view);
            line = view.findViewById(R.id.iv_line);
            mAdView = (NativeExpressAdView) view.findViewById(R.id.adView);
            mAdView.setAdListener(new AdListener()
            {
                @Override
                public void onAdClosed()
                {
                    super.onAdClosed();
                }

                @Override
                public void onAdFailedToLoad(int i)
                {
                    super.onAdFailedToLoad(i);
                }

                @Override
                public void onAdLeftApplication()
                {
                    super.onAdLeftApplication();
                }

                @Override
                public void onAdOpened()
                {
                    super.onAdOpened();
                }

                @Override
                public void onAdLoaded()
                {
                    super.onAdLoaded();
                    mAdView.setVisibility(View.VISIBLE);
                    line.setVisibility(View.VISIBLE);
                }
            });
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            mAdView.loadAd(adRequest);
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        if (position % 8 == 0 && position != 0)
            return AD_TYPE;
        return CONTENT_TYPE;
    }
}