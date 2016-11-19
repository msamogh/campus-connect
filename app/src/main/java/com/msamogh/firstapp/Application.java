package com.msamogh.firstapp;

import com.parse.Parse;
import com.parse.ParseObject;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amogh on 6/26/15.
 */
public class Application extends android.app.Application {

    private List<ParseObject> feed = new ArrayList<>();

    public void setFeed(List<ParseObject> feed) {
        this.feed = feed;
    }

    public List<ParseObject> getFeed() {
        return feed;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);


        initParse();
    }

    /**
     * Initializes Parse SDK
     */
    private void initParse() {

        Parse.initialize(this, "R0tawVf3WsML1zYL7eLxAfM9TOAuRQsCD4YB3lNs", "kpeoI4mKqU95XEin1rt12ztlNU9ppUib6d45kwwP");
    }
}
