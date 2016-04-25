package magshimim.newzbay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.Vector;

public class ExploreArticles extends AppCompatActivity {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_articles);
        // Create the adapter that will return a fragment for each of the primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        ((GlobalClass) getApplicationContext()).getCategoriesHandler().setHotNewsPageAdapter(mSectionsPagerAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ((GlobalClass) getApplicationContext()).getCategoriesHandler().setHotNewsPageAdapter(null);
        ((GlobalClass) getApplicationContext()).getCategoriesHandler().getCurrentlyInUse().clear();
        ((GlobalClass) getApplicationContext()).getCategoriesHandler().getRecyclerAdapter().notifyDataSetChanged();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        private TextView numberOfLikes;
        private GlobalClass globalClass;
        private Vector<Article> hotNews;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt("NB", sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            globalClass = (GlobalClass) getActivity().getApplicationContext();
            hotNews = globalClass.getCategoriesHandler().getCurrentlyInUse();
            View rootView = inflater.inflate(R.layout.fragment_explore_articles, container, false);
            TextView headline = (TextView) rootView.findViewById(R.id.tv_article_title);
            headline.setText(hotNews.get(getArguments().getInt("NB")).getMainHeadline());
            TextView source = (TextView) rootView.findViewById(R.id.tv_article_site);
            source.setText(hotNews.get(getArguments().getInt("NB")).getSiteName() + "  /" );
            if (hotNews.get(getArguments().getInt("NB")).getDate() != null) {
                Date d = new Date();
                TextView date = (TextView) rootView.findViewById(R.id.tv_article_time);
                date.setText((String) DateUtils.getRelativeTimeSpanString(hotNews.get(getArguments().getInt("NB")).getDate().getTime(), d.getTime(), 0));
            }
            ImageButton articlePic = (ImageButton) rootView.findViewById(R.id.ib_article_pic);
            if (!hotNews.get(getArguments().getInt("NB")).getPicURL().equals("null")) {
                Picasso.with(getContext()).load(hotNews.get(getArguments().getInt("NB")).getPicURL()).fit().centerCrop().into(articlePic);
            } else {
                articlePic.setImageBitmap(hotNews.get(getArguments().getInt("NB")).getPicture());
            }
            articlePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    globalClass.getCategoriesHandler().setCurrentlyOpenURL(hotNews.get(getArguments().getInt("NB")).getUrl());
                    Intent web = new Intent(getActivity(),InnerWeb.class);
                    startActivity(web);
                }
            });
            numberOfLikes = (TextView) rootView.findViewById(R.id.tv_article_likes);
            float numOfLikes = Integer.parseInt(String.valueOf(hotNews.get(getArguments().getInt("NB")).getNumberOfLikes()));
            if(numOfLikes >= 1000)
            {
                numberOfLikes.setText(String.format("%.1f", (numOfLikes / 1000)) + "k");
            }
            else
            {
                numberOfLikes.setText(hotNews.get(getArguments().getInt("NB")).getNumberOfLikes() + "");
            }
            ImageButton like = (ImageButton) rootView.findViewById(R.id.ib_like);
            if(like != null)
            {
                if(!globalClass.getUser().getConnectedVia().equals("Facebook") && !globalClass.getUser().getConnectedVia().equals("Google"))
                {
                    Picasso.with(getContext()).load(R.drawable.hot_article_liked).into(like);
                    like.setAlpha((float) 0.3);
                    like.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast toast = Toast.makeText(getContext(), "רק משתמשים מחוברים יכולים לעשות לייק", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
                }
                else
                {
                    if(hotNews.get(getArguments().getInt("NB")).getLiked())
                    {
                        Picasso.with(getContext()).load(R.drawable.hot_article_liked).into(like);
                    }
                    else
                    {
                        Picasso.with(getContext()).load(R.drawable.like_hot_article).into(like);
                    }
                    like.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ImageButton like = (ImageButton) v.findViewById(R.id.ib_like);
                            if(hotNews.get(getArguments().getInt("NB")).getLiked())
                            {
                                Picasso.with(getContext()).load(R.drawable.like_hot_article).into(like);
                                hotNews.get(getArguments().getInt("NB")).setLiked(false);
                                hotNews.get(getArguments().getInt("NB")).decNumberOfLikes();
                                globalClass.getCommunication().clientSend("110○" + globalClass.getCategoriesHandler().getCurrentlyInUse().get(getArguments().getInt("NB")).getUrl() + "#");
                            }
                            else
                            {
                                Picasso.with(getContext()).load(R.drawable.hot_article_liked).into(like);
                                hotNews.get(getArguments().getInt("NB")).setLiked(true);
                                hotNews.get(getArguments().getInt("NB")).incNumberOfLikes();
                                globalClass.getCommunication().clientSend("110○" + globalClass.getCategoriesHandler().getCurrentlyInUse().get(getArguments().getInt("NB")).getUrl() + "#");
                            }
                            float numOfLikes = Integer.parseInt(String.valueOf(hotNews.get(getArguments().getInt("NB")).getNumberOfLikes()));
                            if(numOfLikes >= 1000)
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
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private GlobalClass globalClass;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            globalClass = (GlobalClass)getApplicationContext();
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return globalClass.getCategoriesHandler().getCurrentlyInUse().size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
}