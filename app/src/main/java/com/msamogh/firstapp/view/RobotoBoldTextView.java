package com.msamogh.firstapp.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by root on 6/29/15.
 */
public class RobotoBoldTextView extends TextView {

    public RobotoBoldTextView(Context context) {
        super(context);
        Typeface robotoRegular = Typeface.createFromAsset(context.getAssets(), "Roboto-Bold.ttf");
        setTypeface(robotoRegular);
    }

    public RobotoBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface robotoRegular = Typeface.createFromAsset(context.getAssets(), "Roboto-Bold.ttf");
        setTypeface(robotoRegular);
    }

    public RobotoBoldTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Typeface robotoRegular = Typeface.createFromAsset(context.getAssets(), "Roboto-Bold.ttf");
        setTypeface(robotoRegular);
    }

}
