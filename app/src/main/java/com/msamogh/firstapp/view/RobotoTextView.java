package com.msamogh.firstapp.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by root on 6/29/15.
 */
public class RobotoTextView extends TextView {

    public RobotoTextView(Context context) {
        super(context);
        Typeface robotoRegular = Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
        setTypeface(robotoRegular);
    }

    public RobotoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface robotoRegular = Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
        setTypeface(robotoRegular);
    }

    public RobotoTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Typeface robotoRegular = Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
        setTypeface(robotoRegular);
    }

}
