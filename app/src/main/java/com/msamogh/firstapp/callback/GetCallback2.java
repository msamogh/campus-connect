package com.msamogh.firstapp.callback;

import android.content.Context;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

/**
 * Created by root on 7/7/15.
 */
public abstract class GetCallback2<T extends ParseObject> implements GetCallback<ParseObject> {

    private String message;
    private final Context ctx;

    public GetCallback2(Context ctx) {
        this.ctx = ctx;
    }

    public GetCallback2(Context ctx, String message) {
        this.ctx = ctx;
        this.message = message;
    }

    @Override
    public void done(ParseObject object, ParseException e) {
        if (e == null) {
            done(object);
        } else {
            Toast.makeText(ctx, message == null ? e.getMessage() : message, Toast.LENGTH_SHORT).show();
        }
    }

    public abstract void done(ParseObject obj);
}
