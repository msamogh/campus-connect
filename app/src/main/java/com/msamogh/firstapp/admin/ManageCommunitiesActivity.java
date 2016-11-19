package com.msamogh.firstapp.admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.msamogh.firstapp.R;
import com.msamogh.firstapp.callback.FindCallback2;
import com.msamogh.firstapp.model.Community;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;


public class ManageCommunitiesActivity extends AppCompatActivity {

    private ListView mCommunitiesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_communties);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My communities");


        mCommunitiesListView = (ListView) findViewById(R.id.communities);
        mCommunitiesListView.setScrollingCacheEnabled(false);
        mCommunitiesListView.setSmoothScrollbarEnabled(false);

        initCommunities(false);
    }

    private void initCommunities(boolean forceReload) {

        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Fetching communities")
                .content(R.string.please_wait)
                .progress(true, 0)
                .show();
        Community.fetchAllCommunitiesWithAdmin(ParseUser.getCurrentUser(), forceReload, new FindCallback2<ParseObject>(ManageCommunitiesActivity.this) {
            @Override
            public void done(final List<ParseObject> list) {

                if (!list.isEmpty()) {
                    findViewById(R.id.placeholder).setVisibility(View.GONE);
                    mCommunitiesListView.setVisibility(View.VISIBLE);
                    mCommunitiesListView.setAdapter(new BaseAdapter() {
                        @Override
                        public int getCount() {
                            return list.size();
                        }

                        @Override
                        public Object getItem(int position) {
                            return list.get(position);
                        }

                        @Override
                        public long getItemId(int position) {
                            return 0;
                        }

                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            final View rootView = LayoutInflater.from(ManageCommunitiesActivity.this).inflate(R.layout.list_item_communtiy, parent, false);
                            final TextView name = (TextView) rootView.findViewById(R.id.communityName);
                            final TextView description = (TextView) rootView.findViewById(R.id.description);
                            final ParseObject po = list.get(position);
                            name.setText(po.getString("communityId"));
                            description.setText(po.getString("name"));
                            rootView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(ManageCommunitiesActivity.this, CommunityActivity.class);
                                    i.putExtra("community", po.getString("communityId"));
                                    startActivity(i);
                                }
                            });
                            return rootView;
                        }
                    });
                }
                dialog.dismiss();
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manage_communties, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sync) {
            initCommunities(true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
