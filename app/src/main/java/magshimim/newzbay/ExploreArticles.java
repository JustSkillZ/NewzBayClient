package magshimim.newzbay;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.TintManager;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
            View rootView = inflater.inflate(R.layout.fragment_explore_articles, container, false);
            TextView source = (TextView) rootView.findViewById(R.id.tv_article_site);
            source.setText(Categories.getHotNews().get(getArguments().getInt("NB")).getSiteName());
            ImageButton articlePic = (ImageButton) rootView.findViewById(R.id.ib_article_pic);
            articlePic.setImageBitmap(Categories.getHotNews().get(getArguments().getInt("NB")).getPicture());
            articlePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Categories.setCurrentlyOpenURL(Categories.getHotNews().get(getArguments().getInt("NB")).getUrl());
                    Intent web = new Intent(getActivity(),InnerWeb.class);
                    startActivity(web);
                }
            });
            numberOfLikes = (TextView) rootView.findViewById(R.id.tv_article_likes);
            numberOfLikes.setText(" " + Categories.getHotNews().get(getArguments().getInt("NB")).getNumberOfLikes());
            ImageButton like = (ImageButton) rootView.findViewById(R.id.ib_like);
            if(like != null)
            {
                if(!FacebookAndGoogle.isLoggedWithGoogle() && !FacebookAndGoogle.isLoggedWithFacebook())
                {
                    like.setBackground(getResources().getDrawable(R.drawable.hot_article_liked));
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
                    if(Categories.getHotNews().get(getArguments().getInt("NB")).getLiked())
                    {
                        like.setBackground(getResources().getDrawable(R.drawable.hot_article_liked));
                    }
                    else
                    {
                        like.setBackground(getResources().getDrawable(R.drawable.like_hot_article));
                    }
                    like.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ImageButton like = (ImageButton) v.findViewById(R.id.ib_like);
                            if(Categories.getHotNews().get(getArguments().getInt("NB")).getLiked())
                            {
                                like.setBackground(getResources().getDrawable(R.drawable.like_hot_article));
                                Categories.getHotNews().get(getArguments().getInt("NB")).setLiked(false);
                                Categories.getHotNews().get(getArguments().getInt("NB")).decNumberOfLikes();
                                numberOfLikes.setText(" " + Categories.getHotNews().get(getArguments().getInt("NB")).getNumberOfLikes());
                            }
                            else
                            {
                                like.setBackground(getResources().getDrawable(R.drawable.hot_article_liked));
                                Categories.getHotNews().get(getArguments().getInt("NB")).setLiked(true);
                                Categories.getHotNews().get(getArguments().getInt("NB")).incNumberOfLikes();
                                numberOfLikes.setText(" " + Categories.getHotNews().get(getArguments().getInt("NB")).getNumberOfLikes());
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

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return Categories.getHotNews().size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
}
