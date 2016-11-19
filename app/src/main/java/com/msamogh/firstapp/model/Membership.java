package com.msamogh.firstapp.model;

import com.msamogh.firstapp.callback.SaveCallback2;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Amogh on 7/11/15.
 */
public class Membership {

    public static void hasRequestedMembership(final String community, final FindCallback callback) {
        ParseQuery<ParseObject> existing = ParseQuery.getQuery("MembershipRequest").whereEqualTo("user", ParseUser.getCurrentUser()).whereEqualTo("communityId", community);
        existing.findInBackground(callback);
    }

    public static void requestMembership(final String community, final SaveCallback2 callback) {
        ParseQuery<ParseObject> existing = ParseQuery.getQuery("MembershipRequest").whereEqualTo("user", ParseUser.getCurrentUser()).whereEqualTo("communityId", community);
        existing.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list.isEmpty()) {
                        ParseObject po = new ParseObject("MembershipRequest");
                        po.put("user", ParseUser.getCurrentUser());
                        po.put("communityId", community);
                        po.saveInBackground(callback);
                    }
                } else {

                }
            }
        });
    }

}
