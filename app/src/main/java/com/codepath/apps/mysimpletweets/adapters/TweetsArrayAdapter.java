package com.codepath.apps.mysimpletweets.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by bass on 2015/8/10.
 */
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {
    private ViewHolder viewHolder;
    private OnTimeLineItemClickListener mListener = null;
    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, android.R.layout.simple_list_item_1 ,tweets);
    }
    private  class ViewHolder {
        TextView tvBody;
        TextView tvUserName;
        TextView tvName;
        TextView tvCreatedTime;
        TextView tvRetweetBy;
        TextView tvRetweet;
        TextView tvReply;
        TextView tvFavorite;
        ImageView ivProfile;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tweet t = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.tvBody = (TextView)convertView.findViewById(R.id.tvBody);
            viewHolder.tvUserName = (TextView)convertView.findViewById(R.id.tvUserName);
            viewHolder.tvName   = (TextView)convertView.findViewById(R.id.tvName);
            viewHolder.tvCreatedTime = (TextView)convertView.findViewById(R.id.tvCreatedTime);
            viewHolder.ivProfile = (ImageView)convertView.findViewById(R.id.ivProfileImage);
            viewHolder.tvRetweetBy = (TextView)convertView.findViewById(R.id.tvRetweetBy);
            viewHolder.tvFavorite = (TextView)convertView.findViewById(R.id.tvFavorite);
            viewHolder.tvReply = (TextView)convertView.findViewById(R.id.tvReply);
            viewHolder.tvRetweet = (TextView)convertView.findViewById(R.id.tvRetweet);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        Tweet tweetToshow = t.getRetweetedStatus();
        if(tweetToshow == null) {
            tweetToshow = t;
            viewHolder.tvRetweetBy.setVisibility(View.GONE);
        } else {
            viewHolder.tvRetweetBy.setVisibility(View.VISIBLE);
            viewHolder.tvRetweetBy.setText(String.format(getContext().getResources().getString(R.string.retweeted_by), t.getUser().getName()));
        }

        viewHolder.tvUserName.setText(tweetToshow.getUser().getName());
        viewHolder.tvName.setText(String.format("@%s", tweetToshow.getUser().getScreenName()));
        viewHolder.tvCreatedTime.setText(getRelativeTimeAgo(tweetToshow.getCreatedAt()));
        viewHolder.tvBody.setText(tweetToshow.getBody());
        viewHolder.tvRetweet.setText(Integer.toString(tweetToshow.getRetweetCount()));
        viewHolder.tvFavorite.setText(Integer.toString(tweetToshow.getFavoriteCount()));
        viewHolder.tvFavorite.setTag(tweetToshow);
        viewHolder.tvReply.setTag(tweetToshow);
        viewHolder.ivProfile.setTag(tweetToshow);
        viewHolder.tvReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onReplyClick((Tweet) v.getTag());
            }
        });
        viewHolder.ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onProfileClick(((Tweet) v.getTag()).getUser());
            }
        });
        viewHolder.ivProfile.setImageResource(android.R.color.transparent);
        if(tweetToshow.isFavorited()) {
            viewHolder.tvFavorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.favorite_on, 0, 0, 0);
        } else {
            viewHolder.tvFavorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.favorite, 0, 0, 0);
        }
        viewHolder.tvFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onFavoriteClick(((Tweet) v.getTag()));
            }
        });

        Picasso.with(getContext()).load(tweetToshow.getUser().getProfileImageUrl()).into(viewHolder.ivProfile);

      //  Drawable img = getContext().getResources().getDrawable(
      //          R.drawable.retweet);
      //  img.setBounds(0, 0, 20, viewHolder.tvRetweetBy.getMeasuredHeight());
     //   viewHolder.tvRetweetBy.setCompoundDrawables(img, null, null, null);
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
                relativeDate = "Just Now";
            }
            //relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
            //        System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public void setOnTimeLineItemClickListener(OnTimeLineItemClickListener listener) {
        mListener = listener;
    }
    public interface OnTimeLineItemClickListener {

        public void onReplyClick(Tweet tweetReply);
        public void onProfileClick(User u);
        public void onFavoriteClick(Tweet tweetReply);
    }
}
