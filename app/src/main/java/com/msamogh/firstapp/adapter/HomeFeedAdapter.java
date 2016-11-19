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
import android.widget.Toast;

import com.msamogh.firstapp.Application;
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
public class HomeFeedAdapter extends BaseAdapter {

    private static final int POST = 1, EVENT = 2;

    private List<ParseObject> allPosts = new ArrayList<>();
    private final Activity mActivity;

    public HomeFeedAdapter(final Activity activity, final boolean forceReload, final MyCallback callback) {
        mActivity = activity;
        final Application app = (Application) activity.getApplicationContext();

        if (!forceReload && !app.getFeed().isEmpty()) {
            allPosts.clear();
            allPosts = app.getFeed();
            notifyDataSetChanged();
        } else {
            HashMap<String, Object> params = new HashMap<>();
            params.put("page", 0);
            ParseCloud.callFunctionInBackground("fetchHomeFeed", params, new FunctionCallback<List>() {
                public void done(List posts, ParseException e) {
                    if (e == null) {
                        allPosts = posts;
                        app.setFeed(posts);
                        notifyDataSetChanged();
                        callback.callback();
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }
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
    public int getItemViewType(int position) {
        final ParseObject po = allPosts.get(position);
        if (po.getString("type") == null || po.getString("type").equals("post"))
            return POST;
        else
            return EVENT;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ParseObject po = allPosts.get(position);

        switch (getItemViewType(position)) {
            case POST:
                PostViewHolder viewHolder;
                View v = convertView;
                if (v == null) {
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_post_with_image, parent, false);
                    viewHolder = new PostViewHolder();
                    viewHolder.communityId = (TextView) v.findViewById(R.id.community_id);
                    viewHolder.postTime = (TextView) v.findViewById(R.id.post_time);
                    viewHolder.communityName = (TextView) v.findViewById(R.id.community_name);
                    viewHolder.postContent = (TextView) v.findViewById(R.id.post_content);
                    viewHolder.attachedImagesParent = v.findViewById(R.id.attached_images_parent);
                    viewHolder.attachedImages = (LinearLayout) v.findViewById(R.id.attached_images);

                    v.setTag(viewHolder);
                } else {
                    viewHolder = (PostViewHolder) v.getTag();
                }

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

            /*case EVENT:
                PostViewHolder eventViewHolder;
                v = convertView;
                if (v == null) {
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_event_3, parent, false);
                    eventViewHolder = new PostViewHolder();
                    // viewHolder.communityId = (TextView) v.findViewById(R.id.community_id);
                    eventViewHolder.postTime = (TextView) v.findViewById(R.id.post_time);
                    eventViewHolder.communityName = (TextView) v.findViewById(R.id.community_name);
                   // eventViewHolder.eventTitle = (TextView) v.findViewById(R.id.event_title);
                    v.setTag(eventViewHolder);
                } else {
                    eventViewHolder = (PostViewHolder) v.getTag();
                    //viewHolder.communityId.setText(po.getParseObject("community").getString("communityId"));
                    eventViewHolder.postTime.setText(new PrettierTime().format(po.getCreatedAt()));
                }
                return v;*/
            default:
                throw new RuntimeException("Invalid layout");
        }
    }

    static class PostViewHolder {
        public TextView communityId;
        public TextView postTime;
        public TextView communityName;
        public TextView postContent;
        public View attachedImagesParent;
        public LinearLayout attachedImages;
    }

    static class EventViewHolder {
        public TextView communityId;
        public TextView postTime;
        public TextView eventTitle;
        public TextView communityName;
        public TextView location;
        public TextView time;
        public TextView date;
    }

}
