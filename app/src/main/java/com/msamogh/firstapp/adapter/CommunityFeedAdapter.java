package com.msamogh.firstapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.msamogh.firstapp.ImageShowcaseActivity;
import com.msamogh.firstapp.R;
import com.msamogh.firstapp.util.MyCallback;
import com.msamogh.firstapp.util.PrettierTime;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Amogh on 7/7/15.
 */
public class CommunityFeedAdapter extends BaseAdapter {

    private List<ParseObject> allPosts = new ArrayList<>();
    private final Activity mActivity;
    public final static int POSTS_ONLY = 1, EVENTS_ONLY = 2, ALL = 0;

    public CommunityFeedAdapter(final Activity activity, final ParseObject community, final int filter, final MyCallback callback) {
        mActivity = activity;
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageNumber", 0);
        params.put("communityId", community.getString("communityId"));
        params.put("filter", filter);
        ParseCloud.callFunctionInBackground("fetchCommunityFeed", params, new FunctionCallback<List>() {
            public void done(List posts, ParseException e) {
                if (e == null) {
                    allPosts = posts;
                    notifyDataSetChanged();
                    callback.callback();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public CommunityFeedAdapter(final Activity activity, final ParseObject community, final MyCallback callback) {
        this(activity, community, ALL, callback);
    }

    @Override
    public int getCount() {
        return allPosts.size();
    }

    @Override
    public Object getItem(int position) {
        return allPosts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        final ParseObject po = allPosts.get(position);
        if (v == null) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_post_with_image, parent, false);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.communityId = (TextView) v.findViewById(R.id.community_id);
            viewHolder.postTime = (TextView) v.findViewById(R.id.post_time);
            viewHolder.communityName = (TextView) v.findViewById(R.id.community_name);
            viewHolder.postContent = (TextView) v.findViewById(R.id.post_content);
            viewHolder.attachedImagesParent = v.findViewById(R.id.attached_images_parent);
            viewHolder.attachedImages = (LinearLayout) v.findViewById(R.id.attached_images);

            v.setTag(viewHolder);
        }

        final ViewHolder viewHolder = (ViewHolder) v.getTag();

        if (null != po.getList("images") && !po.getList("images").isEmpty()) {
            viewHolder.attachedImagesParent.setVisibility(View.VISIBLE);
            viewHolder.attachedImages.removeAllViews();
            for (final Object f : po.getList("images")) {
                final ParseFile file = (ParseFile) f;
                final ImageView image = (ImageView) LayoutInflater.from(v.getContext()).inflate(R.layout.image_create_post, null, false).findViewById(R.id.image);
                Picasso.with(v.getContext()).load(file.getUrl()).placeholder(R.mipmap.ic_launcher).centerCrop().resize(150, 150).into(image);
                image.setPadding(10, 0, 0, 10);

                LinearLayout.LayoutParams l = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
                l.weight = 1;
                image.setLayoutParams(l);

                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mActivity.getBaseContext(), ImageShowcaseActivity.class);
                        intent.putExtra("image", file.getUrl());
                        intent.putExtra("title", (po.getList("images").indexOf(f) + 1) + " of " + po.getList("images").size());
                        mActivity.startActivity(intent);
                    }
                });

                viewHolder.attachedImages.addView(image);
            }
        } else {
            viewHolder.attachedImagesParent.setVisibility(View.GONE);
        }

        String community = po.getParseObject("community").getString("communityId");
        viewHolder.communityId.setText("@" + community);

        PrettyTime t = new PrettierTime();
        viewHolder.postTime.setText(t.format(po.getCreatedAt()));
        viewHolder.communityName.setText(po.getParseObject("community").getString("name"));
        viewHolder.postContent.setText(po.getString("content"));

        return v;
    }

    static class ViewHolder {
        public TextView communityId;
        public TextView postTime;
        public TextView communityName;
        public TextView postContent;
        public View attachedImagesParent;
        public LinearLayout attachedImages;
    }

}
