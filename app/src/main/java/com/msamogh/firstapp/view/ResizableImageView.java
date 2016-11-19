package com.msamogh.firstapp.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class ResizableImageView extends ImageView {

    private Bitmap mBitmap;

    public ResizableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec,
                             int heightMeasureSpec) {
        if (mBitmap != null) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = width * mBitmap.getHeight() / mBitmap.getWidth();
            setMeasuredDimension(width, height);

        } else {
            super.onMeasure(widthMeasureSpec,
                    heightMeasureSpec);
        }
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        super.setImageBitmap(bitmap);
    }

}