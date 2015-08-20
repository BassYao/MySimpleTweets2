package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.adapters.ProfilePagerAdapter;
import com.codepath.apps.mysimpletweets.helpers.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileActivity extends ActionBarActivity implements TimelineFragment.OnTimelineFragmentInteractionListener,PostTweetFragment.OnFragmentInteractionListener {
    private User profileUser;
    private User mCurrentUser;
    ViewPager viewPager;
    ProfilePagerAdapter aPager;
    PagerSlidingTabStrip tabsStrip;
    private TwitterClient client;
    ArrayList<Integer> counts = new ArrayList<Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.twitter)));
        profileUser = (User)getIntent().getSerializableExtra("user");
        mCurrentUser = (User)getIntent().getSerializableExtra("current_user");
        client = TwitterApplication.getRestClient();
        setupViews();
    }

    private void setupViews()
    {
        setTitle(profileUser.getName());
        counts.add(profileUser.getStatusesCount());
        counts.add(profileUser.getFriendsCount());
        counts.add(profileUser.getFollowersCount());
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        aPager    = new ProfilePagerAdapter(getSupportFragmentManager(),counts,profileUser.getUid());
        viewPager.setAdapter(aPager);

        // Give the PagerSlidingTabStrip the ViewPager

        tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);


        ( (TextView)findViewById(R.id.tvUserName)).setText(profileUser.getName());
        ( (TextView)findViewById(R.id.tvName)).setText(String.format("@%s", profileUser.getScreenName()));
        ( (TextView)findViewById(R.id.tvDetail)).setText(
                String.format("TWEETS:%d   FOLLOWING:%d   FOLLOWERS:%d", profileUser.getStatusesCount(), profileUser.getFriendsCount(),
                        profileUser.getFollowersCount()));

        Picasso.with(this).load(profileUser.getProfileImageUrl()).into(((ImageView) findViewById(R.id.ivProfileImage)));
        if(!profileUser.getProfileBackgroundImageUrl().isEmpty())
           Picasso.with(this).load(profileUser.getProfileBackgroundImageUrl()).into(((ImageView) findViewById(R.id.ivProfileBackground)));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
      //  getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetailClicked(Tweet t) {
        Intent i = new Intent(ProfileActivity.this, TweetDetailActivity.class);
        i.putExtra("tweet", t);
        i.putExtra("user", mCurrentUser);
        startActivityForResult(i, 123);
    }

    @Override
    public void onReplyClicked(Tweet tweetReply) {
        FragmentManager fm = getSupportFragmentManager();
        PostTweetFragment f = PostTweetFragment.newInstance(mCurrentUser, tweetReply);
        f.show(fm, "POST_TWEET");
    }

    @Override
    public void onProfileClick(User u) {
        Intent i = new Intent(ProfileActivity.this, ProfileActivity.class);
        i.putExtra("user", u);
        i.putExtra("current_user", mCurrentUser);
        startActivityForResult(i, 123);
    }

    @Override
    public void onFavoriteClick(Tweet tweetReply) {
        boolean create = !tweetReply.isFavorited();
        if(create){
            client.createFavorite(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                    long id = 0;
                    try {
                        id = json.getLong("id");
                        ((TimelineFragment) aPager.getRegisteredFragment(viewPager.getCurrentItem())).UpdateTimeLine(id,true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject error) {
                    Toast.makeText(getBaseContext(), "Post Fail!", Toast.LENGTH_SHORT).show();
                    Log.d("DEBUG", error.toString());
                }
            }, tweetReply.getUid());
        } else {
            client.destroyFavorite(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                    long id = 0;
                    try {
                        id = json.getLong("id");
                        ((TimelineFragment) aPager.getRegisteredFragment(viewPager.getCurrentItem())).UpdateTimeLine(id,false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject error) {
                    Toast.makeText(getBaseContext(), "Post Fail!", Toast.LENGTH_SHORT).show();
                    Log.d("DEBUG", error.toString());
                }
            }, tweetReply.getUid());

        }
    }

    @Override
    public void onTweetPost(String body, Tweet tweetReply) {
        long replyId = 0;
        if(body.length() == 0 )
            return;
        if(tweetReply != null) {
            replyId = tweetReply.getUid();
        }
        client.updateStatuses(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {

                ((TimelineFragment)aPager.getRegisteredFragment(viewPager.getCurrentItem())).RefreshTimeline();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject error) {
                Toast.makeText(getBaseContext(), "Post Fail!", Toast.LENGTH_SHORT).show();
                Log.d("DEBUG", error.toString());
            }
        }, body,replyId);
    }
}
