package com.codepath.apps.mysimpletweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by bass on 2015/8/10.
 */
@Table(name = "User")
public class User extends Model implements Serializable {
    @Column(name = "name")
    private String name;
    @Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long uid;
    @Column(name = "screenName")
    private String screenName;
    @Column(name = "profileImageUrl")
    private String profileImageUrl;

    public String getProfileBackgroundImageUrl() {
        return profileBackgroundImageUrl;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public int getStatusesCount() {
        return statusesCount;
    }

    private String profileBackgroundImageUrl = "";

    private int followersCount;
    private int friendsCount;
    private int statusesCount;

    public static User fromJSON(JSONObject json) {
        User u = new User();
        try {
            u.name = json.getString("name");
            u.uid  = json.getLong("id");
            u.screenName = json.getString("screen_name");
            u.profileImageUrl = json.getString("profile_image_url");
            u.followersCount  = json.getInt("followers_count");
            u.friendsCount  = json.getInt("friends_count");
            u.statusesCount  = json.getInt("statuses_count");
            if(json.has("profile_banner_url"))
              u.profileBackgroundImageUrl =  json.getString("profile_banner_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return u;
    }

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
