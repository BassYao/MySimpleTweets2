package com.codepath.apps.mysimpletweets.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by bass on 2015/8/10.
 */
public class Tweet {
    private String body;
    private long uid;
    private String createdAt;
    private User user;
    private Tweet retweetedStatus = null;
    private int favoriteCount = 0;
    private boolean favorited = false;
    private int retweetCount = 0;

    public static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid  = jsonObject.getLong("id");

            tweet.createdAt = jsonObject.getString("created_at");
            tweet.favoriteCount = jsonObject.getInt("favorite_count");
            tweet.favorited = jsonObject.getBoolean("favorited");
            tweet.retweetCount = jsonObject.getInt("retweet_count");

            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
            JSONObject retweet =  jsonObject.getJSONObject("retweeted_status");
            if(retweet != null)
               tweet.retweetedStatus = fromJSON(retweet);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tweet;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<>();
        for(int i=0; i<jsonArray.length();i++) {
            try {
                JSONObject tweetJson = jsonArray.getJSONObject(i);
                Tweet t = Tweet.fromJSON(tweetJson);
                if(t!=null)
                    tweets.add(t);
            } catch (JSONException e) {
                continue;
            }
        }
        return tweets;

    }

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }
    public Tweet getRetweetedStatus() {
        return retweetedStatus;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public int getRetweetCount() {
        return retweetCount;
    }
}
