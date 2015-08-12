package com.codepath.apps.mysimpletweets.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.codepath.apps.mysimpletweets.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by bass on 2015/8/12.
 */
public class PhotosArrayAdapter extends ArrayAdapter<String> {
    private static class ViewHolder {
        ImageView ivPhoto;
    }
    ArrayList<String> mUrls ;
    Context mContext;
    public PhotosArrayAdapter(Context context, ArrayList<String> urls) {
        super(context, android.R.layout.simple_list_item_1 ,urls);
        mUrls = urls;
        mContext = context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.detail_photo,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.ivPhoto.setImageResource(0);
        Picasso.with(getContext()).load(mUrls.get(position)).placeholder(R.mipmap.ic_launcher).into(viewHolder.ivPhoto);
        //return the view
        return convertView;
    }

}
