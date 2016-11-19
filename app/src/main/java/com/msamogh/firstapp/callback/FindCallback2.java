package com.msamogh.firstapp.callback;

import android.content.Context;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.List;

/**
 * Created by root on 7/7/15.
 */
public abstract class FindCallback2<T extends ParseObject> implements FindCallback<T> {

    private String message;
    private final Context ctx;

    public FindCallback2(Context ctx) {
        this.ctx = ctx;
    }

    public FindCallback2(Context ctx, String message) {
        this.ctx = ctx;
        this.message = message;
    }

    @Override
    public void done(List<T> list, ParseException e) {
        if (e == null) {
            done(list);
        } else {
            Toast.makeText(ctx, message == null ? e.getMessage() : message, Toast.LENGTH_SHORT).show();
        }
    }

    public abstract void done(List<T> list);
}
