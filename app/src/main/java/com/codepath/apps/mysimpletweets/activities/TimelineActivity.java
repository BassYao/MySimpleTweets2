package com.codepath.apps.mysimpletweets.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.adapters.TimelineFragmentPagerAdapter;
import com.codepath.apps.mysimpletweets.helpers.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class TimelineActivity extends ActionBarActivity
        implements PostTweetFragment.OnFragmentInteractionListener,
        TimelineFragment.OnTimelineFragmentInteractionListener
{
    ViewPager viewPager;
    TimelineFragmentPagerAdapter aPager;
    PagerSlidingTabStrip tabsStrip;

    private TwitterClient client;
    private User mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        client = TwitterApplication.getRestClient();
        setupViews();
        getCurrentUser();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.twitter)));

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    private void setupViews() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        aPager    = new TimelineFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(aPager);

        // Give the PagerSlidingTabStrip the ViewPager

        tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);
    }
    private void getCurrentUser()
    {
        client.getVerifyCredentials(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                //Log.d("DEBUG",json.toString());
                mCurrentUser = User.fromJSON(json);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject error) {

                Log.d("DEBUG", error.toString());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new_tweet) {
            if(mCurrentUser == null) {
                Toast.makeText(getBaseContext(),"Getting User info...Please try again later.",Toast.LENGTH_SHORT).show();
            } else {
                startPost(null);
            }


            return true;
        } else if(id == R.id.action_profile) {
            Intent i = new Intent(TimelineActivity.this, ProfileActivity.class);
            i.putExtra("user", mCurrentUser);
            i.putExtra("current_user", mCurrentUser);
            startActivityForResult(i, 123);
            //ProfileActivity
        }

        return super.onOptionsItemSelected(item);
    }
    public void startPost(Tweet tweetReply){
        FragmentManager fm = getSupportFragmentManager();
        PostTweetFragment f = PostTweetFragment.newInstance(mCurrentUser, tweetReply);
        f.show(fm, "POST_TWEET");
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
                Toast.makeText(getBaseContext(),"Post Fail!",Toast.LENGTH_SHORT).show();
                Log.d("DEBUG", error.toString());
            }
        }, body,replyId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 123) {
            onTweetPost(data.getExtras().getString("body"),(Tweet)data.getExtras().getSerializable("tweet"));
        }
    }

    @Override
    public void onDetailClicked(Tweet t) {

        Intent i = new Intent(TimelineActivity.this, TweetDetailActivity.class);
        i.putExtra("tweet", t);
        i.putExtra("user", mCurrentUser);
        startActivityForResult(i, 123);

    }

    @Override
    public void onReplyClicked(Tweet tweetReply) {
        startPost(tweetReply);
    }

    @Override
    public void onProfileClick(User u) {
        Intent i = new Intent(TimelineActivity.this, ProfileActivity.class);
        i.putExtra("user", u);
        i.putExtra("current_user", mCurrentUser);
        startActivityForResult(i, 123);
    }

    @Override
    public void onFavoriteClick(final Tweet tweetReply) {
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
}
