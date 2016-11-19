package com.msamogh.firstapp.admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.msamogh.firstapp.R;
import com.msamogh.firstapp.adapter.CommunityFeedAdapter;
import com.msamogh.firstapp.callback.FindCallback2;
import com.msamogh.firstapp.model.Community;
import com.msamogh.firstapp.util.MyCallback;
import com.parse.ParseObject;

import java.util.List;


public class PostsFragment extends Fragment {

    private static final String ARG_COMMUNITY = "community";

    private String mCommunityId;

    public static PostsFragment newInstance(String communityId) {
        PostsFragment f = new PostsFragment();
        Bundle b = new Bundle();
        b.putString(ARG_COMMUNITY, communityId);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCommunityId = getArguments().getString(ARG_COMMUNITY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manage_posts, container, false);
        AddFloatingActionButton newPost = (AddFloatingActionButton) rootView.findViewById(R.id.new_post);
        newPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CreatePostActivity.class);
                i.putExtra(ARG_COMMUNITY, mCommunityId);
                startActivity(i);
            }
        });

        final View progress = rootView.findViewById(R.id.progress_view);
        final ListView posts = (ListView) rootView.findViewById(R.id.list);

        posts.setDrawingCacheEnabled(true);
        posts.setScrollingCacheEnabled(false);
        posts.setSmoothScrollbarEnabled(false);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.activity_main_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Community(mCommunityId, new FindCallback2<ParseObject>(getActivity()) {
                    @Override
                    public void done(List<ParseObject> list) {
                        if (!list.isEmpty()) {
                            posts.setAdapter(new CommunityFeedAdapter(getActivity(), list.get(0), new MyCallback() {
                                @Override
                                public void callback() {
                                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                                    swipeRefreshLayout.setRefreshing(false);
                                    progress.setVisibility(View.GONE);
                                }
                            }));
                        }
                    }
                });
            }
        });

        new Community(mCommunityId, new FindCallback2<ParseObject>(getActivity()) {
            @Override
            public void done(List<ParseObject> list) {
                System.out.println(list.size() + mCommunityId);
                if (!list.isEmpty()) {
                    posts.setAdapter(new CommunityFeedAdapter(getActivity(), list.get(0), CommunityFeedAdapter.POSTS_ONLY, new MyCallback() {
                        @Override
                        public void callback() {
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                            progress.setVisibility(View.GONE);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }));
                }
            }
        });

        ViewCompat.setElevation(rootView, 50);
        return rootView;
    }
}