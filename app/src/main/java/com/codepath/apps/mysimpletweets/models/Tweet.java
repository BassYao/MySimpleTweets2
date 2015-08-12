package com.codepath.apps.mysimpletweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bass on 2015/8/10.
 */
@Table(name = "Tweet")
public class Tweet  extends Model implements Serializable {
    @Column(name = "User", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private User user;
    @Column(name = "Tweet", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private Tweet retweetedStatus = null;


    @Column(name = "body")
    private String body;
    @Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long uid;
    @Column(name = "createdAt")
    private String createdAt;

    @Column(name = "favoriteCount")
    private int favoriteCount = 0;
    @Column(name = "favorited")
    private boolean favorited = false;
    @Column(name = "retweetCount")
    private int retweetCount = 0;
    @Column(name = "imgUrls")
    private ArrayList<String> imgUrls = new ArrayList<String>() ;

    @Column(name = "isRetweet")
    private boolean isRetweet = false;


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
         //   tweet.user.save();
            if(jsonObject.has("extended_entities"))
                tweet.imgUrls = getImageUrlsFromEntities(jsonObject.getJSONObject("extended_entities"));
            if(jsonObject.has("retweeted_status")) {
                JSONObject retweet = jsonObject.getJSONObject("retweeted_status");
                if (retweet != null) {
                    tweet.retweetedStatus = fromJSON(retweet);
                    tweet.retweetedStatus.isRetweet = true;
                 //   tweet.retweetedStatus.save();
                }
            }

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
                if(t!=null) {

                    tweets.add(t);
                }
            } catch (JSONException e) {
                continue;
            }
        }
        return tweets;

    }
    public static ArrayList<String> getImageUrlsFromEntities(JSONObject jsonObject){
        ArrayList<String> urls = new ArrayList<String>();
        try {
          JSONArray jsonArray = jsonObject.getJSONArray("media");
          for(int i=0; i<jsonArray.length();i++) {
                JSONObject media = jsonArray.getJSONObject(i);
                urls.add(media.get("media_url").toString());
          }
        } catch (JSONException e) {

        }
        return urls;
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

    public ArrayList<String> getImgUrls() {
        return imgUrls;
    }

    public static List<Tweet> getAll() {
        // This is how you execute a query
        try {
            List<Tweet> ret = new Select()
                    .from(Tweet.class)
                    .where("isRetweet = ?", false)
                    .orderBy("uid DESC")
                    .execute();
            int i = ret.size();
            return ret;
        }catch (Exception e) {
            return null;
        }

    }

}
