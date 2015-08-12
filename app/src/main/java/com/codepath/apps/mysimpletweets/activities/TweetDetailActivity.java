package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.adapters.PhotosArrayAdapter;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.squareup.picasso.Picasso;

public class TweetDetailActivity extends ActionBarActivity implements PostTweetFragment.OnFragmentInteractionListener {
    private Tweet tweet;
    private User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        tweet = (Tweet)getIntent().getSerializableExtra("tweet");
        currentUser = (User)getIntent().getSerializableExtra("user");
        setupViews();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.twitter)));
    }
    private void setupViews()
    {
        Tweet tweetToshow = tweet.getRetweetedStatus();
        if(tweetToshow == null) {
            tweetToshow = tweet;
            ( (TextView)findViewById(R.id.tvRetweetBy)).setVisibility(View.GONE);
        } else {
            ( (TextView)findViewById(R.id.tvRetweetBy)).setVisibility(View.VISIBLE);
            ( (TextView)findViewById(R.id.tvRetweetBy)).setText(String.format(getBaseContext().getResources().getString(R.string.retweeted_by), tweet.getUser().getName()));
        }
        ( (TextView)findViewById(R.id.tvBody)).setText(tweetToshow.getBody());
        ( (TextView)findViewById(R.id.tvUserName)).setText(tweetToshow.getUser().getName());
        ( (TextView)findViewById(R.id.tvName)).setText(String.format("@%s", tweetToshow.getUser().getScreenName()));
        ( (TextView)findViewById(R.id.tvRetweet)).setText(Integer.toString(tweetToshow.getRetweetCount()));
        ( (TextView)findViewById(R.id.tvFavorite)).setText(Integer.toString(tweetToshow.getFavoriteCount()));
        Picasso.with(this).load(tweetToshow.getUser().getProfileImageUrl()).into(((ImageView) findViewById(R.id.ivProfileImage)));

        ( (TextView)findViewById(R.id.tvReply)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                PostTweetFragment f = PostTweetFragment.newInstance(currentUser, tweet);
                f.show(fm, "POST_TWEET");
            }
        });
        ( (ListView)findViewById(R.id.lvPhotos)).setAdapter(new PhotosArrayAdapter(this, tweetToshow.getImgUrls()));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_tweet_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    @Override
    public void onTweetPost(String body, Tweet tweetReply) {
        Intent i = new Intent();
        i.putExtra("body",body);
        i.putExtra("tweet",tweetReply);
        setResult(RESULT_OK, i);
        finish();
    }
}
