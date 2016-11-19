package com.msamogh.firstapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.msamogh.firstapp.adapter.CommunityFeedAdapter;
import com.msamogh.firstapp.adapter.HomeFeedAdapter;
import com.msamogh.firstapp.adapter.SearchAdapter;
import com.msamogh.firstapp.admin.CreateCommunityActivity;
import com.msamogh.firstapp.admin.ManageCommunitiesActivity;
import com.msamogh.firstapp.callback.FindCallback2;
import com.msamogh.firstapp.callback.SaveCallback2;
import com.msamogh.firstapp.model.Community;
import com.msamogh.firstapp.model.Membership;
import com.msamogh.firstapp.model.Subscription;
import com.msamogh.firstapp.util.MyCallback;
import com.parse.CountCallback;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private Drawer mDrawer;
    private ListView mFeed;
    private View progress;
    private Toolbar toolbar;
    private String currentCommunityId = null;
    private int currentPosition = 0;
    private Menu mMenu;
    private final static int META_DRAWER_ITEMS_COUNT = 3;
    private View noPostsToShow;


    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            Toast.makeText(this, "Restoring failed", Toast.LENGTH_SHORT).show();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Home");

        progress = findViewById(R.id.progress_view);
        noPostsToShow = findViewById(R.id.no_posts_to_show);


        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mFeed.setAdapter(new HomeFeedAdapter(MainActivity.this, true, new MyCallback() {
                    @Override
                    public void callback() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }));
            }
        });

        mSwipeRefreshLayout.setVisibility(View.GONE);

        mFeed = (ListView) findViewById(R.id.feed);
        mFeed.setEmptyView(noPostsToShow);

        mFeed.setDrawingCacheEnabled(true);
        mFeed.setScrollingCacheEnabled(false);
        mFeed.setSmoothScrollbarEnabled(false);

        mFeed.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if (mFeed != null && mFeed.getChildCount() > 0) {
                    // check if the first item of the list is visible
                    boolean firstItemVisible = mFeed.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = mFeed.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                mSwipeRefreshLayout.setEnabled(enable);
            }
        });

        currentCommunityId = null;
        currentPosition = 0;
        progress.setVisibility(View.VISIBLE);

        final HomeFeedAdapter adapter = new HomeFeedAdapter(this, true, new MyCallback() {
            @Override
            public void callback() {
                progress.setVisibility(View.GONE);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
            }
        });

        mFeed.setAdapter(adapter);


        initSubscribedCommunities(false);
    }

    /**
     * Initializes Navigation Drawer and Toolbar and its children items
     */
    private void initDrawer() {
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withIcon((Drawable) null).withName(ParseUser.getCurrentUser().getString("name")).withEmail(ParseUser.getCurrentUser().getEmail())
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Home").withIcon(R.drawable.activity_feed).withTextColor(Color.argb(120, 0, 0, 0)).withSelectedColor(android.R.color.transparent).withSelectedTextColor(Color.BLACK),
                        new PrimaryDrawerItem().withName("Search").withIcon(R.drawable.search).withTextColor(Color.argb(120, 0, 0, 0)).withSelectedColor(android.R.color.transparent).withSelectedTextColor(Color.BLACK),
                        new SectionDrawerItem().withName("Subscribed"))
                .build();
        mDrawer.getAdapter().notifyDataSetChanged();
        mDrawer.closeDrawer();
    }

    /**
     * 1. Changes current community ID
     * 2. Reloads menu
     * 3. Sets up action bar
     * 4. Changes pin icon depending on subscribed / not
     *
     * @param community community chosen
     */
    private void handleCommunityChoose(final String community) {
        currentCommunityId = community;
        reloadMenu();
        getSupportActionBar().setDisplayShowCustomEnabled(false);
        getSupportActionBar().setTitle(community);

        findViewById(R.id.private_community_layout).setVisibility(View.GONE);

        progress.setVisibility(View.VISIBLE);
        noPostsToShow.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.GONE);

        Subscription.countSubscribed(ParseUser.getCurrentUser(), community, new CountCallback() {
            @Override
            public void done(final int i, ParseException e) {
                if (e == null) {
                    if (i == 0) {
                        mMenu.findItem(R.id.action_subscribe).setVisible(true);
                        mMenu.findItem(R.id.action_unsubscribe).setVisible(false);
                    } else {
                        mMenu.findItem(R.id.action_subscribe).setVisible(false);
                        mMenu.findItem(R.id.action_unsubscribe).setVisible(true);
                    }
                    new Community(community, new FindCallback2<ParseObject>(MainActivity.this) {
                        @Override
                        public void done(List<ParseObject> list) {
                            if (!list.isEmpty()) {
                                ParseObject selectedCommunity = list.get(0);
                                if (selectedCommunity.getBoolean("private") && i == 0) {
                                    mMenu.findItem(R.id.action_subscribe).setVisible(false); // disables subscribe button for private communities
                                    progress.setVisibility(View.GONE);
                                    findViewById(R.id.private_community_layout).setVisibility(View.VISIBLE);
                                    final Button requestMembership = (Button) findViewById(R.id.request_membership);
                                    Membership.hasRequestedMembership(community, new FindCallback2<ParseObject>(MainActivity.this) {
                                        @Override
                                        public void done(List<ParseObject> list) {
                                            if (list.isEmpty()) {
                                                requestMembership.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Membership.requestMembership(community, new SaveCallback2(MainActivity.this) {
                                                            @Override
                                                            public void done() {
                                                                requestMembership.setEnabled(false);
                                                                requestMembership.setText("Requested");
                                                            }
                                                        });
                                                    }
                                                });
                                            } else {
                                                requestMembership.setEnabled(false);
                                                requestMembership.setText("Requested");
                                            }
                                        }
                                    });

                                } else {
                                    CommunityFeedAdapter adapter = new CommunityFeedAdapter(MainActivity.this, selectedCommunity, new MyCallback() {
                                        @Override
                                        public void callback() {
                                            progress.setVisibility(View.GONE);
                                            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    mFeed.setAdapter(adapter);
                                }
                            }
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Could not fetch subscribed communities", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mMenu.findItem(R.id.action_subscribe).setVisible(false);
        mMenu.findItem(R.id.action_unsubscribe).setVisible(true);

    }

    private void reloadMenu() {
        mMenu.clear();
        getMenuInflater().inflate(R.menu.menu_main, mMenu);
    }

    private boolean handleDrawerItemClick(int position, List<ParseObject> communities) {
        reloadMenu();
        handleActionBar(communities, position);

        currentPosition = position;


        if (position == 0) {
            currentCommunityId = null;
            mSwipeRefreshLayout.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            noPostsToShow.setVisibility(View.GONE);

            HomeFeedAdapter adapter = new HomeFeedAdapter(this, true, new MyCallback() {
                @Override
                public void callback() {
                    progress.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                }
            });
            refreshFeed(adapter);

        } else if (position == 1) {
            currentCommunityId = null;
            mMenu.clear();
        } else if (position > 2) {
            position -= META_DRAWER_ITEMS_COUNT;

            mSwipeRefreshLayout.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            noPostsToShow.setVisibility(View.GONE);
            final String community = communities.get(position).getString("communityId");
            currentCommunityId = community;

            new Community(community, new FindCallback2<ParseObject>(MainActivity.this) {
                @Override
                public void done(List<ParseObject> list) {
                    if (!list.isEmpty()) {
                        CommunityFeedAdapter adapter = new CommunityFeedAdapter(MainActivity.this, list.get(0), new MyCallback() {
                            @Override
                            public void callback() {
                                progress.setVisibility(View.GONE);
                                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                            }
                        });
                        refreshFeed(adapter);
                    }
                }
            });
        }
        return false;
    }

    private void initSubscribedCommunities(boolean force) {

        Subscription.fetchSubscribedCommunities(ParseUser.getCurrentUser(), force, new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> communities, ParseException e) {
                if (mDrawer != null) {
                    mDrawer.removeAllItems();
                    mDrawer.getAdapter().notifyDataSetChanged();
                    mDrawer.closeDrawer();
                }
                initDrawer();

                if (e == null) {
                    if (communities.isEmpty()) {
                        mDrawer.setItem(new SectionDrawerItem().withName("No subscribed communities"), META_DRAWER_ITEMS_COUNT - 1);
                    }
                    for (ParseObject po : communities) {
                        String community = po.getString("communityId");
                        mDrawer.addItem(new PrimaryDrawerItem().withName(community).withTextColor(Color.argb(120, 0, 0, 0)).withSelectedColor(android.R.color.transparent).withSelectedTextColor(Color.BLACK));
                    }
                    mDrawer.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(AdapterView<?> view, View view1, int position, long l, IDrawerItem item) {
                            return handleDrawerItemClick(position, communities);
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Couldn't fetch subscribed communities.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void handleActionBar(List<ParseObject> communities, int position) {
        switch (position) {
            case 0:
                getSupportActionBar().setDisplayShowCustomEnabled(false);
                getSupportActionBar().setTitle("Home");
                mMenu.findItem(R.id.action_subscribe).setVisible(false);
                mMenu.findItem(R.id.action_unsubscribe).setVisible(false);
                break;
            case 1:
                mFeed.invalidate();
                mSwipeRefreshLayout.setVisibility(View.GONE);
                LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                // inflate the view that we created before
                View v = inflater.inflate(R.layout.toolbar_search, null);
                AutoCompleteTextView textView = (AutoCompleteTextView) v.findViewById(R.id.search_box);

                textView.setAdapter(new SearchAdapter(this));
                textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                        TextView communityId = (TextView) view.findViewById(R.id.search_item_venue_address);
                        String community = communityId.getText().toString();
                        handleCommunityChoose(community);
                    }
                });

                getSupportActionBar().setDisplayShowCustomEnabled(true);
                getSupportActionBar().setCustomView(v);
                break;
            default:
                getSupportActionBar().setDisplayShowCustomEnabled(false);
                if (position > 2) {
                    mMenu.findItem(R.id.action_subscribe).setVisible(false);
                    mMenu.findItem(R.id.action_unsubscribe).setVisible(true);
                    String community = communities.get(position - META_DRAWER_ITEMS_COUNT).getString("communityId");
                    getSupportActionBar().setTitle(community);
                }
        }
    }

    private void refreshFeed(BaseAdapter adapter) {
        mFeed.invalidate();
        mFeed.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer != null) {
            if (mDrawer.isDrawerOpen()) {
                mDrawer.closeDrawer();
            } else {
                if (currentPosition != 0) {
                    currentPosition = 0;
                    mDrawer.setSelection(0);
                    handleDrawerItemClick(0, null);
                } else {
                    finish();
                }
            }
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu;
        menu.findItem(R.id.action_subscribe).setVisible(false);
        menu.findItem(R.id.action_unsubscribe).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(i);
        }

        if (id == R.id.action_subscribe) {
            item.setVisible(false);
            mMenu.findItem(R.id.action_unsubscribe).setVisible(true);
            if (currentCommunityId != null) {
                Subscription.subscribe(ParseUser.getCurrentUser(), currentCommunityId, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(MainActivity.this, "'" + currentCommunityId + "' added to subscribed communities", Toast.LENGTH_SHORT).show();
                            initSubscribedCommunities(true);
                            mDrawer.getAdapter().notifyDataSetChanged();
                        } else {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            mMenu.findItem(R.id.action_unsubscribe).setVisible(false);
                            mMenu.findItem(R.id.action_subscribe).setVisible(true);
                        }
                    }
                });
            }
        }

        if (id == R.id.action_unsubscribe) {
            item.setVisible(false);
            mMenu.findItem(R.id.action_subscribe).setVisible(true);
            if (currentCommunityId != null) {
                Subscription.unsubscribe(ParseUser.getCurrentUser(), currentCommunityId, new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        if (e == null) {
                            if (!list.isEmpty()) {
                                ParseObject po = list.get(0);
                                po.deleteInBackground(new DeleteCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Toast.makeText(MainActivity.this, "Unsubscribed from " + currentCommunityId, Toast.LENGTH_SHORT).show();
                                            initSubscribedCommunities(true);
                                            mDrawer.getAdapter().notifyDataSetChanged();
                                        } else {
                                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            mMenu.findItem(R.id.action_subscribe).setVisible(false);
                                            item.setVisible(true);

                                        }
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            mMenu.findItem(R.id.action_subscribe).setVisible(false);
                            item.setVisible(true);
                        }
                    }
                });
            }
        }

        if (id == R.id.action_new_community) {
            startActivity(new Intent(this, CreateCommunityActivity.class));
        }

        if (id == R.id.action_manage_communities) {
            startActivity(new Intent(this, ManageCommunitiesActivity.class));
        }

        if (id == R.id.action_signout) {
            new MaterialDialog.Builder(MainActivity.this)
                    .content("Are you sure you want to sign out?")
                    .positiveText("Sign Out")
                    .negativeColor(Color.parseColor("#666666"))
                    .negativeText("Cancel").callback(new MaterialDialog.ButtonCallback() {
                @Override
                public void onPositive(MaterialDialog dialog) {
                    ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
                        @Override
                        public void done(ParseException e) {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    });
                }
            }).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("dummy", "dummy");
        super.onSaveInstanceState(outState);
    }
}

