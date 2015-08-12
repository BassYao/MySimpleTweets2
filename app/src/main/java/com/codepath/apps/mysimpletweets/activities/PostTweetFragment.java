package com.codepath.apps.mysimpletweets.activities;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PostTweetFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PostTweetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostTweetFragment extends DialogFragment {
    private User mUser;
    private Tweet mTweetReply = null;
    private TextView tvStringLen;
    private EditText edtBody;

    private OnFragmentInteractionListener mListener;

    // TODO: Rename and change types and number of parameters
    public static PostTweetFragment newInstance(User user, Tweet tweetReply) {
        PostTweetFragment fragment = new PostTweetFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        args.putSerializable("tweetReply", tweetReply);
        fragment.setArguments(args);
        return fragment;
    }

    public PostTweetFragment() {
        // Required empty public constructor
    }
    private String GetDefaultBody() {
        String ret = "";
        if(mTweetReply != null) {
            if(mTweetReply.getRetweetedStatus() != null) {
                ret = String.format("@%s ", mTweetReply.getRetweetedStatus().getUser().getScreenName());
            }
            ret += String.format("@%s ", mTweetReply.getUser().getScreenName());
        }
        return ret;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUser = (User)getArguments().getSerializable("user");
            mTweetReply = (Tweet)getArguments().getSerializable("tweetReply");
        }

        setStyle(DialogFragment.STYLE_NO_TITLE,getTheme());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_post_tweet, container, false);

        Button btTweet  = (Button) v.findViewById(R.id.btTweet);
        edtBody  = (EditText) v.findViewById(R.id.etBody);
        TextView tvName  = (TextView) v.findViewById(R.id.tvName);
        TextView tvUserName  = (TextView) v.findViewById(R.id.tvUserName);
        tvStringLen  = (TextView) v.findViewById(R.id.tvStringLen);
        ImageView ivProfile  = (ImageView) v.findViewById(R.id.ivProfileImage);
        ImageButton ibBack = (ImageButton) v.findViewById(R.id.ibBack);

        tvUserName.setText(mUser.getName());
        tvName.setText(String.format("@%s", mUser.getScreenName()));
        Picasso.with(getActivity()).load(mUser.getProfileImageUrl()).into(ivProfile);

        edtBody.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                tvStringLen.setText(Integer.toString(140 - s.length()));
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        edtBody.append(GetDefaultBody());
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onTweetPost(edtBody.getText().toString(), mTweetReply);
                }
                dismiss();
            }
        });
        return v;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onTweetPost(String body, Tweet tweetReply);
    }

}
