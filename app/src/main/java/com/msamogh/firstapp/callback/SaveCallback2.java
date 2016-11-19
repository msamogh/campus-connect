package com.msamogh.firstapp.callback;

import android.content.Context;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.SaveCallback;

/**
 * Created by Amogh on 7/7/15.
 */
public abstract class SaveCallback2 implements SaveCallback {

    private String message;
    private final Context ctx;

    public SaveCallback2(Context ctx) {
        this.ctx = ctx;
        this.message = "Could not create community. Please try again.";
    }

    @Override
    public void done(ParseException e) {
        if (e == null) {
            done();
        } else {
            Toast.makeText(ctx, message == null ? e.getMessage() : message, Toast.LENGTH_SHORT).show();
        }
    }

    public abstract void done();

}


