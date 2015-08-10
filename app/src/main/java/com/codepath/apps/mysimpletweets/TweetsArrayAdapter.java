package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by bass on 2015/8/10.
 */
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {
    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, android.R.layout.simple_list_item_1 ,tweets);
    }
    private static class ViewHolder {
        TextView tvBody;
        TextView tvUserName;
        TextView tvName;
        TextView tvCreatedTime;
        ImageView ivProfile;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tweet t = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.tvBody = (TextView)convertView.findViewById(R.id.tvBody);
            viewHolder.tvUserName = (TextView)convertView.findViewById(R.id.tvUserName);
            viewHolder.tvName   = (TextView)convertView.findViewById(R.id.tvName);
            viewHolder.tvCreatedTime = (TextView)convertView.findViewById(R.id.tvCreatedTime);
            viewHolder.ivProfile = (ImageView)convertView.findViewById(R.id.ivProfileImage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.tvUserName.setText(t.getUser().getScreenName());
        viewHolder.tvName.setText(String.format("@%s", t.getUser().getName()));
        viewHolder.tvCreatedTime.setText(getRelativeTimeAgo(t.getCreatedAt()));
        viewHolder.tvBody.setText(t.getBody());
        viewHolder.ivProfile.setImageResource(android.R.color.transparent);
        Picasso.with(getContext()).load(t.getUser().getProfileImageUrl()).into(viewHolder.ivProfile);
        return convertView;

    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            long dateDiff   = System.currentTimeMillis() - dateMillis;

            if(dateDiff > 86400000) {
                relativeDate = Long.toString(dateDiff / 86400000) + "d";
            } else if(dateDiff > 3600000) {
                relativeDate = Long.toString(dateDiff / 3600000) + "h";
            } else if(dateDiff > 60000) {
                relativeDate = Long.toString(dateDiff / 60000) + "m";
            } else {
                relativeDate = Long.toString(dateDiff / 60000) + "s";
            }
            //relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
            //        System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}
