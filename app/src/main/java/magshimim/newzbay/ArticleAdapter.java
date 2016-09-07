package magshimim.newzbay;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
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

    private final String signInAsGuest = "signInAsGuest";
    private final String prefsConnection = "magshimim.newzbay.ConnectionPrefs";

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
//        if(firstTime)
//        {
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_admob, parent, false);
//            AdRequest adRequest = new AdRequest.Builder()
//                    .build();
//            ((NativeExpressAdView) view.findViewById(R.id.adView)).loadAd(adRequest);
//            firstTime = false;
//        }
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
                holder.line1.setBackgroundColor(categoriesHandler.getCategoryColor().get(categoriesHandler.getCurrentCategoryID()));
                holder.line2.setBackgroundColor(categoriesHandler.getCategoryColor().get(categoriesHandler.getCurrentCategoryID()));
                holder.line3.setBackgroundColor(categoriesHandler.getCategoryColor().get(categoriesHandler.getCurrentCategoryID()));
                holder.line4.setBackgroundColor(categoriesHandler.getCategoryColor().get(categoriesHandler.getCurrentCategoryID()));
                holder.line5.setBackgroundColor(categoriesHandler.getCategoryColor().get(categoriesHandler.getCurrentCategoryID()));
                holder.imComments.getBackground().setColorFilter(categoriesHandler.getCategoryColor().get(categoriesHandler.getCurrentCategoryID()), PorterDuff.Mode.SRC_ATOP);
                holder.imLikes.getBackground().setColorFilter(categoriesHandler.getCategoryColor().get(categoriesHandler.getCurrentCategoryID()), PorterDuff.Mode.SRC_ATOP);
                if (user.getConnectedVia().equals("Guest")) //Guest cant like or comment
                {
                    holder.like.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            new AlertDialog.Builder(globalClass.getCurrentActivity(), R.style.NBAlertDialog)
                                    .setTitle("טיפ:")
                                    .setCancelable(false)
                                    .setMessage(globalClass.getResources().getString(R.string.connectWithSocialNet1) + "\n"
                                            + globalClass.getResources().getString(R.string.connectWithSocialNet2) + "\n"
                                            + globalClass.getResources().getString(R.string.connectWithSocialNet3))
                                    .setPositiveButton("התחבר באמצעות רשת חברתית", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            SharedPreferences sharedpreferences = globalClass.getCurrentActivity().getSharedPreferences(prefsConnection, Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedpreferences.edit();
                                            editor.putBoolean(signInAsGuest, false);
                                            editor.commit();
                                            Context current = globalClass.getCurrentActivity();
                                            globalClass.endClass();
                                            Intent intent = new Intent(current, ActivityEntrance.class);
                                            current.startActivity(intent);
                                            ((Activity) current).finish();
                                        }
                                    })
                                    .setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .show();
                        }
                    });
                    holder.comment.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            new AlertDialog.Builder(globalClass.getCurrentActivity(), R.style.NBAlertDialog)
                                    .setTitle("טיפ:")
                                    .setCancelable(false)
                                    .setMessage(globalClass.getResources().getString(R.string.connectWithSocialNet1) + "\n"
                                            + globalClass.getResources().getString(R.string.connectWithSocialNet2) + "\n"
                                            + globalClass.getResources().getString(R.string.connectWithSocialNet3))
                                    .setPositiveButton("התחבר באמצעות רשת חברתית", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            SharedPreferences sharedpreferences = globalClass.getCurrentActivity().getSharedPreferences(prefsConnection, Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedpreferences.edit();
                                            editor.putBoolean(signInAsGuest, false);
                                            editor.commit();
                                            Context current = globalClass.getCurrentActivity();
                                            globalClass.endClass();
                                            Intent intent = new Intent(current, ActivityEntrance.class);
                                            current.startActivity(intent);
                                            ((Activity) current).finish();
                                        }
                                    })
                                    .setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .show();
                        }
                    });

                }
                else //The rest can like or comment
                {
                    if (categoriesHandler.getCurrentlyInUse().get(position).isLiked())
                    {
                        holder.like.setTextColor(categoriesHandler.getCategoryColor().get(categoriesHandler.getCurrentCategoryID()));
                        holder.like.getCompoundDrawables()[0].mutate().setColorFilter(categoriesHandler.getCategoryColor().get(categoriesHandler.getCurrentCategoryID()), PorterDuff.Mode.SRC_ATOP);
                    }
                    else
                    {
                        holder.like.setTextColor(globalClass.getResources().getColor(R.color.grey));
                        holder.like.getCompoundDrawables()[0].mutate().setColorFilter(null);
                    }
                    holder.like.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Button like = (Button) v.findViewById(R.id.btn_like);
                            ViewGroup item = (ViewGroup) v.getParent().getParent();
                            TextView countLikes = (TextView) item.findViewById(R.id.tv_likes);
                            if (categoriesHandler.getCurrentlyInUse().get(tempPosition).isLiked()) //Unlike
                            {
                                like.setTextColor(globalClass.getResources().getColor(R.color.grey));
                                like.getCompoundDrawables()[0].mutate().setColorFilter(null);
                                categoriesHandler.getCurrentlyInUse().get(tempPosition).setLiked(false);
                                categoriesHandler.getCurrentlyInUse().get(tempPosition).decNumberOfLikes();
                                globalClass.getCommunication().like(categoriesHandler.getCurrentlyInUse().get(tempPosition).getUrl(), globalClass);
                            }
                            else //Like
                            {
                                like.setTextColor(categoriesHandler.getCategoryColor().get(categoriesHandler.getCurrentCategoryID()));
                                like.getCompoundDrawables()[0].mutate().setColorFilter(categoriesHandler.getCategoryColor().get(categoriesHandler.getCurrentCategoryID()), PorterDuff.Mode.SRC_ATOP);
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
                if(categoriesHandler.getCurrentlyInUse().elementAt(position).getSecondHeadline().equals(""))
                {
                    holder.secondHeadline.setVisibility(View.GONE);
                }
                else
                {
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
                }
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
                holder.share.setOnClickListener(new View.OnClickListener() //Go to comments activity
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,
                                categoriesHandler.getCurrentlyInUse().elementAt(tempPosition).getMainHeadline()
                                        + "\n" +
                                categoriesHandler.getCurrentlyInUse().elementAt(tempPosition).getUrl());
                        sendIntent.setType("text/plain");
                        globalClass.getCurrentActivity().startActivity(Intent.createChooser(sendIntent, globalClass.getResources().getText(R.string.share)));
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
        public Button share;
        public ImageView line;
        public View line1;
        public View line2;
        public View line3;
        public View line4;
        public View line5;
        public ImageView imComments;
        public ImageView imLikes;

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
            this.share = (Button) itemView.findViewById(R.id.btn_share);
            this.line = (ImageView) itemView.findViewById(R.id.iv_line);
            this.line1 = (View) itemView.findViewById(R.id.line1);
            this.line2 = (View) itemView.findViewById(R.id.line2);
            this.line3 = (View) itemView.findViewById(R.id.line3);
            this.line4 = (View) itemView.findViewById(R.id.line4);
            this.line5 = (View) itemView.findViewById(R.id.line5);
            this.imComments = (ImageView) itemView.findViewById(R.id.im_comments);
            this.imLikes = (ImageView) itemView.findViewById(R.id.im_likes);

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
//        if (position % 8 == 0 && position != 0)
//            return AD_TYPE;
        return CONTENT_TYPE;
    }
}