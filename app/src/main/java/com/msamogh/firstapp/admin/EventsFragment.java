package com.msamogh.firstapp.admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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


public class EventsFragment extends Fragment {

    private static final String ARG_COMMUNITY = "community";

    private String community;
    private TextView mTextView;

    public static EventsFragment newInstance(String community) {
        EventsFragment f = new EventsFragment();
        Bundle b = new Bundle();
        b.putString(ARG_COMMUNITY, community);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        community = getArguments().getString(ARG_COMMUNITY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manage_events, container, false);
        ViewCompat.setElevation(rootView, 50);
        AddFloatingActionButton newPost = (AddFloatingActionButton) rootView.findViewById(R.id.new_event);
        newPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CreateEventActivity.class);
                i.putExtra("community", community);
                startActivity(i);
            }
        });

        final View progress = rootView.findViewById(R.id.progress_view);
        final ListView posts = (ListView) rootView.findViewById(R.id.list);

        progress.setVisibility(View.VISIBLE);
        posts.setVisibility(View.GONE);

        new Community(community, new FindCallback2<ParseObject>(getActivity()) {
            @Override
            public void done(List<ParseObject> list) {
                if (!list.isEmpty()) {
                    posts.setAdapter(new CommunityFeedAdapter(getActivity(), list.get(0), CommunityFeedAdapter.EVENTS_ONLY, new MyCallback() {
                        @Override
                        public void callback() {
                            posts.setVisibility(View.VISIBLE);
                            progress.setVisibility(View.GONE);
                        }
                    }));
                }
            }
        });


        ViewCompat.setElevation(rootView, 50);
        return rootView;
    }
}