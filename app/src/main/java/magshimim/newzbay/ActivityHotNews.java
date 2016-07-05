package magshimim.newzbay;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.Vector;

public class ActivityHotNews extends AppCompatActivity
{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotnews);
        // Create the adapter that will return a fragment for each of the primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        ((GlobalClass) getApplicationContext()).setCurrentActivity(ActivityHotNews.this);
        ((GlobalClass) getApplicationContext()).getCategoriesHandler().setHotNewsPageAdapter(mSectionsPagerAdapter); //Save adapter in order to notify on change in another thread
        //((GlobalClass) getApplicationContext()).getCommunication().send("126#");
    }

    @Override
    public void onBackPressed()
    {
        ((GlobalClass) getApplicationContext()).getCategoriesHandler().getHotNewsArticles().clear();
        ((GlobalClass) getApplicationContext()).getCategoriesHandler().getHotNewsPageAdapter().notifyDataSetChanged();
        ((GlobalClass) getApplicationContext()).getCategoriesHandler().setHotNewsPageAdapter(null);
        ((GlobalClass) getApplicationContext()).getCategoriesHandler().setCurrentlyInUseCategoryServer("");
        ((GlobalClass) getApplicationContext()).getCategoriesHandler().setCurrentlyInUseCategory("");
        ((GlobalClass) getApplicationContext()).getCategoriesHandler().getCurrentlyInUse().clear();
        ((GlobalClass) getApplicationContext()).getCategoriesHandler().getArticlesRecyclerAdapter().notifyDataSetChanged();
        super.onBackPressed();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment
    {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        private TextView numberOfLikes;
        private GlobalClass globalClass;
        private Vector<Article> hotNews;

        public PlaceholderFragment()
        {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber)
        {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt("NB", sectionNumber); //Bundle: Key - NB | Value: position in the vector
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {
            //Using Bundle in order to get the position of the item in articles vector. Key - NB | Value: position in the vector
            globalClass = (GlobalClass) getActivity().getApplicationContext();
            hotNews = globalClass.getCategoriesHandler().getHotNewsArticles();
            View rootView = inflater.inflate(R.layout.fragment_explore_articles, container, false);
            TextView headline = (TextView) rootView.findViewById(R.id.tv_article_title);
            headline.setText(hotNews.get(getArguments().getInt("NB")).getMainHeadline());
            TextView source = (TextView) rootView.findViewById(R.id.tv_article_site);
            source.setText(hotNews.get(getArguments().getInt("NB")).getWebSite() + "  /");
            ImageView categoryIcon = (ImageView) rootView.findViewById(R.id.iv_categoryIcon);
            categoryIcon.setImageBitmap(globalClass.getCategoriesHandler().getCategoryIcon().get(globalClass.getCategoriesHandler().getCategoriesForServer().indexOf(hotNews.get(getArguments().getInt("NB")).getSubject())));
            categoryIcon.setColorFilter(globalClass.getCategoriesHandler().getCategoryColor().get(globalClass.getCategoriesHandler().getCategoriesForServer().indexOf(hotNews.get(getArguments().getInt("NB")).getSubject())), PorterDuff.Mode.SRC_ATOP);
            if (hotNews.get(getArguments().getInt("NB")).getDate() != null)
            {
                Date d = new Date();
                TextView date = (TextView) rootView.findViewById(R.id.tv_article_time);
                date.setText((String) DateUtils.getRelativeTimeSpanString(hotNews.get(getArguments().getInt("NB")).getDate().getTime(), d.getTime(), 0));
            }
            ImageButton articlePic = (ImageButton) rootView.findViewById(R.id.ib_article_pic);
            if (!hotNews.get(getArguments().getInt("NB")).getPicURL().equals("null")) //If there is a URL, load the pic.
            {
                Picasso.with(getContext()).load(hotNews.get(getArguments().getInt("NB")).getPicURL()).fit().centerCrop().placeholder(R.drawable.loading_hotnews).into(articlePic);
            }
            else //If there is no URL, set default image.
            {
                articlePic.setImageBitmap(hotNews.get(getArguments().getInt("NB")).getPicture());
            }
            articlePic.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    globalClass.getCategoriesHandler().setCurrentlyOpenURL(hotNews.get(getArguments().getInt("NB")).getUrl());
                    Intent web = new Intent(getActivity(), ActivityInnerWeb.class);
                    startActivity(web);
                }
            });
            numberOfLikes = (TextView) rootView.findViewById(R.id.tv_article_likes);
            float numOfLikes = Integer.parseInt(String.valueOf(hotNews.get(getArguments().getInt("NB")).getNumberOfLikes()));
            if (numOfLikes >= 1000) //Nice format, if there is more than 1000 comments. Example: (1.5k)
            {
                numberOfLikes.setText(String.format("%.1f", (numOfLikes / 1000)) + "k");
            }
            else
            {
                numberOfLikes.setText(hotNews.get(getArguments().getInt("NB")).getNumberOfLikes() + "");
            }
            ImageButton like = (ImageButton) rootView.findViewById(R.id.ib_like);
            if (like != null)
            {
                if (globalClass.getUser().getConnectedVia().equals("Guest")) //Only connected users can like
                {
                    Picasso.with(getContext()).load(R.drawable.hot_article_liked).into(like);
                    like.setAlpha((float) 0.3);
                    like.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Toast toast = Toast.makeText(getContext(), "רק משתמשים מחוברים יכולים לעשות לייק", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
                }
                else //Rest of the users
                {
                    if (hotNews.get(getArguments().getInt("NB")).isLiked())
                    {
                        Picasso.with(getContext()).load(R.drawable.hot_article_liked).into(like);
                    }
                    else
                    {
                        Picasso.with(getContext()).load(R.drawable.like_hot_article).into(like);
                    }
                    like.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            ImageButton like = (ImageButton) v.findViewById(R.id.ib_like);
                            if (hotNews.get(getArguments().getInt("NB")).isLiked()) //Dislike
                            {
                                Picasso.with(getContext()).load(R.drawable.like_hot_article).into(like);
                                hotNews.get(getArguments().getInt("NB")).setLiked(false);
                                hotNews.get(getArguments().getInt("NB")).decNumberOfLikes();
                                //globalClass.getCommunication().send("110○" + globalClass.getCategoriesHandler().getHotNewsArticles().get(getArguments().getInt("NB")).getUrl() + "#");
                            }
                            else //Like
                            {
                                Picasso.with(getContext()).load(R.drawable.hot_article_liked).into(like);
                                hotNews.get(getArguments().getInt("NB")).setLiked(true);
                                hotNews.get(getArguments().getInt("NB")).incNumberOfLikes();
                                //globalClass.getCommunication().send("110○" + globalClass.getCategoriesHandler().getHotNewsArticles().get(getArguments().getInt("NB")).getUrl() + "#");
                            }
                            float numOfLikes = Integer.parseInt(String.valueOf(hotNews.get(getArguments().getInt("NB")).getNumberOfLikes()));
                            if (numOfLikes >= 1000) //Nice format, if there is more than 1000 likes. Example: (1.5k)
                            {
                                numberOfLikes.setText(String.format("%.1f", (numOfLikes / 1000)) + "k");
                            }
                            else
                            {
                                numberOfLikes.setText(hotNews.get(getArguments().getInt("NB")).getNumberOfLikes() + "");
                            }
                        }
                    });
                }
            }
            return rootView;
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter
    {

        private GlobalClass globalClass;

        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
            globalClass = (GlobalClass) getApplicationContext();
        }

        @Override
        public Fragment getItem(int position)
        {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount()
        {
            return globalClass.getCategoriesHandler().getHotNewsArticles().size();
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return null;
        }

        @Override
        public int getItemPosition(Object item) {
            return POSITION_UNCHANGED;
        }
    }
}