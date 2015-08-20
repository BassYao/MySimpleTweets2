package com.codepath.apps.mysimpletweets.activities;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.helpers.EndlessScrollListener;
import com.codepath.apps.mysimpletweets.helpers.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class TimelineFragment extends Fragment implements TweetsArrayAdapter.OnTimeLineItemClickListener{
    private int mNum; // 1:home 2:Mention
    private long mUid;
    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private ListView lvTweets;
    private SwipeRefreshLayout swipeContainer;
    private long mMax_id = 0;
    private long mSince_id = 0;


    private OnTimelineFragmentInteractionListener mListener;
    static final int ITEMS_PER_PAGE = 25;

    public static TimelineFragment newInstance(int num) {
        TimelineFragment fragment = new TimelineFragment();
        Bundle args = new Bundle();
        args.putInt("num", num);
        fragment.setArguments(args);
        return fragment;
    }
    public static TimelineFragment newInstance(int num, long userId) {
        TimelineFragment fragment = new TimelineFragment();
        Bundle args = new Bundle();
        args.putInt("num", num);
        args.putLong("uid", userId);
        fragment.setArguments(args);
        return fragment;
    }

    public TimelineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        if (getArguments() != null) {
            mNum = getArguments().getInt("num");
            mUid = getArguments().getLong("uid", 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_timeline, container, false);

        lvTweets = (ListView) v.findViewById(R.id.lvTweets);

        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(getActivity(), tweets);
        aTweets.setOnTimeLineItemClickListener(this);
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
                if(mListener != null) {
                    mListener.onDetailClicked(t);
                }
            }
        });
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
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
        populteTimeline();
        return v;
    }
    JsonHttpResponseHandler onAPIResponse = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
            //Log.d("DEBUG",json.toString());
            ArrayList<Tweet> tweetsNew = Tweet.fromJSONArray(json);
            if (tweetsNew != null) {

                mMax_id = tweetsNew.get(tweetsNew.size() - 1).getUid() - 1;
            }
            aTweets.addAll(tweetsNew);
            swipeContainer.setRefreshing(false);

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject error) {
            Toast.makeText(getActivity(), "API FAILED. please try again later", Toast.LENGTH_SHORT).show();
            swipeContainer.setRefreshing(false);
            Log.d("DEBUG", error.toString());
        }
    };

    private void populteTimeline() {
        if(mMax_id == 0 && mSince_id == 0) {
            aTweets.clear();
        }
        if(mUid==0) {
            if (mNum == 0)
                client.getHomeTimeline(onAPIResponse, mSince_id, mMax_id, ITEMS_PER_PAGE);
            else
                client.getMentionsTimeline(onAPIResponse, mSince_id, mMax_id, ITEMS_PER_PAGE);
        } else {
            if (mNum == 0)
               client.getUserTimeline(onAPIResponse, mUid, mSince_id, mMax_id, ITEMS_PER_PAGE);

        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnTimelineFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onReplyClick(Tweet tweetReply) {
        mListener.onReplyClicked(tweetReply);
    }

    @Override
    public void onProfileClick(User u) {
        mListener.onProfileClick(u);
    }

    @Override
    public void onFavoriteClick(Tweet tweetReply) {
        mListener.onFavoriteClick(tweetReply);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnTimelineFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onDetailClicked(Tweet t);
        public void onReplyClicked(Tweet tweetReply);
        public void onProfileClick(User u);
        public void onFavoriteClick(Tweet tweetReply);
    }

    public void RefreshTimeline() {
        mMax_id = 0;
        mSince_id = 0;
        populteTimeline();
    }
    public void UpdateTimeLine(long uid, boolean isFavorite) {

        for(int i = 0; i<tweets.size();i++) {
          Tweet t = tweets.get(i);
            if(t.getUid() == uid && t.isFavorited() != isFavorite) {
                t.setFavorited(isFavorite);
                if(isFavorite) {
                    t.setFavoriteCount(t.getFavoriteCount()+1);
                } else {
                    t.setFavoriteCount(t.getFavoriteCount()-1);
                }
            }
            Tweet tt = t.getRetweetedStatus();
            if(tt!=null && t.getUid() == uid && tt.isFavorited() != isFavorite) {
                tt.setFavorited(isFavorite);
                if(isFavorite) {
                    tt.setFavoriteCount(t.getFavoriteCount()+1);
                } else {
                    tt.setFavoriteCount(t.getFavoriteCount()-1);
                }
            }


        }

        aTweets.notifyDataSetChanged();
    }
}
