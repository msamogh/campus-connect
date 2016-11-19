package com.msamogh.firstapp.model;

import android.content.Context;
import android.support.annotation.Nullable;

import com.msamogh.firstapp.callback.FindCallback2;
import com.msamogh.firstapp.util.MyCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

/**
 * Created by Amogh on 6/27/15.
 */
public class Community {

    private ParseObject community;

    public Community(final String id, @Nullable FindCallback<ParseObject> callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Community");
        query.whereEqualTo("communityId", id);
        if (callback == null)
            query.findInBackground();
        else
            query.findInBackground(callback);
    }

    public Community(ParseObject community) {
        this.community = community;
    }

    public Community(final Context ctx, final ParseUser admin, final String id, final String name, final String description, final boolean isPrivate, @Nullable final SaveCallback callback) throws ParseModelException {
        final ExceptionWrapper bw = new ExceptionWrapper();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Community");
        query.whereEqualTo("communityId", id);
        query.findInBackground(new FindCallback2<ParseObject>(ctx) {
            @Override
            public void done(List<ParseObject> list) {
                if (!list.isEmpty()) {
                    bw.title = "ID already taken";
                    bw.message = "Community " + id + " already exists.";
                } else {
                    community = new ParseObject("Community");
                    community.put("admin", admin);
                    community.put("communityId", id);
                    community.put("name", name);
                    community.put("private", isPrivate);
                    community.put("description", description);
                    if (callback != null)
                        community.saveInBackground(callback);
                    else
                        community.saveInBackground();
                }
            }

        });
        if (bw.title != null)
            throw new ParseModelException(bw.title, bw.message);
    }

    public static void fetchAllCommunitiesWithAdmin(ParseUser admin, boolean forceReload, FindCallback<ParseObject> callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Community");
        query.setCachePolicy(forceReload ? ParseQuery.CachePolicy.NETWORK_ELSE_CACHE : ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.whereEqualTo("admin", admin);
        query.findInBackground(callback);
    }

    public static void fetchAllCommunities(@Nullable final String searchTerm, final MyCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Community");
        if (searchTerm != null)
            query.whereContains("communityId", searchTerm);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> list, ParseException e) {
                for (final ParseObject po : list) {
                    po.getParseObject("community").fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject community, ParseException e) {
                            callback.callback();
                        }
                    });
                }
            }
        });
    }
}







