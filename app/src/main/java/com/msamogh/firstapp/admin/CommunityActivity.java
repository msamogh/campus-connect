package com.msamogh.firstapp.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.msamogh.firstapp.R;
import com.msamogh.firstapp.callback.FindCallback2;
import com.msamogh.firstapp.model.Community;
import com.msamogh.firstapp.model.Subscription;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirections;

import java.util.ArrayList;
import java.util.List;

import io.karim.MaterialTabs;

public class CommunityActivity extends AppCompatActivity {

    private String communityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        communityId = getIntent().getExtras().getString("community");

        MaterialTabs tabs = (MaterialTabs) findViewById(R.id.material_tabs);
        tabs.setIndicatorColor(Color.WHITE);
        tabs.setTextColorSelected(Color.WHITE);
        tabs.setTextColorUnselected(Color.argb(170, 255, 255, 255));
        tabs.setAllCaps(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Manage - " + communityId);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new PostsAndEventPagerAdapter(getSupportFragmentManager()));

        tabs.setViewPager(pager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_community, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_subscription_requests) {
            ParseQuery<ParseObject> requests = ParseQuery.getQuery("MembershipRequest").whereEqualTo("communityId", communityId);
            requests.include("user");
            requests.findInBackground(new FindCallback2<ParseObject>(CommunityActivity.this) {
                @Override
                public void done(List<ParseObject> list) {
                    MaterialDialog dialog = new SubscriptionRequestsDialogBuilder(list).buildAndShow();
                    Snackbar.make(dialog.getView(), "Swipe left/right to accept/reject requests", Snackbar.LENGTH_SHORT).show();
                }
            });
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            new MaterialDialog.Builder(this)
                    .title("Delete community")
                    .input("\"Enter community ID to proceed with deletion\"", null, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            if (!TextUtils.isEmpty(input) && input.toString().equals(communityId)) {
                                new MaterialDialog.Builder(CommunityActivity.this)
                                        .title("Do you really want to delete " + communityId + "?")
                                        .positiveText("Delete")
                                        .positiveColor(Color.RED)
                                        .negativeColor(Color.parseColor("#666666"))
                                        .negativeText("Cancel")
                                        .callback(new MaterialDialog.ButtonCallback() {
                                            @Override
                                            public void onPositive(MaterialDialog dialog) {
                                                new Community(communityId, new FindCallback2<ParseObject>(CommunityActivity.this) {
                                                    @Override
                                                    public void done(List<ParseObject> list) {
                                                        if (!list.isEmpty()) {
                                                            ParseObject community = list.get(0);
                                                            community.deleteInBackground(new DeleteCallback() {
                                                                @Override
                                                                public void done(ParseException e) {
                                                                    if (e == null) {
                                                                        Toast.makeText(CommunityActivity.this, communityId + " deleted!", Toast.LENGTH_SHORT).show();
                                                                        CommunityActivity.this.finish();
                                                                    } else {
                                                                        Toast.makeText(CommunityActivity.this, "An error occurred while deleting community (a divine sign, perhaps?)", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                                ParseQuery<ParseObject> subscribed = ParseQuery.getQuery("Subscribed").whereEqualTo("communityId", communityId);
                                                subscribed.findInBackground(new FindCallback2<ParseObject>(getApplicationContext()) {
                                                    @Override
                                                    public void done(List<ParseObject> list) {
                                                        if (!list.isEmpty()) {
                                                            ParseObject po = list.get(0);
                                                            po.deleteInBackground();
                                                        }
                                                    }
                                                });
                                            }
                                        }).show();
                            }
                        }
                    }).show();
        }

        return super.onOptionsItemSelected(item);
    }

    public class PostsAndEventPagerAdapter extends FragmentPagerAdapter {

        private final ArrayList<String> mTitles;

        public PostsAndEventPagerAdapter(FragmentManager fm) {
            super(fm);
            mTitles = new ArrayList<String>() {{
                add("Posts");
                add("Events");
            }};
        }

        @Override
        public int getCount() {
            return mTitles.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return PostsFragment.newInstance(communityId);
            else
                return EventsFragment.newInstance(communityId);
        }
    }

    private class SubscriptionRequestsDialogBuilder {

        final List<ParseObject> memberships;
        final List<String> usernames = new ArrayList<>();

        SubscriptionRequestsDialogBuilder(List<ParseObject> memberships) {
            this.memberships = memberships;
            System.out.println(this.memberships.size());
            for (ParseObject po : memberships) {
                usernames.add(po.getParseUser("user").getString("username"));
            }
        }

        public MaterialDialog buildAndShow() {
            final ArrayAdapter<String> array = new ArrayAdapter<>(CommunityActivity.this, R.layout.row_bg, R.id.text, usernames);
            final SwipeActionAdapter adapter = new SwipeActionAdapter(array);
            adapter.addBackground(SwipeDirections.DIRECTION_NORMAL_LEFT, R.layout.row_bg_reject)
                    .addBackground(SwipeDirections.DIRECTION_NORMAL_RIGHT, R.layout.row_bg_accept);

            final MaterialDialog dialog = new MaterialDialog.Builder(CommunityActivity.this)
                    .title("Accept/reject requests")
                    .adapter(adapter, new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int i, CharSequence sequence) {

                        }
                    })
                    .build();


            adapter.setDimBackgrounds(true);

            adapter.setSwipeActionListener(new SwipeActionAdapter.SwipeActionListener() {
                @Override
                public boolean hasActions(int position) {
                    // All items can be swiped
                    return true;
                }

                @Override
                public boolean shouldDismiss(int position, int direction) {
                    // Only dismiss an item when swiping normal left
                    return direction == SwipeDirections.DIRECTION_NORMAL_LEFT;
                }

                @Override
                public void onSwipe(int[] positionList, int[] directionList) {
                    for (final int[] i = {0}; i[0] < positionList.length; i[0]++) {
                        int direction = directionList[i[0]];
                        final ParseObject membership = memberships.get(i[0]);
                        switch (direction) {
                            case SwipeDirections.DIRECTION_FAR_LEFT:
                            case SwipeDirections.DIRECTION_NORMAL_LEFT:
                                Subscription.subscribe(ParseUser.getCurrentUser(), membership.getString("communityId"), new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            memberships.remove(i[0]);
                                            membership.deleteInBackground();
                                        } else {
                                            Toast.makeText(CommunityActivity.this, "Something went wrong. Unable to accept request.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                if (memberships.isEmpty()) {
                                    dialog.dismiss();
                                }
                                array.notifyDataSetChanged();
                                adapter.notifyDataSetChanged();

                                break;
                            case SwipeDirections.DIRECTION_FAR_RIGHT:
                            case SwipeDirections.DIRECTION_NORMAL_RIGHT:
                                memberships.remove(i[0]);
                                membership.deleteInBackground();

                                if (memberships.isEmpty()) {
                                    dialog.dismiss();
                                }

                                array.notifyDataSetChanged();
                                adapter.notifyDataSetChanged();
                                break;
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            //View v = LayoutInflater.from(CommunityActivity.this).inflate(R.layout.list_item_subscription_request, null);

            adapter.setListView(dialog.getListView());

            if (!memberships.isEmpty())
                dialog.show();
            else
                Toast.makeText(CommunityActivity.this, "No requests", Toast.LENGTH_SHORT).show();

            return dialog;
        }

    }

}

