package com.msamogh.firstapp.model;

import android.support.annotation.Nullable;

import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.Date;

/**
 * Created by Amogh on 6/27/15.
 */
public class Event  {

    private final ParseObject event;

    public Event(final String id, @Nullable GetCallback<ParseObject> callback) {
        event = ParseObject.createWithoutData("Event", id);
        if (callback != null)
            event.fetchInBackground(callback);
        else
            event.fetchInBackground();
    }

    public Event(ParseObject community, String name, Date date, String location, String contact, boolean rsvp, String description, @Nullable final SaveCallback callback) {
        event = ParseObject.create("Post");
        event.put("name", name);
        event.put("date", date);
        event.put("community", community);
        event.put("location", location);
        event.put("contact", contact);
        event.put("description", description);
        event.put("rsvp", rsvp);
        event.put("type", "event");
        if (callback != null)
            event.saveInBackground(callback);
        else
            event.saveInBackground();
    }
}
