package com.codepath.apps.mysimpletweets.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.helpers.EndlessScrollListener;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.helpers.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends ActionBarActivity implements PostTweetFragment.OnFragmentInteractionListener{

    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private ListView lvTweets;
    private SwipeRefreshLayout swipeContainer;
    private User mCurrentUser;
    private long mMax_id = 0;
    private long mSince_id = 0;
    
    static final int ITEMS_PER_PAGE = 25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        client = TwitterApplication.getRestClient();
        setupViews();
        getCurrentUser();
        /*
        List<Tweet> lt = Tweet.getAll();
        if(lt != null && lt.size()>0) {
            aTweets.addAll(lt);
            for (Tweet t:lt) {
                if((mMax_id==0) || t.getUid()<mMax_id)
                    mMax_id = t.getUid();

            }
            if(mMax_id!=0)
                mMax_id++;
        } else {
            populteTimeline();
        }
        */
        populteTimeline();
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
        lvTweets = (ListView) findViewById(R.id.lvTweets);

        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(this, tweets);
        lvTweets.setAdapter(aTweets);
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                populteTimeline();
                // or customLoadMoreDataFromApi(totalItemsCount);
            }
        });
        lvTweets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tweet t = aTweets.getItem(position);
                Intent i = new Intent(TimelineActivity.this, TweetDetailActivity.class);
                i.putExtra("tweet", t);
                i.putExtra("user", mCurrentUser);
                startActivityForResult(i, 123);
            }
        });
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                mMax_id = 0;
                mSince_id = 0;
                populteTimeline();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);



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

    private void populteTimeline() {
        if(mMax_id == 0 && mSince_id == 0) {
            aTweets.clear();
            /*
            try {
                SQLiteUtils.execSql("DELETE FROM Tweet");
                SQLiteUtils.execSql("DELETE FROM User");
            } catch (Exception e){}
            */
        }
        if(!isNetworkAvailable()) {
            Toast.makeText(getBaseContext(), "Please check your network status and try again.", Toast.LENGTH_SHORT).show();
            swipeContainer.setRefreshing(false);
            return;
        }
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                //Log.d("DEBUG",json.toString());
                ArrayList<Tweet> tweets = Tweet.fromJSONArray(json);
                if (tweets != null) {

                    mMax_id = tweets.get(tweets.size() - 1).getUid() - 1;
                }
/*
                for (Tweet tweet:tweets) {
                    tweet.save();
                }
*/
                aTweets.addAll(tweets);
                swipeContainer.setRefreshing(false);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject error) {
                Toast.makeText(getBaseContext(), "API FAILED. please try again later", Toast.LENGTH_SHORT).show();
                swipeContainer.setRefreshing(false);
                Log.d("DEBUG", error.toString());
            }
        }, mSince_id, mMax_id, ITEMS_PER_PAGE);
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
                mMax_id = 0;
                mSince_id = 0;
                populteTimeline();
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
}
