package com.msamogh.firstapp.model;

import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by Amogh on 7/3/15.
 */

public class Subscription {

    public static final String COLLECTION_NAME = "Subscription";

    public static void subscribe(final ParseUser user, final String community, SaveCallback callback) {
        ParseObject subscription = ParseObject.create("Subscription");
        subscription.put("user", user);
        subscription.put("communityId", community);
        subscription.saveInBackground(callback);
    }

    public static void unsubscribe(final ParseUser user, final String community, FindCallback<ParseObject> callback) {
        ParseQuery<ParseObject> subscription = ParseQuery.getQuery("Subscription");
        subscription.whereEqualTo("user", user);
        subscription.whereEqualTo("communityId", community);
        subscription.findInBackground(callback);
    }

    public static void fetchSubscribedCommunities(ParseUser user, boolean forceReload, final FindCallback<ParseObject> callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Subscription").whereEqualTo("user", user);
        query.setCachePolicy(forceReload ? ParseQuery.CachePolicy.CACHE_THEN_NETWORK : ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.findInBackground(callback);
    }

    public static void countSubscribed(ParseUser user, String community, CountCallback callback) {
        ParseQuery<ParseObject> count = ParseQuery.getQuery("Subscription");
        count.whereEqualTo("user", user);
        count.whereEqualTo("communityId", community);
        count.countInBackground(callback);
    }

}
